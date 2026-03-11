package ohio.rizz.homeappliancestore.controllers;

import lombok.RequiredArgsConstructor;
import ohio.rizz.homeappliancestore.dto.CustomerCreateDto;
import ohio.rizz.homeappliancestore.dto.CustomerDto;
import ohio.rizz.homeappliancestore.services.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    public String listCustomers(@RequestParam(required = false) String search, Model model) {
        List<CustomerDto> customers;
        if (search != null && !search.isEmpty()) {
            customers = customerService.searchCustomers(search);
        } else {
            customers = customerService.getAllCustomers();
        }
        model.addAttribute("customers", customers);
        return "customers";
    }

    @GetMapping("/{id}")
    public String getCustomerDetails(@PathVariable UUID id, Model model) {
        CustomerDto customer = customerService.getCustomerById(id);
        model.addAttribute("customer", customer);
        return "customer-details";
    }

    @GetMapping("/add")
    public String showAddCustomerForm(Model model) {
        model.addAttribute("customer", new CustomerCreateDto());
        return "add-customer";
    }

    @PostMapping("/add")
    public String addCustomer(
            @ModelAttribute("customer") CustomerCreateDto createDto,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {

        try {
            CustomerDto savedCustomer = customerService.createCustomer(createDto);

            // Если есть изображение, обновляем его
            if (!imageFile.isEmpty()) {
                customerService.updateCustomer(savedCustomer.getId(), createDto, imageFile, false);
            }

            redirectAttributes.addFlashAttribute("successMessage", "Клиент успешно добавлен!");
            return "redirect:/customers";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при загрузке изображения");
            return "redirect:/customers/add";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при добавлении: " + e.getMessage());
            return "redirect:/customers/add";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditCustomerForm(@PathVariable UUID id, Model model) {
        CustomerDto customer = customerService.getCustomerById(id);

        CustomerCreateDto createDto = new CustomerCreateDto();
        createDto.setFirstName(customer.getFirstName());
        createDto.setLastName(customer.getLastName());
        createDto.setEmail(customer.getEmail());
        createDto.setPhone(customer.getPhone());
        createDto.setAddress(customer.getAddress());

        model.addAttribute("customerDto", createDto);
        model.addAttribute("customerId", id);
        model.addAttribute("currentImage", customer.getImagePath());
        return "edit-customer";
    }

    @PutMapping("/{id}")
    public String updateCustomer(
            @PathVariable UUID id,
            @ModelAttribute("customerDto") CustomerCreateDto createDto,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "removeImage", required = false, defaultValue = "false") boolean removeImage,
            RedirectAttributes redirectAttributes) {

        try {
            customerService.updateCustomer(id, createDto, imageFile, removeImage);
            redirectAttributes.addFlashAttribute("successMessage", "Данные клиента успешно обновлены!");
            return "redirect:/customers";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/customers/" + id + "/edit";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обработке изображения");
            return "redirect:/customers/" + id + "/edit";
        }
    }

    @DeleteMapping("/{id}")
    public String deleteCustomer(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("successMessage", "Клиент успешно удалён");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении клиента: " + e.getMessage());
        }
        return "redirect:/customers";
    }
}