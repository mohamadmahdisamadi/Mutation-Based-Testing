package com.example.demo.features.order.service;

import com.example.demo.features.user.model.User;

public interface ConfirmationSender {
    void sendConfirmation(User user, double amount);
}
