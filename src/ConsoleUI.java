import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Scanner;

public class ConsoleUI {
    private EmailService emailService;
    private Scanner input;
    private Message[] messages;
    private int maxEmails = 10;
    private String signature = "Gesendet mit ConsoleMail";

    public ConsoleUI(EmailService emailService) {
        this.emailService = emailService;
        this.input = new Scanner(System.in);
        this.messages = emailService.fetchEmail(maxEmails);
    }

    /**
     * Das Hauptmenü der Programms
     */
    public void showMenu() {
        boolean running = true;
        while (running) {

            System.out.println("Welcome back " + emailService.getEmail());
            System.out.println("You have " + messages.length + " messages");
            System.out.println("Please enter your choice: ");
            System.out.println("1. Send a Message");
            System.out.println("2. See Inbox");
            System.out.println("3. Settings");
            System.out.println("4. Exit");
            int choice = Integer.parseInt(input.nextLine());
            switch (choice) {
                case 1:
                    composeEmail();
                    break;
                case 2:
                    showInbox();
                    break;
                case 3:
                    settings();
                    break;
                case 4:
                    running = false;
                    break;

            }
        }
        System.out.println("Bye!");
        System.exit(0);
    }

    /**
     * Wird verwendet um die Inbox anzuzeigen
     */
    public void showInbox() {
        messages = emailService.fetchEmail(maxEmails);
        try {
            for (int i = 0; i < messages.length; i++) {
                System.out.println("Message "+(i+1)+": ");
                System.out.println("From:"+ messages[i].getFrom()[0].toString());
                System.out.println("Subject"+ messages[i].getSubject());
                System.out.println("Press "+(i+1)+" to read");

            }
            } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Please enter the Email you want to read ");
        System.out.println("(Press 0 to exit to the Main Menu)");
        int choice = Integer.parseInt(input.nextLine());

        if(choice == 0) {
            showMenu();
        } else if(choice >= 1 && choice <= messages.length) {
            showEmail(messages[choice - 1]);
        } else {
            System.out.println("Invalid choice.");
        }
    }

    /**
     * Zeigt ausgewählte E-Mail
     * @param message Ausgewählte E-Mail
     */
    public void showEmail(Message message) {
        try {
            System.out.println("From: " + message.getFrom()[0].toString());
            System.out.println("Subject: " + message.getSubject());

            Object content = message.getContent();

            if(content instanceof String) {
                System.out.println("Content: " + content);
            } else if(content instanceof MimeMultipart) {

                MimeMultipart multipart = (MimeMultipart) content;
                System.out.println("Content: " + multipart.getBodyPart(0).getContent().toString());
            }

        } catch (MessagingException | IOException e) {
            System.out.println("Error (Failed to read): "+e.getMessage());
        }

    }

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

    public void settings() {
        System.out.println("Settings:");
        System.out.println("1. Max Emails shown: "+maxEmails);
        System.out.println("2. Edit Signature: "+signature);
        System.out.println("Please enter the Number of the Setting you want to edit:");
        int choice = Integer.parseInt(input.nextLine());

        switch (choice) {
            case 1:
                System.out.println("Set new Maximum:");
                maxEmails = Integer.parseInt(input.nextLine());
                break;
            case 2:
                System.out.println("New Signature:");
                signature = input.nextLine();
                break;
            default:
                System.out.println("Invalid choice.");
                break;
        }
    }
}
