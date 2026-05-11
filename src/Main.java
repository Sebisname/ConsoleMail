import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        StorageService storage = new StorageService();
        EmailService service;
        showLogo();
        int id = -1;
        if(storage.checkIfCredentialsSaved()) {

            id = storage.selectAccount();

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

            //Login

            System.out.print("Enter your Email: ");
            String email = input.nextLine();

            while (!isValidEmail(email)) {
                System.out.println("Please enter a valid Gmail Address");
                email = input.nextLine();
            }

            System.out.print("Enter your Password: ");
            String password = input.nextLine();

            service = new EmailService(email, password);

            if (!service.validateLogin()) {
                System.out.println("Falsches Passwort oder Email!");
                return;
            }

            storage.askRememberMe(email,password);
        }


        System.out.println("Logged in successfully.");
        System.out.println("Welcome back " + service.getEmail());
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
        if(email.isEmpty()) return false;
        if(!email.contains("@")) return false;
        return EmailService.getProviderConfig(email, true) != null;
    }

    /**
     * Zeigt das ASCII-Art-Logo von ConsoleMail an
     */
    public static void showLogo() {
        System.out.println("  ______                                           __            __       __            __  __ ");
        System.out.println(" /      \\                                         |  \\          |  \\     /  \\          |  \\|  \\");
        System.out.println("|  $$$$$$\\  ______   _______    _______   ______  | $$  ______  | $$\\   /  $$  ______   \\$$| $$");
        System.out.println("| $$   \\$$ /      \\ |       \\  /       \\ /      \\ | $$ /      \\ | $$$\\ /  $$$ |      \\ |  \\| $$");
        System.out.println("| $$      |  $$$$$$\\| $$$$$$$\\|  $$$$$$$|  $$$$$$\\| $$|  $$$$$$\\| $$$$\\  $$$$  \\$$$$$$\\| $$| $$");
        System.out.println("| $$   __ | $$  | $$| $$  | $$ \\$$    \\ | $$  | $$| $$| $$    $$| $$\\$$ $$ $$ /      $$| $$| $$");
        System.out.println("| $$__/  \\| $$__/ $$| $$  | $$ _\\$$$$$$\\| $$__/ $$| $$| $$$$$$$$| $$ \\$$$| $$|  $$$$$$$| $$| $$");
        System.out.println(" \\$$    $$ \\$$    $$| $$  | $$|       $$ \\$$    $$| $$ \\$$     \\| $$  \\$ | $$ \\$$    $$| $$| $$");
        System.out.println("  \\$$$$$$   \\$$$$$$  \\$$   \\$$ \\$$$$$$$   \\$$$$$$  \\$$  \\$$$$$$$ \\$$      \\$$  \\$$$$$$$ \\$$ \\$$");
        System.out.println();
        System.out.println();
    }

}
