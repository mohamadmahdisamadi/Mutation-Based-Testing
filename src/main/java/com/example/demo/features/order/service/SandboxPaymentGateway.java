package com.example.demo.features.order.service;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.demo.features.user.model.User;

@Component
@Profile("!prod")
public class SandboxPaymentGateway implements PaymentGateway {

	private static final Logger log = LoggerFactory.getLogger(SandboxPaymentGateway.class);
	private static final double MAX_ALLOWED_AMOUNT = 1_000.0;
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
	private static final Set<String> BLACKLISTED_EMAIL_DOMAINS = Set.of("fraud.com", "test-spam.org");

	@Override
	public boolean charge(User user, double amount) {
		if (user == null) {
			log.warn("Sandbox payment declined: user details missing for amount {}", amount);
			return false;
		}

		if (amount <= 0) {
			log.warn("Sandbox payment declined for {}: non-positive amount {}", safeEmail(user), amount);
			return false;
		}

		if (hasTooManyDecimalPlaces(amount)) {
			log.warn("Sandbox payment declined for {}: invalid amount precision {}", safeEmail(user), amount);
			return false;
		}

		if (amount > MAX_ALLOWED_AMOUNT) {
			log.warn("Sandbox payment declined for {}: amount {} exceeds sandbox limit {}", safeEmail(user), amount,
					MAX_ALLOWED_AMOUNT);
			return false;
		}

		if (!hasValidEmail(user)) {
			log.warn("Sandbox payment declined: invalid email for user {}", safeEmail(user));
			return false;
		}

		if (isBlacklistedDomain(user)) {
			log.warn("Sandbox payment declined for {}: blacklisted email domain", safeEmail(user));
			return false;
		}

		int riskScore = calculateRiskScore(user, amount);
		boolean accepted = riskScore < 70;

		if (accepted) {
			log.info("Sandbox payment accepted for {}: amount {}, risk score {}", safeEmail(user), amount, riskScore);
		} else {
			log.warn("Sandbox payment declined for {}: amount {}, risk score {}", safeEmail(user), amount, riskScore);
		}

		return accepted;
	}

	private int calculateRiskScore(User user, double amount) {
		int score = 0;

		if (amount > 200) {
			score += 30;
		}
		if (amount > 500) {
			score += 30;
		}
		if (amount > 800) {
			score += 15;
		}

		if (user.getUnumber() == null) {
			score += 20;
		}

		if (user.getUname() == null || user.getUname().trim().isEmpty()) {
			score += 10;
		}

		String email = user.getUemail();
		if (email != null) {
			int hash = email.toLowerCase(Locale.ROOT).hashCode();
			int positiveHash = hash == Integer.MIN_VALUE ? 0 : Math.abs(hash);
			score += positiveHash % 20;
			if (email.endsWith(".ru") || email.endsWith(".cn")) {
				score += 20;
			}
		}

		return score;
	}

	private boolean hasValidEmail(User user) {
		String email = user.getUemail();
		return email != null && EMAIL_PATTERN.matcher(email).matches();
	}

	private String safeEmail(User user) {
		return user.getUemail() != null ? user.getUemail() : "unknown";
	}

	private boolean isBlacklistedDomain(User user) {
		String email = user.getUemail();
		if (email == null || !email.contains("@")) {
			return false;
		}
		String domain = email.substring(email.indexOf('@') + 1).toLowerCase(Locale.ROOT);
		return BLACKLISTED_EMAIL_DOMAINS.contains(domain);
	}

	private boolean hasTooManyDecimalPlaces(double amount) {
		BigDecimal value = BigDecimal.valueOf(amount);
		return value.scale() > 2;
	}
}
