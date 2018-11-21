package Ex_04;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

	private int port;
	private List<User> clients;
	private ServerSocket server;

	public static void main(String[] args) throws IOException {
		new Server(8080).run();
	}

	public Server(int port) {
		this.port = port;
		this.clients = new ArrayList<User>();
	}

	public void run() throws IOException {
		server = new ServerSocket(port) {
			protected void finalize() throws IOException {
				this.close();
			}
		};
		System.out.println("Port "+port+" is now open.");

		while (true) {
			// accepts a new client
			Socket client = server.accept();

			// get nickname of newUser
			String nickname = (new Scanner ( client.getInputStream() )).nextLine();
			nickname = nickname.replace(",", ""); //  ',' use for serialisation
			nickname = nickname.replace(" ", "_");
			System.out.println("New Client: \"" + nickname +"\" is connected"+"\n\t     "+nickname+"'s IP: " + client.getInetAddress().getHostAddress());

			// create new User
			User newUser = new User(client, nickname);

			// add newUser message to list
			this.clients.add(newUser);

			// Welcome message
			newUser.getOutStream().println("<b>Welcome to our cool chat:)</b> " + newUser.toString());


			// create a new thread for newUser incoming messages handling
			new Thread(new UserHandler(this, newUser)).start();
		}
	}

	// delete a user from the list
	public void removeUser(User user){
		this.clients.remove(user);
	}

	// send incoming messages to all Users
	public void broadcastMessages(String msg, User userSender) {
		for (User client : this.clients) {
			client.getOutStream().println(
					userSender.toString() + "<span>: " + msg+"</span>");
		}
	}

	// send the list of clients to all the Users
	public void broadcastAllUsers(){
		for (User client : this.clients) {
			client.getOutStream().println(this.clients);
		}
	}

	// send a message to a User (String)
	public void sendMessageToUser(String msg, User userSender, String user){
		boolean find = false;
		for (User client : this.clients) {
			if (client.getNickname().equals(user) && client != userSender) {
				find = true;
				userSender.getOutStream().println(userSender.toString() + " -> " + client.toString() +": " + msg);
				client.getOutStream().println(
						"(<b>Private</b>)" + userSender.toString() + "<span>: " + msg+"</span>");
			}
		}
		if (!find) {
			userSender.getOutStream().println(userSender.toString() + " -> (<b>There is no such user</b>): " + msg);
		}
	}
}

class UserHandler implements Runnable {

	private Server server;
	private User user;

	public UserHandler(Server server, User user) {
		this.server = server;
		this.user = user;
		this.server.broadcastAllUsers();
	}

	public void run() {
		String message;

		// when there is a new message, broadcast to all
		Scanner sc = new Scanner(this.user.getInputStream());
		while (sc.hasNextLine()) {
			message = sc.nextLine();

			// Private message management
			if (message.charAt(0) == '@'){
				if(message.contains(" ")){
					System.out.println("private msg : " + message);
					int firstSpace = message.indexOf(" ");
					String userPrivate= message.substring(1, firstSpace);
					server.sendMessageToUser(
							message.substring(
									firstSpace+1, message.length()
									), user, userPrivate
							);
				}

				// Change management
			}else if (message.charAt(0) == '#'){
				// update color for all other users
				this.server.broadcastAllUsers();
			}else{
				// update user list
				server.broadcastMessages(message, user);
			}
		}
		// end of Thread
		server.removeUser(user);
		this.server.broadcastAllUsers();
		sc.close();
	}
}

class User {
	private PrintStream streamOut;
	private InputStream streamIn;
	private String nickname;
	private String color;

	// constructor
	public User(Socket client, String name) throws IOException {
		this.streamOut = new PrintStream(client.getOutputStream());
		this.streamIn = client.getInputStream();
		this.nickname = name;
	}

	// getter
	public PrintStream getOutStream(){
		return this.streamOut;
	}

	public InputStream getInputStream(){
		return this.streamIn;
	}

	public String getNickname(){
		return this.nickname;
	}

	// print user with his color
	public String toString(){

		return "<u><span style='color:"+ this.color
				+"'>" + this.getNickname() + "</span></u>";
	}
}
