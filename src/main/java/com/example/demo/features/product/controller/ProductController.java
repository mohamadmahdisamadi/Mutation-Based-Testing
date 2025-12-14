package com.example.demo.features.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.features.product.model.Product;
import com.example.demo.features.product.service.ProductServices;

@Controller
public class ProductController 
{
	@Autowired
	private ProductServices productServices;

	//	AddProduct
	@PostMapping("/addingProduct")
	public String addProduct(@ModelAttribute Product product)
	{

		this.productServices.addProduct(product);
		return "redirect:/admin/services";
	}

	//	UpdateProduct
	@GetMapping("/updatingProduct/{productId}")
	public String updateProduct(@ModelAttribute Product product,@PathVariable("productId") int id)
	{

		this.productServices.updateproduct(product, id);
		return "redirect:/admin/services";
	}
	//DeleteProduct
	@GetMapping("/deleteProduct/{productId}")
	public String delete(@PathVariable("productId") int id)
	{
		this.productServices.deleteProduct(id);
		return "redirect:/admin/services";
	}
	
}
