import java.io.Serializable;

public class Message implements Serializable {

  byte[] message;
  byte[] hash;

  public Message(byte[] m, byte[] h) {
    message = m;
    hash = h;
  }

  public Message(byte[] m) {
    message = m;
  }

  public byte[] getMessage() {
    return message;
  }

  public byte[] getHash() {
    return hash;
  }
}
