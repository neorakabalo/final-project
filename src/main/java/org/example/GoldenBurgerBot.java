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
import java.time.YearMonth;
import java.util.Random;
import jakarta.mail.MessagingException;

public class GoldenBurgerBot extends Application {

    // תיקיית התמונות של הלוגו ושל פריטי התפריט.
    private static final String IMAGE_DIR = "src/assets/images/";

    // רכיבי הצ'אט: אזור ההודעות, הגלילה ושורת הקלט של המשתמש.
    private VBox chatBox;
    private ScrollPane chatScroll;

    private TextField inputField;
    private Button sendButton;
    private FlowPane topPane;
    private HBox bottomBox;
    private Button langButton;

    // התפריט בשפה הפעילה וההזמנה שנבנית לאורך השיחה.
    private MenuItem[] menu;
    private final OrderService orderList = new OrderService();

    /*
     * מצב השיחה קובע כיצד לפרש את הקלט הבא:
     * 0 - בחירת משלוח/איסוף, 1 - שם, 2 - טלפון, 3 - בחירת דרך אימות,
     * 4 - כתובת מייל, 5 - קוד אימות, 6 - כתובת משלוח, 7 - צפייה בתפריט,
     * 8 - מספר כרטיס, 9 - תוקף כרטיס, 10 - CVV.
     * כל שלב תקין מעדכן את הערך ומעביר את המשתמש לשלב הבא.
     */
    private int chatState = 0;
    // פרטי הלקוח נשמרים בהדרגה ומשמשים בסיום ההזמנה ובקבלה.
    private String orderType = "";
    private String customerName = "";
    private String customerPhone = "";
    private String customerEmail = "";
    private String customerAddress = "";
    private String paymentMethod = "";
    private String cardLastFour = "";
    private int pendingOrderId;

    // השירותים מפרידים בין ממשק הצ'אט לבין מייל, תפריט וקוד האימות הנוכחי.
    private final EmailService emailService = new EmailService();
    private final MenuService menuService = new MenuService();
    private final VerificationService verificationService = new VerificationService();

    // קובע באיזו שפה יוצגו התפריט, הכפתורים והודעות הבוט.
    private boolean isEnglish = false;

    private final String GOLD = "#FFD700";
    private final String DARK_BG = "#1A1A1A";
    private final String CARD_BG = "#2B2B2B";

    // נקודת הכניסה שמפעילה את מחזור החיים של JavaFX.
    public static void main(String[] args) { launch(args); }

    // יוצרת בעת האתחול את קובץ העיצוב שבו משתמשים חלונות האפליקציה.
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

    // נטענת בתחילת האפליקציה ובהחלפת שפה, ומחליפה את מערך התפריט המוצג.
    private void loadMenu() {
        menu = menuService.getMenu(isEnglish);
    }

    // נקראת מלחצן השפה; מאפסת את השיחה והסל ומתחילה שוב מבחירת סוג הזמנה.
    private void toggleLanguage() {
        isEnglish = !isEnglish;
        loadMenu();

        langButton.setText(isEnglish ? "🌐 עברית" : "🌐 English");
        inputField.setPromptText(isEnglish ? "Type here..." : "הקלידי כאן...");
        sendButton.setText(isEnglish ? "Send" : "שלח");

        chatBox.getChildren().clear();
        chatState = 0;
        verificationService.clear();
        orderList.clear();
        orderList.setDiscount(0.0);
        paymentMethod = "";
        cardLastFour = "";
        pendingOrderId = 0;

        chatBox.setNodeOrientation(isEnglish ? NodeOrientation.LEFT_TO_RIGHT : NodeOrientation.RIGHT_TO_LEFT);
        chatScroll.setNodeOrientation(isEnglish ? NodeOrientation.LEFT_TO_RIGHT : NodeOrientation.RIGHT_TO_LEFT);

        showTypeSelection();
    }

