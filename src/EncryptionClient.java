import java.security.PublicKey;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionClient {

  //  Genera una clau simètrica, genera un hash sobre aquesta clau simètrica
  public static SecretKey passwordKeyGeneration(String text, int keySize) {
    SecretKey sKey = null;
    if ((keySize == 128) || (keySize == 192) || (keySize == 256)) {
      try {
        byte[] data = text.getBytes("UTF-8");
        byte[] hash = EncryptionCommon.generateHash(data);
        byte[] key = Arrays.copyOf(hash, keySize / 8);
        sKey = new SecretKeySpec(key, "AES");
      } catch (Exception ex) {
        System.err.println("Error generant la clau:" + ex);
      }
    }
    return sKey;
  }

  // Encriptació de dades amb clau pública
  public static byte[] encryptData(byte[] data, PublicKey pub) {
    byte[] encryptedData = null;
    try {
      Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "SunJCE");
      cipher.init(Cipher.ENCRYPT_MODE, pub);
      encryptedData = cipher.doFinal(data);
    } catch (Exception ex) {
      System.err.println("Error xifrant: " + ex);
    }
    return encryptedData;
  }
}
