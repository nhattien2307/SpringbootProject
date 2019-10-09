package com.nhattien.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhattien.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

}
