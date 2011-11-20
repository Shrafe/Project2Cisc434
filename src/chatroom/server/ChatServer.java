package chatroom.server;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Set;

public class ChatServer {
	protected ServerSocket serverSocket;
	protected String[] chatRoomNames; // we keep this so we can quickly send a String array to the clients containing the names of chatrooms
	protected HashMap<String, Chatroom> chatRooms;
	protected HashMap<String, String> users;
	protected int port;

	public ChatServer(int port) throws IOException{
		this.port = port;
		HashMap<String, Chatroom> chatRooms = new HashMap<String,Chatroom>(1);
		this.chatRooms = chatRooms;
		HashMap<String, String> users = loadUsers();
		this.users = users;
		this.chatRoomNames = loadChatRoomNames();
		acceptConnections();
	}

	public static void main (String[] args){
		int port = Integer.parseInt(args[0]);
		try{
			new ChatServer(port);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public String[] loadChatRoomNames(){
		Object[] keys = chatRooms.keySet().toArray();
		String [] returnVal = new String[keys.length];
		for (int i = 0; i < returnVal.length; i++){
			returnVal[i] = (String)keys[i];
		}
		return returnVal;
	}

	private void acceptConnections() throws IOException{
		try{
			this.serverSocket = new ServerSocket(this.port);
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
		chatRooms.put(crn, new Chatroom(crn, this));
	}

	public void removeChatroom(String crn){
		chatRooms.remove(crn);
	}

	// method for loading the users in from a file
	// Expected format in file is space seperated entries like this
	// thauser password
	// bryce password

	public HashMap<String,String> loadUsers(){
		HashMap<String, String> returnVal = new HashMap<String,String>();
		String input;
		try{
			BufferedReader userList = new BufferedReader(new FileReader("C:\\Users\\TomW7\\workspace\\Project2Cisc434\\users.txt"));
			while((input = userList.readLine())!=null){
				String[] values = input.split(" ");
				returnVal.put(values[0], values[1]);
			}
		} catch (FileNotFoundException e){
			System.err.println("The users file could not be found.");
			e.printStackTrace();
			System.exit(10);
		} catch (IOException e){
			System.err.println("There was an error reading the users file. Ensure proper formatting.");
			e.printStackTrace();
			System.exit(11);
		}
		return returnVal;
	}

	public void joinChatroom(String crn, ObjectOutputStream oos, String userName){
		try{
			if (!chatRooms.containsKey(crn))
				createChatroom(crn);
			
			chatRooms.get(crn).addClient(oos, userName);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
