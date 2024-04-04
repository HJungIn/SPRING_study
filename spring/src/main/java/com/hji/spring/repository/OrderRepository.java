package com.hji.spring.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.hji.spring.model.Order;
import com.hji.spring.model.User;

public interface OrderRepository extends CrudRepository<Order, String>{ // CrudRepository 인터페이스에는 DB의 CRUD 연산을 위한 많은 메서드가 선언되어 있다.
	List<Order> findByUserOrderByPlacedAtDesc(User user, Pageable pageable);
}
