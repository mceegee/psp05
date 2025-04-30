import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import javax.crypto.SecretKey;

public class Server {

  protected static final String IP = "localhost";
  private static final int PORT_TCP = 9999;
  private PublicKey serverPublicKey = null;

  private SecretKey clientSK = null;

  public static void main(String[] args) {
    Server currentServer = new Server();
    currentServer.runServer();
  }

  // accepta noves connexions dels clients i assigna un fil
  private void runServer() {
    try {
      serverPublicKey = EncryptionServer.serverPublicKey();
      ServerSocket serverSocket = new ServerSocket(PORT_TCP);
      String message = "";

      while(true) {
        Socket currentSocket = serverSocket.accept();
        ClientConnection currentClient = new ClientConnection(currentSocket, this::OnClientMessage);
        Thread newClientThread = new Thread(currentClient);
        newClientThread.start();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void OnClientMessage(ClientConnection cc, Message message) {

    // Processam el missatge del client

    String decodedString = new String (message.getMessage(), StandardCharsets.UTF_8);
    try {
      // Enviam la clau pública
      if (decodedString.equalsIgnoreCase("Envia clau pública")) {

        cc.send(new Message(SerializationUtils.serialize(serverPublicKey)));

        System.out.println("Clau pública enviada");
      } else if (clientSK == null) {
        // Guardam la clau secreta del client
        byte[] encryptedMessage = message.getMessage();
        byte[] hash = message.getHash();

        byte[] decryptedMessage = EncryptionServer.decryptData(encryptedMessage, EncryptionServer.getKeys().getPrivate());
        byte[] decryptedHash = EncryptionServer.decryptData(hash, EncryptionServer.getKeys().getPrivate());

        byte[] serverHash = EncryptionCommon.generateHash(decryptedMessage);

        if(EncryptionServer.compareHash(decryptedHash, serverHash)) {
          clientSK = (SecretKey) SerializationUtils.deserialize(decryptedMessage);
        }

        System.out.println(clientSK.toString());

      } else {
        // Processam els missatges rebuts i enviam acús de rebut
        byte[] encryptedMessage = message.getMessage();
        byte[] hash = message.getHash();

        byte[] decryptedMessage = EncryptionCommon.decryptData(clientSK, encryptedMessage);
        byte[] decryptedHash = EncryptionCommon.decryptData(clientSK, hash);

        byte[] serverHash = EncryptionCommon.generateHash(decryptedMessage);

        if (EncryptionServer.compareHash(decryptedHash, serverHash)) {
          System.out.println(new String (decryptedMessage, StandardCharsets.UTF_8));

          byte[] received = "Missatge rebut".getBytes(StandardCharsets.UTF_8);

          cc.send(new Message(EncryptionCommon.encryptData(clientSK, received)));
        }

      }
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
