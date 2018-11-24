package Chat;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ServerGUI extends JFrame implements ActionListener, WindowListener {
	
	private static final long serialVersionUID = 1L;
	private JButton stopStart;	// the stop and start buttons.
	private JTextArea chat, event;	// JTextArea for the chat room and the events.
	private JTextField tPortNumber;	// The port number.
	private Server server;	// my server.

	// server constructor that receive the port to listen to for connection as parameter
	 ServerGUI(int port) {
		super("Chat Server");
		server = null;
		JPanel top = new JPanel(); // in the top Panel - the PortNumber, the Start, and Stop buttons.
		top.add(new JLabel("Port number: "));
		tPortNumber = new JTextField("  " + port);
		top.add(tPortNumber);
		stopStart = new JButton("Start"); // to stop or start the server, we start with "Start"
		stopStart.addActionListener(this);
		top.add(stopStart);
		add(top, BorderLayout.NORTH);
		
		// the event and chat room
		JPanel center = new JPanel(new GridLayout(2,1));
		chat = new JTextArea(80,80);
		chat.setEditable(false);
		appendRoom("Chat room.\n");
		center.add(new JScrollPane(chat));
		event = new JTextArea(80,80);
		event.setEditable(false);
		appendEvent("Events log.\n");
		center.add(new JScrollPane(event));	
		add(center);
		
		// need to be informed when the user click the close button on the frame
		addWindowListener(this);
		setSize(400, 600);
		setVisible(true);
	}		

	// append message to the two JTextArea
	// position at the end
	void appendRoom(String str) {
		chat.append(str);
		chat.setCaretPosition(chat.getText().length() - 1);
	}
	void appendEvent(String str) {
		event.append(str);
		event.setCaretPosition(chat.getText().length() - 1);
		
	}
	
	// start or stop where clicked
	public void actionPerformed(ActionEvent e) {
		// if running we have to stop
		if(server != null) {
			server.stop();
			server = null;
			tPortNumber.setEditable(true);
			stopStart.setText("Start");
			return;
		}
      	// OK start the server	
		int port;
		try {
			port = Integer.parseInt(tPortNumber.getText().trim());
		}
		catch(Exception er) {
			appendEvent("Invalid port number");
			return;
		}
		server = new Server(port, this); // create a new Server
		new ServerRunning().start(); // and start it as a thread
		stopStart.setText("Stop");
		tPortNumber.setEditable(false);
	}
	// If the user click the X button to close the application close the sever connection.
	public void windowClosing(WindowEvent e) {
		// if my Server exist
		if(server != null) {
			try {
				server.stop(); // ask the server to close the connection
			}
			catch(Exception eClose) {
			}
			server = null;
		}
		dispose();		// dispose the frame
		System.exit(0);
	}
	
	// I can ignore the other WindowListener method
	public void windowClosed(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}

	// A thread to run the Server
	class ServerRunning extends Thread {
		public void run() {
			server.start(); // should execute until it fails
			// the server failed
			stopStart.setText("Start");
			tPortNumber.setEditable(true);
			appendEvent("Server crashed\n");
			server = null;
		}
	}
	// entry point to start the Server
	public static void main(String[] arg) {
		new ServerGUI(8080); // start server default port 8080
	}
}