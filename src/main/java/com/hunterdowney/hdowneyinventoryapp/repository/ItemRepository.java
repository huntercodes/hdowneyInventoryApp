package com.hunterdowney.hdowneyinventoryapp.repository;

import com.hunterdowney.hdowneyinventoryapp.domain.Item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends CrudRepository<Item, String> {

}
