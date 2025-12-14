package com.example.demo.features.user.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.demo.features.product.model.NutritionProfile;
import com.example.demo.features.product.model.Product;

@Component
@Profile("!prod")
public class NutritionBasedCalorieEstimator implements CalorieEstimator {

    @Override
    public double estimateCalories(Product product) {
        if (product == null) {
            return 0.0;
        }
        NutritionProfile profile = product.getNutritionProfile();
        if (profile != null && profile.getCaloriesPerServing() > 0) {
            int servingSize = product.getDefaultServingSize() > 0 ? product.getDefaultServingSize() : 1;
            return profile.getCaloriesPerServing() * servingSize;
        }
        return Math.max(product.getPprice(), 1.0) * 50.0;
    }
}
