import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailService {

    private final String email;
    private final String password;
    private Store store;
    private Folder inbox;

    public EmailService(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * Methode für die Email (um DRY zu bleiben weil oft verwendet)
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
     * Um SMTP Props zu kriegen
     * Für sendEmail
     * @return props um alle Properties
     */
    private Properties getSmtpProps() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "587");
        return props;
    }

    /**
     * Um IMAP Props zu kriegen
     * Für fetchEmail
     * @return props um alle Properties
     */
    private Properties getImapProps() {
        Properties props = new Properties();
        props.put("mail.imap.host", "imap.gmail.com");
        props.put("mail.imap.port", "993");
        props.put("mail.imap.ssl.enable", "true");
        return props;
    }

    /**
     * Methode um Emails zu versenden
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
     * Probiert mir den IAMP Props alle Emails abzurufen
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

    public void disconnect() {
        try {
            store.close();
            inbox.close(true);
        } catch (MessagingException e) {
            System.out.println("Email service failed to close email: "+e.getMessage());
        }
        }

}
