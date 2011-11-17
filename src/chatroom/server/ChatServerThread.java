package chatroom.server;

import java.io.DataInputStream;
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
	String userName;
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

				oos.writeObject(chatServer.chatRooms); // send the client the map of available chatrooms. process clientside

				String crn = (String)ois.readObject(); // wait here for the name of the chatroom the client wants to join

				if (!chatServer.chatRooms.containsKey(crn)) // if the requested chatroom doesn't exist
					chatServer.createChatroom(crn); // create it on the server
				else // it exists; add the client to that chatroom
					chatServer.chatRooms.get(crn).addClient(this.socket, this.userName); // add the client to the appropriate chatroom's client list
				startChat(crn); // start chatting in that room

				// TODO: need to add a way to have startChat() exist elegantly.
				// TODO: need some kind of looping structure here, maybe while (true) will work, perhaps something mroe elegant
			}
			else
				System.out.println("User validation failed for "+ socket);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		} 
	}

	public boolean validUser() throws IOException, ClassNotFoundException{
		return chatServer.users.get(userName).equals(password);
	}
	
	public void loadUserInfo() throws IOException, ClassNotFoundException{
		this.userName = (String)ois.readObject();
		this.password = (String)ois.readObject();
	}

	/**
	 * Method for being in a chat session. loops until the user leaves the chatroom (not yet implemented.)
	 * @param crn
	 */

	public void startChat(String crn){
		try{
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			while (true){
				String message = dis.readUTF();
				System.out.println("Received message from: "+socket);
				chatServer.chatRooms.get(crn).sendToClients(message);
			}
		}
		catch(EOFException e){}
		catch(IOException e){
			e.printStackTrace();
		}
		finally{
			chatServer.chatRooms.get(crn).removeConnection(socket); // may not want to do this
		}
	}



}
