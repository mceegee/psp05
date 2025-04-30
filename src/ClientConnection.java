import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClientConnection implements Runnable {

  private int port;
  private InetAddress ip;
  private Socket clientSocket;
  ObjectOutputStream sendDataPackage;

  private SocketCallback callback;

  // Inicialització connexió del client
  public ClientConnection(Socket clientSocket, SocketCallback callback) {
    this.clientSocket = clientSocket;
    port = clientSocket.getPort();
    ip = clientSocket.getInetAddress();

    this.callback = callback;

    try {
      sendDataPackage = new ObjectOutputStream(clientSocket.getOutputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void run() {

    Message message;

    try {
      ObjectInputStream getDataPackage = new ObjectInputStream(clientSocket.getInputStream());
      while (clientSocket.isConnected()) {
        message = (Message) getDataPackage.readObject();
        if (callback != null) {
          callback.socketCallback(this, message);
        }

      }
    } catch (ClassNotFoundException | IOException e) {

    }
  }

  // Enviar missatges
  public void send(Message message) {
    try {
      sendDataPackage.writeObject(message);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  //Desconnexió del server
  public void disconnect() {
    try {
      clientSocket.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
