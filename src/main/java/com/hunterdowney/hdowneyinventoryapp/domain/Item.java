package com.hunterdowney.hdowneyinventoryapp.domain;

import java.util.UUID;

public class Item {
    private String id;
    private String name;
    private String manufacturer;
    private double price;
    private int inventory;
    private ItemType itemType;
    private Image image;

    public Item() {
        this.id = UUID.randomUUID().toString(); // Generate unique ID
    }

    public Item(String name, String manufacturer, double price, int inventory, ItemType itemType, Image image) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.manufacturer = manufacturer;
        this.price = price;
        this.inventory = inventory;
        this.itemType = itemType;
        this.image = image;
    }

    // getters + setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getInventory() { return inventory; }
    public void setInventory(int inventory) { this.inventory = inventory; }

    public ItemType getItemType() { return itemType; }
    public void setItemType(ItemType itemType) { this.itemType = itemType; }

    public Image getImage() { return image; }
    public void setImage(Image image) { this.image = image; }

    @Override
    public String toString() {
        return "Item{id='" + id + "', name='" + name + "', manufacturer='" + manufacturer + "', price=" + price +
                ", inventory=" + inventory + ", itemType=" + itemType + "}";
    }
}