package ohio.rizz.homeappliancestore.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ohio.rizz.homeappliancestore.dto.*;
import ohio.rizz.homeappliancestore.entities.Product;
import ohio.rizz.homeappliancestore.entities.Supplier;
import ohio.rizz.homeappliancestore.entities.Supply;
import ohio.rizz.homeappliancestore.entities.SupplyItem;
import ohio.rizz.homeappliancestore.enums.AuditAction;
import ohio.rizz.homeappliancestore.enums.AuditEntityType;
import ohio.rizz.homeappliancestore.enums.SupplyStatus;
import ohio.rizz.homeappliancestore.exceptions.ProductNotFoundException;
import ohio.rizz.homeappliancestore.exceptions.SupplierNotFoundException;
import ohio.rizz.homeappliancestore.exceptions.SupplyNotFoundException;
import ohio.rizz.homeappliancestore.mappers.SupplyItemMapper;
import ohio.rizz.homeappliancestore.mappers.SupplyMapper;
import ohio.rizz.homeappliancestore.repositories.ProductRepository;
import ohio.rizz.homeappliancestore.repositories.SupplierRepository;
import ohio.rizz.homeappliancestore.repositories.SupplyItemRepository;
import ohio.rizz.homeappliancestore.repositories.SupplyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplyService {
    private final SupplyRepository supplyRepository;
    private final SupplyItemRepository supplyItemRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final SupplyMapper supplyMapper;
    private final SupplyItemMapper supplyItemMapper;
    private final AuditService auditService;

    public List<SupplyDto> getAllSupplies() {
        return supplyMapper.toDto(supplyRepository.findAll());
    }

    public SupplyDto getSupplyById(UUID id) {
        Supply supply = supplyRepository.findByIdWithItems(id)
                .orElseThrow(() -> new SupplyNotFoundException("Поставка не найдена"));
        return supplyMapper.toDto(supply);
    }

    public Supply getSupplyEntityById(UUID id) {
        return supplyRepository.findById(id)
                .orElseThrow(() -> new SupplyNotFoundException("Поставка не найдена"));
    }

    public List<SupplyDto> getSuppliesBySupplier(UUID supplierId) {
        return supplyMapper.toDto(supplyRepository.findBySupplierId(supplierId));
    }

    public List<SupplyDto> getSuppliesByStatus(SupplyStatus status) {
        return supplyMapper.toDto(supplyRepository.findByStatus(status));
    }

    public List<SupplyDto> searchSupplies(String searchTerm) {
        return supplyMapper.toDto(supplyRepository.searchBySupplyNumber(searchTerm));
    }

    public List<SupplyDto> getFilteredSupplies(String searchTerm, SupplyStatus status) {
        // Начинаем со всех поставок
        List<Supply> supplies;

        // Сначала применяем статус, если он указан
        if (status != null) {
            supplies = supplyRepository.findByStatus(status);
        } else {
            supplies = supplyRepository.findAll();
        }

        // Применяем поиск по номеру, если он указан
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String search = searchTerm.trim().toLowerCase();
            supplies = supplies.stream()
                    .filter(supply -> supply.getSupplyNumber().toLowerCase().contains(search))
                    .collect(Collectors.toList());
        }

        return supplyMapper.toDto(supplies);
    }

    @Transactional
    public SupplyDto createSupply(SupplyCreateDto createDto) {
        // Получаем поставщика
        Supplier supplier = supplierRepository.findById(createDto.getSupplierId())
                .orElseThrow(() -> new SupplierNotFoundException("Поставщик не найден"));

        // Создаем поставку
        Supply supply = supplyMapper.toEntity(createDto);
        supply.setSupplier(supplier);

        // Устанавливаем дату поставки
        if (createDto.getSupplyDate() == null) {
            supply.setSupplyDate(LocalDateTime.now());
        }

        // Добавляем товары
        for (SupplyItemCreateDto itemDto : createDto.getItems()) {
            addSupplyItem(supply, itemDto);
        }

        // Сохраняем поставку
        Supply savedSupply = supplyRepository.save(supply);

        auditService.log(AuditAction.CREATE, AuditEntityType.SUPPLY, savedSupply.getId().toString(),
                String.format("Created supply '%s'", savedSupply.getSupplyNumber()));

        return supplyMapper.toDto(savedSupply);
    }

    @Transactional
    public SupplyDto updateSupply(UUID id, SupplyUpdateDto updateDto) {
        Supply supply = supplyRepository.findByIdWithItems(id)
                .orElseThrow(() -> new SupplyNotFoundException("Поставка не найдена"));

        // Проверяем, можно ли редактировать поставку
        if (supply.getStatus() == SupplyStatus.COMPLETED) {
            throw new IllegalStateException("Нельзя редактировать завершенную поставку");
        }

        // Обновляем поля
        supply.setNotes(updateDto.getNotes());

        if (updateDto.getStatus() != null) {
            supply.setStatus(SupplyStatus.valueOf(updateDto.getStatus()));
        }

        // Возвращаем товары на склад (если меняем состав поставки)
        returnItemsToStock(supply);

        // Очищаем текущие товары
        supply.getSupplyItems().clear();

        // Добавляем новые товары
        for (SupplyItemCreateDto itemDto : updateDto.getItems()) {
            addSupplyItem(supply, itemDto);
        }

        Supply updatedSupply = supplyRepository.save(supply);

        auditService.log(AuditAction.UPDATE, AuditEntityType.SUPPLY, id.toString(),
                String.format("Updated supply '%s'", updatedSupply.getSupplyNumber()));

        return supplyMapper.toDto(updatedSupply);
    }

    @Transactional
    public SupplyDto completeSupply(UUID id) {
        Supply supply = supplyRepository.findByIdWithItems(id)
                .orElseThrow(() -> new SupplyNotFoundException("Поставка не найдена"));

        if (supply.getStatus() == SupplyStatus.COMPLETED) {
            throw new IllegalStateException("Поставка уже завершена");
        }

        // Меняем статус
        supply.setStatus(SupplyStatus.COMPLETED);

        // Увеличиваем количество товара на складе
        addItemsToStock(supply);

        Supply completedSupply = supplyRepository.save(supply);

        auditService.log(AuditAction.COMPLETE, AuditEntityType.SUPPLY, id.toString(),
                String.format("Supply with number '%s' marked as completed", completedSupply.getSupplyNumber()));

        return supplyMapper.toDto(completedSupply);
    }

    @Transactional
    public void deleteSupply(UUID id) {
        Supply supply = supplyRepository.findByIdWithItems(id)
                .orElseThrow(() -> new SupplyNotFoundException("Поставка не найдена"));

        // Если поставка была завершена, уменьшаем количество товара на складе
        if (supply.getStatus() == SupplyStatus.COMPLETED) {
            returnItemsToStock(supply);
        }

        String supplyNumber = supply.getSupplyNumber();

        supplyRepository.delete(supply);

        auditService.log(AuditAction.DELETE, AuditEntityType.SUPPLY, id.toString(),
                String.format("Deleted supply '%s'", supplyNumber));
    }

    private void addSupplyItem(Supply supply, SupplyItemCreateDto itemDto) {
        Product product = productRepository.findById(itemDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден"));

        SupplyItem item = supplyItemMapper.toEntity(itemDto);
        item.setProduct(product);

        supply.addSupplyItem(item);
    }

    private void addItemsToStock(Supply supply) {
        for (SupplyItem item : supply.getSupplyItems()) {
            Product product = item.getProduct();
            product.increaseStock(item.getQuantity());
            product.setCostPrice(item.getPricePerUnit());
            productRepository.save(product);
        }
    }

    private void returnItemsToStock(Supply supply) {
        for (SupplyItem item : supply.getSupplyItems()) {
            Product product = item.getProduct();
            product.decreaseStock(item.getQuantity());
            productRepository.save(product);
        }
    }
}