package com.hunterdowney.hdowneyinventoryapp.repository;

import com.hunterdowney.hdowneyinventoryapp.domain.Item;
import com.hunterdowney.hdowneyinventoryapp.domain.ItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {
    Page<Item> findByNameContainingIgnoreCaseOrManufacturerContainingIgnoreCase(String name, String manufacturer, Pageable pageable);
    Page<Item> findByItemType(ItemType itemType, Pageable pageable);

    List<Item> findByNameContainingIgnoreCaseOrManufacturerContainingIgnoreCase(
            String name, String manufacturer
    );
    List<Item> findByItemType(ItemType itemType);
}