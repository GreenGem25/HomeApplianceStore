package ohio.rizz.homeappliancestore.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ohio.rizz.homeappliancestore.dto.SupplierCreateDto;
import ohio.rizz.homeappliancestore.dto.SupplierDto;
import ohio.rizz.homeappliancestore.services.SupplierService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService supplierService;

    @GetMapping
    public String listSuppliers(@RequestParam(required = false) String search, Model model) {
        List<SupplierDto> suppliers;
        if (search != null && !search.isEmpty()) {
            suppliers = supplierService.searchSuppliers(search);
        } else {
            suppliers = supplierService.getAllSuppliers();
        }
        model.addAttribute("suppliers", suppliers);
        return "suppliers";
    }

    @GetMapping("/{id}")
    public String getSupplierDetails(@PathVariable UUID id, Model model) {
        SupplierDto supplier = supplierService.getSupplierById(id);
        model.addAttribute("supplier", supplier);
        return "supplier-details";
    }

    @GetMapping("/add")
    public String showAddSupplierForm(Model model) {
        model.addAttribute("supplier", new SupplierCreateDto());
        return "add-supplier";
    }

    @PostMapping("/add")
    public String addSupplier(
            @Valid @ModelAttribute("supplier") SupplierCreateDto createDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "add-supplier";
        }

        try {
            supplierService.createSupplier(createDto);
            redirectAttributes.addFlashAttribute("successMessage", "Поставщик успешно добавлен!");
            return "redirect:/suppliers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при добавлении: " + e.getMessage());
            return "redirect:/suppliers/add";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditSupplierForm(@PathVariable UUID id, Model model) {
        SupplierDto supplier = supplierService.getSupplierById(id);

        SupplierCreateDto createDto = new SupplierCreateDto();
        createDto.setName(supplier.getName());
        createDto.setContactName(supplier.getContactName());
        createDto.setAddress(supplier.getAddress());
        createDto.setPhone(supplier.getPhone());
        createDto.setEmail(supplier.getEmail());

        model.addAttribute("supplierDto", createDto);
        model.addAttribute("supplierId", id);
        return "edit-supplier";
    }

    @PutMapping("/{id}")
    public String updateSupplier(
            @PathVariable UUID id,
            @Valid @ModelAttribute("supplierDto") SupplierCreateDto createDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "edit-supplier";
        }

        try {
            supplierService.updateSupplier(id, createDto);
            redirectAttributes.addFlashAttribute("successMessage", "Данные поставщика успешно обновлены!");
            return "redirect:/suppliers";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/suppliers/" + id + "/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении: " + e.getMessage());
            return "redirect:/suppliers/" + id + "/edit";
        }
    }

    @DeleteMapping("/{id}")
    public String deleteSupplier(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes) {
        try {
            supplierService.deleteSupplier(id);
            redirectAttributes.addFlashAttribute("successMessage", "Поставщик успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении поставщика: " + e.getMessage());
        }
        return "redirect:/suppliers";
    }
}