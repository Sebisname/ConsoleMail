import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class SecurityUtils {

     private final String key = System.getenv("CONSOLEMAIL_KEY");

     //Prüft ob Umgebungsvariable gesetzt
    public SecurityUtils() {
        if(key == null) {
            System.out.println("CONSOLEMAIL_KEY Umgebungsvariable nicht gesetzt!");
            System.exit(0);
        }
    }

    /**
     * Verschlüsselt String
     * @param content Text zur Verschlüsselung
     * @return encrypted String
     */
    public String encrypt(String content) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(content.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.getMessage());
        }
        return null;
    }

    /**
     * Entschlüsselt String
     * @param content Text zur entschlüsselung
     * @return decrypted String
     */

    public String decrypt(String content) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decoded = Base64.getDecoder().decode(content);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.getMessage());
        }
        return null;
    }
}
