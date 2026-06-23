package org.example;

import java.security.SecureRandom;

/** מנהל את קוד האימות היחיד ששייך לשיחה הנוכחית. */
public class VerificationService {
    // מחולל מאובטח ליצירת הקוד והקוד הפעיל שממתין לבדיקה.
    private final SecureRandom secureRandom = new SecureRandom();
    private String currentCode = "";

    // נקראת לפני שליחה במייל או הצגה בטלפון; יוצרת, שומרת ומחזירה קוד בן 6 ספרות.
    public String generateCode() {
        currentCode = String.format("%06d", secureRandom.nextInt(1_000_000));
        return currentCode;
    }

    // נקראת במצב 5 ומחזירה האם הקוד שהמשתמש הזין זהה לקוד הפעיל.
    public boolean verify(String code) {
        return code.equals(currentCode);
    }

    // מנקה את הקוד לאחר הצלחה, כשל בשליחה או איפוס השיחה.
    public void clear() {
        currentCode = "";
    }
}
