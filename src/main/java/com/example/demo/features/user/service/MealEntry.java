package com.example.demo.features.user.service;

public class MealEntry {
	private final int productId;
	private final String productName;
	private final double calories;

	public MealEntry(int productId, String productName, double calories) {
		this.productId = productId;
		this.productName = productName;
		this.calories = calories;
	}

	public int getProductId() {
		return productId;
	}

	public String getProductName() {
		return productName;
	}

	public double getCalories() {
		return calories;
	}
}
