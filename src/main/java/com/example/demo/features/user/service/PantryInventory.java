package com.example.demo.features.user.service;

import com.example.demo.features.product.model.Product;

public interface PantryInventory {
	boolean hasIngredients(Product product);
	void reserve(Product product);
}
