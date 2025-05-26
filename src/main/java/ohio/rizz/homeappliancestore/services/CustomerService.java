package ohio.rizz.homeappliancestore.services;

import jakarta.transaction.Transactional;
import ohio.rizz.homeappliancestore.dto.CustomerDto;
import ohio.rizz.homeappliancestore.entities.Customer;
import ohio.rizz.homeappliancestore.exceptions.CustomerNotFoundException;
import ohio.rizz.homeappliancestore.repositories.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    final private CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Transactional
    public void save(Customer customer) {
        customerRepository.save(customer);
    }

    @Transactional
    public void updateCustomer(Long id, CustomerDto customerDTO, MultipartFile imageFile, boolean removeImage) throws IOException {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Клиент не найден"));

        // Проверка уникальности email
        if (!isEmailUnique(id, customerDTO.getEmail())) {
            throw new IllegalArgumentException("Email уже используется другим клиентом");
        }

        // Обновление полей
        existingCustomer.setFirstName(customerDTO.getFirstName());
        existingCustomer.setLastName(customerDTO.getLastName());
        existingCustomer.setEmail(customerDTO.getEmail());
        existingCustomer.setPhone(customerDTO.getPhone());
        existingCustomer.setAddress(customerDTO.getAddress());

        if (removeImage) {
            // Удаление текущего изображения
            if (customerDTO.getImagePath() != null) {
                Path imagePath = Paths.get(customerDTO.getImagePath().substring(1));
                Files.deleteIfExists(imagePath);
                customerDTO.setImagePath(null);
            }
        } else if (imageFile != null && !imageFile.isEmpty()) {
            // Загрузка нового изображения
            String uploadDir = "uploads/avatars/";
            String fileName = System.currentTimeMillis() + ".jpg";

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Удаление старого изображения, если есть
            if (customerDTO.getImagePath() != null) {
                Path oldImagePath = Paths.get(customerDTO.getImagePath().substring(1));
                Files.deleteIfExists(oldImagePath);
            }

            try (InputStream inputStream = imageFile.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + fileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }

            customerDTO.setImagePath("/" + uploadDir + fileName);
        }

        customerRepository.save(existingCustomer);
    }

    public boolean isEmailUnique(Long id, String email) {
        Customer customer = customerRepository.findByEmail(email);
        return customer == null || customer.getId().equals(id);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    public List<Customer> searchCustomers(String query) {
        return customerRepository.findByFirstNameContainingOrLastNameContainingOrEmailContaining(query, query, query);
    }

}
