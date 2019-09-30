package com.nhattien.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "customer_name", length = 255, nullable = false)
	private String customerName;

	@Column(name = "customer_address", length = 255, nullable = false)
	private String customerAddress;

	@Column(name = "date_order", nullable = false)
	private Date dateOrder;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderDetail> orderDetails;

	@Column(name = "amount")
	private double amount;

	public Order() {
	}
	
	public Order(Long id, String customerName, String customerAddress, Date dateOrder, List<OrderDetail> orderDetails,
			double amount) {
		super();
		this.id = id;
		this.customerName = customerName;
		this.customerAddress = customerAddress;
		this.dateOrder = dateOrder;
		this.orderDetails = orderDetails;
		this.amount = amount;
	}


	public Order(Long id, String customerName, String customerAddress, Date dateOrder, double amount) {
		super();
		this.id = id;
		this.customerName = customerName;
		this.customerAddress = customerAddress;
		this.dateOrder = dateOrder;
		this.amount = amount;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public Date getDateOrder() {
		return dateOrder;
	}

	public void setDateOrder(Date dateOrder) {
		this.dateOrder = dateOrder;
	}

	public List<OrderDetail> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(List<OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

}
