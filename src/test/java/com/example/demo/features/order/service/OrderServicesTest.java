package com.example.demo.features.order.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.demo.features.order.model.Orders;
import com.example.demo.features.order.repository.OrderRepository;
import com.example.demo.features.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderServicesTest {
    private OrderServices orderServices;
    private OrderRepository orderRepository;
    private User user;

    @BeforeEach
    public void mockServices() {
        orderRepository = mock(OrderRepository.class);
        orderServices = new OrderServices();
        ReflectionTestUtils.setField(orderServices, "orderRepository", orderRepository);
        user = new User();
    }

    @Test
    void getOrders_returnsAllOrdersFromRepository() {
        List<Orders> orders = List.of(new Orders(), new Orders());
        when(orderRepository.findAll()).thenReturn(orders);
        List<Orders> result = orderServices.getOrders();

        assertEquals(orders, result);
        assertEquals(2, result.size());
        verify(orderRepository).findAll();
    }

    @Test
    void saveOrder_delegatesToRepository() {
        Orders order = new Orders();
        orderServices.saveOrder(order);
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrder_setsIdAndSaves() {
        Orders order = new Orders();
        orderServices.updateOrder(5, order);

        assertEquals(5, order.getoId());
        verify(orderRepository).save(order);
    }

    @Test
    void deleteOrder_delegatesToRepository() {
        orderServices.deleteOrder(3);
        verify(orderRepository).deleteById(3);
    }

    @Test
    void getOrdersForUser_delegatesToRepository() {
        List<Orders> orders = List.of(new Orders());
        when(orderRepository.findOrdersByUser(user)).thenReturn(orders);
        List<Orders> result = orderServices.getOrdersForUser(user);

        assertEquals(orders, result);
        verify(orderRepository).findOrdersByUser(user);
    }


    @Test
    void calculateTotal_returnsZero_whenRepositoryReturnsNull() {
        when(orderRepository.findOrdersByUser(user)).thenReturn(null);
        double result = orderServices.calculateTotalForUser(user);

        assertEquals(0.0, result);
        verify(orderRepository).findOrdersByUser(user);
    }

    @Test
    void calculateTotal_ignoresNullOrdersInList() {
        Orders valid = order(2, 10.0);

        List<Orders> orders = new ArrayList<>();
        orders.add(valid); orders.add(null);
        when(orderRepository.findOrdersByUser(user)).thenReturn(orders);

        double result = orderServices.calculateTotalForUser(user);
        assertEquals(20.0, result);
    }

    @Test
    void calculateTotal_throwsWhenQuantityIsZeroOrNegative() {
        Orders order = order(0, 10.0);
        when(orderRepository.findOrdersByUser(user)).thenReturn(List.of(order));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> orderServices.calculateTotalForUser(user)
        );

        assertEquals("Quantity must be positive", ex.getMessage());
    }

    @Test
    void calculateTotal_throwsWhenPriceIsNegative() {
        Orders order = order(2, -5.0);
        when(orderRepository.findOrdersByUser(user)).thenReturn(List.of(order));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> orderServices.calculateTotalForUser(user)
        );

        assertEquals("Price cannot be negative", ex.getMessage());
    }

    @Test
    void calculateTotal_usesTotalAmountIfPresent() {
        Orders order = order(2, 50.0);
        order.setTotalAmmout(999.99);
        when(orderRepository
                .findOrdersByUser(user))
                .thenReturn(List.of(order));

        double result = orderServices.calculateTotalForUser(user);
        assertEquals(999.99, result);
    }

    @Test
    void calculateTotal_calculatesPriceTimesQuantity() {
        Orders order = order(3, 12.5);
        when(orderRepository
                .findOrdersByUser(user))
                .thenReturn(List.of(order));

        double result = orderServices.calculateTotalForUser(user);
        assertEquals(37.5, result);
    }

    @Test
    void calculateTotal_roundsToTwoDecimals_halfUp() {
        Orders order = order(3, 3.518);
        when(orderRepository
                .findOrdersByUser(user))
                .thenReturn(List.of(order));

        double result = orderServices.calculateTotalForUser(user);
        assertEquals(10.55, result);
    }

    @Test
    void calculateTotal_throwsWhenTotalExceedsMaximum() {
        Orders order = order(1, 10001.0);
        when(orderRepository
                .findOrdersByUser(user))
                .thenReturn(List.of(order));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> orderServices.calculateTotalForUser(user)
        );

        assertEquals("Order total exceeds maximum allowed value", ex.getMessage());
    }




    // helper method to create an order
    private Orders order(int quantity, double price) {
        Orders o = new Orders();
        o.setoQuantity(quantity);
        o.setoPrice(price);
        return o;
    }
}
