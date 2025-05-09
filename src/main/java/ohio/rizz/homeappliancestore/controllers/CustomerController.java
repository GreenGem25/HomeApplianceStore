package ohio.rizz.homeappliancestore.controllers;

import ohio.rizz.homeappliancestore.entities.Customer;
import ohio.rizz.homeappliancestore.services.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
