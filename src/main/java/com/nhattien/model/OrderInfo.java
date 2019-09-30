package com.nhattien.model;

import java.util.Date;
import java.util.List;

public class OrderInfo {
	private Long id;
	private String customerName;
	private String customerAddress;
	private Date dateOrder;
	private double amount;
	private List<OrderDetailInfo> details;

	public OrderInfo() {
	}

	public OrderInfo(Long id, String customerName, String customerAddress, Date dateOrder, double amount) {
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

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public List<OrderDetailInfo> getDetails() {
		return details;
	}

	public void setDetails(List<OrderDetailInfo> details) {
		this.details = details;
	}

}