    @Override
    // JavaFX קוראת למתודה פעם אחת כדי לבנות ולהציג את הממשק הראשי.
    public void start(Stage primaryStage) {
        createCustomCss();
        loadMenu();

        primaryStage.setTitle("Golden Burger | האתר הרשמי");

        ImageView watermark = new ImageView();
        try {
            File logoFile = new File(IMAGE_DIR + "logo.png");
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

    // מוסיפה בועת הודעה לצ'אט וממקמת אותה לפי השולח והשפה הפעילה.
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

    // קיצור להוספת הודעה של הבוט; אינו מחזיר ערך אלא מעדכן את אזור הצ'אט.
    private void appendMessage(String msg) {
        appendMessage(msg, true);
    }

    // מציגה בתחילת הזרימה את הבחירה בין משלוח לאיסוף עצמי.
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

    // נקראת לאחר לחיצה על סוג הזמנה, שומרת אותו ומעבירה למצב 1: קליטת שם.
    private void handleOrderTypeSelection(String type) {
        orderType = type;
        topPane.getChildren().clear();
        inputField.setDisable(false);
        sendButton.setDisable(false);
        appendMessage(isEnglish ? "You chose " + type + ". What is your name?" : "בחרת ב" + type + ". מה השם שלך?");
        chatState = 1;
        inputField.requestFocus();
    }

    // נקראת בלחיצה על שליחה או Enter ומטפלת בקלט לפי מצב הצ'אט הנוכחי.
    private void processUserInput() {
        String input = inputField.getText().trim();
        if (input.isEmpty()) return;

        if (input.equals("ADMIN_123")) {
            inputField.clear();
            openAdminDashboard();
            return;
        }

        if (chatState == 8) {
            String digits = input.replaceAll("\\D", "");
            String maskedCard = digits.length() >= 4
                    ? "•••• " + digits.substring(digits.length() - 4)
                    : "••••";
            appendMessage(maskedCard, false);
        } else if (chatState == 10) {
            appendMessage("•••", false);
        } else {
            appendMessage(input, false);
        }
        inputField.clear();

        // מצבים 1–2 אוספים שם וטלפון; רק טלפון תקין מאפשר לעבור לבחירת אימות.
        if (chatState == 1) {
            customerName = input;
            appendMessage(isEnglish ? "Nice to meet you, " + customerName + "! What is your phone number?" : "נעים מאוד " + customerName + "! מה הטלפון?");
            chatState = 2;
        } else if (chatState == 2) {
            if (ValidationUtils.isValidPhone(input)) {
                customerPhone = input;
                appendMessage(isEnglish
                        ? "How would you like to receive the verification code? Enter 1 for Email or 2 for Phone:"
                        : "איך תרצה לקבל את קוד האימות? יש להזין 1 לאימייל או 2 לטלפון:");
                chatState = 3;
            } else {
                appendMessage(isEnglish ? "Phone number must be exactly 10 digits. Try again:" : "מספר הטלפון חייב להכיל בדיוק 10 ספרות (ללא אותיות או רווחים). אנא נסי שוב:");
            }
        } else if (chatState == 3) {
            // המשתמש בוחר אם לקבל קוד במייל או לראות אותו בטלפון במצב הדגמה.
            if (input.equals("1") || input.equalsIgnoreCase("email") || input.equals("אימייל")) {
                appendMessage(isEnglish ? "What is your email address?" : "מה כתובת האימייל שלך?");
                chatState = 4;
            } else if (input.equals("2") || input.equalsIgnoreCase("phone") || input.equals("טלפון")) {
                // אין חיבור SMS: נוצר קוד בן 6 ספרות והוא מוצג בצ'אט לצורכי הדגמה.
                String verificationCode = verificationService.generateCode();
                appendMessage(isEnglish
                        ? "SMS service is not connected yet. For demo purposes, your phone verification code is: " + verificationCode
                        : "שירות ה-SMS עדיין לא מחובר. לצורך הדגמה, קוד האימות לטלפון שלך הוא: " + verificationCode);
                appendMessage(isEnglish ? "Enter the verification code:" : "נא להזין את קוד האימות:");
                chatState = 5;
            } else {
                appendMessage(isEnglish
                        ? "Please enter 1 for Email or 2 for Phone:"
                        : "נא להזין 1 לאימייל או 2 לטלפון:");
            }
        } else if (chatState == 4) {
            // במסלול המייל נבדקת הכתובת, נוצר קוד ונשלח באמצעות שירות המייל.
            if (!ValidationUtils.isValidEmail(input)) {
                appendMessage(isEnglish ? "Please enter a valid email address:" : "נא להזין כתובת אימייל תקינה:");
            } else {
                customerEmail = input;
                String verificationCode = verificationService.generateCode();
                try {
                    emailService.sendVerificationCode(customerEmail, verificationCode);
                    appendMessage(isEnglish ? "A verification code has been sent to your email. Enter it here:" : "קוד אימות נשלח לאימייל שלך. נא להזין אותו כאן:");
                    chatState = 5;
                } catch (MessagingException | IllegalStateException e) {
                    verificationService.clear();
                    appendMessage(isEnglish ? "The verification email could not be sent. Check the mail configuration and enter your email again:" : "לא ניתן היה לשלוח את קוד האימות. יש לבדוק את הגדרות המייל ולהזין שוב את כתובת האימייל:");
                }
            }
        } else if (chatState == 5) {
            // הקלט מושווה לקוד שנשמר; הצלחה מנקה אותו וממשיכה להזמנה.
            if (verificationService.verify(input)) {
                verificationService.clear();
                continueAfterVerification();
            } else {
                appendMessage(isEnglish ? "Invalid verification code. Please try again:" : "קוד אימות שגוי. נא לנסות שוב:");
            }
        } else if (chatState == 6) {
            // במשלוח בלבד נבדק שהכתובת מכילה רחוב מרשימת אזור השירות.
            boolean isJerusalem = false;
            try (BufferedReader br = new BufferedReader(new FileReader("streets.txt"))) {
                String line;
                while ((line = br.readLine()) != null) if (input.contains(line.trim())) isJerusalem = true;
            } catch (IOException e) {}

            if (isJerusalem) {
                customerAddress = input;
                showCouponAnnouncement();
                showMenuCategories();
                chatState = 7;
            } else appendMessage(isEnglish ? "Not in our delivery area!" : "לא באזור שלנו!");
        } else if (chatState == 8) {
            if (input.matches("\\d{12,19}")) {
                cardLastFour = input.substring(input.length() - 4);
                appendMessage(isEnglish ? "Enter expiration date (MM/YY):" : "נא להזין תוקף כרטיס (MM/YY):");
                chatState = 9;
            } else {
                appendMessage(isEnglish
                        ? "Card number must contain 12-19 digits only. Try again:"
                        : "מספר הכרטיס חייב להכיל 12-19 ספרות בלבד. נא לנסות שוב:");
            }
        } else if (chatState == 9) {
            if (isValidExpirationDate(input)) {
                appendMessage(isEnglish ? "Enter CVV (3 or 4 digits):" : "נא להזין CVV (3 או 4 ספרות):");
                chatState = 10;
            } else {
                appendMessage(isEnglish
                        ? "Invalid or expired date. Please enter expiration date as MM/YY:"
                        : "תוקף לא תקין או שפג תוקף. נא להזין תוקף בפורמט MM/YY:");
            }
        } else if (chatState == 10) {
            if (input.matches("\\d{3,4}")) {
                finishOrder(pendingOrderId);
            } else {
                appendMessage(isEnglish
                        ? "CVV must contain 3 or 4 digits. Try again:"
                        : "CVV חייב להכיל 3 או 4 ספרות. נא לנסות שוב:");
            }
        }

        inputField.requestFocus();
    }

    // בודקת פורמט MM/YY ומוודאת שהכרטיס תקף בחודש הנוכחי או בחודש עתידי.
    private boolean isValidExpirationDate(String input) {
        if (!input.matches("(0[1-9]|1[0-2])/\\d{2}")) return false;

        int month = Integer.parseInt(input.substring(0, 2));
        int year = 2000 + Integer.parseInt(input.substring(3, 5));
        YearMonth expiration = YearMonth.of(year, month);
        return !expiration.isBefore(YearMonth.now());
    }

    // נקראת אחרי אימות מוצלח: משלוח עובר לכתובת, ואיסוף עובר ישירות לתפריט.
    private void continueAfterVerification() {
        if (orderType.equals("משלוח") || orderType.equals("Delivery")) {
            appendMessage(isEnglish ? "Delivery address?" : "כתובת למשלוח?");
            chatState = 6;
        } else {
            showCouponAnnouncement();
            showMenuCategories();
            chatState = 7;
        }
    }

    // נפתחת מקוד המנהל ומציגה הזמנות קודמות וסיכום הכנסות מקובץ CSV.
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

    // מודיעה בצ'אט על הקופונים הזמינים לפני שהמשתמש מתחיל לבחור מנות.
    private void showCouponAnnouncement() {
        if (isEnglish) {
            appendMessage("✨ Special Offers in Cart! ✨\n\n• 10% off first order with code: GOLDEN10\n• Support special - 20% off with code: VIP\n\n(Enter the code at checkout)");
        } else {
            appendMessage("✨ הטבות מיוחדות שמחכות לך בסל הקניות! ✨\n\n• 10% הנחה להזמנה ראשונה באתר בקוד: GOLDEN10\n• בגלל המצב והמלחמה יצאנו במבצע תמיכה מיוחד - 20% הנחה בקוד: VIP\n\n(תוכלו להזין את הקוד שתבחרו בשלב התשלום בתוך חלון הסל)");
        }
    }

    // מציגה את קטגוריות התפריט וכפתור הסל; נקראת גם לאחר שינוי בסל.
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

        double currentTotal = orderList.calculateFinalTotal();
        String cartTxt = isEnglish ? "🛒 Cart (" + orderList.size() + ") - ₪" + String.format("%.2f", currentTotal) : "🛒 סל (" + orderList.size() + ") - " + String.format("%.2f", currentTotal) + "₪";

        Button cart = new Button(cartTxt);
        cart.setPrefHeight(45);
        cart.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-background-radius: 10; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
        cart.setOnAction(e -> showCart());
        topPane.getChildren().add(cart);
    }

    // מציגה רק את הפריטים השייכים לקטגוריה שנבחרה ומאפשרת לבחור מנה.
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
    // נפתחת בלחיצה על תמונת פריט ומציגה תמונה ותיאור מוגדלים.
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

    // מוסיפה תוספות/שתייה ישירות, ולמנות עיקריות פותחת חלון התאמה לפני הוספה לסל.
    private void openCustomizationWindow(MenuItem item) {
        if (item.category.equals("שתייה") || item.category.equals("תוספות") || item.category.equals("Sides") || item.category.equals("Drinks")) {
            orderList.add(item.name, item.price);
            double currentTotal = orderList.calculateFinalTotal();
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

            double currentTotal = orderList.calculateFinalTotal();
            appendMessage((isEnglish ? "Added: " : "הוספת: ") + orderString + (isEnglish ? " | Total: ₪" : " | סה\"כ בינתיים: ") + String.format("%.2f", currentTotal) + (isEnglish ? "" : "₪"));

            customStage.close();
            showMenuCategories();
        });

        root.getChildren().add(addBtn);
        Scene scene = new Scene(root, 380, 480);
        customStage.setScene(scene);
        customStage.show();
    }

