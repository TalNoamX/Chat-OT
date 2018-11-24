package Chat;

import java.io.*;
/**
 * This class defines the different type of messages that will be exchanged between the
 * Clients and the Server.
 *	 @author Tal
 *   @author Oranit
 */
public class ChatMessage implements Serializable {

	protected static final long serialVersionUID = 1112122200L;
	/**
	 * 	 The different types of message sent by the Client:
	 *	1. WHOISIN to receive the list of the users connected
	 *	2. MESSAGE an ordinary message
	 *	3. LOGOUT to disconnect from the Server
	 */
	static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2;
	private int type;
	private String message;

	/**
	 * constructor
	 * 
	 * @param type - WHOISIN,MESSAGE,LOGOUT.
	 * @param message - just a String
	 */
	ChatMessage(int type, String message) {
		this.type = type;
		this.message = message;

	}
	/**
	 * getter 
	 * @return the type
	 */
	int getType() {
		return type;
	}
	/**
	 * getter 
	 * @return the message
	 */
	String getMessage() {
		return message;
	}
}
