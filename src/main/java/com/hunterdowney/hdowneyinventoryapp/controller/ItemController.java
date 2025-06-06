package com.hunterdowney.hdowneyinventoryapp.controller;

import com.hunterdowney.hdowneyinventoryapp.config.ItemProperties;
import com.hunterdowney.hdowneyinventoryapp.config.PageConfig;
import com.hunterdowney.hdowneyinventoryapp.messaging.InventoryProducer;
import com.hunterdowney.hdowneyinventoryapp.repository.ItemRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;

import com.hunterdowney.hdowneyinventoryapp.domain.Item;
import com.hunterdowney.hdowneyinventoryapp.domain.Image;
import com.hunterdowney.hdowneyinventoryapp.domain.ItemType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import java.io.IOException;
import java.util.Optional;

@Controller
public class ItemController {

    private final ItemRepository itemRepo;
    private final InventoryProducer producer;
    private final ItemProperties props;
    private PageConfig pageConfig;


    @Autowired
    public ItemController(ItemRepository itemRepo, PageConfig pageConfig, InventoryProducer producer, ItemProperties props) {
        this.itemRepo = itemRepo;
        this.pageConfig = pageConfig;
        this.producer = producer;
        this.props = props;
    }

    @PreAuthorize("hasAnyRole('MNGR', 'ADMIN')")
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("siteTitle", "Register Item");
        model.addAttribute("item", new Item());
        model.addAttribute("itemTypes", ItemType.values());
        return "register";
    }

    @PreAuthorize("hasAnyRole('MNGR', 'ADMIN')")
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
                            @RequestParam(defaultValue = "0") int page,
                            Model model) {

        Pageable pageable = PageRequest.of(page, pageConfig.getSize());
        Page<Item> itemsPage;

        if (search != null && !search.isEmpty()) {
            itemsPage = itemRepo.findByNameContainingIgnoreCaseOrManufacturerContainingIgnoreCase(search, search, pageable);
        } else if (filter != null) {
            itemsPage = itemRepo.findByItemType(filter, pageable);
        } else {
            itemsPage = itemRepo.findAll(pageable);
        }

        model.addAttribute("itemsPage", itemsPage);
        model.addAttribute("itemTypes", ItemType.values());
        model.addAttribute("search", search);
        model.addAttribute("filter", filter);
        model.addAttribute("siteTitle", "Inventory List");
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
    @PreAuthorize("hasAnyRole('ASSOC', 'MNGR', 'ADMIN')")
    public String showEditForm(@PathVariable String id, Model model) {
        Optional<Item> itemOptional = itemRepo.findById(id);
        if (itemOptional.isPresent()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isManagerOrAdmin = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_MNGR") || role.equals("ROLE_ADMIN"));

            model.addAttribute("isManagerOrAdmin", isManagerOrAdmin);
            model.addAttribute("item", itemOptional.get());
            model.addAttribute("itemTypes", ItemType.values());
            return "edit";
        } else {
            return "redirect:/items";
        }
    }

    @PostMapping("/items/edit/{id}")
    @PreAuthorize("hasAnyRole('ASSOC', 'MNGR', 'ADMIN')")
    public String updateItem(@PathVariable String id,
                             @ModelAttribute Item item,
                             BindingResult result,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        Optional<Item> existingOptional = itemRepo.findById(id);
        if (existingOptional.isEmpty()) return "redirect:/items";

        Item existing = existingOptional.get();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isManagerOrAdmin = auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .anyMatch(role -> role.equals("ROLE_MNGR") || role.equals("ROLE_ADMIN"));

        if (isManagerOrAdmin) {
            if (result.hasErrors()) return "edit";

            existing.setName(item.getName());
            existing.setManufacturer(item.getManufacturer());
            existing.setPrice(item.getPrice());

            if (item.getItemType() != null) {
                existing.setItemType(item.getItemType().name());
            }

            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    existing.setImage(new Image(imageFile.getOriginalFilename(), imageFile.getBytes()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        existing.setInventory(item.getInventory());

        Item updated = itemRepo.save(existing);

        if (updated.getInventory() <= props.getLowThreshold()) {
            producer.sendLowInventory(updated);
        }

        return "redirect:/items";
    }

    @PreAuthorize("hasAnyRole('MNGR', 'ADMIN')")
    @GetMapping("/items/delete/{id}")
    public String deleteItem(@PathVariable String id) {
        itemRepo.deleteById(id);
        return "redirect:/items";
    }

}