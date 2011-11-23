package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class ChatServerThread implements Runnable {
	private Socket socket;
	private ChatServer chatServer;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private String username;
	private String password;
	private String crn; // the chatroom we're currently in
	private boolean connected;

	public ChatServerThread(Socket socket, ChatServer chatserver){
		this.connected = true;
		this.socket = socket;
		this.chatServer = chatserver;
		try {
			oos = new ObjectOutputStream(this.socket.getOutputStream());
			ois = new ObjectInputStream(this.socket.getInputStream());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Case 0: Request for list of Chat Rooms
	 * Case 1: User Exiting Chat Room
	 * Case 2: User Entering Chat Room
	 * Case 3: Posting to a Chat room
	 * Case 4: Whispering one target user
	 * Case 5: Existing user wishes to log in
	 * Case 6: New user wishes to register
	 * Case 7: Request for list of users 
	 */

	public void run(){

		while (true){
			try {
				MsgObj message = (MsgObj) ois.readObject();

				switch (message.getType()){
				case 0: 
					sendChatrooms();
					break;
				case 1:
					leaveRoom(message);
					break;
				case 2:
					joinRoom(message);
					break;
				case 3:
					sendMessage(message, false);
					break;
				case 4:
					sendMessage(message, true);
					break;
				case 5:
					validUser(message);
					break;
				case 6:
					createUser(message);
					break;
				case 7:
					sendUsers();
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e){
				e.printStackTrace();
			} 
		}

	}

	/**
	 * Method that checks whether or not the data given represents a valid user
	 * 
	 * Input from socket:
	 * String username
	 * AND
	 * String password
	 * 
	 * Output to socket: 
	 * String[] of chatroom names if validation is successful
	 * OR
	 * null if validation fails 
	 * 
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */

	public void validUser(MsgObj message) throws IOException, ClassNotFoundException{
		ArrayList<Object> payload = message.getPayload();
		String checkUsername = (String)payload.get(0); // we know that the username is first
		String checkPassword = (String)payload.get(1); // and the password is second
		if (chatServer.users.containsKey(checkUsername)){
			if (chatServer.users.get(checkUsername).equals(checkPassword)){
				this.username = checkUsername;
				this.password = checkPassword;
				System.out.println("User: "+this.username+" validated successfully.");
				sendChatrooms();
			}
			else {
				System.err.println("User: "+checkUsername+" validation failed from: "+socket);
				loginFailed();
			}
		}
		else {
			System.err.println("User: "+checkUsername+" gave wrong credentials from: "+socket);
			loginFailed();
		}
	}

	/**
	 * Helper to send an empty MsgObj with type = 2, to indicate a failed login attempt.
	 * no message is sent when login is a success; we simply recieve the list of chatrooms
	 * and go on our way 
	 */
	public void loginFailed(){
		MsgObj sendMessage = new MsgObj();
		byte type = 2;
		oos.writeObject(sendMessage);
	}

	/**
	 * Method that adds a new user to the chatserver. 
	 * 
	 * Input from socket:
	 * String username
	 * AND
	 * String password
	 * 
	 * Output to socket:
	 * String result of the addUser operation ("dup" || "success") 
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */

	public void createUser(MsgObj message) throws IOException, ClassNotFoundException{
		ArrayList<Object> payload = message.getPayload();
		String newUsername = (String)payload.get(0); // first value in the payload is the username;
		String newPassword = (String)payload.get(1); // second is the password
		String result = chatServer.addUser(newUsername, newPassword);
		System.out.println("New user created: "+ newUsername + " | Result of operation was: "+result);
		MsgObj sendMessage = new MsgObj();
		sendMessage.addToPayload(result);
		byte type = 3;
		oos.writeObject(sendMessage);		
	}

	/** 
	 * Method that adds the user associated with this thread to the named chatroom
	 * 
	 * Input from socket:
	 * String chatroomName
	 * 
	 * Output to socket:
	 * list of users in this chatroom 
	 * 
	 */

	public void joinRoom(MsgObj message) throws IOException, ClassNotFoundException{
		ArrayList<Object> payload = message.getPayload();
		String crn = (String) payload.get(0);
		chatServer.joinChatroom(crn, oos, this.username);
		System.out.println("User "+username+" joined room: "+ crn);
		this.crn = crn;
		sendUsers();
	}
	
	/**
	 * method that nulls out the crn for this thread
	 */
	public void leaveRoom(){
		chatServer.chatRooms.get(this.crn).removeClient(this.username); // remove us from the chatroom so we don't get anymore messages from it
		this.crn = ""; // empty string for crn
	}
	
	
	/**
	 * Method that sends the data from the client as a message to all
	 * clients connected to the chatroom that this client is in
	 * 
	 * Input from socket:
	 * String message
	 * 
	 * Output to socket (done by the chatroom):
	 * String message
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */

	public void sendMessage(MsgObj message, boolean isWhisper) throws IOException, ClassNotFoundException{
		// we know what to do. the payload is going to contain a single string, containing the 
		// message he wants to send to the chatroom he's currently in. 
		ArrayList<Object> payload = message.getPayload();
		String msg = (String)payload.get(0);
		List<String> clients = null;
		if (isWhisper){
			// get the list of clients we should send to
			clients = (List<String>) payload.get(1);
		}
		System.out.println("Message received from user: "+this.username+" at: "+this.socket);
		chatServer.chatRooms.get(this.crn).sendToClients(msg, clients);	
	}

	/**
	 * Method that sends the String[] of chatrooms, because the client 
	 * requested it for some reason
	 * 
	 * Input from socket:
	 * none 
	 * 
	 * Output to socket:
	 * String[] of chat rooms on the server at the moment
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */

	public void sendChatrooms() throws IOException, ClassNotFoundException{
		//send this client a list of chatrooms. easy enough
		MsgObj sendMessage = new MsgObj();
		sendMessage.addToPayload(chatServer.chatRoomNames);
		byte type = 0; // is there a better way to do this?
		sendMessage.setType(type);
		oos.writeObject(sendMessage);
		System.out.println("Sent chatroom list to: "+this.username+" at: "+this.socket);
	}

	/**
	 * Same as above, but sends the list of users, not chatrooms
	 * will fail if crn is null, but we should never be requesting refreshes if crn is null
	 * 
	 * Input from socket: 
	 * none
	 * 
	 * Output to socket:
	 * String[] users in the chatroom we are in
	 * 
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */

	public void sendUsers(){
		MsgObj sendMessage = new MsgObj();
		// dirty tricks: payload in MsgObj is of type Object; we can add the entire list at once. 
		// do it and since we can tell the reciever what to do, we can be sure of the casting clientside
		sendMessage.addToPayload(this.chatServer.chatRooms.get(this.crn).getClientList());
		byte type = 1; // argh really?
		sendMessage.setType(type);
		oos.writeObject(sendMessage);	
		System.out.println("Sent user list to: "+this.username+" at: "+this.socket);
	}
}
