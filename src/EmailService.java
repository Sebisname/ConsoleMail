import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailService {

    private String email;
    private String password;
    private Store store;
    private Folder inbox;

    /**
     * Erstellt einen neuen EmailService
     * @param email Die Gmail-Adresse des Benutzers
     * @param password Das App-Passwort des Benutzers
     */
    public EmailService(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * Methode für die E-Mail (um DRY zu bleiben, weil oft verwendet)
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Überprüft, ob die Zugangsdaten korrekt sind
     * @return true wenn erfolgreich, false wenn fehlgeschlagen
     */
    public boolean validateLogin() {
        try {
            Store testStore = Session.getInstance(getImapProps(), new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(email, password);
                }
            }).getStore("imaps");
            testStore.connect("imap.gmail.com", email, password);
            testStore.close();
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }

    /**
     * Um SMTP Props zu kriegen,
     * für sendEmail
     * @return props um alle Properties
     */
    private Properties getSmtpProps() {
        Properties props = new Properties();
        String[] config = getProviderConfig(email,false);
        props.put("mail.smtp.host", config[0]);
        props.put("mail.smtp.auth", config[1]);
        props.put("mail.smtp.starttls.enable", config[2]);
        props.put("mail.smtp.port", config[3]);
        return props;
    }

    /**
     * Um IMAP Props zu kriegen,
     * für fetchEmail
     * @return props um alle Properties
     */
    private Properties getImapProps() {
        Properties props = new Properties();
        String[] config = getProviderConfig(email,true);
        props.put("mail.imap.host", config[0]);
        props.put("mail.imap.port", config[1]);
        props.put("mail.imap.ssl.enable", config[2]);
        return props;
    }

    /**
     * Um die richtige Config für den richtigen Anbieter zu bekommen
     * @param email wählt den richtigen anbieter
     * @param imap damit man zwischen imap und smtp entscheiden kann
     * @return Config
     */
    public static String[] getProviderConfig(String email, boolean imap) {
        switch (email.substring(email.indexOf('@'))) {
            case "@gmail.com":
                if(imap){
                    return new String[]{"imap.gmail.com","993","true"};
                } else {
                    return new String[]{"smtp.gmail.com","true","true","587"};
                }
            case "@outlook.com":
                if(imap){
                    return new String[]{"outlook.office365.com","993","true"};
                } else {
                    return new String[]{"smtp.office365.com","true","true","587"};
                }
            case "@icloud.com":
                if(imap){
                    return new String[]{"imap.mail.me.com","993","true"};
                } else {
                    return new String[]{"smtp.mail.me.com","true","true","587"};
                }
            case "@gmx.at":
                if(imap){
                    return new String[]{"imap.gmx.net","993","true"};
                } else {
                    return new String[]{"mail.gmx.net","true","true","587"};
                }
            default:
                return null;
        }
    }

    /**
     * Methode um E-Mails zu versenden
     * (verwendet SMTP Props)
     *
     * @param to Empfänger
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
            System.out.println("Email service failed to send email: "+e.getMessage());
        }


    }

    /**
     * Probiert mir den IAMP Props alle E-Mails abzurufen
     * (falls fehlschlägt wird nichts zurückgegeben)
     * @return messages
     */
    public Message[] fetchEmail(int maxEmails){
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
            //Letzten 10 Email in Array (Alle wären zu viel)
            Message[] messages = inbox.getMessages(total -(maxEmails-1),total);

            return messages;

        } catch (MessagingException e) {
            System.out.println("Email service failed to fetch email: "+e.getMessage());
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
            System.out.println("Email service failed to close email: "+e.getMessage());
        }
    }
}
