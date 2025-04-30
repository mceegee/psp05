import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Scanner;
import javax.crypto.SecretKey;

// Alumna: Marta Carbonell Giménez
// Assignatura: Programació de Serveis i Processos
// Curs 2024/25

// Bytes to String i viceversa https://mkyong.com/java/how-do-convert-byte-array-to-string-in-java/

public class Client {

  protected static final String IP = "localhost";
  private static int PORT_TCP = 9999;

  private static PublicKey serverPublicKey = null;

  private static SecretKey secretKey = null;


  public static void main(String[] args) {

    Scanner sc = new Scanner(System.in);

    try {
      // Connexió amb el servidor
      Socket clientSocket = new Socket(IP, PORT_TCP);
      ClientConnection serverConnection = new ClientConnection(clientSocket,
          Client::OnServerMessage);
      Thread newListener = new Thread(serverConnection);
      newListener.start();

      // Sol·licitam la clau pública al servidor
      serverConnection.send(new Message("Envia clau pública"
          .getBytes(StandardCharsets.UTF_8)));

      Message message;

      while (true) {
        String userInput;
        userInput = sc.nextLine();

        // Desconnexió del server
        if (userInput.equalsIgnoreCase("bye")) {
          serverConnection.disconnect();
          break;
        }

        // Enviam missatges encriptats al server
        byte[] rawMessage = userInput.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedMessage = EncryptionCommon.encryptData(secretKey, rawMessage);
        byte[] hash = EncryptionCommon.encryptData(secretKey,
            EncryptionCommon.generateHash(rawMessage));

        message = new Message(encryptedMessage, hash);

        serverConnection.send(message);
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  // Llegim els missatges del server
  public static void OnServerMessage(ClientConnection cc, Message message) {
    try {
      if (serverPublicKey == null) {
        // Si no tenim la clau pública del servidor, la processam i la guardam
        byte[] serialized = message.getMessage();

        serverPublicKey = (PublicKey) SerializationUtils.deserialize(serialized);
        System.out.println(serverPublicKey.toString());

        // A continuació cream la clau secreta comuna
        secretKey = EncryptionClient.passwordKeyGeneration("newKey", 256);
        byte[] serializedData = SerializationUtils.serialize(secretKey);
        byte[] hash = EncryptionCommon.generateHash(serializedData);

        byte[] encryptedKey = EncryptionClient.encryptData(serializedData, serverPublicKey);
        byte[] encryptedHash = EncryptionClient.encryptData(hash, serverPublicKey);

        cc.send(new Message(encryptedKey, encryptedHash));


      } else {
        // Per a qualsevol altre tipu de missatge, desencriptam i imprimir per pantalla
        byte[] messageReceived = message.getMessage();
        System.out.println(new String(EncryptionCommon.decryptData(secretKey, messageReceived),
            StandardCharsets.UTF_8));
      }

    } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }


}
