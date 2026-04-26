# ConsoleMail

Ein einfacher E-Mail-Client der vollständig in der Konsole läuft.

---

## Wichtig vor dem Start

Das Programm funktioniert **nur mit Gmail** und benötigt ein sogenanntes **App-Passwort** — kein normales Gmail-Passwort!

---

## Einrichtung

### 1. 2-Faktor-Authentifizierung aktivieren
Falls noch nicht aktiv:
- `myaccount.google.com → Sicherheit → 2-Schritt-Verifizierung → Aktivieren`

### 2. App-Passwort erstellen
- `myaccount.google.com/apppasswords`
- Namen eingeben (z.B. "KonsolenMail") → Erstellen
- Das **16-stellige Passwort** kopieren — dieses wird im Programm als Passwort verwendet

### 3. IMAP in Gmail aktivieren
- Gmail öffnen → Einstellungen (Zahnrad) → Alle Einstellungen
- `Weiterleitung und POP/IMAP → IMAP aktivieren → Speichern`

---

## Starten

1. Projekt in IntelliJ öffnen
2. `Main.java` ausführen
3. Gmail-Adresse eingeben
4. App-Passwort eingeben (nicht das normale Gmail-Passwort!)

---

## Verwendete Library

[Jakarta Mail 2.0.1](https://mvnrepository.com/artifact/com.sun.mail/jakarta.mail/2.0.1) — muss als JAR in IntelliJ eingebunden sein.