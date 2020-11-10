package com.junior.ecommerce.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.junior.ecommerce.data.entity.Categories;
import com.junior.ecommerce.data.entity.Products;
import com.junior.ecommerce.data.repository.CategoriesRepository;
import com.junior.ecommerce.data.repository.ProductsRepository;

@Controller
@RequestMapping("/admin/products")
public class AdminProductsController {

	@Autowired
	private ProductsRepository productRepository;

	@Autowired
	private CategoriesRepository categoriesRepository;

	@GetMapping
	public String getProducts(Model model, @RequestParam(value = "page", required = false) Integer p) {
		int perPage = 4;
		int numberOfPage = (p == null) ? 0 : p;
		Pageable pageAble = PageRequest.of(numberOfPage, perPage);
		long count = 0;
		Map<Integer, String> mapList = new HashMap<>();
		List<Categories> categoriesList = categoriesRepository.findAll();
		for (Categories categories : categoriesList) {
			mapList.put(categories.getId(), categories.getName());
		}
		Page<Products> productsList = productRepository.findAll(pageAble);
		model.addAttribute("mapList", mapList);
		model.addAttribute("productsList", productsList);

		count = categoriesRepository.count();
		double pageCount = Math.ceil((double) count / (double) perPage);

		model.addAttribute("pageCount", (int) pageCount);
		model.addAttribute("perPage", perPage);
		model.addAttribute("count", count);
		model.addAttribute("numberOfPage", numberOfPage);

		return "admin/products/index";

	}

	@GetMapping("/add")
	public String addProduct(Model model) {
		List<Categories> categoryList = categoriesRepository.findAll();
		model.addAttribute("products", new Products());
		model.addAttribute("categoryList", categoryList);
		return "admin/products/add";
	}

	@PostMapping("/add")
	public String addProduct(
			@Valid Products product,
			BindingResult bindingResult,
			MultipartFile file,
			RedirectAttributes redirectAttributes,
			Model model) 
					throws IOException {
		if (bindingResult.hasErrors()) {
			List<Categories> categoryList = categoriesRepository.findAll();
			model.addAttribute("categoryList", categoryList);
			return "admin/products/add";
		}
		
		boolean fileOK = false;
		byte[] bytes = file.getBytes();
		String fileName = file.getOriginalFilename();
		Path path = Paths.get("src/main/resources/static/media/"+fileName);

		String slug = product.getName().toLowerCase().replace(" ", "-");
		Products value = productRepository.findBySlug(slug);
		
		if( fileName.endsWith("jpg") || fileName.endsWith("png") ) {
			fileOK = true;
		}
		
		if(! fileOK ) {
			redirectAttributes.addFlashAttribute("message", "Image must be a jpg or a png!");
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
			return "redirect:/admin/products/add";
		}
		else if (value != null) {
			redirectAttributes.addFlashAttribute("message", "Product Exists!");
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
			return "redirect:/admin/products/add";
		}
		product.setSlug(slug);
		product.setImage(fileName);
		productRepository.save(product);
		Files.write(path, bytes);
		redirectAttributes.addFlashAttribute("message", "Product Added!");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/admin/products/add";
	}

	@GetMapping("/edit/{id}")
	public String editCategory(@PathVariable("id") int id, Model model) {
		Optional<Products> optionalProduct = productRepository.findById(id);
		if (!optionalProduct.isPresent()) {
			return "redirect:/admin/products";
		}
		List<Categories> categoryList = categoriesRepository.findAll();
		Products products = optionalProduct.get();
		model.addAttribute("products", products);
		model.addAttribute("categoryList", categoryList);
		return "admin/products/edit";
	}

	@PostMapping("/edit")
	public String editCategory(@Valid Products products, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, Model model) {
		if (bindingResult.hasErrors()) {
			return "admin/products/edit/" + products.getId();
		}
		String slug = products.getName().toLowerCase().replace(" ", "-");
		Products value = productRepository.findBySlug(slug);
		if(value!=null) {
			redirectAttributes.addFlashAttribute("message", "Product exists!!");
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
			return "redirect:/admin/products";
		}
		products.setSlug(slug);
		productRepository.save(products);
		return "redirect:/admin/products";
	}

	@GetMapping("/delete/{id}")
	public String deleteProducts(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
		productRepository.deleteById(id);
		redirectAttributes.addFlashAttribute("message", "Product deleted successfully");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/admin/products";
	}

}
