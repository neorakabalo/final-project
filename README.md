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
