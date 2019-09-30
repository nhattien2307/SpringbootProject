package com.nhattien.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Product implements Serializable{
	private static final long serialVersionUID = -1000119078147252957L;
	
	@Id
    @Column(name = "id", length = 20, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "name", length = 255, nullable = false)
	private String name;
	
	@Column(name = "quantity", nullable = false)
	private Integer quantity;
	
	@Column(name = "price", nullable = false)
	private double price;
	
	//@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	//private List<OrderDetail> orderDetails;
	
	public Product() {
	}

	public Product(int id, String name, Integer quantity, double price) {
		this.id = id;
		this.name = name;
		this.quantity = quantity;
		this.price = price;
	}

	//public List<OrderDetail> getOrderDetails() {
	//	return orderDetails;
	//}

	//public void setOrderDetails(List<OrderDetail> aSet) {
	//	this.orderDetails.clear();
	//	this.orderDetails.addAll(aSet);
	//}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

}
