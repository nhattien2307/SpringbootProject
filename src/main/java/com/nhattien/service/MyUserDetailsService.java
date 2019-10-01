package com.nhattien.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nhattien.repository.UserRepositoty;

@Service
public class MyUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepositoty userRepo;

	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		com.nhattien.entities.User user = userRepo.loadUserByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("Username Not Found");
		}
		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;
		return new User(username, user.getPassword(), enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, user.getAuthorities());
	}
}
