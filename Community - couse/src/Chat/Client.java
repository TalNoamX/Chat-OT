package Chat;
import java.net.*;
import java.io.*;
/**
 * This class represents the client side of the chat
 * @author Tal
 * @author Oranit
 */
public class Client  {

	private ObjectInputStream sInput; // to read from the socket
	private ObjectOutputStream sOutput;	// to write on the socket
	private Socket socket;
	private ClientGUI clGUI;
	private String server, username; // the server, the port and the user name
	private int port;
	/**
	 * constructor used when start GUI
	 * @param server
	 * @param port
	 * @param username
	 * @param clGUI
	 */
	public Client(String server, int port, String username, ClientGUI clGUI) {
		this.server = server;
		this.port = port;
		this.username = username;
		this.clGUI = clGUI;
	}
	/**
	 * start the communication with the server
	 */
	public boolean start() {
		try {
			socket = new Socket(server, port);
		} 
		catch(Exception ec) {
			display("Error connectiong to server:" + ec);
			return false;
		}

		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);

		// Creating both Data Stream
		try {
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}
		// creates the Thread to listen from the server 
		new ListenFromServer().start();
		// Send our user name to the server.
		try {
			sOutput.writeObject(username);
		}
		catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		return true;
	}
	/**
	 * To send a message to the GUI
	 * @param msg - the message is about to send
	 */
	private void display(String msg) {
		clGUI.append(msg + "\n"); // append to the ClientGUI.
	}
	/**
	 * To send a message to the server
	 * @param msg - ChatMessage object
	 */
	public void sendMessage(ChatMessage msg) {
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}
	/**
	 * When something goes wrong Close the Input/Output streams and disconnect.
	 */
	private void disconnect() {
		try { 
			if(sInput != null) sInput.close();
			if(socket != null) socket.close();
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {}

		// inform the GUI
		clGUI.connectionFailed();		
	}
	/**
	 * inner class to client class.
	 * a class that waits for the message from the server and append them to the JTextArea.
	 * @author Tal
	 * @author Oranit
	 */
	class ListenFromServer extends Thread {
		/**
		 * get a message from the server and pass it on to the client GUI.
		 */
		public void run() {
			while(true) {
				try {
					String msg = (String) sInput.readObject();
					clGUI.append(msg);
				}
				catch(IOException e) {
					display("Server has close the connection: " + e);
					clGUI.connectionFailed();
					break;
				}
				catch(ClassNotFoundException e2) {
				}
			}
		}
	}
}