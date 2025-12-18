package com.example.demo.features.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class PantryItemTest {

    @Test
    void constructor_clampsNegativeStockAndMinimumReserve() {
        PantryItem item = new PantryItem(1, "Rice", -10, 0);

        assertEquals(0, item.getAvailableServings());
        assertEquals(1, item.getMinimumReserve());
    }

    @Test
    void canFulfill_trueWhenEnoughServings_andFalseWhenNotEnough() {
        PantryItem item = new PantryItem(1, "Rice", 3, 1);

        assertTrue(item.canFulfill(3));
        assertFalse(item.canFulfill(4));
    }

    @Test
    void reserve_noopWhenServingsRequestedNonPositive() {
        PantryItem item = new PantryItem(1, "Rice", 5, 1);

        item.reserve(0);
        assertEquals(5, item.getAvailableServings());

        item.reserve(-2);
        assertEquals(5, item.getAvailableServings());
    }

    @Test
    void reserve_neverMakesAvailableServingsNegative() {
        PantryItem item = new PantryItem(1, "Rice", 3, 1);

        item.reserve(10);
        assertEquals(0, item.getAvailableServings());
    }
}
