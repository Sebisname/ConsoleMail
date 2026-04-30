import java.io.*;
import java.util.Properties;
import java.util.Scanner;

public class StorageService {
    Properties properties = new Properties();
    public void askRememberMe(String email, String password) {
        Scanner input = new Scanner(System.in);
        System.out.println("Angemeldet Bleiben? (j/n)");
        String answer = input.nextLine();
        switch (answer) {
            case "j":
                saveCredentials(email,password);
                break;
            case "n":
                break;
            default:
                System.out.println("Invalid input");
        }
    }

    public void saveCredentials(String email, String password){
        properties.setProperty("email", email);
        properties.setProperty("password", password);
        try {
            properties.store(new FileOutputStream("email.properties"), null);
        } catch (IOException e) {
            System.out.println("Error saving Credentials");
        }

    }

    public String[] loadCredentials() throws IOException {
        try {
            FileInputStream inputStream = new FileInputStream("email.properties");
            properties.load(inputStream);
            String email = properties.getProperty("email");
            String password = properties.getProperty("password");
            return new String[]{email,password};
        } catch (IOException e) {
            System.out.println("Error loading Credentials");
            return new String[0];
        }

    }
}
