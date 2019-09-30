package com.nhattien.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhattien.entities.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
