package com.junior.ecommerce.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.junior.ecommerce.data.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin,Integer>{
	
	Admin findByUsername(String username);
}
