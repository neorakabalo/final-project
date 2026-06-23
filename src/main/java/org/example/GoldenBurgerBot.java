package org.example;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.input.MouseButton;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class GoldenBurgerBot extends Application {

    private VBox chatBox;
    private ScrollPane chatScroll;

    private TextField inputField;
    private Button sendButton;
    private FlowPane topPane;
    private HBox bottomBox;
    private Button langButton;

    private MenuItem[] menu;
    private double discount = 0.0;
    private OrderLinkedList orderList = new OrderLinkedList();

    private int chatState = 0;
    private String orderType = "";
    private String customerName = "";
    private String customerPhone = "";
    private String customerAddress = "";

    private boolean isEnglish = false;

    private final String GOLD = "#FFD700";
    private final String DARK_BG = "#1A1A1A";
    private final String CARD_BG = "#2B2B2B";

    public static void main(String[] args) { launch(args); }

    private void createCustomCss() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("style.css", false))) {
            writer.println(".scroll-pane { -fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0; }");
            writer.println(".scroll-pane .viewport { -fx-background-color: transparent; }");
            writer.println(".scroll-bar:vertical { -fx-background-color: rgba(0,0,0,0.3); -fx-pref-width: 8px; }");
            writer.println(".scroll-bar:vertical .thumb { -fx-background-color: " + GOLD + "; -fx-background-radius: 4px; }");
            writer.println(".scroll-bar:horizontal { -fx-pref-height: 0px; }");
            writer.println(".list-view { -fx-background-color: transparent; -fx-control-inner-background: #2B2B2B; }");
            writer.println(".list-cell { -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px; -fx-border-color: #444; -fx-border-width: 0 0 1 0; }");
            writer.println(".list-cell:empty { -fx-background-color: transparent; -fx-border-width: 0; }");
        } catch (IOException e) {}
    }

    private void loadMenu() {
        menu = new MenuItem[21];
        if (isEnglish) {
            menu[0] = new MenuItem("Golden Burger Meal", 85.0, "Meals", "200g 100% Entrecote patty in our secret Golden Aioli.", "golden_meal.png");
            menu[1] = new MenuItem("Classic Meal", 85.0, "Meals", "200g 100% Entrecote patty with toppings of your choice.", "classic_meal.png");
            menu[2] = new MenuItem("Crispy Chicken Meal", 85.0, "Meals", "Crunchy chicken fillet coated in rice crispies.", "crispy_meal.png");
            menu[3] = new MenuItem("Burger & Beer Meal", 95.0, "Meals", "200g Entrecote patty with fries and cold Carlsberg.", "burger_beer.png");
            menu[4] = new MenuItem("Golden Burger", 62.0, "Burgers", "200g Entrecote patty in our secret Golden Aioli.", "golden.png");
            menu[5] = new MenuItem("Classic Burger", 62.0, "Burgers", "200g Entrecote patty with toppings of your choice.", "classic.png");
            menu[6] = new MenuItem("Crispy Chicken", 58.0, "Burgers", "Crunchy chicken fillet coated in rice crispies.", "crispy.png");
            menu[7] = new MenuItem("Fries", 22.0, "Sides", "Crispy potato fries.", "fries.png");
            menu[8] = new MenuItem("Onion Rings", 24.0, "Sides", "10 pieces of onion rings.", "onion_rings.png");
            menu[9] = new MenuItem("Mashed Potato Balls", 24.0, "Sides", "10 pieces of potato balls.", "mash_balls.png");
            menu[10] = new MenuItem("Corn Chicken", 48.0, "Sides", "8 pcs chicken fillet in cornflakes coating.", "corn_chicken.png");
            menu[11] = new MenuItem("Wings", 34.0, "Sides", "6 pcs chicken wings in house sauce.", "wings.png");
            menu[12] = new MenuItem("Coca Cola", 12.0, "Drinks", "330ml Can.", "cola.png");
            menu[13] = new MenuItem("Cola Zero", 12.0, "Drinks", "330ml Can.", "cola_zero.png");
            menu[14] = new MenuItem("Sprite", 12.0, "Drinks", "330ml Can.", "sprite.png");
            menu[15] = new MenuItem("Sprite Zero", 12.0, "Drinks", "330ml Can.", "sprite_zero.png");
            menu[16] = new MenuItem("Fanta Orange", 12.0, "Drinks", "330ml Can.", "fanta.png");
            menu[17] = new MenuItem("Peach Fuze Tea", 12.0, "Drinks", "330ml Can.", "fuze_tea.png");
            menu[18] = new MenuItem("Mineral Water", 10.0, "Drinks", "500ml Bottle.", "water.png");
            menu[19] = new MenuItem("Soda", 10.0, "Drinks", "330ml Can.", "soda.png");
            menu[20] = new MenuItem("Carlsberg Beer", 18.0, "Drinks", "330ml Bottle.", "beer.png");
        } else {
            menu[0] = new MenuItem("ארוחת גולדן בורגר", 85.0, "ארוחות", "כ-200 גר׳ של 100% קציצת אנטריקוט שמתבשלת ברוטב איולי גולדן.", "golden_meal.png");
            menu[1] = new MenuItem("ארוחה קלאסית", 85.0, "ארוחות", "כ-200 גר׳ של 100% קציצת אנטריקוט עם מרכיבים לבחירה.", "classic_meal.png");
            menu[2] = new MenuItem("ארוחת קריספי צ'יקן", 85.0, "ארוחות", "פילה עוף קראנצ׳י בציפוי פצפוצי אורז.", "crispy_meal.png");
            menu[3] = new MenuItem("ארוחת המבורגר עם בירה", 95.0, "ארוחות", "כ-200 גר׳ של 100% קציצת אנטריקוט עם צ'יפס ובירה קרה.", "burger_beer.png");
            menu[4] = new MenuItem("גולדן בורגר", 62.0, "המבורגרים", "כ-200 גר׳ קציצת אנטריקוט שמתבשלת ברוטב איולי גולדן.", "golden.png");
            menu[5] = new MenuItem("המבורגר קלאסי", 62.0, "המבורגרים", "כ-200 גר׳ קציצת אנטריקוט עם מרכיבים לבחירה.", "classic.png");
            menu[6] = new MenuItem("קריספי צ'יקן", 58.0, "המבורגרים", "פילה עוף קראנצ׳י בציפוי פצפוצי אורז.", "crispy.png");
            menu[7] = new MenuItem("צ'יפס", 22.0, "תוספות", "צ'יפס תפוחי אדמה פריך.", "fries.png");
            menu[8] = new MenuItem("טבעות בצל", 24.0, "תוספות", "10 יחידות טבעות בצל.", "onion_rings.png");
            menu[9] = new MenuItem("כדורי פירה", 24.0, "תוספות", "10 יחידות כדורי פירה.", "mash_balls.png");
            menu[10] = new MenuItem("קורנצ'יקן", 48.0, "תוספות", "8 יח׳ פילה עוף בציפוי קורנפלקס.", "corn_chicken.png");
            menu[11] = new MenuItem("כנפיים", 34.0, "תוספות", "6 יח׳ כנפיים ברוטב הבית.", "wings.png");
            menu[12] = new MenuItem("קוקה קולה", 12.0, "שתייה", "פחית 330 מ\"ל.", "cola.png");
            menu[13] = new MenuItem("קולה זירו", 12.0, "שתייה", "פחית 330 מ\"ל.", "cola_zero.png");
            menu[14] = new MenuItem("ספרייט", 12.0, "שתייה", "פחית 330 מ\"ל.", "sprite.png");
            menu[15] = new MenuItem("ספרייט זירו", 12.0, "שתייה", "פחית 330 מ\"ל.", "sprite_zero.png");
            menu[16] = new MenuItem("פאנטה אורנג'", 12.0, "שתייה", "פחית 330 מ\"ל.", "fanta.png");
            menu[17] = new MenuItem("פיוז טי אפרסק", 12.0, "שתייה", "פחית 330 מ\"ל.", "fuze_tea.png");
            menu[18] = new MenuItem("מים נביעות", 10.0, "שתייה", "בקבוק חצי ליטר.", "water.png");
            menu[19] = new MenuItem("סודה", 10.0, "שתייה", "פחית 330 מ\"ל.", "soda.png");
            menu[20] = new MenuItem("בירה קרלסברג", 18.0, "שתייה", "בקבוק 330 מ\"ל.", "beer.png");
        }
    }

    private void toggleLanguage() {
        isEnglish = !isEnglish;
        loadMenu();

        langButton.setText(isEnglish ? "🌐 עברית" : "🌐 English");
        inputField.setPromptText(isEnglish ? "Type here..." : "הקלידי כאן...");
        sendButton.setText(isEnglish ? "Send" : "שלח");

        chatBox.getChildren().clear();
        chatState = 0;
        orderList.clear();
        discount = 0.0;

        chatBox.setNodeOrientation(isEnglish ? NodeOrientation.LEFT_TO_RIGHT : NodeOrientation.RIGHT_TO_LEFT);
        chatScroll.setNodeOrientation(isEnglish ? NodeOrientation.LEFT_TO_RIGHT : NodeOrientation.RIGHT_TO_LEFT);

        showTypeSelection();
    }

    @Override
    public void start(Stage primaryStage) {
        createCustomCss();
        loadMenu();

        primaryStage.setTitle("Golden Burger | האתר הרשמי");

        ImageView watermark = new ImageView();
        try {
            File logoFile = new File("logo.png");
            if(logoFile.exists()) {
                Image logoImage = new Image(logoFile.toURI().toString());
                if (!logoImage.isError()) {
                    watermark.setImage(logoImage);
                    watermark.setOpacity(0.15);
                    watermark.setFitWidth(400);
                    watermark.setPreserveRatio(true);
                }
            }
        } catch (Exception e) {}

        chatBox = new VBox(15);
        chatBox.setPadding(new Insets(20));
        chatBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        chatBox.setStyle("-fx-background-color: transparent;");

        chatScroll = new ScrollPane(chatBox);
        chatScroll.setFitToWidth(true);
        chatScroll.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        chatScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        chatBox.heightProperty().addListener((obs, oldVal, newVal) -> chatScroll.setVvalue(1.0));

        inputField = new TextField();
        inputField.setDisable(true);
        inputField.setPromptText("הקלידי כאן...");
        inputField.setPrefHeight(45);
        inputField.setStyle("-fx-background-radius: 25; -fx-background-color: #2B2B2B; -fx-text-fill: white; -fx-padding: 0 15 0 15; -fx-font-size: 16px;");
        inputField.setOnAction(e -> processUserInput());
        HBox.setHgrow(inputField, Priority.ALWAYS);

        sendButton = new Button("שלח");
        sendButton.setDisable(true);
        sendButton.setPrefHeight(45);
        sendButton.setPrefWidth(90);
        sendButton.setStyle("-fx-background-color: " + GOLD + "; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: 25; -fx-font-size: 16px; -fx-cursor: hand;");
        sendButton.setOnAction(e -> processUserInput());

        bottomBox = new HBox(15, inputField, sendButton);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(15, 20, 15, 20));
        bottomBox.setStyle("-fx-background-color: #111111;");

        topPane = new FlowPane();
        topPane.setHgap(20);
        topPane.setVgap(20);
        topPane.setAlignment(Pos.CENTER);

        langButton = new Button("🌐 English");
        langButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #999; -fx-cursor: hand; -fx-font-size: 14px;");
        langButton.setOnAction(e -> toggleLanguage());

        HBox topBar = new HBox(langButton);
        topBar.setAlignment(Pos.TOP_LEFT);

        VBox topContainer = new VBox(10, topBar, topPane);
        topContainer.setAlignment(Pos.CENTER);
        topContainer.setPadding(new Insets(10, 20, 25, 20));
        topContainer.setStyle("-fx-background-color: rgba(26, 26, 26, 0.85); -fx-border-color: " + GOLD + "; -fx-border-width: 0 0 2 0;");

        BorderPane mainContent = new BorderPane();
        mainContent.setTop(topContainer);
        mainContent.setCenter(chatScroll);
        mainContent.setBottom(bottomBox);

        StackPane root = new StackPane();
        root.getChildren().addAll(watermark, mainContent);
        root.setStyle("-fx-background-color: radial-gradient(center 50% 10%, radius 90%, #3a3a3a 0%, #000000 100%);");

        showTypeSelection();

        Scene scene = new Scene(root, 750, 850);
        scene.getStylesheets().add("file:style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void appendMessage(String msg, boolean isBot) {
        Label bubble = new Label(msg);
        bubble.setWrapText(true);
        bubble.setMaxWidth(450);
        bubble.setPadding(new Insets(12, 18, 12, 18));
        bubble.setFont(Font.font("Segoe UI", 16));

        HBox row = new HBox();
        if (isBot) {
            bubble.setStyle("-fx-background-color: rgba(255, 215, 0, 0.15); -fx-text-fill: #FFD700; -fx-background-radius: 20; -fx-border-color: rgba(255, 215, 0, 0.4); -fx-border-radius: 20;");
            row.setAlignment(isEnglish ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
        } else {
            bubble.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0, 0, 2);");
            row.setAlignment(isEnglish ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        }

        row.getChildren().add(bubble);
        chatBox.getChildren().add(row);
    }

    private void appendMessage(String msg) {
        appendMessage(msg, true);
    }

    private void showTypeSelection() {
        topPane.getChildren().clear();
        Button del = new Button(isEnglish ? "Delivery" : "משלוח");
        del.setPrefSize(180, 50);
        del.setStyle("-fx-background-color: transparent; -fx-text-fill: " + GOLD + "; -fx-border-color: " + GOLD + "; -fx-border-width: 2; -fx-border-radius: 25; -fx-font-weight: bold; -fx-font-size: 16px; -fx-cursor: hand;");
        del.setOnAction(e -> handleOrderTypeSelection(isEnglish ? "Delivery" : "משלוח"));

        Button pick = new Button(isEnglish ? "Pickup" : "איסוף עצמי");
        pick.setPrefSize(180, 50);
        pick.setStyle("-fx-background-color: transparent; -fx-text-fill: " + GOLD + "; -fx-border-color: " + GOLD + "; -fx-border-width: 2; -fx-border-radius: 25; -fx-font-weight: bold; -fx-font-size: 16px; -fx-cursor: hand;");
        pick.setOnAction(e -> handleOrderTypeSelection(isEnglish ? "Pickup" : "איסוף עצמי"));

        topPane.getChildren().addAll(del, pick);

        if (chatBox.getChildren().isEmpty()) {
            appendMessage(isEnglish ? "Welcome to Golden Burger! How would you like your order?" : "ברוך הבא לגולדן בורגר! איך תרצה את ההזמנה?");
        }
    }

    private void handleOrderTypeSelection(String type) {
        orderType = type;
        topPane.getChildren().clear();
        inputField.setDisable(false);
        sendButton.setDisable(false);
        appendMessage(isEnglish ? "You chose " + type + ". What is your name?" : "בחרת ב" + type + ". מה השם שלך?");
        chatState = 1;
        inputField.requestFocus();
    }

    private void processUserInput() {
        String input = inputField.getText().trim();
        if (input.isEmpty()) return;

        if (input.equals("ADMIN_123")) {
            inputField.clear();
            openAdminDashboard();
            return;
        }

        appendMessage(input, false);
        inputField.clear();

        if (chatState == 1) {
            customerName = input;
            appendMessage(isEnglish ? "Nice to meet you, " + customerName + "! What is your phone number?" : "נעים מאוד " + customerName + "! מה הטלפון?");
            chatState = 2;
        } else if (chatState == 2) {
            boolean isAllDigits = true;
            for (int i = 0; i < input.length(); i++) {
                if (!Character.isDigit(input.charAt(i))) { isAllDigits = false; break; }
            }
            if (input.length() == 10 && isAllDigits) {
                customerPhone = input;
                appendMessage(isEnglish ? "A code has been sent to your phone. Enter it here.\nYour code is: 1234" : "נשלח לך קוד לנייד, הזיני אותו.\nהקוד שלך הוא: 1234");
                chatState = 3;
            } else {
                appendMessage(isEnglish ? "Phone number must be exactly 10 digits. Try again:" : "מספר הטלפון חייב להכיל בדיוק 10 ספרות (ללא אותיות או רווחים). אנא נסי שוב:");
            }
        } else if (chatState == 3) {
            if (input.equals("1234")) {
                if(orderType.equals("משלוח") || orderType.equals("Delivery")) {
                    appendMessage(isEnglish ? "Delivery address?" : "כתובת למשלוח?");
                    chatState = 4;
                } else {
                    showCouponAnnouncement();
                    showMenuCategories();
                    chatState = 5;
                }
            } else {
                appendMessage(isEnglish ? "Invalid code. Please enter 1234:" : "קוד אימות שגוי. אנא הקישי 1234:");
            }
        } else if (chatState == 4) {
            boolean isJerusalem = false;
            try (BufferedReader br = new BufferedReader(new FileReader("streets.txt"))) {
                String line;
                while ((line = br.readLine()) != null) if (input.contains(line.trim())) isJerusalem = true;
            } catch (IOException e) {}

            if (isJerusalem) {
                customerAddress = input;
                showCouponAnnouncement();
                showMenuCategories();
                chatState = 5;
            } else appendMessage(isEnglish ? "Not in our delivery area!" : "לא באזור שלנו!");
        }

        inputField.requestFocus();
    }

    private void openAdminDashboard() {
        Stage adminStage = new Stage();
        adminStage.setTitle(isEnglish ? "Admin Dashboard" : "מערכת ניהול - גולדן בורגר");

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30; -fx-background-color: " + DARK_BG + ";");

        Label title = new Label(isEnglish ? "Golden Burger Admin" : "ניהול גולדן בורגר");
        title.setStyle("-fx-text-fill: " + GOLD + "; -fx-font-size: 28px; -fx-font-weight: bold;");

        int orderCount = 0;
        double totalRevenue = 0;
        ListView<String> ordersListView = new ListView<>();
        ordersListView.setStyle("-fx-background-color: #2B2B2B;");

        try (BufferedReader br = new BufferedReader(new FileReader("orders.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    orderCount++;
                    totalRevenue += Double.parseDouble(parts[4]);
                    String orderTxt = isEnglish ?
                            "Order #" + parts[0] + " | " + parts[1] + " | Phone: " + parts[2] + " | " + parts[3] + " | Total: ₪" + String.format("%.2f", Double.parseDouble(parts[4])) :
                            "הזמנה #" + parts[0] + " | " + parts[1] + " | טלפון: " + parts[2] + " | " + parts[3] + " | סה\"כ: ₪" + String.format("%.2f", Double.parseDouble(parts[4]));
                    ordersListView.getItems().add(orderTxt);
                }
            }
        } catch (IOException e) {
            ordersListView.getItems().add(isEnglish ? "No previous orders." : "אין הזמנות קודמות במערכת.");
        }

        String statsTxt = isEnglish ?
                "Total Orders: " + orderCount + "   |   Revenue: ₪" + String.format("%.2f", totalRevenue) :
                "סה\"כ הזמנות: " + orderCount + "   |   סך הכנסות: ₪" + String.format("%.2f", totalRevenue);

        Label stats = new Label(statsTxt);
        stats.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 20px; -fx-font-weight: bold;");

        root.getChildren().addAll(title, stats, ordersListView);

        Scene scene = new Scene(root, 600, 500);
        scene.getStylesheets().add("file:style.css");
        adminStage.setScene(scene);
        adminStage.show();
    }

    private void showCouponAnnouncement() {
        if (isEnglish) {
            appendMessage("✨ Special Offers in Cart! ✨\n\n• 10% off first order with code: GOLDEN10\n• Support special - 20% off with code: VIP\n\n(Enter the code at checkout)");
        } else {
            appendMessage("✨ הטבות מיוחדות שמחכות לך בסל הקניות! ✨\n\n• 10% הנחה להזמנה ראשונה באתר בקוד: GOLDEN10\n• בגלל המצב והמלחמה יצאנו במבצע תמיכה מיוחד - 20% הנחה בקוד: VIP\n\n(תוכלו להזין את הקוד שתבחרו בשלב התשלום בתוך חלון הסל)");
        }
    }

    private void showMenuCategories() {
        topPane.getChildren().clear();
        String[] cats = isEnglish ? new String[]{"Meals", "Burgers", "Sides", "Drinks"} : new String[]{"ארוחות", "המבורגרים", "תוספות", "שתייה"};

        for (String c : cats) {
            Button b = new Button(c);
            b.setPrefSize(120, 45);
            b.setStyle("-fx-background-color: " + CARD_BG + "; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
            b.setOnAction(e -> showItemsForCategory(c));
            topPane.getChildren().add(b);
        }

        double currentTotal = Math.round((orderList.calculateTotal() - (orderList.calculateTotal() * discount)) * 100.0) / 100.0;
        String cartTxt = isEnglish ? "🛒 Cart (" + orderList.size() + ") - ₪" + String.format("%.2f", currentTotal) : "🛒 סל (" + orderList.size() + ") - " + String.format("%.2f", currentTotal) + "₪";

        Button cart = new Button(cartTxt);
        cart.setPrefHeight(45);
        cart.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-background-radius: 10; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
        cart.setOnAction(e -> showCart());
        topPane.getChildren().add(cart);
    }

    private void showItemsForCategory(String cat) {
        topPane.getChildren().clear();
        for (MenuItem item : menu) {
            if (item.category.equals(cat)) {
                Button btn = new Button(item.name + "\n\n" + item.description + "\n\n₪" + String.format("%.2f", item.price));
                btn.setPrefSize(250, 240);
                btn.setWrapText(true);

                try {
                    if (item.imagePath != null && !item.imagePath.isEmpty()) {
                        File imgFile = new File(item.imagePath);
                        if (imgFile.exists()) {
                            Image img = new Image(imgFile.toURI().toString());
                            if (!img.isError()) {
                                ImageView icon = new ImageView(img);
                                icon.setFitHeight(90);
                                icon.setPreserveRatio(true);
                                btn.setGraphic(icon);
                                btn.setContentDisplay(ContentDisplay.TOP);
                                btn.setGraphicTextGap(10);
                            }
                        }
                    }
                } catch (Exception ex) {}

                String normalStyle = "-fx-background-color: " + CARD_BG + "; -fx-text-fill: white; -fx-text-alignment: center; -fx-background-radius: 15; -fx-border-color: " + GOLD + "; -fx-border-radius: 15; -fx-border-width: 1px; -fx-cursor: hand; -fx-font-size: 13px;";
                String hoverStyle = "-fx-background-color: #383838; -fx-text-fill: white; -fx-text-alignment: center; -fx-background-radius: 15; -fx-border-color: " + GOLD + "; -fx-border-radius: 15; -fx-border-width: 2px; -fx-cursor: hand; -fx-font-size: 13px; -fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.3), 15, 0, 0, 0);";

                btn.setStyle(normalStyle);
                btn.setOnMouseEntered(e -> { btn.setScaleX(1.03); btn.setScaleY(1.03); btn.setStyle(hoverStyle); });
                btn.setOnMouseExited(e -> { btn.setScaleX(1.0); btn.setScaleY(1.0); btn.setStyle(normalStyle); });

                // === מנגנון לחיצה ארוכה (סנכרון בין החזקה ללחיצה רגילה) ===
                final boolean[] isLongPress = {false};
                PauseTransition pause = new PauseTransition(Duration.millis(700)); // 0.7 שניות החזקה

                pause.setOnFinished(event -> {
                    isLongPress[0] = true;
                    showLargeImagePopup(item); // פותח את הפופ-אפ של התמונה הגדולה
                });

                btn.setOnMousePressed(me -> {
                    if (me.getButton() == MouseButton.PRIMARY) {
                        isLongPress[0] = false;
                        pause.playFromStart(); // מתחיל לספור ברגע שהעכבר נלחץ
                    }
                });

                btn.setOnMouseReleased(me -> pause.stop()); // עוצר את הספירה אם שחררו מוקדם

                btn.setOnAction(e -> {
                    if (isLongPress[0]) {
                        isLongPress[0] = false; // מאפס את הדגל
                        return; // מבטל את הפתיחה של חלון ההזמנה הרגיל
                    }
                    openCustomizationWindow(item); // לחיצה רגילה - פותח חלון התאמה
                });

                topPane.getChildren().add(btn);
            }
        }
        Button back = new Button(isEnglish ? "Back to Menu" : "חזרה לתפריט");
        back.setPrefSize(140, 45);
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: #999999; -fx-border-color: #999999; -fx-border-radius: 10; -fx-cursor: hand;");
        back.setOnAction(e -> showMenuCategories());
        topPane.getChildren().add(back);
    }

    // === חלון פופ-אפ להצגת תמונה מוגדלת בלחיצה ארוכה ===
    private void showLargeImagePopup(MenuItem item) {
        if (item.imagePath == null || item.imagePath.isEmpty()) return;
        File imgFile = new File(item.imagePath);
        if (!imgFile.exists()) return;

        Stage popupStage = new Stage();
        popupStage.setTitle(isEnglish ? "Product View" : "הצגת מנה מוגדלת");

        ImageView largeView = new ImageView(new Image(imgFile.toURI().toString()));
        largeView.setFitWidth(400); // רוחב התמונה המוגדלת
        largeView.setPreserveRatio(true);

        Label titleLabel = new Label(item.name);
        titleLabel.setStyle("-fx-text-fill: " + GOLD + "; -fx-font-size: 22px; -fx-font-weight: bold;");

        Label descLabel = new Label(item.description);
        descLabel.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-text-alignment: center;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(360);

        Button closeBtn = new Button(isEnglish ? "Close" : "סגור חלון");
        closeBtn.setPrefSize(120, 35);
        closeBtn.setStyle("-fx-background-color: " + GOLD + "; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> popupStage.close());

        VBox layout = new VBox(20, titleLabel, largeView, descLabel, closeBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 25; -fx-background-color: " + DARK_BG + "; -fx-border-color: " + GOLD + "; -fx-border-width: 2px; -fx-border-radius: 10; -fx-background-radius: 10;");

        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), layout);
        fadeIn.setFromValue(0.0); fadeIn.setToValue(1.0); fadeIn.play();

        Scene scene = new Scene(layout);
        popupStage.setScene(scene);
        popupStage.show();
    }

    private void openCustomizationWindow(MenuItem item) {
        if (item.category.equals("שתייה") || item.category.equals("תוספות") || item.category.equals("Sides") || item.category.equals("Drinks")) {
            orderList.add(item.name, item.price);
            double currentTotal = Math.round((orderList.calculateTotal() - (orderList.calculateTotal() * discount)) * 100.0) / 100.0;
            appendMessage(isEnglish ? "Added: " + item.name + " | Total: ₪" + String.format("%.2f", currentTotal) : "הוספת: " + item.name + " | סה\"כ בינתיים: " + String.format("%.2f", currentTotal) + "₪");
            showMenuCategories();
            return;
        }

        Stage customStage = new Stage();
        customStage.setTitle(isEnglish ? "Customize - " + item.name : "התאמה אישית - " + item.name);

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30; -fx-background-color: " + DARK_BG + ";");

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
        fadeIn.setFromValue(0.0); fadeIn.setToValue(1.0); fadeIn.play();

        Label titleLabel = new Label(item.name);
        titleLabel.setStyle("-fx-text-fill: " + GOLD + "; -fx-font-size: 22px; -fx-font-weight: bold;");
        root.getChildren().add(titleLabel);

        boolean isGolden = item.name.contains("גולדן") || item.name.contains("Golden");
        boolean isCrispy = item.name.contains("קריספי") || item.name.contains("Crispy");

        CheckBox noTomato = new CheckBox(isEnglish ? "No Tomato" : "בלי עגבניה");
        CheckBox noLettuce = new CheckBox(isEnglish ? "No Lettuce" : "בלי חסה");
        CheckBox noOnion = new CheckBox(isEnglish ? "No Onion" : "בלי בצל");
        CheckBox noPickles = new CheckBox(isEnglish ? "No Pickles" : "בלי חמוצים");
        String checkStyle = "-fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5;";
        noTomato.setStyle(checkStyle); noLettuce.setStyle(checkStyle); noOnion.setStyle(checkStyle); noPickles.setStyle(checkStyle);

        ToggleGroup donenessGroup = new ToggleGroup();
        RadioButton btnM = new RadioButton("M"); RadioButton btnMW = new RadioButton("MW"); RadioButton btnWD = new RadioButton("WD");
        btnM.setStyle(checkStyle); btnMW.setStyle(checkStyle); btnWD.setStyle(checkStyle);
        btnM.setToggleGroup(donenessGroup); btnMW.setToggleGroup(donenessGroup); btnWD.setToggleGroup(donenessGroup);
        btnMW.setSelected(true);

        if (isGolden) {
            Label goldenNote = new Label(isEnglish ? "🍔 Signature Dish:\nServed with secret sauce, no veggies." : "🍔 מנת הדגל שלנו:\nמוגשת עם הרוטב הסודי וללא ירקות.");
            goldenNote.setStyle("-fx-text-fill: #CCCCCC; -fx-font-size: 16px; -fx-text-alignment: center;");
            root.getChildren().add(goldenNote);
        } else {
            Label vegLabel = new Label(isEnglish ? "Veggies:" : "התאמת ירקות:");
            vegLabel.setStyle("-fx-text-fill: " + GOLD + "; -fx-font-weight: bold; -fx-font-size: 16px;");
            VBox vegBox = new VBox(5, vegLabel, noTomato, noLettuce, noOnion, noPickles);
            vegBox.setAlignment(isEnglish ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
            root.getChildren().add(vegBox);
        }

        if (!isCrispy) {
            Label doneLabel = new Label(isEnglish ? "Meat Doneness:" : "מידת עשייה קציצת אנטריקוט:");
            doneLabel.setStyle("-fx-text-fill: " + GOLD + "; -fx-font-weight: bold; -fx-font-size: 16px;");
            HBox doneBox = new HBox(15, btnM, btnMW, btnWD);
            doneBox.setAlignment(Pos.CENTER);
            root.getChildren().addAll(doneLabel, doneBox);
        } else {
            Label chickenNote = new Label(isEnglish ? "🍗 Chicken - Fried & Crunchy!" : "🍗 מנת עוף - מטוגן וקראנצ'י!");
            chickenNote.setStyle("-fx-text-fill: #CCCCCC; -fx-font-size: 14px;");
            root.getChildren().add(chickenNote);
        }

        Button addBtn = new Button((isEnglish ? "Add to Cart - ₪" : "הוסף לסל - ") + String.format("%.2f", item.price) + (isEnglish ? "" : "₪"));
        addBtn.setPrefSize(180, 45);
        addBtn.setStyle("-fx-background-color: " + GOLD + "; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: 20; -fx-font-size: 14px; -fx-cursor: hand;");

        addBtn.setOnAction(e -> {
            String modifications = "";
            if (!isCrispy) {
                RadioButton selectedDoneness = (RadioButton) donenessGroup.getSelectedToggle();
                modifications += " [" + selectedDoneness.getText() + "]";
            }
            if (!isGolden) {
                if (noTomato.isSelected()) modifications += isEnglish ? " (No Tomato)" : " (בלי עגבניה)";
                if (noLettuce.isSelected()) modifications += isEnglish ? " (No Lettuce)" : " (בלי חסה)";
                if (noOnion.isSelected()) modifications += isEnglish ? " (No Onion)" : " (בלי בצל)";
                if (noPickles.isSelected()) modifications += isEnglish ? " (No Pickles)" : " (בלי חמוצים)";
            }

            String orderString = item.name + modifications;
            orderList.add(orderString, item.price);

            double currentTotal = Math.round((orderList.calculateTotal() - (orderList.calculateTotal() * discount)) * 100.0) / 100.0;
            appendMessage((isEnglish ? "Added: " : "הוספת: ") + orderString + (isEnglish ? " | Total: ₪" : " | סה\"כ בינתיים: ") + String.format("%.2f", currentTotal) + (isEnglish ? "" : "₪"));

            customStage.close();
            showMenuCategories();
        });

        root.getChildren().add(addBtn);
        Scene scene = new Scene(root, 380, 480);
        customStage.setScene(scene);
        customStage.show();
    }

    private void showCart() {
        Stage cartStage = new Stage();
        cartStage.setTitle(isEnglish ? "Cart - Golden Burger" : "סל קניות - גולדן בורגר");

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30; -fx-background-color: " + DARK_BG + ";");

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
        fadeIn.setFromValue(0.0); fadeIn.setToValue(1.0); fadeIn.play();

        Label title = new Label(isEnglish ? "My Order" : "ההזמנה שלי");
        title.setStyle("-fx-text-fill: " + GOLD + "; -fx-font-size: 24px; -fx-font-weight: bold;");
        root.getChildren().add(title);

        if (orderList.isEmpty()) {
            Label emptyLabel = new Label(isEnglish ? "Cart is empty 🍔" : "הסל שלך ריק 🍔");
            emptyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
            root.getChildren().add(emptyLabel);
        } else {
            for (String item : orderList.getItemsAsList()) {
                HBox itemRow = new HBox(10);
                itemRow.setAlignment(Pos.CENTER);
                Label itemLabel = new Label("• " + item);
                itemLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

                Button removeBtn = new Button("❌");
                removeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #FF5555; -fx-cursor: hand; -fx-font-size: 12px;");
                removeBtn.setOnAction(e -> {
                    orderList.removeExact(item);
                    if (orderList.isEmpty()) discount = 0.0;
                    cartStage.close(); showCart(); showMenuCategories();
                });

                itemRow.getChildren().addAll(itemLabel, removeBtn);
                root.getChildren().add(itemRow);
            }
        }

        HBox couponBox = new HBox(10);
        couponBox.setAlignment(Pos.CENTER);

        TextField couponInput = new TextField();
        couponInput.setPromptText(isEnglish ? "Coupon code..." : "קוד קופון...");
        couponInput.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-border-color: " + GOLD + "; -fx-border-radius: 5;");

        Button applyBtn = new Button(isEnglish ? "Apply" : "הפעל");
        applyBtn.setStyle("-fx-background-color: " + GOLD + "; -fx-text-fill: black; -fx-font-weight: bold; -fx-cursor: hand;");

        Label msgLabel = new Label();
        Label totalLabel = new Label();
        totalLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 20px; -fx-padding: 10 0 20 0;");

        Runnable updateTotalDisplay = () -> {
            double rawTotal = orderList.calculateTotal();
            double finalPrice = Math.round((rawTotal - (rawTotal * discount)) * 100.0) / 100.0;
            if (discount > 0 && !orderList.isEmpty()) {
                totalLabel.setText((isEnglish ? "Total: ₪" : "סה\"כ לתשלום: ") + String.format("%.2f", finalPrice) + (isEnglish ? " (Discounted)" : "₪ (אחרי הנחה)"));
            } else {
                totalLabel.setText((isEnglish ? "Total: ₪" : "סה\"כ לתשלום: ") + String.format("%.2f", finalPrice) + (isEnglish ? "" : "₪"));
            }
        };
        updateTotalDisplay.run();

        applyBtn.setOnAction(e -> {
            if (orderList.isEmpty()) return;
            String code = couponInput.getText().trim().toUpperCase();
            if (code.equals("GOLDEN10")) {
                discount = 0.10;
                msgLabel.setText(isEnglish ? "Coupon applied! 10% off." : "קופון הופעל! 10% הנחה.");
                msgLabel.setStyle("-fx-text-fill: #4CAF50;");
            } else if (code.equals("VIP")) {
                discount = 0.20;
                msgLabel.setText(isEnglish ? "VIP Support applied! 20% off." : "מבצע תמיכה הופעל! 20% הנחה.");
                msgLabel.setStyle("-fx-text-fill: #4CAF50;");
            } else {
                discount = 0.0;
                msgLabel.setText(isEnglish ? "Invalid coupon." : "קופון לא חוקי או פג תוקף.");
                msgLabel.setStyle("-fx-text-fill: #FF5555;");
            }
            updateTotalDisplay.run();
            showMenuCategories();
        });

        if (!orderList.isEmpty()) {
            couponBox.getChildren().addAll(couponInput, applyBtn);
            root.getChildren().addAll(couponBox, msgLabel);
        }

        root.getChildren().add(totalLabel);

        HBox actionButtonsBox = new HBox(15);
        actionButtonsBox.setAlignment(Pos.CENTER);

        Button clearBtn = new Button(isEnglish ? "🗑️ Clear Cart" : "🗑️ נקה סל");
        clearBtn.setStyle("-fx-background-color: #FF5555; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand;");
        clearBtn.setOnAction(e -> {
            orderList.clear(); discount = 0.0; cartStage.close(); showCart(); showMenuCategories();
        });

        Button finishBtn = new Button(isEnglish ? "Checkout" : "תשלום וסיום");
        finishBtn.setPrefSize(160, 45);
        finishBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 25; -fx-cursor: hand;");

        if (orderList.isEmpty()) {
            finishBtn.setDisable(true); actionButtonsBox.getChildren().add(finishBtn);
        } else {
            actionButtonsBox.getChildren().addAll(clearBtn, finishBtn);
        }

        finishBtn.setOnAction(e -> {
            int orderId = 1000 + new Random().nextInt(9000);
            finishOrder(orderId);
            cartStage.close();
        });

        root.getChildren().add(actionButtonsBox);
        cartStage.setScene(new Scene(root, 450, 600));
        cartStage.show();
    }

    private void finishOrder(int orderId) {
        double rawTotal = orderList.calculateTotal();
        double finalPrice = Math.round((rawTotal - (rawTotal * discount)) * 100.0) / 100.0;

        try (PrintWriter csvWriter = new PrintWriter(new FileWriter("orders.csv", true))) {
            csvWriter.println(orderId + "," + customerName + "," + customerPhone + "," + orderType + "," + finalPrice);
        } catch (IOException e) {}

        appendMessage("--------------------------------");
        appendMessage(isEnglish ? "Order received! Order ID: #" + orderId : "ההזמנה התקבלה בהצלחה! מספר הזמנה: #" + orderId);
        appendMessage((isEnglish ? "Total paid: ₪" : "סה\"כ לתשלום: ") + String.format("%.2f", finalPrice) + (isEnglish ? "" : " ש\"ח."));

        if (orderType.equals("משלוח") || orderType.equals("Delivery")) {
            appendMessage(isEnglish ? "Prep time: ~15 mins.\nEstimated delivery: up to 60 mins.\nCourier will call: " + customerPhone : "זמן הכנה: כ-15 דקות.\nזמן הגעה משוער למשלוח: עד 60 דקות.\nהשליח ייצור איתך קשר בטלפון: " + customerPhone);
        } else {
            appendMessage(isEnglish ? "Prep time: 15-20 mins.\nYour order will be waiting hot at the branch!" : "זמן הכנה משוער: 15-20 דקות.\nההזמנה תחכה לך חמה ומוכנה לאיסוף בסניף!");
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter("receipt.txt", false))) {
            writer.print(buildReceiptString(orderId));
            appendMessage("--------------------------------");
            appendMessage(isEnglish ? "Receipt saved to receipt.txt" : "הופקה קבלה ונשמרה בקובץ: receipt.txt");
        } catch (IOException e) {}

        topPane.getChildren().clear();
        inputField.setDisable(true);
        sendButton.setDisable(true);
        showReceiptPopup(orderId);
    }

    private String buildReceiptString(int orderId) {
        String content = isEnglish ? "=== Golden Burger Receipt ===\n" : "=== קבלת הזמנה - גולדן בורגר ===\n";
        content += (isEnglish ? "Order ID: #" : "מספר הזמנה: #") + orderId + "\n" +
                (isEnglish ? "Customer Name: " : "שם הלקוח: ") + customerName + "\n" +
                (isEnglish ? "Phone: " : "טלפון: ") + customerPhone + "\n" +
                (isEnglish ? "Order Type: " : "סוג ההזמנה: ") + orderType + "\n";

        if (orderType.equals("משלוח") || orderType.equals("Delivery")) {
            content += (isEnglish ? "Address: " : "כתובת למשלוח: ") + customerAddress + "\n";
        }

        content += "--------------------------------\n";
        for (String item : orderList.getItemsAsList()) content += "- " + item + "\n";
        content += "--------------------------------\n";

        double rawTotal = orderList.calculateTotal();
        double finalPrice = Math.round((rawTotal - (rawTotal * discount)) * 100.0) / 100.0;

        if (discount > 0) {
            content += (isEnglish ? "Subtotal: ₪" : "סה\"כ לפני הנחה: ") + String.format("%.2f", rawTotal) + (isEnglish ? "\n" : " ש\"ח\n");
            content += (isEnglish ? "Discount: " : "הנחה: ") + (int)(discount * 100) + "%\n";
        }

        content += (isEnglish ? "Total: ₪" : "סה\"כ לתשלום: ") + String.format("%.2f", finalPrice) + (isEnglish ? "\n================================\n" : " ש\"ח\n================================\n");
        return content;
    }

    private void showReceiptPopup(int orderId) {
        Stage receiptStage = new Stage();
        receiptStage.setTitle(isEnglish ? "Digital Receipt" : "קבלה דיגיטלית - גולדן בורגר");

        TextArea receiptText = new TextArea(buildReceiptString(orderId));
        receiptText.setEditable(false);
        receiptText.setNodeOrientation(isEnglish ? NodeOrientation.LEFT_TO_RIGHT : NodeOrientation.RIGHT_TO_LEFT);
        receiptText.setFont(Font.font("Monospaced", 16));

        VBox root = new VBox(receiptText);
        root.setStyle("-fx-padding: 10; -fx-background-color: white;");
        VBox.setVgrow(receiptText, Priority.ALWAYS);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
        fadeIn.setFromValue(0.0); fadeIn.setToValue(1.0); fadeIn.play();

        receiptStage.setScene(new Scene(root, 380, 500));
        receiptStage.show();
    }

    private static class MenuItem {
        String name;
        double price;
        String category;
        String description;
        String imagePath;

        public MenuItem(String name, double price, String category, String description, String imagePath) {
            this.name = name;
            this.price = price;
            this.category = category;
            this.description = description;
            this.imagePath = imagePath;
        }
    }

    private static class OrderNode {
        String itemDetails;
        double price;
        OrderNode next;

        public OrderNode(String itemDetails, double price) {
            this.itemDetails = itemDetails;
            this.price = price;
            this.next = null;
        }
    }

    private static class OrderLinkedList {
        private OrderNode head;
        private int size = 0;

        public void add(String itemDetails, double price) {
            OrderNode newNode = new OrderNode(itemDetails, price);
            if (head == null) { head = newNode; }
            else {
                OrderNode current = head;
                while (current.next != null) { current = current.next; }
                current.next = newNode;
            }
            size++;
        }

        public void removeExact(String exactItem) {
            if (head == null) return;
            if (head.itemDetails.equals(exactItem)) {
                head = head.next;
                size--; return;
            }
            OrderNode current = head;
            while (current.next != null) {
                if (current.next.itemDetails.equals(exactItem)) {
                    current.next = current.next.next;
                    size--; return;
                }
                current = current.next;
            }
        }

        public void clear() { head = null; size = 0; }
        public boolean isEmpty() { return head == null; }
        public int size() { return size; }
        public double calculateTotal() { return calcRecursive(head); }

        private double calcRecursive(OrderNode node) {
            if (node == null) return 0;
            return node.price + calcRecursive(node.next);
        }

        public ArrayList<String> getItemsAsList() {
            ArrayList<String> list = new ArrayList<>();
            OrderNode current = head;
            while (current != null) {
                list.add(current.itemDetails);
                current = current.next;
            }
            return list;
        }
    }
}