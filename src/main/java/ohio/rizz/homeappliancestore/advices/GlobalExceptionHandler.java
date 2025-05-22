package ohio.rizz.homeappliancestore.advices;

import ohio.rizz.homeappliancestore.exceptions.ProductNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    public String handleNotFound(RuntimeException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }
}