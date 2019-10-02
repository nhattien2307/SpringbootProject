package com.nhattien.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.nhattien.entities.Product;
import com.nhattien.model.ProductInfo;
import com.nhattien.repository.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repo;

	public Page<Product> findAll(PageRequest page) {
		return repo.findAll(page);
	}

	public void save(Product product) {
		repo.save(product);
	}

	public Product get(int id) {
		return repo.findById(id).get();
	}

	public ProductInfo findProductInfo(int id) {
		Product product = this.get(id);
		if (product == null) {
			return null;
		}
		return new ProductInfo(product.getId(), product.getName(), product.getQuantity(), product.getPrice());
	}

	/*
	 * public void delete(int id) { repo.deleteById(id); }
	 */
	
	public String delete(int id) {
		JSONObject jsonObject = new JSONObject();
		try {
			repo.deleteById(id);
			jsonObject.put("message", "Deleted Successfully.");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}

	public List<Product> search(String name) {
		return repo.findByNameLike("%" + name + "%");
	}
	
	public List<Map<String, Object>> report() {
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		for(Product product : repo.findAll()) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("id", product.getId());
			parameters.put("name", product.getName());
			parameters.put("quantity", product.getQuantity());
			parameters.put("price", product.getPrice());
			result.add(parameters);
		}
		return result;
	}
}
