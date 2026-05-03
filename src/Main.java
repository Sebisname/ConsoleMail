import java.io.File;
import java.util.Scanner;

public class Main {
    static void main() {
        Scanner input = new Scanner(System.in);
        EmailService service = null;
        StorageService storage = new StorageService();
        if(new File("email.properties").exists()) {
            String[] credentials = null;
            credentials = storage.loadCredentials();

            String email = credentials[0];
            String password = credentials[1];

            service = new EmailService(email, password);

            if (!service.validateLogin()) {
                System.out.println("Credentials are incorrect.");
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
        ConsoleUI ui = new ConsoleUI(service);
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
        return email.endsWith("@gmail.com");
    }
}
