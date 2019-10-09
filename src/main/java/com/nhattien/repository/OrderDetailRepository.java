package com.nhattien.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nhattien.entities.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

	@Query("SELECT e FROM OrderDetail e WHERE e.order.id = :id")
	List<OrderDetail> findByOrder(int id);
}
