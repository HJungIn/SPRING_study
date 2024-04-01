package com.hji.spring.repository;

import org.springframework.data.repository.CrudRepository;

import com.hji.spring.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
	User findByUsername(String username);
}
