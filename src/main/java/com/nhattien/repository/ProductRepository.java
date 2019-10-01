package com.nhattien.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nhattien.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	List<Product> findByNameLike(String name);
	
	@Query("SELECT e FROM Product e ORDER BY e.id DESC")
	Page<Product> findAll(PageRequest page);
}
