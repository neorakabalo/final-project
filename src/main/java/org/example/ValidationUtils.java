package org.example;

/** Small input checks shared by the chat flow. */
public final class ValidationUtils {
    private ValidationUtils() {}

    public static boolean isValidPhone(String phone) {
        return phone.length() == 10 && isDigitsOnly(phone);
    }

    public static boolean isValidEmail(String email) {
        return email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    public static boolean isDigitsOnly(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) return false;
        }
        return true;
    }
}
