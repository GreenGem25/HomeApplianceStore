package ohio.rizz.homeappliancestore.advices;

import jakarta.servlet.http.HttpServletRequest;
import ohio.rizz.homeappliancestore.utils.MenuItem;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class MenuAdvice {

    @ModelAttribute("menuItems")
    public List<MenuItem> getMenuItems(HttpServletRequest request) {
        String currentPath = request.getRequestURI();

        List<MenuItem> items = new ArrayList<>(List.of(
                new MenuItem("Товары", "/products", "bi-box-seam", currentPath.startsWith("/products")),
                new MenuItem("Категории", "/categories", "bi-tags", currentPath.startsWith("/categories")),
                new MenuItem("Заказы", "/orders", "bi-cart", currentPath.startsWith("/orders")),
                new MenuItem("Клиенты", "/customers", "bi-people", currentPath.startsWith("/customers")),
                new MenuItem("Поставщики", "/suppliers", "bi-truck", currentPath.startsWith("/suppliers")),
                new MenuItem("Поставки", "/supplies", "bi-truck", currentPath.startsWith("/supplies"))
        ));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            items.addAll(List.of(
                    new MenuItem("Аналитика", "/analytics", "bi-graph-up", currentPath.startsWith("/analytics")),
                    new MenuItem("Пользователи", "/users", "bi-people", currentPath.startsWith("/users")),
                    new MenuItem("Аудит", "/audit", "bi-journal-text", currentPath.startsWith("/audit")),
                    new MenuItem("Настройки", "/settings", "bi-gear", currentPath.startsWith("/settings"))
            ));
        }

        return items;
    }
}