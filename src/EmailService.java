import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailService {

    private final String email;
    private final String password;
    private Store store;
    private Folder inbox;

    /**
     * Erstellt einen neuen EmailService
     *
     * @param email    Die Gmail-Adresse des Benutzers
     * @param password Das App-Passwort des Benutzers
     */
    public EmailService(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * Um die richtige Config für den richtigen Anbieter zu bekommen
     *
     * @param email wählt den richtigen anbieter
     * @return Config
     */
    public static String[][] getProviderConfig(String email) {
        switch (email.substring(email.indexOf('@'))) {
            case "@gmail.com":
                    return new String[][]{{"imap.gmail.com", "993", "true"},{"smtp.gmail.com", "true", "true", "587"}};
            case "@outlook.com":
                    return new String[][]{{"outlook.office365.com", "993", "true"},{"smtp.office365.com", "true", "true", "587"}};
            case "@icloud.com":
                    return new String[][]{{"imap.mail.me.com", "993", "true"},{"smtp.mail.me.com", "true", "true", "587"}};
            case "@gmx.at":
                    return new String[][]{{"imap.gmx.net", "993", "true"},{"mail.gmx.net", "true", "true", "587"}};
            default:
                return null;
        }
    }

    /**
     * Methode für die E-Mail (um DRY zu bleiben, weil oft verwendet)
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Überprüft, ob die Zugangsdaten korrekt sind
     *
     * @return true wenn erfolgreich, false wenn fehlgeschlagen
     */
    public boolean validateLogin() {
        String[][] config = getProviderConfig(email);

        try {
            Store testStore = Session.getInstance(getImapProps(), new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(email, password);
                }
            }).getStore("imaps");

            testStore.connect(config[0][0], email, password);
            testStore.close();
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }

    /**
     * Um SMTP Props zu kriegen,
     * für sendEmail
     *
     * @return props um alle Properties
     */
    private Properties getSmtpProps() {
        Properties props = new Properties();
        String[][] config = getProviderConfig(email);
        props.put("mail.smtp.host", config[1][0]);
        props.put("mail.smtp.auth", config[1][1]);
        props.put("mail.smtp.starttls.enable", config[1][2]);
        props.put("mail.smtp.port", config[1][3]);
        return props;
    }

    /**
     * Um IMAP Props zu kriegen,
     * für fetchEmail
     *
     * @return props um alle Properties
     */
    private Properties getImapProps() {
        Properties props = new Properties();
        String[][] config = getProviderConfig(email);
        props.put("mail.imap.host", config[0][0]);
        props.put("mail.imap.port", config[0][1]);
        props.put("mail.imap.ssl.enable", config[0][2]);
        return props;
    }

    /**
     * Methode um E-Mails zu versenden
     * (verwendet SMTP Props)
     *
     * @param to      Empfänger
     * @param subject Betreff
     * @param content Inhalt der Email
     */
    public void sendEmail(String to, String subject, String content) {

        //Session erstellen
        Session session = Session.getInstance(getSmtpProps(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });

        //Email senden
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(content);
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException e) {
            System.out.println("Email service failed to send email: " + e.getMessage());
        }


    }

    public void deleteEmail(Message message) {

        Properties props = getImapProps();
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });

        try {
            message.setFlag(Flags.Flag.DELETED, true);

            if (inbox != null && inbox.isOpen()) {
                inbox.close(true);
            }
            if (store != null && store.isConnected()) {
                store.close();
            }

            System.out.println("Email erfolgreich zum Löschen markiert und Ordner bereinigt.");


        } catch (MessagingException e){
            System.out.println("Email service failed to delete email: " + e.getMessage());
        }

    }


    /**
     * Probiert mir den IAMP Props alle E-Mails abzurufen
     * (falls fehlschlägt wird nichts zurückgegeben)
     *
     * @return messages
     */
    public Message[] fetchEmail() {
        Properties props = getImapProps();

        //Session erstellen
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });
        try {

            store = session.getStore("imaps");
            store.connect("imap.gmail.com", email, password);

            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            int total = inbox.getMessageCount();
            Message[] messages = inbox.getMessages();

            return messages;

        } catch (MessagingException e) {
            System.out.println("Email service failed to fetch email: " + e.getMessage());
            return new Message[0];
        }
    }

    /**
     * Zum Logout
     */
    public void disconnect() {
        try {
            store.close();
            inbox.close(true);
        } catch (MessagingException e) {
            System.out.println("Email service failed to close email: " + e.getMessage());
        }
    }
}
