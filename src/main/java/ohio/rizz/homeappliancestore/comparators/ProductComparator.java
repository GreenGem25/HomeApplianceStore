package ohio.rizz.homeappliancestore.comparators;

import ohio.rizz.homeappliancestore.entities.Product;

import java.math.BigDecimal;
import java.util.Comparator;

public class ProductComparator {
    public static Comparator<Product> byName() {
        return Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
    }

    public static Comparator<Product> byPrice() {
        return Comparator.comparing(Product::getPrice, BigDecimal::compareTo);
    }
}