package shit;

import java.io.*;
import java.net.*;

public class Client {
	    private Socket clientSocket;
	    private PrintWriter out;
	    private BufferedReader in;
	    public void startConnection(String ip, int port) {
	    	try {
	        clientSocket = new Socket(ip, port);
	        out = new PrintWriter(clientSocket.getOutputStream(), true);
	        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	    	}catch (Exception e) {
				e.printStackTrace();
			}
	    }
	 
	    public String sendMessage(String msg) {
	        out.println(msg);
	        String resp = null;
	        try {
	        resp = in.readLine();
	        }catch (Exception e) {
				e.printStackTrace();
			}
	        return resp;    
	    }
	 
	    public void stopConnection() throws IOException {
	        in.close();
	        out.close();
	        clientSocket.close();
	}
}
