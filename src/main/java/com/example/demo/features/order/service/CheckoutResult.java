package com.example.demo.features.order.service;

public class CheckoutResult {

	private final boolean successful;
	private final String message;
	private final double chargedAmount;

	private CheckoutResult(boolean successful, String message, double chargedAmount) {
		this.successful = successful;
		this.message = message;
		this.chargedAmount = chargedAmount;
	}

	public static CheckoutResult success(double chargedAmount) {
		return new CheckoutResult(true, "Payment accepted", chargedAmount);
	}

	public static CheckoutResult failure(String message) {
		return new CheckoutResult(false, message, 0.0);
	}

	public boolean isSuccessful() {
		return successful;
	}

	public String getMessage() {
		return message;
	}

	public double getChargedAmount() {
		return chargedAmount;
	}
}