    // מציגה את פריטי ההזמנה, מאפשרת הסרה/ניקוי, החלת קופון ומעבר לסיום.
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
                    if (orderList.isEmpty()) orderList.setDiscount(0.0);
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

        // מחשב מחדש את הסכום לאחר שינוי פריט או הנחה ומעדכן את התצוגה.
        Runnable updateTotalDisplay = () -> {
            double finalPrice = orderList.calculateFinalTotal();
            if (orderList.getDiscount() > 0 && !orderList.isEmpty()) {
                totalLabel.setText((isEnglish ? "Total: ₪" : "סה\"כ לתשלום: ") + String.format("%.2f", finalPrice) + (isEnglish ? " (Discounted)" : "₪ (אחרי הנחה)"));
            } else {
                totalLabel.setText((isEnglish ? "Total: ₪" : "סה\"כ לתשלום: ") + String.format("%.2f", finalPrice) + (isEnglish ? "" : "₪"));
            }
        };
        updateTotalDisplay.run();

        // הקופון משנה את אחוז ההנחה בשירות ההזמנה; קוד שגוי מאפס את ההנחה.
        applyBtn.setOnAction(e -> {
            if (orderList.isEmpty()) return;
            String code = couponInput.getText().trim().toUpperCase();
            if (code.equals("GOLDEN10")) {
                orderList.setDiscount(0.10);
                msgLabel.setText(isEnglish ? "Coupon applied! 10% off." : "קופון הופעל! 10% הנחה.");
                msgLabel.setStyle("-fx-text-fill: #4CAF50;");
            } else if (code.equals("VIP")) {
                orderList.setDiscount(0.20);
                msgLabel.setText(isEnglish ? "VIP Support applied! 20% off." : "מבצע תמיכה הופעל! 20% הנחה.");
                msgLabel.setStyle("-fx-text-fill: #4CAF50;");
            } else {
                orderList.setDiscount(0.0);
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
            orderList.clear(); orderList.setDiscount(0.0); cartStage.close(); showCart(); showMenuCategories();
        });

        Button finishBtn = new Button(isEnglish ? "Checkout" : "תשלום וסיום");
        finishBtn.setPrefSize(160, 45);
        finishBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 25; -fx-cursor: hand;");

        if (orderList.isEmpty()) {
            finishBtn.setDisable(true); actionButtonsBox.getChildren().add(finishBtn);
        } else {
            actionButtonsBox.getChildren().addAll(clearBtn, finishBtn);
        }

        // בקופה נוצר מספר הזמנה, אך ההזמנה מסתיימת רק לאחר בחירת אמצעי תשלום.
        finishBtn.setOnAction(e -> {
            pendingOrderId = 1000 + new Random().nextInt(9000);
            cartStage.close();
            showPaymentSelection();
        });

        root.getChildren().add(actionButtonsBox);
        cartStage.setScene(new Scene(root, 450, 600));
        cartStage.show();
    }

