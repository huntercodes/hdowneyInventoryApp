package com.hunterdowney.hdowneyinventoryapp.api.controller;

import com.hunterdowney.hdowneyinventoryapp.api.dto.ItemDto;
import com.hunterdowney.hdowneyinventoryapp.domain.Item;
import com.hunterdowney.hdowneyinventoryapp.domain.ItemType;
import com.hunterdowney.hdowneyinventoryapp.repository.ItemRepository;
import com.hunterdowney.hdowneyinventoryapp.security.TokenService;
import com.hunterdowney.hdowneyinventoryapp.security.UserRole;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(path = "/api/items", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemApiController {

    private final ItemRepository repo;
    private final TokenService tokenService;

    public ItemApiController(ItemRepository repo, TokenService tokenService) {
        this.repo = repo;
        this.tokenService = tokenService;
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAll(
            @RequestHeader(value="Authorization", required=false) String auth
    ) {
        UserRole role = tokenService.roleFor(auth);
        List<ItemDto> dtos = repo.findAll().stream()
                .map(item -> toDto(item, role))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getOne(
            @PathVariable String id,
            @RequestHeader(value="Authorization", required=false) String auth
    ) {
        UserRole role = tokenService.roleFor(auth);
        return repo.findById(id)
                .map(item -> ResponseEntity.ok(toDto(item, role)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ItemDto>> filterByType(
            @PathVariable String type,
            @RequestHeader(value="Authorization", required=false) String auth
    ) {
        UserRole role = tokenService.roleFor(auth);
        ItemType enumType;
        try {
            enumType = ItemType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        List<ItemDto> dtos = repo.findByItemType(enumType).stream()
                .map(item -> toDto(item, role))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(
            @RequestParam("q") String q,
            @RequestHeader(value="Authorization", required=false) String auth
    ) {
        UserRole role = tokenService.roleFor(auth);
        List<ItemDto> dtos = repo
                .findByNameContainingIgnoreCaseOrManufacturerContainingIgnoreCase(q, q)
                .stream()
                .map(item -> toDto(item, role))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDto> create(
            @RequestBody ItemDto dto,
            @RequestHeader("Authorization") String auth
    ) {
        tokenService.requireRole(auth, UserRole.MNGR, UserRole.ADMIN);
        Item saved = repo.save(fromDto(dto));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toDto(saved, tokenService.roleFor(auth)));
    }

    @PutMapping(path="/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDto> replace(
            @PathVariable String id,
            @RequestBody ItemDto updates,
            @RequestHeader("Authorization") String auth
    ) {
        tokenService.requireRole(auth, UserRole.MNGR, UserRole.ADMIN);
        return repo.findById(id)
                .map(existing -> {
                    existing.setName(updates.getName());
                    existing.setManufacturer(updates.getManufacturer());
                    existing.setPrice(updates.getPrice());
                    existing.setInventory(updates.getInventory());
                    existing.setItemType(updates.getItemType());
                    Item saved = repo.save(existing);
                    return ResponseEntity.ok(toDto(saved, tokenService.roleFor(auth)));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PatchMapping(path="/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDto> patch(
            @PathVariable String id,
            @RequestBody Map<String,Object> changes,
            @RequestHeader("Authorization") String auth
    ) {
        if (changes.size()==1 && changes.containsKey("inventory")) {
            tokenService.requireRole(auth, UserRole.ASSOC, UserRole.MNGR, UserRole.ADMIN);
        } else {
            tokenService.requireRole(auth, UserRole.MNGR, UserRole.ADMIN);
        }

        return repo.findById(id)
                .map(item -> {
                    if (changes.containsKey("name")) {
                        item.setName((String)changes.get("name"));
                    }
                    if (changes.containsKey("manufacturer")) {
                        item.setManufacturer((String)changes.get("manufacturer"));
                    }
                    if (changes.containsKey("price")) {
                        item.setPrice(((Number)changes.get("price")).doubleValue());
                    }
                    if (changes.containsKey("inventory")) {
                        item.setInventory((Integer)changes.get("inventory"));
                    }
                    if (changes.containsKey("itemType")) {
                        item.setItemType((String)changes.get("itemType"));
                    }
                    Item saved = repo.save(item);
                    return ResponseEntity.ok(toDto(saved, tokenService.roleFor(auth)));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id,
            @RequestHeader("Authorization") String auth
    ) {
        tokenService.requireRole(auth, UserRole.MNGR, UserRole.ADMIN);
        if (!repo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        repo.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private ItemDto toDto(Item item, UserRole role) {
        int inv = (role == UserRole.ANONYMOUS) ? -1 : item.getInventory();
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getManufacturer(),
                item.getPrice(),
                inv,
                item.getItemType() != null ? item.getItemType().name() : null
        );
    }

    private Item fromDto(ItemDto dto) {
        Item item = new Item();
        item.setName(dto.getName());
        item.setManufacturer(dto.getManufacturer());
        item.setPrice(dto.getPrice());
        item.setInventory(dto.getInventory());
        item.setItemType(dto.getItemType());
        return item;
    }
}