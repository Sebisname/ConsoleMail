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
    private String signature = "Sent with ConsoleMail";
    SortTypes sortType = SortTypes.DATE;

    private static final String ORANGE = "\033[38;5;208m";
    private static final String BOLD   = "\033[1m";
    private static final String DIM    = "\033[2m";
    private static final String RESET  = "\033[0m";


    /**
     * Prints a Seperator
     */
    private void printSeparator() {
        System.out.println(DIM + "─────────────────────────────────────────" + RESET);
    }

    /**
     * Prints an Option with Orange Bold Number
     * @param key The Key (Number) for the List
     * @param label The Lable for the List
     */
    private void printOption(String key, String label) {
        System.out.println("  " + ORANGE + BOLD + key + RESET + "  " + label);
    }

    public ConsoleUI(EmailService emailService, int id) {
        this.emailService = emailService;
        this.input = new Scanner(System.in);
        this.messages = emailService.fetchEmail(sortType);
        this.id = id;
    }

    public int getIntInput() {
        try {
            return Integer.parseInt(input.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void showMenu() {
        while (running) {
            printSeparator();
            System.out.println("  " + ORANGE + BOLD + messages.length + " messages" + RESET + " in your inbox");
            printSeparator();
            printOption("1.", "Compose message");
            printOption("2.", "Inbox");
            printOption("3.", "Account");
            printOption("4.", "Settings");
            printOption("5.", "Exit");
            printOption("6.", "Logout");
            printSeparator();
            System.out.print("  > ");
            int choice = getIntInput();
            switch (choice) {
                case 1: composeEmail(); break;
                case 2: showInbox(); break;
                case 3: account(); break;
                case 4: settings(); break;
                case 5: running = false; break;
                case 6:
                    storage.deleteCredentials(id);
                    running = false;
                    break;
                default:
                    System.out.println("  Invalid choice.");
            }
        }
        System.out.println("  Goodbye!");
        System.exit(0);
    }

    public void showInbox() {
        messages = emailService.fetchEmail(sortType);
        try {
            int start = currentPage * emailsPerPage;
            int end = Math.min(start + emailsPerPage, messages.length);

            printSeparator();
            System.out.println("  " + ORANGE + BOLD + "Inbox" + RESET
                    + DIM + "  (page " + (currentPage + 1) + ")" + RESET);
            printSeparator();

            for (int i = start; i < end; i++) {
                System.out.println("  " + ORANGE + "[" + (i + 1) + "]" + RESET
                        + " " + BOLD + messages[i].getSubject() + RESET);
                System.out.println("      " + DIM + "From: " + messages[i].getFrom()[0]
                        + "   " + sdf.format(messages[i].getSentDate()) + RESET);
            }

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        printSeparator();
        printOption("N", "Next page");
        printOption("P", "Previous page");
        printOption("0", "Back to main menu");
        System.out.print("  > ");

        String choice = input.nextLine();
        if (choice.equals("0")) {
            return;
        } else if (choice.equalsIgnoreCase("n")) {
            currentPage++;
            showInbox();
        } else if (choice.equalsIgnoreCase("p")) {
            if (currentPage > 0) {
                currentPage--;
                showInbox();
            }
        } else {
            try {
                showEmail(messages[Integer.parseInt(choice) - 1]);
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input.");
            }
        }
    }

    public void showEmail(Message message) {
        try {
            printSeparator();
            System.out.println("  " + ORANGE + BOLD + message.getSubject() + RESET);
            System.out.println("  " + DIM + "From: " + message.getFrom()[0]
                    + "   " + sdf.format(message.getSentDate()) + RESET);
            printSeparator();

            Object content = message.getContent();
            if (content instanceof String) {
                System.out.println(content);
            } else if (content instanceof MimeMultipart multipart) {
                System.out.println(multipart.getBodyPart(0).getContent().toString());
            }

            printSeparator();
            printOption("A", "Reply");
            printOption("D", "Delete");
            printOption("0", "Back");
            System.out.print("  > ");

            String choice = input.nextLine();
            switch (choice.toLowerCase()) {
                case "d":
                    emailService.deleteEmail(message);
                    System.out.println("  Message deleted.");
                    break;
                case "a":
                    System.out.print("  Reply: ");
                    String replyText = input.nextLine();
                    emailService.replyEmail(message, replyText);
                    break;
                case "0":
                    break;
                default:
                    System.out.println("  Invalid choice.");
            }
        } catch (MessagingException | IOException e) {
            System.out.println("  Error (failed to read message): " + e.getMessage());
        }
    }

    public void composeEmail() {
        printSeparator();
        System.out.println("  " + ORANGE + BOLD + "New Message" + RESET);
        printSeparator();
        System.out.print("  To:      ");
        String to = input.nextLine();
        System.out.print("  Subject: ");
        String subject = input.nextLine();
        System.out.print("  Body:    ");
        String body = input.nextLine();
        body = body + "\n\n-- \n" + signature;
        emailService.sendEmail(to, subject, body);
        System.out.println("  Message sent.");
    }

    /**
     *
     */
    public void account() {
        printSeparator();
        System.out.println("  " + ORANGE + BOLD + "Account" + RESET);
        printSeparator();
        printOption("1.", "Show account");
        printOption("2.", "Switch account");
        printOption("3.", "Logout");
        System.out.print("  > ");
        int choice = getIntInput();

        switch (choice) {
            case 1:
                System.out.println("  " + emailService.getEmail());
                break;
            case 2:
                String[] credentials = storage.changeAccount();
                EmailService newService = new EmailService(credentials[0], credentials[1]);
                System.out.println("  Switched to: " + newService.getEmail());
                break;
            case 3:
                storage.deleteCredentials(id);
                running = false;
                break;
            default:
                System.out.println("  Invalid choice.");
        }
    }

    public void settings() {
        printSeparator();
        System.out.println("  " + ORANGE + BOLD + "Settings" + RESET);
        printSeparator();
        printOption("0.", "Back");
        printOption("1.", "Signature:       " + DIM + signature + RESET);
        printOption("2.", "Emails per page: " + DIM + emailsPerPage + RESET);
        printOption("3.", "Sort by:         " + DIM + sortType + RESET);
        System.out.print("  > ");
        int choice = getIntInput();

        switch (choice) {
            case 0:
                break;
            case 1:
                System.out.print("  New signature: ");
                signature = input.nextLine();
                break;
            case 2:
                System.out.print("  Emails per page: ");
                emailsPerPage = getIntInput();
                break;
            case 3:
                printOption("1.", "Date");
                printOption("2.", "Name (a-z)");
                System.out.print("  > ");
                int choice2 = getIntInput();
                switch (choice2) {
                    case 1: sortType = SortTypes.DATE; break;
                    case 2: sortType = SortTypes.NAME; break;
                    default: System.out.println("  Invalid choice.");
                }
                break;
            default:
                System.out.println("  Invalid choice.");
        }
    }
}