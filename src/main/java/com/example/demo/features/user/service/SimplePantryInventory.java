package com.example.demo.features.user.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.demo.features.product.model.Product;

@Component
@Profile("!prod")
public class SimplePantryInventory implements PantryInventory {

	private final ConcurrentMap<Integer, PantryItem> stock = new ConcurrentHashMap<>();

	@Override
	public boolean hasIngredients(Product product) {
		if (product == null) {
			return false;
		}
		PantryItem item = stock.computeIfAbsent(product.getPid(), id -> createItem(product));
		return item.canFulfill(requestedServings(product));
	}

	@Override
	public void reserve(Product product) {
		if (product == null) {
			return;
		}
		stock.compute(product.getPid(), (id, item) -> {
			PantryItem current = item != null ? item : createItem(product);
			current.reserve(requestedServings(product));
			return current;
		});
	}

	private PantryItem createItem(Product product) {
		int initialStock = product.getInitialStock();
		int servingsPerMeal = requestedServings(product);
		if (initialStock <= 0) {
			initialStock = servingsPerMeal * 7;
		}
		return new PantryItem(product.getPid(), product.getPname(), initialStock, servingsPerMeal);
	}

	private int requestedServings(Product product) {
		int servings = product.getDefaultServingSize();
		return servings > 0 ? servings : 1;
	}
}
