import jakarta.mail.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
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

    public static boolean isValidEmail(String email) {
        if(email.isEmpty()) return false;
        if(!email.contains("@")) return false;
        if(!email.endsWith("@gmail.com")) return false;
        return true;
    }
}
