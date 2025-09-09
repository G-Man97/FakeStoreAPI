package com.gman97.fakestoreapi.http.controller;

import com.gman97.fakestoreapi.dto.UserCreateDto;
import com.gman97.fakestoreapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.util.List;

@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @GetMapping
    public String registrationPage(Model model, @ModelAttribute("user") UserCreateDto user) {
        model.addAttribute("user", user);
        return "registration";
    }

    @PostMapping
    public String register(@Valid UserCreateDto userCreateDto,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            redirectAttributes.addFlashAttribute("user", userCreateDto);
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/registration";
        }

        try {
            userService.create(userCreateDto);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("user", userCreateDto);
            redirectAttributes.addFlashAttribute("errors", List.of("Пользователь с таким именем уже существует!"));
            return "redirect:/registration";
        }
        return "redirect:/api/v1/products";
    }
}
