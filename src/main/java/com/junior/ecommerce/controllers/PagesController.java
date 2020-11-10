package com.junior.ecommerce.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.junior.ecommerce.data.entity.Categories;
import com.junior.ecommerce.data.entity.Products;
import com.junior.ecommerce.data.repository.CategoriesRepository;
import com.junior.ecommerce.data.repository.ProductsRepository;

@Controller
public class PagesController {
	
	@Autowired
	private CategoriesRepository categoriesRepository;
	
	@Autowired
	private ProductsRepository productsRepository;
	
	@GetMapping("/")
	public String getHome(@RequestParam(value = "page", required = false) Integer p,Model model) {
		int perPage = 9;
		int numberOfPage = (p == null) ? 0 : p;
		Pageable pageAble = PageRequest.of(numberOfPage, perPage);
		long count = 0;
		Page<Products> productList = productsRepository.findAll(pageAble);
		model.addAttribute("productList", productList);
		count = productsRepository.count();
		double pageCount = Math.ceil((double) count / (double) perPage);

		model.addAttribute("pageCount", (int) pageCount);
		model.addAttribute("perPage", perPage);
		model.addAttribute("count", count);
		model.addAttribute("numberOfPage", numberOfPage);
		return "home";
	}
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/view/{slug}")
	public String getDatas(@PathVariable(name = "slug",required = false) String slug, @RequestParam(value = "page", required = false) Integer p,Model model) {
		int perPage = 9;
		int numberOfPage = (p == null) ? 0 : p;
		Pageable pageAble = PageRequest.of(numberOfPage, perPage);
		long count = 0;
		
		Categories category = categoriesRepository.findBySlug(slug);
		Page<Products> productList = productsRepository.findByCategoriesId(category.getId(),pageAble);
		model.addAttribute("productList", productList);
		count = productList.getSize();
		double pageCount = Math.ceil((double)count/(double)perPage);
		
		String slugValue = slug.toUpperCase();
		
		model.addAttribute("slug", slugValue);
		model.addAttribute("pageCount", (int) pageCount);
		model.addAttribute("perPage", perPage);
		model.addAttribute("count", count);
		model.addAttribute("numberOfPage", numberOfPage);

		return "homeCategory";
	}
	
}
