package com.hunterdowney.hdowneyinventoryapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.UUID;

@Entity
public class Item {
    @Id
    private String id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Manufacturer is required")
    private String manufacturer;

    @Positive(message = "Price must be greater than zero")
    private double price;

    @Min(value = 0, message = "Inventory cannot be negative")
    private int inventory;

    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private Image image;

    public Item() {
        this.id = java.util.UUID.randomUUID().toString();
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
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getInventory() { return inventory; }
    public void setInventory(int inventory) { this.inventory = inventory; }

    public ItemType getItemType() { return itemType; }

    public void setItemType(String itemType) {
        try {
            this.itemType = ItemType.valueOf(itemType.replace(" & ", "_").toUpperCase());
        } catch (IllegalArgumentException e) {
            this.itemType = null;
        }
    }

    public Image getImage() { return image; }
    public void setImage(Image image) { this.image = image; }

    @Override
    public String toString() {
        return "Item{id='" + id + "', name='" + name + "', manufacturer='" + manufacturer + "', price=" + price +
                ", inventory=" + inventory + ", itemType=" + itemType + "}";
    }
}