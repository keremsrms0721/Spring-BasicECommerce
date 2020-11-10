package com.junior.ecommerce.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.junior.ecommerce.data.entity.Products;

public interface ProductsRepository extends JpaRepository<Products, Integer>{

	Products findBySlug(String slug);
	
	@Query("Select p from Products p where p.categoriesId = :categoriesId")
	Page<Products> findByCategoriesId(@Param(value = "categoriesId") int categoriesId,Pageable pageable);
}
