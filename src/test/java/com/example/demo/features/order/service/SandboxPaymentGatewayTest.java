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

    @Test
    void charge_killsBoundaryMutants_forAmountThresholds() {
        assertTrue(gateway.charge(validUser(), 200.0));
        assertTrue(gateway.charge(validUser(), 200.01));
    }

    @Test
    void charge_isDeclined_whenRiskScoreIsExactly70() {
        User user = validUser();
        assertFalse(gateway.charge(user, 501.0));
    }

    @Test
    void charge_killsDecimalPrecisionMutants() {
        // 2 decimal places should pass
        assertTrue(gateway.charge(validUser(), 10.45));
        // 3 decimal places should fail
        assertFalse(gateway.charge(validUser(), 10.456));
    }

    @Test
    void charge_killsBlacklistMutants() {
        User user = validUser();
        user.setUemail("user@FRAUD.com");
        assertFalse(gateway.charge(user, 10.0));
    }

    @Test
    void charge_killsBoundaryMutants_onAmountThresholds() {
        assertTrue(gateway.charge(validUser(), 200.0));
        assertTrue(gateway.charge(validUser(), 500.0));
    }

    @Test
    void charge_killsBoundaryMutants_onMaxLimit() {
        User verySafeUser = new User();
        verySafeUser.setUemail("a@b.com"); // Choose an email with a low hash % 20
        verySafeUser.setUname("Safe User");
        verySafeUser.setUnumber(12345L);
        assertDoesNotThrow(() -> gateway.charge(verySafeUser, 1000.0));
    }

    @Test
    void charge_killsMutants_inRiskLogic() {
        User riskyUser = validUser();
        riskyUser.setUname("");
        riskyUser.setUnumber(null);
        assertFalse(gateway.charge(riskyUser, 250.0));
    }

    @Test
    void charge_killsMutants_inEmailValidation() {
        User user = validUser();
        user.setUemail("test@mail.ru");
        assertTrue(gateway.charge(user, 100.0));
        assertFalse(gateway.charge(user, 600.0));
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

