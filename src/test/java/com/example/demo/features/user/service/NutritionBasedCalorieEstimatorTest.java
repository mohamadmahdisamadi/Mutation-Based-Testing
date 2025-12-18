package com.example.demo.features.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.example.demo.features.product.model.NutritionProfile;
import com.example.demo.features.product.model.Product;

class NutritionBasedCalorieEstimatorTest {

    private final NutritionBasedCalorieEstimator estimator = new NutritionBasedCalorieEstimator();

    @Test
    void estimateCalories_returnsZeroForNullProduct() {
        assertEquals(0.0, estimator.estimateCalories(null));
    }

    @Test
    void estimateCalories_usesNutritionProfileWhenCaloriesPerServingPositive() {
        Product p = new Product();
        p.setDefaultServingSize(2);

        NutritionProfile profile = new NutritionProfile();
        profile.setCaloriesPerServing(100);
        p.setNutritionProfile(profile);

        assertEquals(200.0, estimator.estimateCalories(p));
    }

    @Test
    void estimateCalories_defaultsServingSizeToOneWhenNonPositive() {
        Product p = new Product();
        p.setDefaultServingSize(0);

        NutritionProfile profile = new NutritionProfile();
        profile.setCaloriesPerServing(100);
        p.setNutritionProfile(profile);

        assertEquals(100.0, estimator.estimateCalories(p));
    }

    @Test
    void estimateCalories_fallsBackToPriceWhenNoNutritionProfile() {
        Product p = new Product();
        p.setNutritionProfile(null);
        p.setPprice(0.5);

        assertEquals(50.0, estimator.estimateCalories(p));
    }

    @Test
    void estimateCalories_fallsBackToPriceWhenCaloriesPerServingNotPositive() {
        Product p = new Product();
        NutritionProfile profile = new NutritionProfile();
        profile.setCaloriesPerServing(0);
        p.setNutritionProfile(profile);

        p.setPprice(3.0); 
        assertEquals(150.0, estimator.estimateCalories(p));
    }
}
