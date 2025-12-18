package com.example.demo.features.order.service;

import com.example.demo.features.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderCheckoutServiceTest {
    OrderServices orderServices;
    PaymentGateway paymentGateway;
    ConfirmationSender confirmationSender;
    OrderCheckoutService orderCheckoutService;
    User user;

    @BeforeEach
    void mockServices() {
        orderServices = mock(OrderServices.class);
        paymentGateway = mock(PaymentGateway.class);
        confirmationSender = mock(ConfirmationSender.class);
        orderCheckoutService =
                new OrderCheckoutService(orderServices, paymentGateway, confirmationSender);
        user = new User();
    }

    @Test
    void checkout_fails_whenUserIsNull() {
        CheckoutResult result = orderCheckoutService.checkout(null);

        assertFalse(result.isSuccessful());
        assertEquals("User details are required", result.getMessage());

        verifyNoInteractions(orderServices, paymentGateway, confirmationSender);
    }

    @Test
    void checkout_fails_whenCartTotalIsZeroOrNegative() {
        when(orderServices
                .calculateTotalForUser(any(User.class)))
                .thenReturn(-1.0);

        CheckoutResult result = orderCheckoutService.checkout(user);

        assertFalse(result.isSuccessful());
        assertEquals("Cart total must be greater than zero", result.getMessage());

        verify(orderServices).calculateTotalForUser(user);
        verifyNoInteractions(paymentGateway, confirmationSender);
    }

    @Test
    void checkout_fails_whenPaymentDeclined() {
        double cartTotal = 1.0;
        when(orderServices
                .calculateTotalForUser(any(User.class)))
                .thenReturn(cartTotal);

        when(paymentGateway
                .charge(user, cartTotal))
                .thenReturn(false);

        CheckoutResult result = orderCheckoutService.checkout(user);

        assertFalse(result.isSuccessful());
        assertEquals("Payment declined", result.getMessage());

        verify(orderServices).calculateTotalForUser(user);
        verify(paymentGateway).charge(user, cartTotal);
        verifyNoInteractions(confirmationSender);
    }

    @Test
    void checkout_succeeds_whenAllConditionsAreValid() {
        double cartTotal = 1.0;
        when(orderServices
                .calculateTotalForUser(any(User.class)))
                .thenReturn(cartTotal);

        when(paymentGateway
                .charge(user, cartTotal))
                .thenReturn(true);

        CheckoutResult result = orderCheckoutService.checkout(user);

        assertTrue(result.isSuccessful());
        assertEquals("Payment accepted", result.getMessage());
        assertEquals(cartTotal, result.getChargedAmount());

        verify(orderServices).calculateTotalForUser(user);
        verify(paymentGateway).charge(user, cartTotal);
        verify(confirmationSender).sendConfirmation(user, cartTotal);
    }
}
