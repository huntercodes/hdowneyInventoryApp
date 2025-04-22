package com.hunterdowney.hdowneyinventoryapp.api.controller;

import com.hunterdowney.hdowneyinventoryapp.api.dto.ItemDto;
import com.hunterdowney.hdowneyinventoryapp.domain.Item;
import com.hunterdowney.hdowneyinventoryapp.domain.ItemType;
import com.hunterdowney.hdowneyinventoryapp.repository.ItemRepository;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(
        path = "/api/items",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ItemApiController {

    private final ItemRepository repo;

    public ItemApiController(ItemRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAll() {
        List<ItemDto> dtos = repo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getOne(@PathVariable String id) {
        Optional<Item> opt = repo.findById(id);
        return opt
                .map(item -> ResponseEntity.ok(toDto(item)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/type/{itemType}")
    public ResponseEntity<List<ItemDto>> filterByType(
            @PathVariable String itemType) {

        ItemType typeEnum;
        try {
            typeEnum = ItemType.valueOf(itemType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        List<ItemDto> dtos = repo.findByItemType(typeEnum).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(
            @RequestParam("q") String q) {

        List<ItemDto> dtos = repo
                .findByNameContainingIgnoreCaseOrManufacturerContainingIgnoreCase(q, q)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDto> create(@RequestBody ItemDto dto) {
        Item toSave = fromDto(dto);
        Item saved = repo.save(toSave);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toDto(saved));
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDto> replace(
            @PathVariable String id,
            @RequestBody ItemDto updates
    ) {
        return repo.findById(id)
                .map(existing -> {
                    existing.setName(updates.getName());
                    existing.setManufacturer(updates.getManufacturer());
                    existing.setPrice(updates.getPrice());
                    existing.setInventory(updates.getInventory());
                    existing.setItemType(updates.getItemType());
                    Item saved = repo.save(existing);
                    return ResponseEntity.ok(toDto(saved));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDto> patch(
            @PathVariable String id,
            @RequestBody Map<String, Object> changes
    ) {
        return repo.findById(id)
                .map(item -> {
                    if (changes.containsKey("name")) {
                        item.setName((String) changes.get("name"));
                    }
                    if (changes.containsKey("manufacturer")) {
                        item.setManufacturer((String) changes.get("manufacturer"));
                    }
                    if (changes.containsKey("price")) {
                        item.setPrice(((Number) changes.get("price")).doubleValue());
                    }
                    if (changes.containsKey("inventory")) {
                        item.setInventory((Integer) changes.get("inventory"));
                    }
                    if (changes.containsKey("itemType")) {
                        item.setItemType((String) changes.get("itemType"));
                    }
                    Item saved = repo.save(item);
                    return ResponseEntity.ok(toDto(saved));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        repo.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private ItemDto toDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getManufacturer(),
                item.getPrice(),
                item.getInventory(),
                item.getItemType() != null
                        ? item.getItemType().name()
                        : null
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