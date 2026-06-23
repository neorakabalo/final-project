package org.example;

public class MenuItem {
    // נתיב בסיס משותף לכל תמונות המנות.
    private static final String IMAGE_DIR = "src/assets/images/";

    // הנתונים שממשק התפריט מציג ומשתמש בהם לסינון ולחישוב המחיר.
    public String name;
    public double price;
    public String category;
    public String description;
    public String imagePath;

    // בונה פריט תפריט ומחברת את שם קובץ התמונה לנתיב המלא שלו.
    public MenuItem(String name, double price, String category, String description, String imagePath) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.imagePath = IMAGE_DIR + imagePath;
    }
}
