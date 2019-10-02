package com.nhattien.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nhattien.entities.User;

@Repository
@Transactional(rollbackFor = Exception.class)
public class UserRepositoty {

	@Autowired
	private EntityManager entityManager;
	
	public User loadUserByUsername(final String username) {
		List<User> users = new ArrayList<User>();
		users = entityManager.createQuery("from User where username=?0", User.class).setParameter(0, username).getResultList();
		//users = session.createQuery("from User where username=?0", User.class).setParameter(0, username).list();
		if (users.size() > 0) {
			return users.get(0);
		} else {
			return null;
		}
	}

}
