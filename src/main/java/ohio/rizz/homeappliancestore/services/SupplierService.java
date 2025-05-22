package ohio.rizz.homeappliancestore.services;

import ohio.rizz.homeappliancestore.dto.SupplierDto;
import ohio.rizz.homeappliancestore.entities.Customer;
import ohio.rizz.homeappliancestore.entities.Product;
import ohio.rizz.homeappliancestore.entities.Supplier;
import ohio.rizz.homeappliancestore.exceptions.SupplierNotFoundException;
import ohio.rizz.homeappliancestore.repositories.ProductRepository;
import ohio.rizz.homeappliancestore.repositories.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierService {
    final private SupplierRepository supplierRepository;
    final private ProductRepository productRepository;

    public SupplierService(SupplierRepository supplierRepository, ProductRepository productRepository) {
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Optional<Supplier> getSupplierById(Long id) {
        return supplierRepository.findById(id);
    }

    @Transactional
    public void updateSupplier(Long id, SupplierDto supplierDto) {
        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException("Поставщик не найден"));

        // Проверка уникальности email
        if (!isEmailUnique(id, supplierDto.getEmail())) {
            throw new IllegalArgumentException("Email уже используется другим поставщиком");
        }

        existingSupplier.setId(id);
        existingSupplier.setName(supplierDto.getName());
        existingSupplier.setContactName(supplierDto.getContactName());
        existingSupplier.setAddress(supplierDto.getAddress());
        existingSupplier.setPhone(supplierDto.getPhone());
        existingSupplier.setEmail(supplierDto.getEmail());

        supplierRepository.save(existingSupplier);
    }

    public void save(Supplier supplier) {
        supplierRepository.save(supplier);
    }

    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException("Поставщик не найден"));
        productRepository.findBySupplier_Id(supplier.getId()).forEach(product -> {
            product.setSupplier(null);
            productRepository.save(product);
        });
        supplierRepository.delete(supplier);
    }

    public boolean isEmailUnique(Long id, String email) {
        Supplier supplier = supplierRepository.findByEmail(email);
        return supplier == null || supplier.getId().equals(id);
    }
}
