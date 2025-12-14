package com.example.demo.features.order.service;

import com.example.demo.features.user.model.User;

public interface PaymentGateway {
    boolean charge(User user, double amount);
}
