package org.example;

/** בדיקות קלט קצרות שמשותפות לשלבים שונים בזרימת הצ'אט. */
public final class ValidationUtils {
    // מחלקת עזר סטטית בלבד, ולכן אין צורך ליצור ממנה מופעים.
    private ValidationUtils() {}

    // נקראת במצב 2 ומחזירה אמת רק עבור מספר שמכיל בדיוק 10 ספרות.
    public static boolean isValidPhone(String phone) {
        return phone.length() == 10 && isDigitsOnly(phone);
    }

    // נקראת לפני שליחת קוד במייל ומחזירה האם הכתובת במבנה בסיסי תקין.
    public static boolean isValidEmail(String email) {
        return email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    // עוברת על כל התווים ומחזירה האם כולם ספרות; משמשת את בדיקת הטלפון.
    public static boolean isDigitsOnly(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) return false;
        }
        return true;
    }
}
