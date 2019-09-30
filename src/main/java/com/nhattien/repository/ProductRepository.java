package com.nhattien.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhattien.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	List<Product> findByNameLike(String name);

}
