import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class EncryptionCommon {

  public static final byte[] IV_PARAM = {0x00, 0x01, 0x02, 0x03,
      0x04, 0x05, 0x06, 0x07,
      0x08, 0x09, 0x0A, 0x0B,
      0x0C, 0x0D, 0x0E, 0x0F};

  // Generació de hash
  public static byte[] generateHash(byte[] data) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    return md.digest(data);
  }

  // Desencriptació de dades
  public static byte[] decryptData(SecretKey sKey, byte[] dataEncrypted) {
    byte[] data = null;

    Cipher cipher = null;
    try {
      cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

      IvParameterSpec iv = new IvParameterSpec(IV_PARAM);
      cipher.init(Cipher.DECRYPT_MODE, sKey, iv);
      data = cipher.doFinal(dataEncrypted);
    } catch (NoSuchAlgorithmException | InvalidKeyException | BadPaddingException |
             IllegalBlockSizeException | InvalidAlgorithmParameterException |
             NoSuchPaddingException e) {
      throw new RuntimeException(e);
    }
    return data;
  }

  //   Encriptació de dades
  public static byte[] encryptData(SecretKey sKey, byte[] data) {
    byte[] encryptedData = null;
    try {
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      IvParameterSpec iv = new IvParameterSpec(IV_PARAM);
      cipher.init(Cipher.ENCRYPT_MODE, sKey, iv);
      encryptedData = cipher.doFinal(data);
    } catch (Exception ex) {
      System.err.println("Error xifrant les dades: " + ex);
    }
    return encryptedData;
  }
}
