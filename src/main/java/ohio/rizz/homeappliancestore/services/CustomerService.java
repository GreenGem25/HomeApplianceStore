package ohio.rizz.homeappliancestore.services;

import jakarta.transaction.Transactional;
import ohio.rizz.homeappliancestore.dto.CustomerCreateDto;
import ohio.rizz.homeappliancestore.dto.CustomerDto;
import ohio.rizz.homeappliancestore.entities.Customer;
import ohio.rizz.homeappliancestore.exceptions.CustomerNotFoundException;
import ohio.rizz.homeappliancestore.mappers.CustomerMapper;
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
import java.util.UUID;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository,
                           CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public List<CustomerDto> getAllCustomers() {
        return customerMapper.toDto(customerRepository.findAll());
    }

    public CustomerDto getCustomerById(UUID id) {
        Customer customer = getCustomerEntityOrThrow(id);
        return customerMapper.toDto(customer);
    }

    public Customer getCustomerEntityOrThrow(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Клиент не найден"));
    }

    public Optional<Customer> findCustomerById(UUID id) {
        return customerRepository.findById(id);
    }

    @Transactional
    public CustomerDto createCustomer(CustomerCreateDto createDto) {
        Customer customer = customerMapper.toEntity(createDto);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toDto(savedCustomer);
    }

    @Transactional
    public CustomerDto updateCustomer(UUID id, CustomerCreateDto createDto,
                                      MultipartFile imageFile, boolean removeImage) throws IOException {
        Customer existingCustomer = getCustomerEntityOrThrow(id);

        // Проверка уникальности email
        if (!isEmailUnique(id, createDto.getEmail())) {
            throw new IllegalArgumentException("Email уже используется другим клиентом");
        }

        // Обновление полей
        customerMapper.updateEntity(existingCustomer, createDto);

        // Обработка изображения
        if (removeImage) {
            deleteCustomerImage(existingCustomer);
        } else if (imageFile != null && !imageFile.isEmpty()) {
            saveCustomerImage(existingCustomer, imageFile);
        }

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return customerMapper.toDto(updatedCustomer);
    }

    private void deleteCustomerImage(Customer customer) throws IOException {
        if (customer.getImagePath() != null) {
            Path imagePath = Paths.get(customer.getImagePath().substring(1));
            Files.deleteIfExists(imagePath);
            customer.setImagePath(null);
        }
    }

    private void saveCustomerImage(Customer customer, MultipartFile imageFile) throws IOException {
        String uploadDir = "uploads/avatars/";
        String fileName = System.currentTimeMillis() + "_" + customer.getId() + ".jpg";

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Удаление старого изображения, если есть
        if (customer.getImagePath() != null) {
            Path oldImagePath = Paths.get(customer.getImagePath().substring(1));
            Files.deleteIfExists(oldImagePath);
        }

        // Сохранение нового изображения
        try (InputStream inputStream = imageFile.getInputStream()) {
            Files.copy(inputStream, Paths.get(uploadDir + fileName),
                    StandardCopyOption.REPLACE_EXISTING);
        }

        customer.setImagePath("/" + uploadDir + fileName);
    }

    public boolean isEmailUnique(UUID id, String email) {
        Customer customer = customerRepository.findByEmail(email);
        return customer == null || customer.getId().equals(id);
    }

    @Transactional
    public void deleteCustomer(UUID id) {
        Customer customer = getCustomerEntityOrThrow(id);

        // Удаляем изображение, если есть
        try {
            if (customer.getImagePath() != null) {
                Files.deleteIfExists(Paths.get(customer.getImagePath().substring(1)));
            }
        } catch (IOException e) {
            // Логируем ошибку, но не прерываем удаление
            System.err.println("Ошибка при удалении изображения: " + e.getMessage());
        }

        customerRepository.delete(customer);
    }

    public List<CustomerDto> searchCustomers(String query) {
        return customerMapper.toDto(
                customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        query, query, query)
        );
    }

    public void save(Customer customer) {
        customerRepository.save(customer);
    }
}