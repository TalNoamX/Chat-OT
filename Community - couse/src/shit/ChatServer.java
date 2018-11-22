package shit;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
	public static void main (String[] args) {
		int PortNumber = 6666;
		try {
			ServerSocket serverSocket = new ServerSocket(PortNumber);
			while(true) {
				Socket clientSocket = serverSocket.accept();
				Thread t = new Thread() {
					public void run() {
						acceptClients(clientSocket);
					}
				};
				t.start();
			}
		} catch(IOException e) {
			System.err.println("could not  listen on port: " + PortNumber);
			System.exit(1);
		}
	}
	public static void acceptClients(Socket clientSocket) {
		OutputStream outputStream;
		try {
			outputStream = clientSocket.getOutputStream();
			outputStream.write("Welcom to the chat\n".getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
