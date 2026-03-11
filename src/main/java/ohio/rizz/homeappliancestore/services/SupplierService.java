package ohio.rizz.homeappliancestore.services;

import lombok.RequiredArgsConstructor;
import ohio.rizz.homeappliancestore.dto.ProductDto;
import ohio.rizz.homeappliancestore.dto.SupplierCreateDto;
import ohio.rizz.homeappliancestore.dto.SupplierDto;
import ohio.rizz.homeappliancestore.entities.Product;
import ohio.rizz.homeappliancestore.entities.Supplier;
import ohio.rizz.homeappliancestore.exceptions.ProductNotFoundException;
import ohio.rizz.homeappliancestore.exceptions.SupplierNotFoundException;
import ohio.rizz.homeappliancestore.mappers.SupplierMapper;
import ohio.rizz.homeappliancestore.repositories.ProductRepository;
import ohio.rizz.homeappliancestore.repositories.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final SupplierMapper supplierMapper;

    public List<SupplierDto> getAllSuppliers() {
        return supplierMapper.toDto(supplierRepository.findAll());
    }

    public SupplierDto getSupplierById(UUID id) {
        Supplier supplier = getSupplierEntityOrThrow(id);
        return supplierMapper.toDto(supplier);
    }

    public Supplier getSupplierEntityOrThrow(UUID id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException("Поставщик не найден"));
    }

    public Optional<Supplier> findSupplierById(UUID id) {
        return supplierRepository.findById(id);
    }

    @Transactional
    public SupplierDto createSupplier(SupplierCreateDto createDto) {
        Supplier supplier = supplierMapper.toEntity(createDto);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toDto(savedSupplier);
    }

    @Transactional
    public SupplierDto updateSupplier(UUID id, SupplierCreateDto updateDto) {
        Supplier existingSupplier = getSupplierEntityOrThrow(id);

        // Проверка уникальности email
        if (!isEmailUnique(id, updateDto.getEmail())) {
            throw new IllegalArgumentException("Email уже используется другим поставщиком");
        }

        supplierMapper.updateEntity(existingSupplier, updateDto);

        Supplier updatedSupplier = supplierRepository.save(existingSupplier);
        return supplierMapper.toDto(updatedSupplier);
    }

    @Transactional
    public void deleteSupplier(UUID id) {
        Supplier supplier = getSupplierEntityOrThrow(id);

        productRepository.findBySupplier_Id(supplier.getId())
                .forEach(product -> {
                    product.setSupplier(null);
                    productRepository.save(product);
                });

        supplierRepository.delete(supplier);
    }

    public List<SupplierDto> searchSuppliers(String query) {
        return supplierMapper.toDto(
                supplierRepository.findByNameContainingIgnoreCaseOrContactNameContainingIgnoreCase(query, query)
        );
    }

    public boolean isEmailUnique(UUID id, String email) {
        Supplier supplier = supplierRepository.findByEmail(email);
        return supplier == null || supplier.getId().equals(id);
    }
}