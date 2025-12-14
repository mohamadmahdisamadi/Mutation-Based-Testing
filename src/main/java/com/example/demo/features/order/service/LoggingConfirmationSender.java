package com.example.demo.features.order.service;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.demo.features.user.model.User;

@Component
@Profile("!prod")
public class LoggingConfirmationSender implements ConfirmationSender {

	private static final Logger log = LoggerFactory.getLogger(LoggingConfirmationSender.class);
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

	@Override
	public void sendConfirmation(User user, double amount) {
		if (user == null) {
			log.warn("Skipped confirmation: user details missing for amount {}", amount);
			return;
		}

		if (amount <= 0) {
			log.warn("Skipped confirmation for {}: non-positive amount {}", safeEmail(user), amount);
			return;
		}

		if (!hasValidEmail(user)) {
			log.warn("Skipped confirmation: invalid email for user {}", safeEmail(user));
			return;
		}

		log.info("Checkout confirmation queued for {} for {}", safeEmail(user), amount);
	}

	private boolean hasValidEmail(User user) {
		String email = user.getUemail();
		return email != null && EMAIL_PATTERN.matcher(email).matches();
	}

	private String safeEmail(User user) {
		return user.getUemail() != null ? user.getUemail() : "unknown";
	}
}
