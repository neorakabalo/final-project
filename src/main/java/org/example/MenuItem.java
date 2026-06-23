package org.example;

public class MenuItem {
    private static final String IMAGE_DIR = "src/assets/images/";

    public String name;
    public double price;
    public String category;
    public String description;
    public String imagePath;

    public MenuItem(String name, double price, String category, String description, String imagePath) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.imagePath = IMAGE_DIR + imagePath;
    }
}
