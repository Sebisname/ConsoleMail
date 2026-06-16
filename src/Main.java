import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        StorageService storage = new StorageService();
        EmailService service;

        showLogo();
        int id = -1;
        if (storage.checkIfCredentialsSaved()) {

            //Falls nur ein Account gespeichert, wird dieser Automatisch aufgerufen
            if (storage.listPropFiles().length <= 1) {
                id = 1;
            } else {
                id = storage.selectAccount();
            }

            String[] credentials;
            credentials = storage.loadCredentials(id);

            String email = credentials[0];
            String password = credentials[1];

            service = new EmailService(email, password);

            if (!service.validateLogin()) {
                System.out.println("Credentials are incorrect.");
                storage.deleteCredentials(storage.findAccountId(email));
                return;
            }
        } else {

            System.out.print("Enter your Email: ");
            String email = input.nextLine();

            while (!isValidEmail(email)) {
                System.out.println("Please enter a valid E-mail Address");
                email = input.nextLine();
            }

            System.out.print("Enter your Password: ");
            String password = input.nextLine();

            service = new EmailService(email, password);

            if (!service.validateLogin()) {
                System.out.println("Falsches Passwort oder Email!");
                return;
            }

            storage.askRememberMe(email, password);
        }


        System.out.println("Logged in successfully.");
        System.out.println("Welcome back " + service.getEmail());

        String[][] config = EmailService.getProviderConfig(service.getEmail());
        NetworkStates status = NetworkUtils.checkStability(config[0][0], 993);
        switch (status) {
            case STABLE:
                System.out.println("Network: " + status.getColor() + "Stable" + status.getReset());
                break;
            case UNSTABLE:
                System.out.println("Network: " + status.getColor() + "Unstable - sending emails may be slow" + status.getReset());
                break;
            case VERY_UNSTABLE:
                System.out.println("Network: " + status.getColor() + "Very unstable - sending emails may fail" + status.getReset());
                break;
            case OFFLINE:
                System.out.println("Network: " + status.getColor() + "Offline - no connection to mail server" + status.getReset());
                break;
        }


        //Main Menu aufrufen
        ConsoleUI ui = new ConsoleUI(service, id);
        ui.showMenu();

        //Logout
        service.disconnect();
    }

    /**
     * Prüft, ob die eingegebene E-Mail eine gmail.com Adresse ist
     * (Sonst würde es nicht funktionieren)
     *
     * @param email E-Mail-Adresse zum Prüfen
     * @return true/false je nachdem ob es eine Valide Adresse ist
     */
    public static boolean isValidEmail(String email) {
        if (email.isEmpty()) return false;
        if (!email.contains("@")) return false;
        return EmailService.getProviderConfig(email) != null;
    }

    /**
     * Zeigt das ASCII-Art-Logo von ConsoleMail an
     */
    public static void showLogo() {
        final String ORANGE = "\033[38;5;208m";
        final String RESET  = "\033[0m";

        System.out.println(ORANGE + "  ______                                           __            __       __            __  __ " + RESET);
        System.out.println(ORANGE + " /      \\                                         |  \\          |  \\     /  \\          |  \\|  \\" + RESET);
        System.out.println(ORANGE + "|  $$$$$$\\  ______   _______    _______   ______  | $$  ______  | $$\\   /  $$  ______   \\$$| $$" + RESET);
        System.out.println(ORANGE + "| $$   \\$$ /      \\ |       \\  /       \\ /      \\ | $$ /      \\ | $$$\\ /  $$$ |      \\ |  \\| $$" + RESET);
        System.out.println(ORANGE + "| $$      |  $$$$$$\\| $$$$$$$\\|  $$$$$$$|  $$$$$$\\| $$|  $$$$$$\\| $$$$\\  $$$$  \\$$$$$$\\| $$| $$" + RESET);
        System.out.println(ORANGE + "| $$   __ | $$  | $$| $$  | $$ \\$$    \\ | $$  | $$| $$| $$    $$| $$\\$$ $$ $$ /      $$| $$| $$" + RESET);
        System.out.println(ORANGE + "| $$__/  \\| $$__/ $$| $$  | $$ _\\$$$$$$\\| $$__/ $$| $$| $$$$$$$$| $$ \\$$$| $$|  $$$$$$$| $$| $$" + RESET);
        System.out.println(ORANGE + " \\$$    $$ \\$$    $$| $$  | $$|       $$ \\$$    $$| $$ \\$$     \\| $$  \\$ | $$ \\$$    $$| $$| $$" + RESET);
        System.out.println(ORANGE + "  \\$$$$$$   \\$$$$$$  \\$$   \\$$ \\$$$$$$$   \\$$$$$$  \\$$  \\$$$$$$$ \\$$      \\$$  \\$$$$$$$ \\$$ \\$$" + RESET);
        System.out.println();
        System.out.println();
    }}
