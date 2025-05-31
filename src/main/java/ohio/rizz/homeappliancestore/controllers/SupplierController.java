package ohio.rizz.homeappliancestore.controllers;

import ohio.rizz.homeappliancestore.dto.SupplierDto;
import ohio.rizz.homeappliancestore.entities.Supplier;
import ohio.rizz.homeappliancestore.exceptions.SupplierNotFoundException;
import ohio.rizz.homeappliancestore.services.SupplierService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class SupplierController {
    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping("/suppliers")
    public String listSuppliers(@RequestParam(required = false) String search, Model model) {
        if (search != null) {
            model.addAttribute("suppliers", supplierService.searchSuppliers(search));
        } else {
            model.addAttribute("suppliers", supplierService.getAllSuppliers());
        }

        return "suppliers";
    }

    @GetMapping("/suppliers/{id}")
    public String getSupplierDetails(@PathVariable Long id, Model model) {
        Supplier supplier = supplierService.getSupplierById(id)
                .orElseThrow(() -> new SupplierNotFoundException("Поставщик не найден!"));
        model.addAttribute("supplier", supplier);
        return "supplier-details";
    }

    @GetMapping("/suppliers/add")
    public String showAddSupplierForm(Model model) {
        model.addAttribute("supplier", new Supplier());
        return "add-supplier";
    }

    @PostMapping("/suppliers/add")
    public String addSupplier(@ModelAttribute Supplier supplier,
                              RedirectAttributes redirectAttributes) {
        try {
            supplierService.save(supplier);
            redirectAttributes.addFlashAttribute("successMessage", "Поставщик успешно добавлен!");
            return "redirect:/suppliers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при добавлении: " + e.getMessage());
            return "redirect:/suppliers/add";
        }
    }

    @GetMapping("/suppliers/{id}/edit")
    public String showEditSupplierForm(@PathVariable Long id, Model model) {
        Supplier supplier = supplierService.getSupplierById(id)
                .orElseThrow(() -> new SupplierNotFoundException("Поставщик не найден"));

        SupplierDto supplierDto = new SupplierDto();
        supplierDto.setId(id);
        supplierDto.setName(supplier.getName());
        supplierDto.setContactName(supplier.getContactName());
        supplierDto.setAddress(supplier.getAddress());
        supplierDto.setPhone(supplier.getPhone());
        supplierDto.setEmail(supplier.getEmail());

        model.addAttribute("supplierDto", supplierDto);
        return "edit-supplier";
    }

    @PutMapping("/suppliers/{id}")
    public String updateSupplier(
            @PathVariable Long id,
            SupplierDto supplierDto,
            RedirectAttributes redirectAttributes) {
        try {
            supplierService.updateSupplier(id, supplierDto);
            redirectAttributes.addFlashAttribute("successMessage", "Данные поставшика успешно обновлены!");
            return "redirect:/suppliers";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/suppliers/" + id + "/edit";
        }

    }

    @DeleteMapping("/suppliers/{id}")
    public String deleteSupplier(
            @PathVariable Long id,
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
