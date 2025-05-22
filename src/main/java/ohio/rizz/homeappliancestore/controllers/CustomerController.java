package ohio.rizz.homeappliancestore.controllers;

import ohio.rizz.homeappliancestore.dto.CustomerDto;
import ohio.rizz.homeappliancestore.entities.Customer;
import ohio.rizz.homeappliancestore.entities.Product;
import ohio.rizz.homeappliancestore.exceptions.CustomerNotFoundException;
import ohio.rizz.homeappliancestore.exceptions.ProductNotFoundException;
import ohio.rizz.homeappliancestore.services.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public String listCustomers(@RequestParam(required = false) String search, Model model) {
        List<Customer> customers;
        if (search != null && !search.isEmpty()) {
            customers = customerService.searchCustomers(search);
        } else {
            customers = customerService.getAllCustomers();
        }
        model.addAttribute("customers", customers);
        return "customers";
    }

    @GetMapping("/customers/{id}")
    public String getCustomerById(@PathVariable Long id, Model model) {
        Customer customer = customerService.getCustomerById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Клиент не найден!"));
        model.addAttribute("customer", customer);
        return "customer-details";
    }

    @GetMapping("/add")
    public String showAddCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "add-customer";
    }

    @PostMapping("/add")
    public String addCustomer(
            @ModelAttribute Customer customer,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {

        try {
            // Обработка изображения
            if (!imageFile.isEmpty()) {
                String uploadDir = "uploads/avatars/";
                String fileName = System.currentTimeMillis() + "_" + customer.getId() + ".jpg";

                // Создаем директорию, если не существует
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Сохраняем файл
                try (InputStream inputStream = imageFile.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + fileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }

                customer.setImagePath("/" + uploadDir + fileName);
            }

            customerService.save(customer);
            redirectAttributes.addFlashAttribute("successMessage", "Клиент успешно добавлен!");
            return "redirect:/customers";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при загрузке изображения");
            return "redirect:/customers/add";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditCustomerForm(@PathVariable Long id, Model model) {
        Customer customer = customerService.getCustomerById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Клиент не найден"));

        CustomerDto customerDTO = new CustomerDto();
        customerDTO.setId(customer.getId());
        customerDTO.setFirstName(customer.getFirstName());
        customerDTO.setLastName(customer.getLastName());
        customerDTO.setEmail(customer.getEmail());
        customerDTO.setPhone(customer.getPhone());
        customerDTO.setAddress(customer.getAddress());
        customerDTO.setImagePath(customer.getImagePath());

        model.addAttribute("customerDto", customerDTO);
        return "edit-customer";
    }

    @PostMapping("/{id}/edit")
    public String updateCustomer(
            @PathVariable Long id,
            @ModelAttribute CustomerDto customerDTO,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "removeImage", required = false, defaultValue = "false") boolean removeImage,
            RedirectAttributes redirectAttributes) {

        try {
            customerService.updateCustomer(id, customerDTO, imageFile, removeImage);
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

}
