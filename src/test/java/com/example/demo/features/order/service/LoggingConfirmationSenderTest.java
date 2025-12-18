package com.example.demo.features.order.service;

import com.example.demo.features.user.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoggingConfirmationSenderTest {

    LoggingConfirmationSender sender = new LoggingConfirmationSender();

    @Test
    void sendConfirmation_doesNothing_whenUserIsNull() {
        assertDoesNotThrow(() -> sender.sendConfirmation(null, 100.0));
    }

    @Test
    void sendConfirmation_doesNothing_whenAmountIsZeroOrNegative() {
        User user = validUser();
        assertDoesNotThrow(() -> sender.sendConfirmation(user, 0.0));
        assertDoesNotThrow(() -> sender.sendConfirmation(user, -10.0));
    }

    @Test
    void sendConfirmation_doesNothing_whenEmailIsNull() {
        User user = validUser();
        user.setUemail(null);
        assertDoesNotThrow(() -> sender.sendConfirmation(user, 100.0));
    }

    @Test
    void sendConfirmation_doesNothing_whenEmailIsInvalid() {
        User user = validUser();
        user.setUemail("not-an-email");
        assertDoesNotThrow(() -> sender.sendConfirmation(user, 100.0));
    }

    @Test
    void sendConfirmation_completesSuccessfully_whenAllInputsAreValid() {
        assertDoesNotThrow(() ->
                sender.sendConfirmation(validUser(), 100.0)
        );
    }



    // helper method to create a valid user
    private User validUser() {
        User user = new User();
        user.setUemail("user@example.com");
        user.setUname("User");
        user.setUnumber((long)12345);
        return user;
    }
}
