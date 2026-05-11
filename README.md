# ConsoleMail

Ein einfacher E-Mail-Client der vollständig in der Konsole läuft.
Unterstützt Gmail, Outlook, GMX und iCloud.

---

## Libraries einbinden

Folgende JARs herunterladen und in IntelliJ einbinden (`File → Project Structure → Libraries → +`):
- [jakarta.mail-2.0.1.jar](https://mvnrepository.com/artifact/com.sun.mail/jakarta.mail/2.0.1)
- [jakarta.activation-2.0.1.jar](https://mvnrepository.com/artifact/com.sun.activation/jakarta.activation/2.0.1)

---

## Einrichtung je nach Anbieter

### Gmail
1. 2-Faktor-Authentifizierung aktivieren: `myaccount.google.com → Sicherheit → 2-Schritt-Verifizierung`
2. App-Passwort erstellen: `myaccount.google.com/apppasswords` → Namen eingeben → 16-stelliges Passwort kopieren
3. IMAP aktivieren: Gmail → Einstellungen → Alle Einstellungen → `Weiterleitung und POP/IMAP → IMAP aktivieren`
4. Login: Gmail-Adresse + App-Passwort

### Outlook
1. IMAP aktivieren: `outlook.com → Einstellungen → Mail → Sync Email → POP und IMAP → IMAP aktivieren`
2. Login: Outlook-Adresse + normales Passwort

### GMX
1. IMAP aktivieren: `gmx.at → Einstellungen → E-Mail → Postfach → POP3/IMAP Abruf → IMAP aktivieren`
2. Login: GMX-Adresse + normales Passwort

### iCloud
1. 2-Faktor-Authentifizierung aktivieren: `appleid.apple.com → Sicherheit`
2. App-Passwort erstellen: `appleid.apple.com → Anmeldung und Sicherheit → App-Passwörter`
3. Login: iCloud-Adresse + App-Passwort

---

## Starten

1. Projekt in IntelliJ öffnen
2. `Main.java` ausführen
3. E-Mail-Adresse eingeben
4. Passwort eingeben (je nach Anbieter normales Passwort oder App-Passwort)

---

## Features

- Posteingang lesen
- Emails verfassen und senden
- Mehrere Accounts speichern und wechseln
- Auto-Login
- Signatur
- Einstellbare Anzahl angezeigter Emails

---

## Hinweise

- Passwörter werden unverschlüsselt gespeichert — nur auf vertrauenswürdigen Geräten verwenden
- Das Passwort wird bei der Eingabe sichtbar angezeigt (Einschränkung der IntelliJ Konsole)
- Schul- oder Work-Konten funktionieren möglicherweise nicht wenn der Admin IMAP blockiert hat