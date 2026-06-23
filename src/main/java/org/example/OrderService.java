package org.example;

import java.util.ArrayList;
import java.util.List;

/** Keeps the current order and calculates its raw total. */
public class OrderService {
    private OrderNode head;
    private int size;
    private double discount;

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

    public void clear() {
        head = null;
        size = 0;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int size() {
        return size;
    }

    public double calculateTotal() {
        return calculateTotal(head);
    }

    public double calculateFinalTotal() {
        double total = calculateTotal();
        return Math.round((total - (total * discount)) * 100.0) / 100.0;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public List<String> getItemsAsList() {
        List<String> items = new ArrayList<>();
        OrderNode current = head;
        while (current != null) {
            items.add(current.itemDetails);
            current = current.next;
        }
        return items;
    }

    private double calculateTotal(OrderNode node) {
        if (node == null) return 0;
        return node.price + calculateTotal(node.next);
    }

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
