package com.example.demo.features.product.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class NutritionProfile {

	private double caloriesPerServing;
	private double proteinGrams;
	private double carbohydrateGrams;
	private double fatGrams;

	public double getCaloriesPerServing() {
		return caloriesPerServing;
	}

	public void setCaloriesPerServing(double caloriesPerServing) {
		this.caloriesPerServing = caloriesPerServing;
	}

	public double getProteinGrams() {
		return proteinGrams;
	}

	public void setProteinGrams(double proteinGrams) {
		this.proteinGrams = proteinGrams;
	}

	public double getCarbohydrateGrams() {
		return carbohydrateGrams;
	}

	public void setCarbohydrateGrams(double carbohydrateGrams) {
		this.carbohydrateGrams = carbohydrateGrams;
	}

	public double getFatGrams() {
		return fatGrams;
	}

	public void setFatGrams(double fatGrams) {
		this.fatGrams = fatGrams;
	}
}
