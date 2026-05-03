import java.io.*;
import java.util.Properties;
import java.util.Scanner;

public class StorageService {

    Properties properties = new Properties();

    /**
     * Abfrage für das speichern der Email, und Passwort
     * @param email
     * @param password
     */
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

    /**
     * erstellet email.properties und speichert dort die email und das passwort als Property
     * @param email
     * @param password
     */
    public void saveCredentials(String email, String password){
        // Passwort wird unverschlüsselt gespeichert
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
     * @throws IOException wegen dem try, catch
     */
    public String[] loadCredentials() throws IOException {
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
