package com.hunterdowney.hdowneyinventoryapp.controller;

import com.hunterdowney.hdowneyinventoryapp.repository.ItemRepository;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import com.hunterdowney.hdowneyinventoryapp.database.ItemDB;
import com.hunterdowney.hdowneyinventoryapp.domain.Item;
import com.hunterdowney.hdowneyinventoryapp.domain.Image;
import com.hunterdowney.hdowneyinventoryapp.domain.ItemType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
public class ItemController {

    private final ItemRepository itemRepo;

    @Autowired
    public ItemController(ItemRepository itemRepo) {
        this.itemRepo = itemRepo;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("siteTitle", "Register Item");
        model.addAttribute("item", new Item());
        model.addAttribute("itemTypes", ItemType.values());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute Item item, BindingResult result, MultipartFile imageFile) {
        if (result.hasErrors()) {
            return "register";
        }

        if (!imageFile.isEmpty()) {
            try {
                item.setImage(new Image(imageFile.getOriginalFilename(), imageFile.getBytes()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        itemRepo.save(item);
        return "redirect:/items";
    }

    @GetMapping("/items")
    public String listItems(Model model) {
        model.addAttribute("siteTitle", "Inventory List");
        model.addAttribute("items", itemRepo.findAll());
        return "list";
    }

    @GetMapping("/items/{id}")
    public String viewItem(@PathVariable String id, Model model) {
        Optional<Item> item = itemRepo.findById(id);
        if (item.isEmpty()) {
            return "redirect:/items";
        }
        model.addAttribute("siteTitle", "Item Details");
        model.addAttribute("item", item.get());
        return "view";
    }

}