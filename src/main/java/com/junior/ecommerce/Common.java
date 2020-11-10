package com.junior.ecommerce;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.junior.ecommerce.data.entity.Cart;
import com.junior.ecommerce.data.entity.Categories;
import com.junior.ecommerce.data.entity.Pages;
import com.junior.ecommerce.data.repository.CategoriesRepository;
import com.junior.ecommerce.data.repository.PagesRepository;

@ControllerAdvice
@SuppressWarnings("unchecked")
public class Common {
	
	@Autowired
	private PagesRepository pagesRepository;
	
	@Autowired
	private CategoriesRepository categoriesRepository;
	
	@ModelAttribute
	public void sharedData(Model model,HttpSession session,Principal principal) {
		if(principal != null) {
			model.addAttribute("principal",principal.getName());
		}
		List<Pages> pagesList = pagesRepository.findAll();
		List<Categories> categoriesList = categoriesRepository.findAll();
		boolean cartActive = false;
		if(session.getAttribute("cart") != null) {
			Map<Integer,Cart> cart = (Map<Integer, Cart>) session.getAttribute("cart");
			int size = 0;
			double total = 0.0;
			for(Cart value : cart.values()) {
				size+=value.getQuantity();
				total+=value.getQuantity() * Double.parseDouble(value.getPrice());
			}
			model.addAttribute("csize", size);
			model.addAttribute("ctotal", total);
			cartActive = true;
		}
		model.addAttribute("cpagesList", pagesList);
		model.addAttribute("ccategoriesList", categoriesList);
		model.addAttribute("cartActive", cartActive);
	}
}
