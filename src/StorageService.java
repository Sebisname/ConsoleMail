import java.io.*;
import java.util.Properties;
import java.util.Scanner;

public class StorageService {

    Properties properties = new Properties();
    private final Scanner input = new Scanner(System.in);

    /**
     * Abfrage für das Speichern der E-Mail, und Passwort
     * @param email Email
     * @param password Password
     */
    public void askRememberMe(String email, String password) {
        System.out.println("Angemeldet Bleiben? (j/n)");
        String answer;
        try {
            answer = input.nextLine();
        } catch (Exception e) {
            answer = "-1";
        }
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

    /**
     * erstellet E-Mail.properties und speichert dort die E-Mail und das passwort als Property
     * @param email E-Mail
     * @param password User Password
     */
    public void saveCredentials(String email, String password){
        // Passwort wird unverschlüsselt gespeichert.
        // Für Produktionscode sollte Verschlüsselung verwendet werden
        properties.setProperty("email", email);
        properties.setProperty("password", password);
        try {
            properties.store(new FileOutputStream("email.properties"), null);
        } catch (IOException e) {
            System.out.println("Error saving Credentials");
        }
    }

    /**
     * Ladet die Credentials aus email.properties
     * @return {email,password} in einem Array
     */
    public String[] loadCredentials(){
        try {
            FileInputStream inputStream = new FileInputStream("email.properties");
            properties.load(inputStream);
            String email = properties.getProperty("email");
            String password = properties.getProperty("password");
            inputStream.close();
            return new String[]{email,password};
        } catch (IOException e) {
            System.out.println("Error loading Credentials");
            return new String[0];
        }
    }

    /**
     * Löscht die Credentials zum Abmelden
     */
    public void deleteCredentials() {
        boolean delete = new File("email.properties").delete();
        if (!delete) {
            System.out.println("Error deleting Credentials");
        } else  {
            System.out.println("Successfully Logged out");
        }
    }
}
