import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.net.ssl.*;

public class ServerMain {

  // Socket used to create connections.
  private SSLServerSocket serverSocket;
  private List<ConnectionThread> serverConnections;

  /**
   * Creates a socket to accept connections on the declared port.
   * Listens for connections, creating separate threads for each connection.
   * @param portNumber Port to accept connections on.
   */
  public ServerMain(Integer portNumber) {
	  //specifying the keystore file which contains the certificate/public key and private key
	  System.setProperty("javax.net.ssl.keyStore", "myKeyStore.jks");
	  //Specify password of the keystore file
	  System.setProperty("javax.net.ssl.keyStorePassword", "123456789");
	  //Dump details of the handshake process
	  //System.setProperty("javax.net.debug", "all");

    // Establish the server on a designated port and provide information about it.
    try {
    	//SSLServerSocketFactory establishes the ssl context and creates the SSLServerSocket
    	SSLServerSocketFactory factory = 
    			(SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
    	//Create SSL Socket using portNumber
    	serverSocket = (SSLServerSocket)factory.createServerSocket(portNumber);
    	serverConnections = new ArrayList<ConnectionThread>();
    	printServerInformation();
    	//At this point we are ready to accept a client connection

    } catch (IOException e) {
      System.out.println("Server could not start on " + String.valueOf(portNumber));
      System.out.println("Error: " + e.getMessage());
      System.exit(-1);
    }

    // Start listening for connections on that port.
    boolean finishedListening = false;
    while(!finishedListening) {

      // Wait for connections and thread if successful connection made.
      try {
        SSLSocket connection = (SSLSocket)serverSocket.accept();
        if(connection.isConnected()) {
          ConnectionThread connectionThread = new ConnectionThread(serverSocket, connection);
          connectionThread.start();
          serverConnections.add(connectionThread);
        }

      } catch (Exception e) {
        System.out.println("Accept failed: " + String.valueOf(portNumber));
        System.exit(-1);
      }

    }

  }

  /**
   * Display information about the server.
   */
  private void printServerInformation() {
    String status = "Running " +
      String.valueOf(serverSocket.getInetAddress().getHostName()) +
      " on port " + String.valueOf(serverSocket.getLocalPort());
    System.out.println(status);
  }

  /**
   * Executable method for running a server application.
   * @param args List of arguments to start the server.
   */
  public static void main(String[] args) {
    int port = Integer.valueOf(args[0]);
    @SuppressWarnings("unused")
    ServerMain server = new ServerMain(port);
  }

}