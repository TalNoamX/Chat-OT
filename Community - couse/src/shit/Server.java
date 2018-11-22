package shit;

import java.io.*;
import java.net.*;

public class Server {
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private PrintWriter output;
	private BufferedReader input;
	public void start(int port)  {
		try {
			serverSocket= new ServerSocket(port);
			clientSocket = serverSocket.accept();
			output = new PrintWriter(clientSocket.getOutputStream(), true);
			input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String msg;
			while ((msg = input.readLine()) != null) {
				if (".".equals(input)) {
					output.println("good bye");
					break;
				}
				output.println(input);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public void stop() throws IOException {
		input.close();
		output.close();
		clientSocket.close();
		serverSocket.close();
	}
	public static void main(String[] args) {
        Server server=new Server();
        server.start(6666);
    }

}
