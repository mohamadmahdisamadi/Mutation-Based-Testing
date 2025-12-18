package com.example.demo.features.user.service;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.features.product.model.Product;
import com.example.demo.features.product.service.ProductServices;

class MealPlanServiceTest {

    @Test
    void generateWeeklyPlan_throwsWhenDailyTargetNonPositive() {
        ProductServices productServices = mock(ProductServices.class);
        CalorieEstimator estimator = p -> 100.0;
        PantryInventory inventory = new AlwaysAvailableInventory();

        MealPlanService service = new MealPlanService(productServices, estimator, inventory);

        assertThrows(IllegalArgumentException.class, () -> service.generateWeeklyPlan(0, 1));
        assertThrows(IllegalArgumentException.class, () -> service.generateWeeklyPlan(-10, 1));
    }

    @Test
    void generateWeeklyPlan_throwsWhenMealsPerDayNonPositive() {
        ProductServices productServices = mock(ProductServices.class);
        CalorieEstimator estimator = p -> 100.0;
        PantryInventory inventory = new AlwaysAvailableInventory();

        MealPlanService service = new MealPlanService(productServices, estimator, inventory);

        assertThrows(IllegalArgumentException.class, () -> service.generateWeeklyPlan(1000, 0));
        assertThrows(IllegalArgumentException.class, () -> service.generateWeeklyPlan(1000, -1));
    }

    @Test
    void generateWeeklyPlan_throwsWhenNoProductsReturned() {
        ProductServices productServices = mock(ProductServices.class);
        when(productServices.getAllProducts()).thenReturn(null);

        MealPlanService service = new MealPlanService(productServices, p -> 100.0, new AlwaysAvailableInventory());
        assertThrows(IllegalStateException.class, () -> service.generateWeeklyPlan(1000, 1));
    }

    @Test
    void generateWeeklyPlan_throwsWhenEmptyProductsReturned() {
        ProductServices productServices = mock(ProductServices.class);
        when(productServices.getAllProducts()).thenReturn(List.of());

        MealPlanService service = new MealPlanService(productServices, p -> 100.0, new AlwaysAvailableInventory());
        assertThrows(IllegalStateException.class, () -> service.generateWeeklyPlan(1000, 1));
    }

    @Test
    void generateWeeklyPlan_fulfillsWeekAndRespectsMaxOccurrencesPerProduct() {
        Product p1 = product(1, "P1");
        Product p2 = product(2, "P2");
        Product p3 = product(3, "P3");

        ProductServices productServices = mock(ProductServices.class);
        when(productServices.getAllProducts()).thenReturn(Arrays.asList(p1, p2, p3));

        CalorieEstimator estimator = product -> {
            if (product == null) {
                return 0.0;
            }
            if (product.getPid() == 1) {
                return 1000.0; 

                        }if (product.getPid() == 2) {
                return 1100.0; 

                        }return 1200.0; 
        };

        CountingInventory inventory = new CountingInventory();
        MealPlanService service = new MealPlanService(productServices, estimator, inventory);

        MealPlan plan = service.generateWeeklyPlan(1000, 1);

        Map<DayOfWeek, List<MealEntry>> map = plan.asMap();
        assertEquals(7, map.size());

        long p1Count = map.values().stream().flatMap(List::stream).filter(m -> m.getProductId() == 1).count();
        long p2Count = map.values().stream().flatMap(List::stream).filter(m -> m.getProductId() == 2).count();
        long p3Count = map.values().stream().flatMap(List::stream).filter(m -> m.getProductId() == 3).count();

        assertEquals(3, p1Count);
        assertEquals(3, p2Count);
        assertEquals(1, p3Count);

        assertEquals(7, inventory.reserveCalls);
    }

    @Test
    void generateWeeklyPlan_throwsWhenUnableToFillMealsDueToInventory() {
        Product p1 = product(1, "Available");
        Product p2 = product(2, "Unavailable");

        ProductServices productServices = mock(ProductServices.class);
        when(productServices.getAllProducts()).thenReturn(Arrays.asList(p1, p2));

        CalorieEstimator estimator = product -> 500.0;

        PantryInventory inventory = new PantryInventory() {
            @Override
            public boolean hasIngredients(Product product) {
                return product != null && product.getPid() == 1;
            }

            @Override
            public void reserve(Product product) {
                
            }
        };

        MealPlanService service = new MealPlanService(productServices, estimator, inventory);

        assertThrows(IllegalStateException.class, () -> service.generateWeeklyPlan(1000, 2));
    }

    private static Product product(int id, String name) {
        Product p = new Product();
        p.setPid(id);
        p.setPname(name);
        return p;
    }

    private static final class AlwaysAvailableInventory implements PantryInventory {

        @Override
        public boolean hasIngredients(Product product) {
            return product != null;
        }

        @Override
        public void reserve(Product product) {
            
        }
    }

    private static final class CountingInventory implements PantryInventory {

        int reserveCalls = 0;

        @Override
        public boolean hasIngredients(Product product) {
            return product != null;
        }

        @Override
        public void reserve(Product product) {
            reserveCalls++;
        }
    }
}
