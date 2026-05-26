import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMultipart;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class ConsoleUI {
    private final EmailService emailService;
    private final Scanner input;
    private final int id;
    private int currentPage = 0;
    private int emailsPerPage = 10;
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    StorageService storage = new StorageService();
    private Message[] messages;
    private boolean running = true;
    private String signature = "Gesendet mit ConsoleMail";

    /**
     * Vorbereitung (E-Mails werden schon mal vorab gefetcht)
     *
     * @param emailService wird für this.emailService gebraucht
     */
    public ConsoleUI(EmailService emailService, int id) {
        this.emailService = emailService;
        this.input = new Scanner(System.in);
        this.messages = emailService.fetchEmail();
        this.id = id;
    }

    /**
     * Regelt den Fehler, falls kein Int angegeben wird
     *
     * @return Input xin Int oder -1 wenn keine Zahl
     */
    public int getIntInput() {
        try {
            return Integer.parseInt(input.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Das Hauptmenü der Programms
     */
    public void showMenu() {
        while (running) {

            System.out.println("You have " + messages.length + " messages");
            System.out.println("Please enter your choice: ");
            System.out.println("1. Send a Message");
            System.out.println("2. See Inbox");
            System.out.println("3. Account");
            System.out.println("4. Settings");
            System.out.println("5. Exit");
            System.out.println("6. Logout");
            int choice = getIntInput();
            switch (choice) {
                case 1:
                    composeEmail();
                    break;
                case 2:
                    showInbox();
                    break;
                case 3:
                    account();
                    break;
                case 4:
                    settings();
                    break;
                case 5:
                    running = false;
                    break;
                case 6:
                    storage.deleteCredentials(id);
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
        System.out.println("Bye!");
        System.exit(0);
    }

    /**
     * Wird verwendet, um die Inbox anzuzeigen
     */
    public void showInbox() {
        messages = emailService.fetchEmail();
        try {
            int start = currentPage * emailsPerPage;
            int end = Math.min(start + emailsPerPage, messages.length);

            for (int i = start; i < end; i++) {
                System.out.println("Message " + (i + 1) + ": ");
                System.out.println("From:" + messages[i].getFrom()[0].toString());
                System.out.println("Subject" + messages[i].getSubject());
                System.out.println(sdf.format(messages[i].getSentDate()));
                System.out.println("Press " + (i + 1) + " to read");

            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Please enter the Email you want to read ");
        System.out.println("N. Nächste Seite");
        System.out.println("P. Vorherige Seite");
        System.out.println("(Press 0 to exit to the Main Menu)");
        String choice = input.nextLine();
        if (choice.equals("0")) {
            showMenu();
        } else if (choice.equalsIgnoreCase("n")) {
            currentPage++;
            showInbox();
        } else if (choice.equalsIgnoreCase("p")) {
            if (currentPage > 0) {
                currentPage --;
                showInbox();
            }
        } else {
            try {
                showEmail(messages[Integer.parseInt(choice) - 1]);
            } catch (NumberFormatException e) {
                System.out.println("Ungültige Eingabe.");
            }
        }

    }

    /**
     * Zeigt ausgewählte E-Mail
     *
     * @param message Ausgewählte E-Mail
     */
    public void showEmail(Message message) {
        try {
            System.out.println(sdf.format(message.getSentDate()));
            System.out.println("From: " + message.getFrom()[0].toString());
            System.out.println("Subject: " + message.getSubject());

            Object content = message.getContent();

            if (content instanceof String) {
                System.out.println("Content: " + content);
            } else if (content instanceof MimeMultipart multipart) {

                System.out.println("Content: " + multipart.getBodyPart(0).getContent().toString());
            }

            System.out.println("Please enter a choice");
            System.out.println("D. Nachricht löschen");
            System.out.println("(Press 0 to exit to the Main Menu)");
            String choice = input.nextLine();
            switch (choice.toLowerCase()) {
                case "d":
                    emailService.deleteEmail(message);
                    break;
                case "0":
                    break;
                default:
                    System.out.println("Invalid choice.");
            }

        } catch (MessagingException | IOException e) {
            System.out.println("Error (Failed to read): " + e.getMessage());
        }

    }

    /**
     * Modul zum E-Mail schreiben
     */
    public void composeEmail() {
        System.out.println("To:");
        String to = input.nextLine();

        System.out.println("Subject:");
        String subject = input.nextLine();

        System.out.println("Body:");
        String body = input.nextLine();
        body = body + "\n\n-- \n" + signature;

        emailService.sendEmail(to, subject, body);

    }

    /**
     * Account Menü
     */
    public void account() {
        System.out.println("1. Show Account");
        System.out.println("2. Change Account");
        System.out.println("3. Logout");
        int choice = getIntInput();

        switch (choice) {
            case 1:
                System.out.println(emailService.getEmail());
                break;
            case 2:
                //emailService.disconnect();
                String[] credentials = storage.changeAccount();
                EmailService emailService = new EmailService(credentials[0], credentials[1]);
                System.out.println("Changed Account to: "+emailService.getEmail());
                break;
            case 3:
                storage.deleteCredentials(id);
                running = false;
                break;
            default:
                System.out.println("Invalid choice.");
                break;
        }
    }

    /**
     * Einstellungen des Programms
     */
    public void settings() {
        System.out.println("Settings:");
        System.out.println("0. to Exit");
        System.out.println("1. Edit Signature: " + signature);
        System.out.println("2. Emails per Page: " + emailsPerPage);
        System.out.println("Please enter the Number of the Setting you want to edit:");
        int choice = getIntInput();

        switch (choice) {
            case 0:
                break;
            case 1:
                System.out.println("New Signature:");
                signature = input.nextLine();
                break;
            case 2:
                System.out.println("Emails per Page:");
                emailsPerPage = getIntInput();
            default:
                System.out.println("Invalid choice.");
                break;
        }
    }
}
