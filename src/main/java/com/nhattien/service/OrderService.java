package com.nhattien.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nhattien.entities.Order;
import com.nhattien.repository.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository repo;

	public List<Order> findAll() {
		return repo.findAll();
	}

	public Order findById(int id) {
		return repo.findById(id).get();
	}

	public void delete(int id) {
		repo.deleteById(id);
	}

	public Order create(Order order) {
		return repo.save(order);
	}

}
