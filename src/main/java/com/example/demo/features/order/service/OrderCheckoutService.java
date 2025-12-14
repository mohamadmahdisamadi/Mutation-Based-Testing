package com.example.demo.features.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.features.user.model.User;

@Component
public class OrderCheckoutService {

	private final OrderServices orderServices;
	private final PaymentGateway paymentGateway;
	private final ConfirmationSender confirmationSender;

	@Autowired
	public OrderCheckoutService(OrderServices orderServices, PaymentGateway paymentGateway,
			ConfirmationSender confirmationSender) {
		this.orderServices = orderServices;
		this.paymentGateway = paymentGateway;
		this.confirmationSender = confirmationSender;
	}

	public CheckoutResult checkout(User user) {
		if (user == null) {
			return CheckoutResult.failure("User details are required");
		}

		double cartTotal = orderServices.calculateTotalForUser(user);
		if (cartTotal <= 0) {
			return CheckoutResult.failure("Cart total must be greater than zero");
		}

		boolean paymentAccepted = paymentGateway.charge(user, cartTotal);
		if (!paymentAccepted) {
			return CheckoutResult.failure("Payment declined");
		}

		confirmationSender.sendConfirmation(user, cartTotal);
		return CheckoutResult.success(cartTotal);
	}
}
