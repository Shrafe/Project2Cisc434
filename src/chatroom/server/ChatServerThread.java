package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ChatServerThread implements Runnable {
	private Socket socket;
	static int count; // REMOVE THIS
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
	 * Case 5: Whispering many target users
	 * Case 6: Existing user wishes to log in
	 * Case 7: New user wishes to register
	 */
	
	public void run(){
		byte messageType;
		try {
			while (connected){
				messageType = getMessageType();
				switch (messageType){
					case 0: 
						
						break;
					case 1:
						
						break;
					case 2:
						joinRoom();
						break;
					case 3:
						sendMessage();
						break;
					case 4:
						sendChatrooms();
						break;
					case 5:
						sendUsers();
						break;
					case 6:
						validUser();
						break;
					case 7:
						createUser();
						break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		} 
	}
	
	/** 
	 * Method that returns the value that precedes any date for all communication with the server
	 * 0: login with the following data
	 * 1: create a new user with the following data
	 * 2: join a room with the following data
	 * 3: send a message with the following data
	 * 4: send me a list of chatrooms
	 * 5: send me a list of users
	 * 
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	
	public byte getMessageType() throws IOException, ClassNotFoundException{
		byte result = -1;
		try{
			result = ((MsgObj)ois.readObject()).getType();
		} catch (SocketException e){
			oos.close();
			ois.close();
			socket.close();
			this.connected = false; //dirty again
			System.out.println("Client disconnected from: "+socket);
		}
		return result;
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

	public void validUser() throws IOException, ClassNotFoundException{
		String checkUsername = (String)ois.readObject();
		String checkPassword = (String)ois.readObject();
		if (chatServer.users.containsKey(checkUsername)){
			if (chatServer.users.get(checkUsername).equals(checkPassword)){
				this.username = checkUsername;
				this.password = checkPassword;
				System.out.println("User: "+this.username+" validated successfully.");
				sendChatrooms();
			}
			else {
				System.err.println("User: "+checkUsername+" validation failed from: "+socket);
				oos.writeObject(null);
			}
		}
		else {
			System.err.println("User: "+checkUsername+" gave wrong credentials from: "+socket);
			oos.writeObject(null);
		}
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
	
	public void createUser() throws IOException, ClassNotFoundException{
		String newUsername = (String)ois.readObject();
		String newPassword = (String)ois.readObject();
		String result = chatServer.addUser(newUsername, newPassword);
		System.out.println("New user created: "+ newUsername + " | Result of operation was: "+result);
		oos.writeObject(result);		
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
	
	public void joinRoom() throws IOException, ClassNotFoundException{
		String crn = (String)ois.readObject();		
		chatServer.joinChatroom(crn, oos, this.username); 
		System.out.println("User "+username+" joined room: "+ crn);
		this.crn = crn;
		sendUsers();
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
	
	public void sendMessage() throws IOException, ClassNotFoundException{
		String message = (String)ois.readObject();
		System.out.println("Message received from user: "+this.username+" at: "+this.socket);
		chatServer.chatRooms.get(this.crn).sendToClients(message);	
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
		oos.writeObject(chatServer.chatRoomNames);
		// for testing
		chatServer.createChatroom("test"+count);
		count++;
		System.out.println("Sent chatroom list to: "+this.username+" at: "+this.socket);
	}
	
	/**
	 * Same as above, but sends the list of users, not chatrooms
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
	
	public void sendUsers() throws IOException, ClassNotFoundException{
		oos.writeObject(this.chatServer.chatRooms.get(crn).getClientList());	
		System.out.println("Sent user list to: "+this.username+" at: "+this.socket);
	}
}
