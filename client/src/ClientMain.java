import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.ssl.*;


public class ClientMain {
	
	//Socket factory
	private SSLSocketFactory factory;

  // Socket for connecting with the server.
  private SSLSocket serverSocket;

  // Thread for writing the users input to the servers output.
  private StreamSwapper serverReader;

  // Thread for writing the servers output to the users output.
  private StreamSwapper serverWriter;

  /**
   * Connect with the provided server details and start reading and writing to the server.
   * @param serverName Name of the server to connect to.
   * @param portNumber Port of the server to connect to.
   * @throws IOException
   */
  public ClientMain(String serverName, int portNumber) {
	  //Specify trustStore file which contains the certificate & public key of the server
	  System.setProperty("javax.net.ssl.trustStore", "myTrustStore.jts");
	  //Set password for TrustStore
	  System.setProperty("javax.net.ssl.trustStorePassword", "123456789");
	  //OPTIONAL: dump details of handshake
	  //System.setProperty("javax.net.debug", "all");

    // Try and connect to the server.
    try {
    	//SSLSocketFactory establishes the SSL context and creates SSLSocket
    	factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
    	
      serverSocket = (SSLSocket)factory.createSocket(serverName, portNumber);

      // Start a thread to read output from the server.
      serverReader = new StreamSwapper(serverSocket.getInputStream(), System.out);
      serverReader.start();

      // Start a thread to write input to the server.
      serverWriter = new StreamSwapper(System.in, serverSocket.getOutputStream());
      serverWriter.start();

      // Keep the threads and socket active until we are finished.
      boolean finished = false;
      while(!finished);

      // Close the socket if we ever finish.
      serverSocket.close();

    } catch (UnknownHostException e) {
      System.err.println("Can't find the server.");
      System.exit(-1);

    } catch (IOException e) {
      System.err.println("Couldn't get I/O from the server.");
      System.exit(-1);
    }

  }

  /**
   * Executable method for running a client application.
   * @param args Name of the server and its operating port.
   */
  public static void main(String[] args) {
    @SuppressWarnings("unused")
    ClientMain clientApplication = new ClientMain(args[0], Integer.valueOf(args[1]));
  }

}