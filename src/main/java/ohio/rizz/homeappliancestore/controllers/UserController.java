package ohio.rizz.homeappliancestore.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ohio.rizz.homeappliancestore.dto.UserCreateDto;
import ohio.rizz.homeappliancestore.dto.UserEditDto;
import ohio.rizz.homeappliancestore.entities.User;
import ohio.rizz.homeappliancestore.services.UserService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
@Secured("ROLE_ADMIN")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("user", new UserCreateDto());
        return "add-user";
    }

    @PostMapping("/add")
    public String addUser(@Valid @ModelAttribute("user") UserCreateDto dto,
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "add-user";
        }
        try {
            userService.createUser(dto.getUsername(), dto.getPassword(), dto.getRole(), dto.getFullName());
            redirectAttributes.addFlashAttribute("successMessage", "Пользователь создан");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable UUID id, Model model) {
        User user = userService.getUserById(id);
        UserEditDto dto = new UserEditDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setRole(user.getRole());
        model.addAttribute("user", dto);
        model.addAttribute("username", user.getUsername());
        return "edit-user";
    }

    @PostMapping("/{id}/edit")
    public String editUser(@PathVariable UUID id,
                           @Valid @ModelAttribute("user") UserEditDto dto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "edit-user";
        }
        try {
            userService.updateUser(id, dto.getFullName(), dto.getRole(), dto.getPassword());
            redirectAttributes.addFlashAttribute("successMessage", "Данные пользователя обновлены");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/users/" + id + "/edit";
        }
        return "redirect:/users";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "Пользователь удалён");
        return "redirect:/users";
    }

    @PostMapping("/{id}/block")
    public String blockUser(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        userService.blockUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "Пользователь заблокирован");
        return "redirect:/users";
    }

    @PostMapping("/{id}/unblock")
    public String unblockUser(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        userService.unblockUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "Пользователь разблокирован");
        return "redirect:/users";
    }
}