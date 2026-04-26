import java.util.Scanner;

public class Main {
    static void main() {
        Scanner input = new Scanner(System.in);

        //Login

        System.out.print("Enter your Email: ");
        String email = input.nextLine();

        while(!isValidEmail(email)){
            System.out.println("Please enter a valid Gmail Address");
            email = input.nextLine();
        }

        System.out.print("Enter your Password: ");
        String password = input.nextLine();
        EmailService service = new EmailService(email,password);

        if(!service.validateLogin()) {
            System.out.println("Falsches Passwort oder Email!");
            return;
        }

        ConsoleUI ui = new ConsoleUI(service);
        ui.showMenu();

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
