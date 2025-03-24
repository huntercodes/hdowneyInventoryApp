package com.hunterdowney.hdowneyinventoryapp.database;

import com.hunterdowney.hdowneyinventoryapp.domain.Item;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope("singleton")
public class ItemDB {
    private final Map<String, Item> items = new HashMap<>();

    public Map<String, Item> getAllItems() {
        return items;
    }

    public void addItem(Item item) {
        items.put(item.getId(), item);
    }

    public Item getItemById(String id) {
        return items.get(id);
    }
}