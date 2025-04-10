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
import org.springframework.web.bind.annotation.*;
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
    public String listItems(@RequestParam(required = false) String search,
                            @RequestParam(required = false) ItemType filter,
                            Model model) {
        Iterable<Item> items;
        if (search != null && !search.isEmpty()) {
            items = itemRepo.findByNameContainingIgnoreCaseOrManufacturerContainingIgnoreCase(search, search);
        } else if (filter != null) {
            items = itemRepo.findByItemType(filter);
        } else {
            items = itemRepo.findAll();
        }

        model.addAttribute("siteTitle", "Inventory List");
        model.addAttribute("itemTypes", ItemType.values());
        model.addAttribute("items", items);
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

    @GetMapping("/items/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        Optional<Item> itemOptional = itemRepo.findById(id);
        if (itemOptional.isPresent()) {
            model.addAttribute("item", itemOptional.get());
            model.addAttribute("itemTypes", ItemType.values());
            return "edit";
        } else {
            return "redirect:/items";
        }
    }

    @PostMapping("/items/edit/{id}")
    public String updateItem(@PathVariable String id, @Valid @ModelAttribute Item item,
                             BindingResult result, @RequestParam("imageFile") MultipartFile imageFile) {
        if (result.hasErrors()) return "edit";

        item.setId(id);

        if (!imageFile.isEmpty()) {
            try {
                item.setImage(new Image(imageFile.getOriginalFilename(), imageFile.getBytes()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            itemRepo.findById(id).ifPresent(existing -> item.setImage(existing.getImage()));
        }

        itemRepo.save(item);
        return "redirect:/items";
    }

    @GetMapping("/items/delete/{id}")
    public String deleteItem(@PathVariable String id) {
        itemRepo.deleteById(id);
        return "redirect:/items";
    }

}