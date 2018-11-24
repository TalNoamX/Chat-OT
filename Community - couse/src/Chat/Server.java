package Chat;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {
	private static int uniqueId; 	// a unique ID for each connection
	private ArrayList<ClientThread> cList; 	// an ArrayList to keep the list of the Client
	private ServerGUI serGUI; 
	private SimpleDateFormat sdf;	// to display time
	private int port; 	// the port number to listen for connection
	private boolean keepGoing; 	// the boolean that will be turned of to stop the server

	//server constructor that receive the port to listen to for connection.
	public Server(int port, ServerGUI serGUI) {
		this.serGUI = serGUI;
		this.port = port;
		sdf = new SimpleDateFormat("HH:mm:ss"); // to display hh:mm:ss
		cList = new ArrayList<ClientThread>();	// ArrayList for the Client list
	}

	public void start() { // create socket server and wait for connection requests
		keepGoing = true;
		try {
			ServerSocket serverSocket = new ServerSocket(port); // the socket used by the server

			while(keepGoing) { // infinite loop to wait for connections

				display("Server waiting for Clients on port " + port + "."); // format message saying we are waiting
				Socket socket = serverSocket.accept();  // accept connection

				// if I was asked to stop
				if(!keepGoing) break;

				ClientThread t = new ClientThread(socket);  // make a thread of it
				cList.add(t); // save it in the ArrayList
				t.start();
			}
			// I was asked to stop
			try {
				serverSocket.close();
				for(int i = 0; i < cList.size(); ++i) {
					ClientThread tc = cList.get(i);
					try {
						tc.sInput.close();
						tc.sOutput.close();
						tc.socket.close();
					}
					catch(IOException ioE) {
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		// something went bad
		catch (IOException e) {
			String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}		
	/*
	 * For the GUI to stop the server
	 */
	protected void stop() {
		keepGoing = false;
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
			// nothing I can really do
		}
	}

	// Display an event.
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		serGUI.appendEvent(time + "\n");
	}
	/*
	 *  to broadcast a message to all Clients
	 */
	private synchronized void broadcast(String message) {
		String time = sdf.format(new Date());		// add HH:mm:ss and \n to the message
		String messageLf = time + " " + message + "\n";
		serGUI.appendRoom(messageLf);     // append in the room window

		// we loop in reverse order in case we would have to remove a Client
		// because it has disconnected
		for(int i = cList.size(); --i >= 0;) {
			ClientThread clientT = cList.get(i);

			// try to write to the Client if it fails remove it from the list
			if(!clientT.writeMsg(messageLf)) {
				cList.remove(i);
				display("Disconnected Client " + clientT.username + " removed from list.");
			}
		}
	}

	// for a client who log off using the LOGOUT message
	synchronized void remove(int id) {
		// scan the array list until we found the Id
		for(int i = 0; i < cList.size(); ++i) {
			ClientThread ct = cList.get(i);
			// found it
			if(ct.id == id) {
				cList.remove(i);
				return;
			}
		}
	}

	/** One instance of this thread will run for each client */
	class ClientThread extends Thread {
		Socket socket; 	// the socket where to listen/talk
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		int id;	// my unique id (easier for disconnection)
		String username; // the User name of the Client
		ChatMessage cm;	// the only type of message a will receive
		String date; // the date I connect

		// Constructor
		ClientThread(Socket socket) {
			id = ++uniqueId; // a unique id
			this.socket = socket;
			try
			{
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				username = (String) sInput.readObject(); // read the username
				display(username + " just connected.");
			}
			catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}

			catch (ClassNotFoundException e) {
			}
			date = new Date().toString() + "\n";
		}

		public void run() {
			// to loop until LOGOUT
			boolean keepGoing = true;
			while(keepGoing) {
				// read a String (which is an object)
				try {
					cm = (ChatMessage) sInput.readObject();
				}
				catch (IOException e) {
					display(username + " Exception reading Streams: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {
					break;
				}
				// the messaage part of the ChatMessage
				String message = cm.getMessage();

				// Switch on the type of message receive
				switch(cm.getType()) {

				case ChatMessage.MESSAGE:
					broadcast(username + ": " + message);
					break;
				case ChatMessage.LOGOUT:
					display(username + " disconnected with a LOGOUT message.");
					keepGoing = false;
					break;
				case ChatMessage.WHOISIN:
					writeMsg("List of the users connected at " + sdf.format(new Date()) + "\n");
					// scan all the users connected
					for(int i = 0; i < cList.size(); ++i) {
						ClientThread ct = cList.get(i);
						writeMsg((i+1) + ") " + ct.username + " since " + ct.date);
					}
					break;
				}
			}
			// remove myself from the arrayList containing the list of the
			// connected Clients
			remove(id);
			close();
		}

		// try to close everything
		private void close() {
			// try to close the connection
			try {
				if(sOutput != null) sOutput.close();
				if(socket != null) socket.close();
				if(sInput != null) sInput.close();
			}
			catch (Exception e) {}
		}

		// Write a String to the Client output stream
		private boolean writeMsg(String msg) {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				sOutput.writeObject(msg);
			}
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}
	}
}

