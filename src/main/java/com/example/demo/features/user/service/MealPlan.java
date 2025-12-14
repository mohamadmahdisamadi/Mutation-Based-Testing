package com.example.demo.features.user.service;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class MealPlan {

	private final Map<DayOfWeek, List<MealEntry>> meals;

	public MealPlan(Map<DayOfWeek, List<MealEntry>> meals) {
		this.meals = new EnumMap<>(meals);
	}

	public List<MealEntry> getMealsForDay(DayOfWeek day) {
		return meals.containsKey(day) ? Collections.unmodifiableList(meals.get(day)) : Collections.emptyList();
	}

	public Map<DayOfWeek, List<MealEntry>> asMap() {
		return Collections.unmodifiableMap(meals);
	}
}
