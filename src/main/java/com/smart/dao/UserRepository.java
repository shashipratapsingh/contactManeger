package com.smart.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smart.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
	
	public User findByEmail(String email);

	
	
//	  public void add(User user);
//    public void modify(User user);
//    public void delete(User user);
}
