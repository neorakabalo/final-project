# Golden Burger FX

Golden Burger FX is a desktop food-ordering application built with JavaFX. It presents the ordering process as a guided chatbot conversation: the customer chooses delivery or pickup, enters personal details, verifies their identity, browses menu categories, customizes and selects items, reviews the cart, and completes the order.

The interface supports both Hebrew and English and keeps the customer focused on one clear step at a time.

## Main Features

- Chat-based, step-by-step ordering flow
- Hebrew and English interface and menu support
- Delivery and pickup ordering paths
- Ten-digit phone number validation
- Six-digit identity verification code generation and validation
- Email verification through Gmail SMTP
- Phone verification demo that displays the code in the chat because an SMS provider is not connected
- Menu categories for meals, burgers, sides, and drinks
- Item selection, burger/meal customization, and cart item removal
- Live cart count, order summary, and total-price calculation
- Coupon discounts: `GOLDEN10` for 10% and `VIP` for 20%
- Delivery-address validation against the configured street list
- Cash and demo credit card payment options
- Basic demo card-field validation with no real payment provider or charge
- Order confirmation, digital receipt display, and local receipt/order-file output
- Receipt display of the selected payment method (and only the card's last four digits for demo card payments)

## User Flow

1. The customer starts an order by selecting **Delivery** or **Pickup**.
2. The chatbot asks for the customer's name and phone number.
3. The application validates that the phone number contains exactly ten digits.
4. The customer chooses a verification method: **Email** or **Phone**.
5. For email verification, the customer enters a valid email address and the application sends a six-digit code through Gmail SMTP.
6. For phone verification, the application generates a six-digit code and displays it in the chat for demonstration purposes. No real SMS is sent because an SMS provider is not connected.
7. The customer enters the verification code and the application checks it.
8. A delivery customer enters an address, which is checked against the supported street list. A pickup customer continues directly to the menu.
9. The customer browses menu categories, customizes applicable items, adds products to the cart, and may apply a coupon.
10. The cart displays the selected items and final total. The customer chooses cash or demo credit card payment before completing the order.
11. Demo credit card payment collects basic card fields in the chat for presentation only; no real payment provider is connected and no real charge is made.
12. The customer receives an order number and receipt showing the selected payment method. Credit card receipts show only the last four digits.

## Project Structure

| File / Class | Responsibility |
| --- | --- |
| `GoldenBurgerBot.java` | Main JavaFX application, user interface, chat state flow, cart windows, order completion, receipts, and local order output. |
| `EmailService.java` | Sends verification codes through Gmail SMTP using credentials from environment variables. |
| `VerificationService.java` | Securely generates, stores, checks, and clears the current six-digit verification code. |
| `ValidationUtils.java` | Validates phone numbers, email addresses, and digits-only input. |
| `MenuService.java` | Provides the Hebrew and English menu data, including categories, descriptions, prices, and image names. |
| `MenuItem.java` | Represents a menu item with its name, price, category, description, and image path. |
| `OrderService.java` | Manages cart items, removal, item count, coupon discount, subtotal, and final total. |
| `Launcher.java` | Provides a small entry point that delegates startup to the main JavaFX application. |
| `pom.xml` | Defines the Maven project, JavaFX and Jakarta Mail dependencies, compiler settings, and JavaFX run plugin. |
| `streets.txt` | Contains the street names used to validate delivery addresses. |

## Email Verification Setup

Email verification uses Gmail SMTP and reads credentials from environment variables. A normal Gmail account password should not be used.

Before running the application:

1. Enable **2-Step Verification** on the Google account.
2. Create a **Gmail App Password** for the application.
3. Set `MAIL_USERNAME` to the complete Gmail address.
4. Set `MAIL_PASSWORD` to the generated Gmail App Password.

PowerShell example for the current terminal session:

```powershell
$env:MAIL_USERNAME="your-address@gmail.com"
$env:MAIL_PASSWORD="your-app-password"
mvn javafx:run
```

> **Security:** Never place Gmail credentials directly in the source code, `README.md`, or another tracked file. Do not commit secrets to GitHub.

If these environment variables are missing or Gmail rejects the credentials, the chat reports that the verification email could not be sent and asks the customer to try again after the mail configuration is corrected.

## How to Run

### Prerequisites

- A JDK compatible with the Java 25 compiler configuration in `pom.xml`
- Apache Maven
- Gmail SMTP environment variables when demonstrating email verification

From the project root, run:

```powershell
mvn javafx:run
```

The command is supported by the configured JavaFX Maven plugin in `pom.xml`.

## Technologies Used

- Java
- JavaFX 21
- Maven
- Jakarta Mail (Eclipse Angus implementation)
- Gmail SMTP with STARTTLS


## Future Improvements

- Connect a real SMS provider such as Twilio for phone verification
- Store customers, orders, and menu data in a database
- Add a dedicated admin screen for managing orders and menu items
- Improve the UI design, responsiveness, and accessibility
- Add customer accounts and order history

