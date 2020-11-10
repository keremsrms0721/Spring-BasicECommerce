package com.junior.ecommerce.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.junior.ecommerce.data.entity.Pages;

public interface PagesRepository extends JpaRepository<Pages,Integer>{

	Pages findBySlug(String slug);

}
