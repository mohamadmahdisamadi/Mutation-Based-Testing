package com.example.demo.features.order.service;

import com.example.demo.features.user.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SandboxPaymentGatewayTest {
    SandboxPaymentGateway gateway = new SandboxPaymentGateway();

    @Test
    void charge_returnsFalse_whenUserIsNull() {
        assertFalse(gateway.charge(null, 100.0));
    }

    @Test
    void charge_returnsFalse_whenAmountIsZeroOrNegative() {
        User user = validUser();
        assertFalse(gateway.charge(user, 0.0));
        assertFalse(gateway.charge(user, -10.0));
    }

    @Test
    void charge_returnsFalse_whenAmountHasTooManyDecimalPlaces() {
        assertFalse(gateway.charge(validUser(), 12.479));
    }

    @Test
    void charge_returnsFalse_whenAmountExceedsSandboxLimit() {
        assertFalse(gateway.charge(validUser(), 1000.01));
    }

    @Test
    void charge_returnsFalse_whenEmailIsNull() {
        User user = validUser();
        user.setUemail(null);
        assertFalse(gateway.charge(user, 100.0));
    }

    @Test
    void charge_returnsFalse_whenEmailIsInvalidFormat() {
        User user = validUser();
        user.setUemail("not-an-email");
        assertFalse(gateway.charge(user, 100.0));
    }

    @Test
    void charge_returnsFalse_whenEmailDomainIsBlacklisted() {
        User user = validUser();
        user.setUemail("user@fraud.com");
        assertFalse(gateway.charge(user, 100.0));
        user.setUemail("user@test-spam.org");
        assertFalse(gateway.charge(user, 100.0));
    }


    @Test
    void charge_returnsTrue_whenRiskScoreIsLow() {
        assertTrue(gateway.charge(validUser(), 50.0));
    }

    @Test
    void charge_returnsFalse_whenRiskScoreIsHigh_dueToAmount() {
        assertFalse(gateway.charge(validUser(), 900.0));
    }

    @Test
    void charge_returnsFalse_whenRiskScoreIsHigh_dueToUserData() {
        User user = new User();
        user.setUemail("suspicious@domain.ru");
        user.setUname("");
        user.setUnumber(null);
        assertFalse(gateway.charge(user, 300.0));
    }




    // helper method to create a valid user
    private User validUser() {
        User user = new User();
        user.setUemail("valid.user@example.com");
        user.setUname("Valid User");
        user.setUnumber((long)123456789);
        return user;
    }
}

