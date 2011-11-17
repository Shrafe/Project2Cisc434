package chatroom.server;


import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Set;

public class ChatServer {
	protected ServerSocket serverSocket;
	protected HashMap<String, Chatroom> chatRooms;
	protected HashMap<String, String> users;
	protected int port;
	
	public ChatServer(int port) throws IOException{
		this.port = port;
		HashMap<String, Chatroom> chatRooms = new HashMap<String,Chatroom>(1);
		this.chatRooms = chatRooms;
		HashMap<String, String> users = loadUsers();
		this.users = users;
		acceptConnections();
	}

	public static void main (String[] args){
		int port = Integer.parseInt(args[0]);
		try{
			new ChatServer(port);
		}catch(IOException e){e.printStackTrace();}
	}

	private void acceptConnections() throws IOException{
		try{
			ServerSocket serverSocket = new ServerSocket(this.port);
			System.out.println("Server accepting connections on "+serverSocket);
			while(true){
				Socket socket = serverSocket.accept();
				System.out.println("Client connection from "+socket);
				new ChatServerThread(socket, this).run();		
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}

	public void createChatroom(String crn){
		if(!chatRooms.containsKey(crn))
			chatRooms.put(crn, new Chatroom(crn, this));
		else
			System.err.println("Chatroom already exists");
	}

	public void removeChatroom(String crn){
		chatRooms.remove(crn);
	}
		
	// method for loading the users in from a file
	public HashMap<String,String> loadUsers(){
		return new HashMap<String,String>(1);
	}



}
