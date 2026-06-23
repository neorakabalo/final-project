# Golden Burger FX

## Gmail verification email setup

The application sends verification codes through Gmail SMTP. Before starting it,
set these environment variables locally:

```text
MAIL_USERNAME=my Gmail address
MAIL_PASSWORD=my Gmail App Password
```

Use a Gmail App Password, not your normal Gmail password. App Passwords require
2-Step Verification on the Google account. Do not store either value in this
repository.

PowerShell example for the current terminal session:

```powershell
$env:MAIL_USERNAME="your-address@gmail.com"
$env:MAIL_PASSWORD="your-app-password"
mvn javafx:run
```

## Project Structure

- `GoldenBurgerBot.java` - JavaFX screens, chat steps, button actions, and messages shown to the user.
- `MenuItem.java` - describes one menu item, including its name, price, category, description, and image.
- `MenuService.java` - provides the Hebrew and English menu data.
- `OrderService.java` - stores the current cart, removes items, keeps the discount, and calculates totals.
- `VerificationService.java` - generates, stores, checks, and clears the current six-digit verification code.
- `ValidationUtils.java` - contains the simple phone, email, and digits-only checks.
- `EmailService.java` - sends email verification codes using `MAIL_USERNAME` and `MAIL_PASSWORD`.
- `Launcher.java` - starts the JavaFX application.
