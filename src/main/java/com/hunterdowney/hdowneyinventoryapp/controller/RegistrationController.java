package com.hunterdowney.hdowneyinventoryapp.controller;

import com.hunterdowney.hdowneyinventoryapp.domain.User;
import com.hunterdowney.hdowneyinventoryapp.dto.UserRegistrationDto;
import com.hunterdowney.hdowneyinventoryapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistrationController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationController(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register-user")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "registerUser";
    }

    @PostMapping("/register-user")
    public String processRegistration(@ModelAttribute("user") UserRegistrationDto dto) {
        User newUser = new User(dto.getUsername(), passwordEncoder.encode(dto.getPassword()), dto.getFullName(), "USER");
        userRepo.save(newUser);
        return "redirect:/login?registered";
    }
}