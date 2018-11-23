package NewChat_1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JLabel label;						// will first hold "Username:", later on "Enter message"
	private JTextField tf;						// to hold the Username and later on the messages
	private JTextField tfServer, tfPort; 		// to hold the server address and the port number
	private JButton login, logout, whoIsIn;		// to Logout and get the list of the users
	private JTextArea cRoom;					// for the chat room
	private boolean connected;					// if it is for connection
	private Client client;						// the Client object
	private int myPort;					// the default port number
	private String myHost;

	// Constructor connection receiving a socket number
	 ClientGUI(String host, int port) {
		super("Chat Client");
		myPort = port;
		myHost = host;
		
		JPanel northPanel = new JPanel(new GridLayout(3,1)); // The NorthPanel.
		JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 3)); // the server name and the port number.
		
		// the two JTextField with default value for server address and port number
		tfServer = new JTextField(host);
		tfPort = new JTextField("" + port);
		tfPort.setHorizontalAlignment(SwingConstants.RIGHT);

		serverAndPort.add(new JLabel("Server Address:  "));
		serverAndPort.add(tfServer);
		serverAndPort.add(new JLabel("Port Number:  "));
		serverAndPort.add(tfPort);
		serverAndPort.add(new JLabel(""));
		// adds the Server a port field to the GUI
		northPanel.add(serverAndPort);

		// the Label and the TextField
		label = new JLabel("Enter your name: ", SwingConstants.CENTER);
		northPanel.add(label);
		tf = new JTextField("John Doe");
		tf.setBackground(Color.PINK);
		northPanel.add(tf);
		add(northPanel, BorderLayout.NORTH);

		// The CenterPanel which is the chat room
		cRoom = new JTextArea("Welcome to the Chat room\n", 80, 80);
		JPanel centerPanel = new JPanel(new GridLayout(1,1));
		centerPanel.add(new JScrollPane(cRoom));
		cRoom.setEditable(false);
		add(centerPanel, BorderLayout.CENTER);

		// the 3 buttons
		login = new JButton("Login");
		login.addActionListener(this);
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);		// you have to login before being able to logout
		whoIsIn = new JButton("Who is in");
		whoIsIn.addActionListener(this);
		whoIsIn.setEnabled(false);		// you have to login before being able to Who is in

		JPanel southPanel = new JPanel();
		southPanel.add(login);
		southPanel.add(logout);
		southPanel.add(whoIsIn);
		add(southPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		tf.requestFocus();
	}

	// called by the Client to append text in the TextArea 
	 void append(String str) {
		cRoom.append(str);
		cRoom.setCaretPosition(cRoom.getText().length() - 1);
	}
	// called by the GUI if the connection failed
	// we reset our buttons, label, textfield.
	  void connectionFailed() {
		login.setEnabled(true);
		logout.setEnabled(false);
		whoIsIn.setEnabled(false);
		label.setText("Enter your name: ");
		tf.setText("John Doe");
		tfPort.setText("" + myPort); // reset port number and host name as a construction time
		tfServer.setText(myHost);
		tfServer.setEditable(false); // let the user change them
		tfPort.setEditable(false);
		tf.removeActionListener(this); // don't react to a <CR> after the username
		connected = false;
	}
		
	// Button or JTextField clicked
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		// if it is the "Logout" button
		if(o == logout) {
			client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
			return;
		}
		// if it's the "who is in" button
		if(o == whoIsIn) {
			client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));				
			return;
		}
		// if it is coming from the JTextField
		if(connected) {
			// just have to send the message
			client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, tf.getText()));				
			tf.setText("");
			return;
		}
		

		if(o == login) {
			String username = tf.getText().trim(); // if it is a connection request
			if(username.length() == 0) return; // empty username ignore it
			String server = tfServer.getText().trim(); // empty serverAddress ignore it
			
			if(server.length() == 0) return;
			String portNumber = tfPort.getText().trim(); // empty or invalid port number, ignore it

			if(portNumber.length() == 0) return;
			int port = 0;
			
			try {
				port = Integer.parseInt(portNumber);
			}
			catch(Exception en) { return; } // nothing I can do if port number is not valid

			client = new Client(server, port, username, this); // try creating a new Client with GUI

			// test if we can start the Client
			if(!client.start()) return;
			tf.setText("");
			label.setText("Enter your message below");
			connected = true;
			
			login.setEnabled(false); // disable login button
			// enable the 2 buttons
			logout.setEnabled(true); 
			whoIsIn.setEnabled(true);
			// disable the Server and Port JTextField
			tfServer.setEditable(false);
			tfPort.setEditable(false);
			tf.addActionListener(this);	// Action listener for when the user enter a message
		}
	}
	// to start the whole thing the server
	public static void main(String[] args) {
		new ClientGUI("localhost", 8080);
	}
}