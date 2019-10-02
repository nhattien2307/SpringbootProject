package com.nhattien.service;

import java.util.List;

import com.nhattien.entities.User;

public interface UserService {
	public void saveUser(User user);
	
	public boolean isUserAlreadyPresent(User user);
	
	public List<User> findAll();
}
