package com.junior.ecommerce.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.junior.ecommerce.data.entity.Categories;
import com.junior.ecommerce.data.entity.Pages;
import com.junior.ecommerce.data.repository.CategoriesRepository;
import com.junior.ecommerce.data.repository.PagesRepository;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoriesController {

	@Autowired
	private CategoriesRepository categoriesRepository;

	@Autowired
	private PagesRepository pagesRepository;

	@GetMapping
	public String getCategories(Model model, @RequestParam(value = "page", required = false) Integer p) {

		int perPage = 4;
		int numberOfPage = (p == null) ? 0 : p;
		Pageable pageAble = PageRequest.of(numberOfPage, perPage);
		long count = 0;

		Map<Integer, String> mapList = new HashMap<>();
		List<Pages> pagesList = pagesRepository.findAll();
		for (Pages page : pagesList) {
			mapList.put(page.getId(), page.getName());
		}
		Page<Categories> categoryList = categoriesRepository.findAll(pageAble);
		model.addAttribute("mapList", mapList);
		model.addAttribute("categoryList", categoryList);

		count = categoriesRepository.count();
		double pageCount = Math.ceil((double) count / (double) perPage);

		model.addAttribute("pageCount", (int) pageCount);
		model.addAttribute("perPage", perPage);
		model.addAttribute("count", count);
		model.addAttribute("numberOfPage", numberOfPage);

		return "admin/categories/index";
	}

	@GetMapping("/add")
	public String addCategory(Model model) {
		List<Pages> pagesList = pagesRepository.findAll();
		model.addAttribute("categories", new Categories());
		model.addAttribute("pagesList", pagesList);
		return "admin/categories/add";
	}

	@PostMapping("/add")
	public String addCategory(@Valid Categories categories, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, Model model) {
		List<Pages> pagesList = pagesRepository.findAll();
		if (bindingResult.hasErrors()) {
			model.addAttribute("pagesList", pagesList);
			return "admin/categories/add";
		}
		String slug = categories.getName().toLowerCase().replace(" ", "-");
		Categories value = categoriesRepository.findBySlug(slug);
		if (value != null) {
			redirectAttributes.addFlashAttribute("message", "Category Exists!");
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
			return "redirect:/admin/categories/add";
		}
		categories.setSlug(slug);
		categoriesRepository.save(categories);
		redirectAttributes.addFlashAttribute("message", "Category Added!");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/admin/categories";
	}

	@GetMapping("/edit/{id}")
	public String editCategory(@PathVariable("id") int id, Model model) {
		Optional<Categories> optionalCategory = categoriesRepository.findById(id);
		if (!optionalCategory.isPresent()) {
			return "redirect:/admin/categories";
		}
		List<Pages> pagesList = pagesRepository.findAll();
		Categories categories = optionalCategory.get();
		model.addAttribute("categories", categories);
		model.addAttribute("pagesList", pagesList);
		return "admin/categories/edit";
	}

	@PostMapping("/edit")
	public String editCategory(@Valid Categories categories, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, Model model) {
		if (bindingResult.hasErrors()) {
			return "admin/categories/edit/" + categories.getId();
		}
		String slug = categories.getName().toLowerCase().replace(" ", "-");
		Categories value = categoriesRepository.findBySlug(slug);
		if (value != null) {
			redirectAttributes.addFlashAttribute("message", "Category Exists!");
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
			return "redirect:/admin/categories";
		}
		categories.setSlug(slug);
		categoriesRepository.save(categories);
		redirectAttributes.addFlashAttribute("message", "Category Updated!");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/admin/categories";

	}

	@GetMapping("/delete/{id}")
	public String deleteCategories(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
		categoriesRepository.deleteById(id);
		redirectAttributes.addFlashAttribute("message", "Category deleted successfully");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/admin/categories";
	}
}
