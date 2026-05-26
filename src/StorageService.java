import java.io.*;
import java.util.Properties;
import java.util.Scanner;

public class StorageService {
    private final Scanner input = new Scanner(System.in);
    Properties properties = new Properties();
    EmailService emailService;
    private int accTotal = 0;
    SecurityUtils securityUtils = new SecurityUtils();


    public StorageService() {
    }

    /**
     * Abfrage für das Speichern der E-Mail, und Passwort
     *
     * @param email    Email
     * @param password Password
     */
    public void askRememberMe(String email, String password) {
        System.out.println("Angemeldet Bleiben? (j/n)");
        String answer;
        try {
            answer = input.nextLine().toLowerCase();
        } catch (Exception e) {
            answer = "-1";
        }
        switch (answer) {
            case "j":
                saveCredentials(email, password);
                break;
            case "n":
                break;
            default:
                System.out.println("Invalid input");
        }
    }

    /**
     * erstellet E-Mail.properties und speichert dort die E-Mail und das passwort als Property
     *
     * @param email    E-Mail
     * @param password User Password
     */
    public void saveCredentials(String email, String password) {
        // Passwort wird unverschlüsselt gespeichert.
        // Für Produktionscode sollte Verschlüsselung verwendet werden
        accTotal++;
        properties.setProperty("id", accTotal + "");
        properties.setProperty("email", email);
        properties.setProperty("password", securityUtils.encrypt(password));
        try {
            properties.store(new FileOutputStream("email" + accTotal + ".properties"), null);
        } catch (IOException e) {
            System.out.println("Error saving Credentials");
        }
    }

    /**
     * Ladet die Credentials aus email.properties
     *
     * @return {email,password} in einem Array
     */
    public String[] loadCredentials(int id) {
        try {
            FileInputStream inputStream = new FileInputStream("email" + id + ".properties");
            properties.load(inputStream);
            String email = properties.getProperty("email");
            String password = properties.getProperty("password");
            password =  securityUtils.decrypt(password);
            inputStream.close();
            return new String[]{email, password};
        } catch (IOException e) {
            System.out.println("Error loading Credentials");
            return new String[0];
        }
    }

    /**
     * Löscht die Credentials zum Abmelden
     */
    public void deleteCredentials(int id) {
        boolean delete = new File("email" + id + ".properties").delete();
        if (!delete) {
            System.out.println("Error deleting Credentials");
        } else {
            System.out.println("Successfully Logged out");
        }
    }

    /**
     * Sucht nach der ID mithilfe der E-mail
     *
     * @param email to search
     * @return Account ID
     */
    public int findAccountId(String email) {

        for (String key : properties.stringPropertyNames()) {
            if (key.equals("email") && properties.getProperty(key).equals(email)) {
                return Integer.parseInt(properties.getProperty("id"));
            }
        }
        return -1;
    }

    /**
     * Listet alle Dateinamen von allen Properties Files
     *
     * @return ein Array mit allen Properties Datein
     */
    private String[] listPropFiles() {
        File dir = new File(".");
        File[] files = dir.listFiles();

        int count = 0;
        for (File file : files) {
            if (file.getName().startsWith("email") && file.getName().endsWith(".properties")) {
                count++;
            }
        }

        String[] propFiles = new String[count];
        int i = 0;
        for (File file : files) {
            if (file.getName().startsWith("email") && file.getName().endsWith(".properties")) {
                propFiles[i] = file.getName();
                i++;
            }
        }

        return propFiles;
    }

    /**
     * Printet alle gespeicherten Accounts
     */
    private void getAllAccounts() {
        String[] propFiles = listPropFiles();
        int i = 1;
        for (String propFile : propFiles) {
            Properties temp = new Properties();
            try (FileInputStream stream = new FileInputStream(propFile)) {
                temp.load(stream);
                System.out.println(i + ". " + temp.getProperty("email"));
            } catch (IOException e) {
                System.out.println("Error listing Accounts: " + e.getMessage());
            }
            i++;
        }
    }

    /**
     * Menü zum Auswählen von allen gespeicherten Accounts
     *
     * @return Id vom ausgewählten Account
     */
    public int selectAccount() {
        System.out.println("Select Account");
        getAllAccounts();
        Properties temp = new Properties();
        int choice = Integer.parseInt(input.nextLine());
        try (FileInputStream stream = new FileInputStream("email" + choice + ".properties")) {
            temp.load(stream);
            return Integer.parseInt(temp.getProperty("id"));
        } catch (FileNotFoundException e) {
            System.out.println("Not a Valid Option");
        } catch (IOException e) {
            System.out.println("Error loading Credentials");
        }
        return -1;
    }

    /**
     * Prüft ob Properties Files existieren
     *
     * @return true/false ob Prop Files existieren
     */
    public boolean checkIfCredentialsSaved() {
        return listPropFiles().length > 0;
    }

    /**
     * Um den Account zu wechseln, während man noch eingeloggt ist
     */
    public String[] changeAccount() {
        System.out.println("Change Account to:");
        int id = selectAccount();
        return loadCredentials(id);
    }
}
