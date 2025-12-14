package com.example.demo.features.user.service;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.features.product.model.Product;
import com.example.demo.features.product.service.ProductServices;

@Component
public class MealPlanService {

	private static final int MAX_OCCURRENCES_PER_PRODUCT_PER_WEEK = 3;

	private final ProductServices productServices;
	private final CalorieEstimator calorieEstimator;
	private final PantryInventory pantryInventory;

	@Autowired
	public MealPlanService(ProductServices productServices, CalorieEstimator calorieEstimator,
			PantryInventory pantryInventory) {
		this.productServices = productServices;
		this.calorieEstimator = calorieEstimator;
		this.pantryInventory = pantryInventory;
	}

	public MealPlan generateWeeklyPlan(double dailyCalorieTarget, int mealsPerDay) {
		if (dailyCalorieTarget <= 0) {
			throw new IllegalArgumentException("Daily calorie target must be positive");
		}
		if (mealsPerDay <= 0) {
			throw new IllegalArgumentException("Meals per day must be positive");
		}
		List<Product> products = productServices.getAllProducts();
		if (products == null || products.isEmpty()) {
			throw new IllegalStateException("No products available for planning");
		}
		double targetPerMeal = dailyCalorieTarget / mealsPerDay;
		List<ProductScore> scoredProducts = scoreProducts(products, targetPerMeal);
		Map<DayOfWeek, List<MealEntry>> plan = new EnumMap<>(DayOfWeek.class);
		Map<Integer, Integer> weeklyUsage = new HashMap<>();
		for (DayOfWeek day : DayOfWeek.values()) {
			List<MealEntry> dayMeals = selectMealsForDay(scoredProducts, mealsPerDay, weeklyUsage);
			if (dayMeals.size() < mealsPerDay) {
				throw new IllegalStateException("Unable to fulfill meal plan due to limited inventory");
			}
			plan.put(day, dayMeals);
		}
		return new MealPlan(plan);
	}

	private List<MealEntry> selectMealsForDay(List<ProductScore> scoredProducts, int mealsPerDay,
			Map<Integer, Integer> weeklyUsage) {
		List<MealEntry> dayMeals = new ArrayList<>();
		for (ProductScore productScore : scoredProducts) {
			Product product = productScore.product;
			if (!pantryInventory.hasIngredients(product)) {
				continue;
			}
			if (hasExceededWeeklyUsage(product, weeklyUsage)) {
				continue;
			}
			pantryInventory.reserve(product);
			dayMeals.add(new MealEntry(product.getPid(), product.getPname(), productScore.estimatedCalories));
			incrementUsage(product, weeklyUsage);
			if (dayMeals.size() == mealsPerDay) {
				break;
			}
		}
		return dayMeals;
	}

	private boolean hasExceededWeeklyUsage(Product product, Map<Integer, Integer> weeklyUsage) {
		int current = weeklyUsage.getOrDefault(product.getPid(), 0);
		return current >= MAX_OCCURRENCES_PER_PRODUCT_PER_WEEK;
	}

	private void incrementUsage(Product product, Map<Integer, Integer> weeklyUsage) {
		int current = weeklyUsage.getOrDefault(product.getPid(), 0);
		weeklyUsage.put(product.getPid(), current + 1);
	}

	private List<ProductScore> scoreProducts(List<Product> products, double targetPerMeal) {
		List<ProductScore> scored = new ArrayList<>();
		for (Product product : products) {
			if (Objects.isNull(product)) {
				continue;
			}
			double calories = calorieEstimator.estimateCalories(product);
			double score = Math.abs(targetPerMeal - calories);
			scored.add(new ProductScore(product, calories, score));
		}
		scored.sort((a, b) -> Double.compare(a.score, b.score));
		return scored;
	}

	private static final class ProductScore {
		private final Product product;
		private final double estimatedCalories;
		private final double score;

		private ProductScore(Product product, double estimatedCalories, double score) {
			this.product = product;
			this.estimatedCalories = estimatedCalories;
			this.score = score;
		}
	}
}
