import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionServer {

  private static SecretKey sharedKey = null;

  private static KeyPair keys = null;
  public  static SecretKey getSharedKey() {
    return sharedKey;
  }

  public static void setSharedKey(SecretKey sk) {
    sharedKey = sk;
  }


  public static KeyPair getKeys() {
    return keys;
  }

  // Comparació de hash
  public static boolean compareHash(byte[] hashclient, byte[] hashserver) {
    if (new String(hashclient, StandardCharsets.UTF_8)
        .equals(new String(hashserver, StandardCharsets.UTF_8))) {
      System.out.println("Els hash coincideixen, no s'ha modificat el missatge");
      SecretKey sharedKey = new SecretKeySpec(hashserver, 0, hashserver.length, "AES");
      setSharedKey(sharedKey);
      return true;
    } else{
      System.out.println("Els hash no coincideixen, el missatge s'ha modificat");
      return false;
    }
  }

  // 2 Servidor: genera una conjunt de claus (públic i privat)
  public static PublicKey serverPublicKey() {
    return EncryptionServer.randomGenerate(2048).getPublic();
  }

  // Generador de claus
  public static KeyPair randomGenerate(int len) {
    try {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      keyGen.initialize(len);
      keys = keyGen.genKeyPair();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }

    return keys;
  }


  // Desencriptació de dades amb clau privada
  public static byte[] decryptData(byte[] dataEncrypted, PrivateKey priv) {
    byte[] data = null;
    try {
      Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "SunJCE");
      cipher.init(Cipher.DECRYPT_MODE, priv);
      data = cipher.doFinal(dataEncrypted);
    } catch (Exception ex) {
      System.err.println("Error desxifrant: " + ex);
    }
    return data;
  }
}
