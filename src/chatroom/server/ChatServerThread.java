package chatroom.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ChatServerThread implements Runnable {
	Socket socket;
	ChatServer chatServer;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	String username;
	String password;

	public ChatServerThread(Socket socket, ChatServer chatserver){
		this.socket = socket;
		this.chatServer = chatserver;
		try {
			oos = new ObjectOutputStream(this.socket.getOutputStream());
			ois = new ObjectInputStream(this.socket.getInputStream());
			loadUserInfo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Initial messages from client to server are
	 * 1) Username 
	 * 2) password
	 * Followed by any number of 
	 * 3) Chatroom name
	 * 4) Any number of messages
	 */

	public void run(){
		try {
			if (validUser()){

				// give the user a list of chatrooms 
				oos.writeObject(chatServer.chatRoomNames); // send the client the map of available chatrooms. process clientside. don't actually need the chatroom objects, just names
				String crn = (String)ois.readObject(); // wait here for the name of the chatroom the client wants to join
				chatServer.joinChatroom(crn, oos, this.username); // some logic to either be added to the chatroom, or create a new one
				chatServer.chatRooms.get(crn).sendClientList(this.username); // send the chatroom's client list to the client (String[])
				startChat(crn); // start chatting in that room
				// TODO: need to add a way to have startChat() exist elegantly.
				// TODO: need some kind of looping structure here, maybe while (true) will work, perhaps something more elegant
			}
			else
				System.err.println("User validation failed for "+ socket);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		} 
	}

	public boolean validUser() throws IOException, ClassNotFoundException{
		return chatServer.users.get(username).equals(password);
	}

	public void loadUserInfo() throws IOException, ClassNotFoundException{
		this.username = (String)ois.readObject();
		this.password = (String)ois.readObject();
	}

	/**
	 * Method for being in a chat session. loops until the user leaves the chatroom (not yet implemented.)
	 * @param crn
	 */

	public void startChat(String crn){
		try{
			while (true){
				String message = (String) ois.readObject();
				System.out.println("Received message from: "+socket);
				chatServer.chatRooms.get(crn).sendToClients(message);
			}
		}
		catch(EOFException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		finally{
			chatServer.chatRooms.get(crn).removeConnection(socket); // may not want to do this
		}
	}



}