    // מציגה בחירה קצרה וברורה בין מזומן לבין תשלום אשראי מדומה.
    private void showPaymentSelection() {
        topPane.getChildren().clear();
        inputField.setDisable(true);
        sendButton.setDisable(true);

        appendMessage(isEnglish ? "Choose a payment method:" : "נא לבחור אמצעי תשלום:");

        Button cashButton = new Button(isEnglish ? "Cash" : "מזומן");
        Button cardButton = new Button(isEnglish ? "Credit card" : "כרטיס אשראי");
        String paymentButtonStyle = "-fx-background-color: transparent; -fx-text-fill: " + GOLD
                + "; -fx-border-color: " + GOLD + "; -fx-border-width: 2; -fx-border-radius: 25;"
                + " -fx-font-weight: bold; -fx-font-size: 16px; -fx-cursor: hand;";
        cashButton.setPrefSize(180, 50);
        cardButton.setPrefSize(180, 50);
        cashButton.setStyle(paymentButtonStyle);
        cardButton.setStyle(paymentButtonStyle);

        cashButton.setOnAction(e -> {
            paymentMethod = "Cash";
            topPane.getChildren().clear();
            appendMessage(isEnglish ? "Payment method: Cash" : "אמצעי תשלום: מזומן");
            finishOrder(pendingOrderId);
        });

        cardButton.setOnAction(e -> {
            paymentMethod = "Credit card";
            topPane.getChildren().clear();
            appendMessage(isEnglish
                    ? "Demo payment only - no real charge will be made."
                    : "תשלום דמו בלבד - לא מתבצע חיוב אמיתי.");
            appendMessage(isEnglish ? "Enter card number:" : "נא להזין מספר כרטיס:");
            inputField.setDisable(false);
            sendButton.setDisable(false);
            chatState = 8;
            inputField.requestFocus();
        });

        topPane.getChildren().addAll(cashButton, cardButton);
    }

