package org.africalib.gallery.backend.repository;

import java.util.List;

import org.africalib.gallery.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {

	List<Order> findByMemberIdOrderByIdDesc(int memberId);
}
