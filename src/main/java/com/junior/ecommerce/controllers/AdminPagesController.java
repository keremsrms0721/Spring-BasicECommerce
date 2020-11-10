package com.junior.ecommerce.controllers;

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

import com.junior.ecommerce.data.entity.Pages;
import com.junior.ecommerce.data.repository.PagesRepository;

@Controller
@RequestMapping("/admin/pages")
public class AdminPagesController {
	
	@Autowired
	private PagesRepository pagesRepository;
	
	@GetMapping
	public String getAdminPanel(Model model, @RequestParam(value = "page", required = false) Integer p) {
		int perPage = 4;
		int numberOfPage = (p==null) ? 0 : p;
		Pageable pageAble = PageRequest.of(numberOfPage, perPage);
		Page<Pages> pagesList = pagesRepository.findAll(pageAble);
		long count = pagesRepository.count();
		double pageCount = Math.ceil((double)count/(double)perPage);
		model.addAttribute("perPage", perPage);
		model.addAttribute("pageCount", (int) pageCount);
		model.addAttribute("count", count);
		model.addAttribute("numberOfPage", numberOfPage);
		model.addAttribute("pagesList", pagesList);
		return "admin/pages/index";
	}
	
	@GetMapping("/delete/{id}")
	public String deleteCategories(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
		pagesRepository.deleteById(id);
		redirectAttributes.addFlashAttribute("message", "Page deleted successfully");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/admin/pages";
	}
	
	@GetMapping("/add")
	public String addPage(Model model) {
		model.addAttribute("pages", new Pages());
		return "admin/pages/add";
	}

	@PostMapping("/add")
	public String addCategory(@Valid Pages pages, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, Model model) {
		System.out.println(pages.getName());
		if (bindingResult.hasErrors()) {
			return "admin/pages/add";
		}
		String slug = pages.getName().toLowerCase().replace(" ", "-");
		Pages value = pagesRepository.findBySlug(slug);
		if (value != null) {
			redirectAttributes.addFlashAttribute("message", "Pages Exists!");
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
			return "redirect:/admin/pages/add";
		}
		pages.setSlug(slug);
		pagesRepository.save(pages);
		redirectAttributes.addFlashAttribute("message", "Pages Added!");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/admin/pages";
	}

	@GetMapping("/edit/{id}")
	public String editPages(@PathVariable("id") int id, Model model) {
		Optional<Pages> optionalPages = pagesRepository.findById(id);
		if (!optionalPages.isPresent()) {
			return "redirect:/admin/pages";
		}
		Pages pages = optionalPages.get();
		model.addAttribute("pages", pages);
		return "admin/pages/edit";
	}

	@PostMapping("/edit")
	public String editCategory(@Valid Pages pages, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, Model model) {
		if (bindingResult.hasErrors()) {
			return "admin/pages/edit/" + pages.getId();
		}
		String slug = pages.getName().toLowerCase().replace(" ", "-");
		Pages value = pagesRepository.findBySlug(slug);
		if (value != null) {
			redirectAttributes.addFlashAttribute("message", "Pages Exists!");
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
			return "redirect:/admin/pages/edit/"+pages.getId();
		}
		pages.setSlug(slug);
		pagesRepository.save(pages);
		redirectAttributes.addFlashAttribute("message", "Pages Updated!");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/admin/pages";

	}
}
