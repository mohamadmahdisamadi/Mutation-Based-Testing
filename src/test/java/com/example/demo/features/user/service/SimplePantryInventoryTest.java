package com.example.demo.features.user.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.example.demo.features.product.model.Product;

class SimplePantryInventoryTest {

    @Test
    void hasIngredients_returnsFalseForNullProduct() {
        SimplePantryInventory inventory = new SimplePantryInventory();

        assertFalse(inventory.hasIngredients(null));
    }

    @Test
    void reserve_noopForNullProduct() {
        SimplePantryInventory inventory = new SimplePantryInventory();

        assertDoesNotThrow(() -> inventory.reserve(null));
    }

    @Test
    void hasIngredients_and_reserve_respectServingSizeAndInitialStock() {
        SimplePantryInventory inventory = new SimplePantryInventory();

        Product p = product(10, "Chicken", 2, 4);
        assertTrue(inventory.hasIngredients(p));

        inventory.reserve(p); 
        assertTrue(inventory.hasIngredients(p)); 

        inventory.reserve(p); 
        assertFalse(inventory.hasIngredients(p));
    }

    @Test
    void whenInitialStockNotProvided_itDefaultsToSevenMealsWorth() {
        SimplePantryInventory inventory = new SimplePantryInventory();

        Product p = product(11, "Beans", 3, 0); 
        assertTrue(inventory.hasIngredients(p));

        for (int i = 0; i < 7; i++) {
            inventory.reserve(p); 
        }
        assertFalse(inventory.hasIngredients(p));
    }

    private static Product product(int id, String name, int defaultServingSize, int initialStock) {
        Product p = new Product();
        p.setPid(id);
        p.setPname(name);
        p.setDefaultServingSize(defaultServingSize);
        p.setInitialStock(initialStock);
        return p;
    }
}
