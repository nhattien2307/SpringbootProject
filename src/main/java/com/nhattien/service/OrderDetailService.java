package com.nhattien.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nhattien.entities.OrderDetail;
import com.nhattien.repository.OrderDetailRepository;

@Service
public class OrderDetailService {

	@Autowired
	private OrderDetailRepository repo;

	public OrderDetail create(OrderDetail orderDetail) {
		return repo.save(orderDetail);
	}

	public List<OrderDetail> findAll() {
		return repo.findAll();
	}

	public List<OrderDetail> findByOrder(int id) {
		return repo.findByOrder(id);
	}
}
