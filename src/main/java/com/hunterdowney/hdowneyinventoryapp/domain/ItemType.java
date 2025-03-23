package com.hunterdowney.hdowneyinventoryapp.domain;

public enum ItemType {
    FOOD_DRINK("Food & Drink"),
    APPAREL("Apparel"),
    ACCESSORY("Accessory"),
    BOOK("Book"),
    SCHOOL_MATERIAL("School Material");

    private final String displayName;

    ItemType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}