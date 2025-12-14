package com.example.demo.features.user.service;

import com.example.demo.features.product.model.Product;

public interface CalorieEstimator {
	double estimateCalories(Product product);
}
