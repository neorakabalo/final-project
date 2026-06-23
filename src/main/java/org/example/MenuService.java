package org.example;

/** Provides the fixed menu in the selected display language. */
public class MenuService {
    public MenuItem[] getMenu(boolean isEnglish) {
        return isEnglish ? createEnglishMenu() : createHebrewMenu();
    }

    private MenuItem[] createEnglishMenu() {
        return new MenuItem[]{
                new MenuItem("Golden Burger Meal", 85.0, "Meals", "200g 100% Entrecote patty in our secret Golden Aioli.", "golden_meal.png"),
                new MenuItem("Classic Meal", 85.0, "Meals", "200g 100% Entrecote patty with toppings of your choice.", "classic_meal.png"),
                new MenuItem("Crispy Chicken Meal", 85.0, "Meals", "Crunchy chicken fillet coated in rice crispies.", "crispy_meal.png"),
                new MenuItem("Burger & Beer Meal", 95.0, "Meals", "200g Entrecote patty with fries and cold Carlsberg.", "burger_beer.png"),
                new MenuItem("Golden Burger", 62.0, "Burgers", "200g Entrecote patty in our secret Golden Aioli.", "golden.png"),
                new MenuItem("Classic Burger", 62.0, "Burgers", "200g Entrecote patty with toppings of your choice.", "classic.png"),
                new MenuItem("Crispy Chicken", 58.0, "Burgers", "Crunchy chicken fillet coated in rice crispies.", "crispy.png"),
                new MenuItem("Fries", 22.0, "Sides", "Crispy potato fries.", "fries.png"),
                new MenuItem("Onion Rings", 24.0, "Sides", "10 pieces of onion rings.", "onion_rings.png"),
                new MenuItem("Mashed Potato Balls", 24.0, "Sides", "10 pieces of potato balls.", "mash_balls.png"),
                new MenuItem("Corn Chicken", 48.0, "Sides", "8 pcs chicken fillet in cornflakes coating.", "corn_chicken.png"),
                new MenuItem("Wings", 34.0, "Sides", "6 pcs chicken wings in house sauce.", "wings.png"),
                new MenuItem("Coca Cola", 12.0, "Drinks", "330ml Can.", "cola.png"),
                new MenuItem("Cola Zero", 12.0, "Drinks", "330ml Can.", "cola_zero.png"),
                new MenuItem("Sprite", 12.0, "Drinks", "330ml Can.", "sprite.png"),
                new MenuItem("Sprite Zero", 12.0, "Drinks", "330ml Can.", "sprite_zero.png"),
                new MenuItem("Fanta Orange", 12.0, "Drinks", "330ml Can.", "fanta.png"),
                new MenuItem("Peach Fuze Tea", 12.0, "Drinks", "330ml Can.", "fuze_tea.png"),
                new MenuItem("Mineral Water", 10.0, "Drinks", "500ml Bottle.", "water.png"),
                new MenuItem("Soda", 10.0, "Drinks", "330ml Can.", "soda.png"),
                new MenuItem("Carlsberg Beer", 18.0, "Drinks", "330ml Bottle.", "beer.png")
        };
    }

    private MenuItem[] createHebrewMenu() {
        return new MenuItem[]{
                new MenuItem("ארוחת גולדן בורגר", 85.0, "ארוחות", "כ-200 גר׳ של 100% קציצת אנטריקוט שמתבשלת ברוטב איולי גולדן.", "golden_meal.png"),
                new MenuItem("ארוחה קלאסית", 85.0, "ארוחות", "כ-200 גר׳ של 100% קציצת אנטריקוט עם מרכיבים לבחירה.", "classic_meal.png"),
                new MenuItem("ארוחת קריספי צ'יקן", 85.0, "ארוחות", "פילה עוף קראנצ׳י בציפוי פצפוצי אורז.", "crispy_meal.png"),
                new MenuItem("ארוחת המבורגר עם בירה", 95.0, "ארוחות", "כ-200 גר׳ של 100% קציצת אנטריקוט עם צ'יפס ובירה קרה.", "burger_beer.png"),
                new MenuItem("גולדן בורגר", 62.0, "המבורגרים", "כ-200 גר׳ קציצת אנטריקוט שמתבשלת ברוטב איולי גולדן.", "golden.png"),
                new MenuItem("המבורגר קלאסי", 62.0, "המבורגרים", "כ-200 גר׳ קציצת אנטריקוט עם מרכיבים לבחירה.", "classic.png"),
                new MenuItem("קריספי צ'יקן", 58.0, "המבורגרים", "פילה עוף קראנצ׳י בציפוי פצפוצי אורז.", "crispy.png"),
                new MenuItem("צ'יפס", 22.0, "תוספות", "צ'יפס תפוחי אדמה פריך.", "fries.png"),
                new MenuItem("טבעות בצל", 24.0, "תוספות", "10 יחידות טבעות בצל.", "onion_rings.png"),
                new MenuItem("כדורי פירה", 24.0, "תוספות", "10 יחידות כדורי פירה.", "mash_balls.png"),
                new MenuItem("קורנצ'יקן", 48.0, "תוספות", "8 יח׳ פילה עוף בציפוי קורנפלקס.", "corn_chicken.png"),
                new MenuItem("כנפיים", 34.0, "תוספות", "6 יח׳ כנפיים ברוטב הבית.", "wings.png"),
                new MenuItem("קוקה קולה", 12.0, "שתייה", "פחית 330 מ\"ל.", "cola.png"),
                new MenuItem("קולה זירו", 12.0, "שתייה", "פחית 330 מ\"ל.", "cola_zero.png"),
                new MenuItem("ספרייט", 12.0, "שתייה", "פחית 330 מ\"ל.", "sprite.png"),
                new MenuItem("ספרייט זירו", 12.0, "שתייה", "פחית 330 מ\"ל.", "sprite_zero.png"),
                new MenuItem("פאנטה אורנג'", 12.0, "שתייה", "פחית 330 מ\"ל.", "fanta.png"),
                new MenuItem("פיוז טי אפרסק", 12.0, "שתייה", "פחית 330 מ\"ל.", "fuze_tea.png"),
                new MenuItem("מים נביעות", 10.0, "שתייה", "בקבוק חצי ליטר.", "water.png"),
                new MenuItem("סודה", 10.0, "שתייה", "פחית 330 מ\"ל.", "soda.png"),
                new MenuItem("בירה קרלסברג", 18.0, "שתייה", "בקבוק 330 מ\"ל.", "beer.png")
        };
    }
}
