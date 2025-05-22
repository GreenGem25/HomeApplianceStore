package ohio.rizz.homeappliancestore.advices;

import jakarta.servlet.http.HttpServletRequest;
import ohio.rizz.homeappliancestore.utils.MenuItem;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class MenuAdvice {

    @ModelAttribute("menuItems")
    public List<MenuItem> getMenuItems(HttpServletRequest request) {
        String currentPath = request.getRequestURI();

        return List.of(
                new MenuItem("Товары", "/products", "bi-box-seam", currentPath.startsWith("/products")),
                new MenuItem("Категории", "/categories", "bi-tags", currentPath.startsWith("/categories")),
                new MenuItem("Заказы (WIP)", "/orders", "bi-cart", currentPath.startsWith("/orders")),
                new MenuItem("Клиенты", "/customers", "bi-people", currentPath.startsWith("/customers")),
                new MenuItem("Поставщики", "/suppliers", "bi-truck", currentPath.startsWith("/suppliers"))
        );
    }
}