package chatroom.server;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Set;

public class ChatServer {
	protected ServerSocket serverSocket;
	protected static HashMap<String, Chatroom> chatrooms;
	protected static HashMap<String, String> users;
	
	public ChatServer(int port) throws IOException{
		HashMap<String, Chatroom> chatrooms = new HashMap<String,Chatroom>(1);
		HashMap<String, String> users = loadUsers();
		acceptConnections(port);
	}

	public static void main (String[] args){
		int port = Integer.parseInt(args[0]);
		try{
			new ChatServer(port);
		}catch(IOException e){e.printStackTrace();}
	}

	private void acceptConnections(int port) throws IOException{
		serverSocket = new ServerSocket(port);
		try{
			ServerSocket serverSocket = new ServerSocket(port);

			while(true){
				Socket socket = serverSocket.accept();
				System.out.println("Client connection from "+socket);
				new ChatServerThread(socket).run();		
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
		System.out.println("Server accepting connections on "+serverSocket);


	}

	public void addToChatroom(String crn, Socket socket){
		try{
			chatrooms.get(crn).addClient(socket);
		}
		catch (IOException e){
			System.err.println("Error adding client:"+socket+" to chatroom:"+crn);
			e.printStackTrace();
		}
	}

	public void removeChatroom(String crn){
		chatrooms.remove(crn);
	}
		
	// method for loading the users in from a file
	public HashMap<String,String> loadUsers(){
		return new HashMap<String,String>(1);
	}



}
