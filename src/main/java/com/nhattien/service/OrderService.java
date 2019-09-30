package com.nhattien.service;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhattien.entities.Order;
import com.nhattien.entities.OrderDetail;
import com.nhattien.entities.Product;
import com.nhattien.model.CartInfo;
import com.nhattien.model.CartLineInfo;
import com.nhattien.model.OrderInfo;
import com.nhattien.repository.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository repo;
	
	@Autowired
    private ProductService productService;

	public List<Order> findAll() {
		return repo.findAll();
	}
	
	public Order findById(Long id) {
		return repo.findById(id).get();
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void saveOrder(CartInfo cartInfo) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Order order = new Order();
		order.setDateOrder(new Date());
		order.setAmount(cartInfo.getAmountTotal());
		order.setCustomerName("");
		order.setCustomerAddress("");
		order.setId(88L);
		
		session.persist(order);
		
		List<CartLineInfo> lines = cartInfo.getCartLines();
		 for (CartLineInfo line : lines) {
			 OrderDetail detail = new OrderDetail();
			 detail.setOrder(order);
			 detail.setAmount(line.getAmount());
			 detail.setQuantity(line.getQuantity());
			 detail.setId(9L);
			 
			 int id = line.getProductInfo().getId();
			 Product pro = this.productService.get(id);
			 detail.setProduct(pro);
			 
			 session.persist(detail);
		 }
		 session.flush();
	}
	
	public OrderInfo getOrderInfo(Long orderId) {
        Order order = this.findById(orderId);
        if (order == null) {
            return null;
        }
        return new OrderInfo(order.getId(), order.getCustomerName(), order.getCustomerAddress(), order.getDateOrder(), order.getAmount());
    }
	

	public void delete(long id) {
		repo.deleteById(id);
	}

}
