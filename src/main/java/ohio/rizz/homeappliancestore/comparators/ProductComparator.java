package ohio.rizz.homeappliancestore.comparators;

import ohio.rizz.homeappliancestore.dto.ProductDto;
import ohio.rizz.homeappliancestore.entities.Product;

import java.math.BigDecimal;
import java.util.Comparator;

public class ProductComparator {
    public static Comparator<ProductDto> byName() {
        return Comparator.comparing(ProductDto::getName, String.CASE_INSENSITIVE_ORDER);
    }

    public static Comparator<ProductDto> byPrice() {
        return Comparator.comparing(ProductDto::getPrice, BigDecimal::compareTo);
    }
}