package org.africalib.gallery.backend.repository;

import java.util.List;

import org.africalib.gallery.backend.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Integer> {

	List<Item> findByIdIn(List<Integer> ids);
}
