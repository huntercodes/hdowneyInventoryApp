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

    // get all items
    public Map<String, Item> getAllItems() {
        return items;
    }

    // add item
    public void addItem(Item item) {
        items.put(item.getId(), item);
    }

    // get item by ID
    public Item getItemById(String id) {
        return items.get(id);
    }

    // check if item exists
    public boolean itemExists(String id) {
        return items.containsKey(id);
    }

    // delete item
    public void deleteItem(String id) {
        items.remove(id);
    }
}
