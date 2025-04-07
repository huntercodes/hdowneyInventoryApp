package com.hunterdowney.hdowneyinventoryapp.repository;

import com.hunterdowney.hdowneyinventoryapp.domain.Item;
import com.hunterdowney.hdowneyinventoryapp.domain.ItemType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemRepository extends CrudRepository<Item, String> {
    List<Item> findByNameContainingIgnoreCaseOrManufacturerContainingIgnoreCase(String name, String manufacturer);
    List<Item> findByItemType(ItemType itemType);
}
