package chatroom.server;

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
	private String ip;
	private String username;
	private String crn; // the chatroom we're currently in
	private boolean connected;

	public ChatServerThread(Socket socket, ObjectOutputStream oos, ObjectInputStream ois, ChatServer chatserver){
		this.connected = true;
		this.socket = socket;
		this.chatServer = chatserver;
		this.ip = socket.getInetAddress().toString();
		try {
			//this.oos = new ObjectOutputStream(this.socket.getOutputStream());
			//ois = new ObjectInputStream(this.socket.getInputStream());
			this.oos = oos;
			this.ois = ois;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Case 0: Request to create a chatroom
	 * Case 1: User Exiting Chat Room
	 * Case 2: User Entering Chat Room
	 * Case 3: Posting to a Chat room
	 * Case 4: Whispering target users
	 * Case 5: Existing user wishes to log in
	 * Case 6: New user wishes to register
	 * Case 7: Notification that we're disconnecting
	 */

	public void run(){

		while (connected){
			try {
				MsgObj message = (MsgObj) ois.readObject();

				switch (message.getType()){
				case 0:
					createRoom(message);
					break;
				case 1:
					leaveRoom();
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
					clientDisconnect();
					break;
				}
			} catch (SocketException se){
				clientDisconnect();
				break;
			}
			catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e){
				e.printStackTrace();
			} 
		}

	}

	public void createRoom(MsgObj message) throws IOException, ClassNotFoundException{
		ArrayList<Object> payload = message.getPayload();
		String crn = (String) payload.get(0);
		MsgObj sendMessage = new MsgObj();
		byte type = 5;
		sendMessage.setType(type);
		boolean success = chatServer.createChatroom(crn);
		if (success){
			sendMessage.addToPayload("t");			
			System.out.println("Chatroom created: "+crn);
		}
		else {
			sendMessage.addToPayload("f");
			System.err.println("Attempt to create duplicate chatroom: "+crn);
		}
		oos.writeObject(sendMessage);		
	}
	
	
	/**
	 * method that nulls out the crn for this thread
	 */
	public void leaveRoom() throws IOException, ClassNotFoundException{
		chatServer.chatRooms.get(this.crn).removeClient(this.username); // remove us from the chatroom so we don't get anymore messages from it
		this.crn = null; // null out CRN
		sendChatrooms();
	}
	

	/** 
	 * Method that adds the user associated with this thread to the named chatroom
	 */

	public void joinRoom(MsgObj message) throws IOException, ClassNotFoundException{
		ArrayList<Object> payload = message.getPayload();
		String crn = (String) payload.get(0);
		MsgObj sendMessage = new MsgObj();
		byte type = 6;
		sendMessage.setType(type);
		boolean success = chatServer.joinChatroom(crn, oos, this.username);
		if (success){
			sendMessage.addToPayload("t");
			System.out.println("User "+username+" joined room: "+ crn);
			this.crn = crn;
		}else{
			sendMessage.addToPayload("f");
			System.err.println("User "+username+" attempted to join non-existent room: "+crn);
		}
		oos.writeObject(sendMessage);
	}
	
	/**
	 * Method that sends the data from the client as a message to all
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */

	public void sendMessage(MsgObj message, boolean isWhisper) throws IOException, ClassNotFoundException{
		// we know what to do. the payload is going to contain a single string, containing the 
		// message he wants to send to the chatroom he's currently in. 
		ArrayList<Object> payload = message.getPayload();
		String msg = (String)payload.get(0);
		String sender = null;
		List<String> clients = null;
		sender = (String) payload.get(1);
		if (isWhisper){
			// get the list of clients we should send to
			clients = (List<String>) payload.get(2);
		}
		System.out.println("Message received from user: "+this.username+" at: "+this.socket);
		chatServer.chatRooms.get(this.crn).sendToClients(msg, sender, clients);	
	}

	/**
	 * Method that checks whether or not the data given represents a valid user
	 * 
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */

	public void validUser(MsgObj message) throws IOException, ClassNotFoundException{
		ArrayList<Object> payload = message.getPayload();
		String checkUsername = (String)payload.get(0); // we know that the username is first
		String checkPassword = (String)payload.get(1); // and the password is second
		String result = chatServer.validateUser(checkUsername, checkPassword);
		MsgObj sendMessage = new MsgObj();
		byte type = 2;
		sendMessage.setType(type);
		sendMessage.addToPayload(result);
		oos.writeObject(sendMessage);
		if (result.equals("t")){
			this.username = checkUsername +" ("+this.ip+")";
			System.out.println("User: "+this.username+" validated successfully.");
			sendChatrooms();
		} else {
			System.err.println("Wrong credentials from: "+socket+" for username: "+checkUsername);			
		}
	}
	
	
	
	/**
	 * Method that sends the String[] of chatrooms, because the client 
	 * requested it for some reason
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
	 * Method that adds a new user to the chatserver. 
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */

	public void createUser(MsgObj message) throws IOException, ClassNotFoundException{
		ArrayList<Object> payload = message.getPayload();
		String newUsername = (String)payload.get(0); // first value in the payload is the username;
		String newPassword = (String)payload.get(1); // second is the password
		String result = chatServer.addUser(newUsername, newPassword);
		System.out.println("New user request: "+ newUsername + " | Result of operation was: "+result);
		MsgObj sendMessage = new MsgObj();
		sendMessage.addToPayload(result);
		byte type = 3;
		sendMessage.setType(type);
		oos.writeObject(sendMessage);		
	}

	/**
	 * Disconnects the client cleanly, so the server won't fail if we have a SocketException
	 * 
	 */
	public void clientDisconnect() {
		try{
			if (crn != null){ // leave the room if we're in one.
				leaveRoom();
			}			
			chatServer.clients.remove(this.oos);
			oos.close();
			ois.close(); // close our streams
			socket.close(); // close the streams
			this.connected = false;
			System.out.println("Client: "+socket+" disconnected.");
			
		} catch(Exception e){} // swallow
	}
}
