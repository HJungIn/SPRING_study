package com.hji.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hji.spring.model.User;
import com.hji.spring.repository.UserRepository;

// 사용자 명세 서비스 생성하기
@Service
public class UserRepositoryUserDetailsService
		implements UserDetailsService {
	private UserRepository userRepo;

	@Autowired
	public UserRepositoryUserDetailsService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	/*
	 * user를 찾는 메소드로 이 메소드에서는  절대 null을 반환하지 않는다는 간단한 규칙이 있다.
	 * null일 경우 UsernameNotFoundException 발생
	 */
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
				System.out.println(username);
		User user = userRepo.findByUsername(username);
		if (user != null) {
			return user;
		}
		throw new UsernameNotFoundException(
				"User '" + username + "' not found");
	}
}