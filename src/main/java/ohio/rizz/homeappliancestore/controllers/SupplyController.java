package ohio.rizz.homeappliancestore.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ohio.rizz.homeappliancestore.dto.*;
import ohio.rizz.homeappliancestore.enums.SupplyStatus;
import ohio.rizz.homeappliancestore.mappers.SupplyItemEditMapper;
import ohio.rizz.homeappliancestore.services.ProductService;
import ohio.rizz.homeappliancestore.services.SupplierService;
import ohio.rizz.homeappliancestore.services.SupplyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/supplies")
@RequiredArgsConstructor
public class SupplyController {
    private final SupplyService supplyService;
    private final SupplierService supplierService;
    private final ProductService productService;
    private final SupplyItemEditMapper supplyItemEditMapper;

    @GetMapping
    public String listSupplies(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            Model model) {

        // Преобразуем статус из строки в enum
        SupplyStatus supplyStatus = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                supplyStatus = SupplyStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                // Игнорируем неверный статус
            }
        }

        // Получаем отфильтрованные поставки одним методом
        List<SupplyDto> supplies = supplyService.getFilteredSupplies(search, supplyStatus);

        // Подсчет статистики
        int totalSupplies = supplies.size();
        int pendingCount = (int) supplies.stream()
                .filter(s -> "PENDING".equals(s.getStatus().name()))
                .count();
        int completedCount = (int) supplies.stream()
                .filter(s -> "COMPLETED".equals(s.getStatus().name()))
                .count();
        int cancelledCount = (int) supplies.stream()
                .filter(s -> "CANCELLED".equals(s.getStatus().name()))
                .count();

        model.addAttribute("supplies", supplies);
        model.addAttribute("totalSupplies", totalSupplies);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("cancelledCount", cancelledCount);

        // Сохраняем параметры для формы
        model.addAttribute("searchParam", search);
        model.addAttribute("statusParam", status);

        return "supplies";
    }

    @GetMapping("/{id}")
    public String getSupplyDetails(@PathVariable UUID id, Model model) {
        SupplyDto supply = supplyService.getSupplyById(id);
        model.addAttribute("supply", supply);
        model.addAttribute("canEdit", !"COMPLETED".equals(supply.getStatus().name()));
        return "supply-details";
    }

    @GetMapping("/add")
    public String showAddSupplyForm(Model model) {
        SupplyCreateDto supplyCreateDto = new SupplyCreateDto();
        supplyCreateDto.setSupplyDate(LocalDateTime.now());

        model.addAttribute("supply", supplyCreateDto);
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("products", productService.getAllProducts());
        return "add-supply";
    }

    @PostMapping("/add")
    public String addSupply(
            @Valid @ModelAttribute("supply") SupplyCreateDto createDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("suppliers", supplierService.getAllSuppliers());
            model.addAttribute("products", productService.getAllProducts());
            return "add-supply";
        }

        try {
            SupplyDto supply = supplyService.createSupply(createDto);
            redirectAttributes.addFlashAttribute("successMessage", "Поставка успешно создана");
            return "redirect:/supplies/" + supply.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при создании: " + e.getMessage());
            return "redirect:/supplies/add";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditSupplyForm(@PathVariable UUID id, Model model) {
        SupplyDto supply = supplyService.getSupplyById(id);

        if ("COMPLETED".equals(supply.getStatus().name())) {
            throw new IllegalStateException("Нельзя редактировать завершенную поставку");
        }

        SupplyCreateDto createDto = new SupplyCreateDto();
        createDto.setSupplierId(supply.getSupplierId());
        createDto.setSupplyDate(supply.getSupplyDate());
        createDto.setNotes(supply.getNotes());
        createDto.setLogisticCost(supply.getLogisticCost());
        createDto.setSupplyNumber(supply.getSupplyNumber());
        List<SupplyItemCreateDto> itemCreateDtos = supply.getItems().stream()
                .map(supplyItemEditMapper::toCreateDto)
                .collect(Collectors.toList());
        createDto.setItems(itemCreateDtos);

        model.addAttribute("supply", createDto);
        model.addAttribute("supplyId", id);
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("products", productService.getAllProducts());
        return "edit-supply";
    }

    @PutMapping("/{id}")
    public String updateSupply(
            @PathVariable UUID id,
            @Valid @ModelAttribute("supply") SupplyUpdateDto updateDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("suppliers", supplierService.getAllSuppliers());
            model.addAttribute("products", productService.getAllProducts());
            return "edit-supply";
        }

        try {
            SupplyDto supply = supplyService.updateSupply(id, updateDto);
            redirectAttributes.addFlashAttribute("successMessage", "Поставка успешно обновлена");
            return "redirect:/supplies/" + supply.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении: " + e.getMessage());
            return "redirect:/supplies/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/complete")
    public String completeSupply(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes) {
        try {
            SupplyDto supply = supplyService.completeSupply(id);
            redirectAttributes.addFlashAttribute("successMessage", "Поставка успешно завершена");
            return "redirect:/supplies/" + supply.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при завершении: " + e.getMessage());
            return "redirect:/supplies/" + id;
        }
    }

    @DeleteMapping("/{id}")
    public String deleteSupply(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes) {
        try {
            supplyService.deleteSupply(id);
            redirectAttributes.addFlashAttribute("successMessage", "Поставка успешно удалена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении: " + e.getMessage());
        }
        return "redirect:/supplies";
    }
}