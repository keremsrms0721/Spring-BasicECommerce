package com.junior.ecommerce.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.junior.ecommerce.data.entity.Categories;

public interface CategoriesRepository extends JpaRepository<Categories,Integer>{
	Categories findBySlug(String slug);
}
