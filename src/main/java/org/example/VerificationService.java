package org.example;

import java.security.SecureRandom;

/** Owns the one verification code used by the current chat. */
public class VerificationService {
    private final SecureRandom secureRandom = new SecureRandom();
    private String currentCode = "";

    public String generateCode() {
        currentCode = String.format("%06d", secureRandom.nextInt(1_000_000));
        return currentCode;
    }

    public boolean verify(String code) {
        return code.equals(currentCode);
    }

    public void clear() {
        currentCode = "";
    }
}
