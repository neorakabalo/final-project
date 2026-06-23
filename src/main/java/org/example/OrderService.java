package org.example;

import java.util.ArrayList;
import java.util.List;

/** שומר את ההזמנה הנוכחית כרשימה מקושרת ומחשב סכומים והנחות. */
public class OrderService {
    // ראש הרשימה, מספר הפריטים ואחוז ההנחה הפעיל בסל.
    private OrderNode head;
    private int size;
    private double discount;

    // נקראת מבחירת פריט בתפריט ומוסיפה אותו לסוף ההזמנה בלי לשנות פריטים קיימים.
    public void add(String itemDetails, double price) {
        OrderNode newNode = new OrderNode(itemDetails, price);
        if (head == null) {
            head = newNode;
        } else {
            OrderNode current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    // נקראת מכפתור ההסרה בסל ומסירה את המופע הראשון ששמו תואם בדיוק.
    public void removeExact(String exactItem) {
        if (head == null) return;
        if (head.itemDetails.equals(exactItem)) {
            head = head.next;
            size--;
            return;
        }

        OrderNode current = head;
        while (current.next != null) {
            if (current.next.itemDetails.equals(exactItem)) {
                current.next = current.next.next;
                size--;
                return;
            }
            current = current.next;
        }
    }

    // מנקה את כל פריטי ההזמנה; משמש באיפוס שפה ובכפתור ניקוי הסל.
    public void clear() {
        head = null;
        size = 0;
    }

    // מחזירה האם אין כרגע פריטים בהזמנה.
    public boolean isEmpty() {
        return head == null;
    }

    // מחזירה את מספר הפריטים ומשמשת להצגת הכמות ליד כפתור הסל.
    public int size() {
        return size;
    }

    // מחזירה את סכום המחירים לפני הנחה באמצעות חישוב רקורסיבי של הרשימה.
    public double calculateTotal() {
        return calculateTotal(head);
    }

    // מחזירה את הסכום לאחר ההנחה הפעילה, מעוגל לשתי ספרות אחרי הנקודה.
    public double calculateFinalTotal() {
        double total = calculateTotal();
        return Math.round((total - (total * discount)) * 100.0) / 100.0;
    }

    public double getDiscount() {
        return discount;
    }

    // מעדכנת את אחוז ההנחה לאחר הזנת קופון או איפוס הסל.
    public void setDiscount(double discount) {
        this.discount = discount;
    }

    // מחזירה עותק של תיאורי הפריטים לצורך הצגת הסל ובניית הקבלה.
    public List<String> getItemsAsList() {
        List<String> items = new ArrayList<>();
        OrderNode current = head;
        while (current != null) {
            items.add(current.itemDetails);
            current = current.next;
        }
        return items;
    }

    // תנאי העצירה והצעד הרקורסיבי שמחברים את מחיר הצומת למחירי ההמשך.
    private double calculateTotal(OrderNode node) {
        if (node == null) return 0;
        return node.price + calculateTotal(node.next);
    }

    // צומת פנימי שמייצג פריט אחד ואת הקישור לפריט הבא בהזמנה.
    private static class OrderNode {
        private final String itemDetails;
        private final double price;
        private OrderNode next;

        private OrderNode(String itemDetails, double price) {
            this.itemDetails = itemDetails;
            this.price = price;
        }
    }
}