    // מסיימת הזמנה: שומרת CSV וקבלה, מציגה אישור ונועלת את המשך הקלט.
    private void finishOrder(int orderId) {
        double finalPrice = orderList.calculateFinalTotal();

        try (PrintWriter csvWriter = new PrintWriter(new FileWriter("orders.csv", true))) {
            csvWriter.println(orderId + "," + customerName + "," + customerPhone + "," + orderType + "," + finalPrice);
        } catch (IOException e) {}

        appendMessage("--------------------------------");
        appendMessage(isEnglish ? "Order received! Order ID: #" + orderId : "ההזמנה התקבלה בהצלחה! מספר הזמנה: #" + orderId);
        appendMessage((isEnglish ? "Total paid: ₪" : "סה\"כ לתשלום: ") + String.format("%.2f", finalPrice) + (isEnglish ? "" : " ש\"ח."));
        appendMessage(isEnglish
                ? "Payment method: " + paymentMethod
                : "אמצעי תשלום: " + (paymentMethod.equals("Cash") ? "מזומן" : "אשראי"));
        if (paymentMethod.equals("Credit card")) {
            appendMessage(isEnglish ? "Card ending with: " + cardLastFour : "כרטיס שמסתיים ב: " + cardLastFour);
        }

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

    // בונה ומחזירה את טקסט הקבלה עם פרטי הלקוח, הפריטים, ההנחה והסכום הסופי.
    private String buildReceiptString(int orderId) {
        String content = isEnglish ? "=== Golden Burger Receipt ===\n" : "=== קבלת הזמנה - גולדן בורגר ===\n";
        content += (isEnglish ? "Order ID: #" : "מספר הזמנה: #") + orderId + "\n" +
                (isEnglish ? "Customer Name: " : "שם הלקוח: ") + customerName + "\n" +
                (isEnglish ? "Phone: " : "טלפון: ") + customerPhone + "\n" +
                (isEnglish ? "Order Type: " : "סוג ההזמנה: ") + orderType + "\n" +
                (isEnglish ? "Payment method: " : "אמצעי תשלום: ")
                        + (isEnglish ? paymentMethod : (paymentMethod.equals("Cash") ? "מזומן" : "אשראי")) + "\n";

        if (paymentMethod.equals("Credit card")) {
            content += (isEnglish ? "Card ending with: " : "כרטיס שמסתיים ב: ") + cardLastFour + "\n";
        }

        if (orderType.equals("משלוח") || orderType.equals("Delivery")) {
            content += (isEnglish ? "Address: " : "כתובת למשלוח: ") + customerAddress + "\n";
        }

        content += "--------------------------------\n";
        for (String item : orderList.getItemsAsList()) content += "- " + item + "\n";
        content += "--------------------------------\n";

        double rawTotal = orderList.calculateTotal();
        double finalPrice = orderList.calculateFinalTotal();

        if (orderList.getDiscount() > 0) {
            content += (isEnglish ? "Subtotal: ₪" : "סה\"כ לפני הנחה: ") + String.format("%.2f", rawTotal) + (isEnglish ? "\n" : " ש\"ח\n");
            content += (isEnglish ? "Discount: " : "הנחה: ") + (int)(orderList.getDiscount() * 100) + "%\n";
        }

        content += (isEnglish ? "Total: ₪" : "סה\"כ לתשלום: ") + String.format("%.2f", finalPrice) + (isEnglish ? "\n================================\n" : " ש\"ח\n================================\n");
        return content;
    }

    // מציגה בחלון נפרד את טקסט הקבלה שנבנה עבור ההזמנה שהסתיימה.
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

}
