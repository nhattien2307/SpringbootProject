package com.nhattien.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.nhattien.entities.Role;
import com.nhattien.entities.User;
import com.nhattien.entities.UsersRoles;
import com.nhattien.repository.RoleRepository;
import com.nhattien.repository.UserRepository;

@Service
public class UserServiceImp implements UserService {
	
	@Autowired
	BCryptPasswordEncoder encoder;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	UserRepository userRepository;

	@Override
	public void saveUser(User user) {
		user.setPassword(encoder.encode(user.getPassword()));
		user.setEnabled(true);
		Role role = roleRepository.findByName("ROLE_USER");
		UsersRoles userRole = new UsersRoles();
		userRole.setRole(role);
		userRole.setUsers(user);
		
		user.getUsersRoleses().add(userRole);
		user.setUsersRoleses(new HashSet<UsersRoles>(Arrays.asList(userRole)));
		userRepository.save(user);
	}

	@Override
	public boolean isUserAlreadyPresent(User user) {
		 boolean isUserAlreadyExists = false;
		 User existingUser = userRepository.findByUsername(user.getUsername());
		 if(existingUser != null) {
			 isUserAlreadyExists = true;
		 }
		return isUserAlreadyExists;
	}

	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

}
