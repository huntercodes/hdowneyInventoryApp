package com.hunterdowney.hdowneyinventoryapp.controller;

import com.hunterdowney.hdowneyinventoryapp.domain.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null && userDetails instanceof User user) {
            model.addAttribute("name", user.getFullName());
        }
        return "index";
    }
}