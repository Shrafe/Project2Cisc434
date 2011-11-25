package chatroom.server;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ChatServer {
	protected ServerSocket serverSocket;
	protected String[] chatRoomNames; // we keep this so we can quickly send a String array to the clients containing the names of chatrooms
	protected HashMap<String, Chatroom> chatRooms;
	protected HashMap<String, String> userCredentials;
	protected ArrayList<ObjectOutputStream> clients;
	protected int port;

	public ChatServer(int port) throws IOException{
		this.port = port;
		HashMap<String, Chatroom> chatRooms = new HashMap<String,Chatroom>();
		this.chatRooms = chatRooms;
		ArrayList<ObjectOutputStream> clients = new ArrayList<ObjectOutputStream>();
		this.clients = clients;
		HashMap<String, String> users = loadUsers();
		this.userCredentials = users;
		this.chatRoomNames = loadChatRoomNames();
		acceptConnections();
	}

	public static void main (String[] args){
		int port = Integer.parseInt(args[0]);
		try{
			new ChatServer(port);
		}catch(Exception e){
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
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());	
				clients.add(oos);
				new Thread(new ChatServerThread(socket, oos, ois, this)).start();		
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
	// tell everyone about this moment!
	public boolean createChatroom(String crn){
		boolean result = false;
		if (!chatRooms.containsKey(crn)){
			chatRooms.put(crn, new Chatroom(crn, this));
			chatRoomNames = loadChatRoomNames();
			updateChatrooms();
			result = true;
		}
		else
			result = false;
		
		
		return result;
	}

	public void removeChatroom(String crn){
		chatRooms.remove(crn);
		chatRoomNames = loadChatRoomNames();
		updateChatrooms();
	}
	
	public void updateChatrooms(){
		MsgObj sendMessage = new MsgObj();
		byte type = 0;
		sendMessage.setType(type);
		sendMessage.addToPayload(chatRoomNames);
		for (ObjectOutputStream oos : clients){
			try{
				oos.writeObject(sendMessage);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	

	public boolean joinChatroom(String crn, ObjectOutputStream oos, String userName, InetAddress ip){
		try{
			Chatroom chatroom = chatRooms.get(crn);
			if (chatroom != null){
				chatRooms.get(crn).addClient(oos, userName, ip);
				return true;
			}
			else
				return false;
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public String validateUser(String username, String password){
		String returnVal = "f";
		if (this.userCredentials.containsKey(username)){
			if(this.userCredentials.get(username).equals(password)){
				returnVal = "t";
			}
		}
		return returnVal;
	}

	public String addUser(String username, String password){
		String returnVal = "f";
	
		if (!this.userCredentials.containsKey(username)){
			this.userCredentials.put(username, password);
			returnVal = "t";
		}
		writeUsersFile();
		return returnVal;
	}

	public void writeUsersFile(){
		// write out the new file
		// TODO: parameterize
		Set<String> keySet = this.userCredentials.keySet();
		PrintWriter out = null;
		try{
			out = new PrintWriter(new FileWriter("C:\\Users\\TomW7\\workspace\\Project2Cisc434\\users.txt"));
			for (String key : keySet){
				out.println(key+" "+this.userCredentials.get(key));
			}
			out.close();
		} catch (Exception e){
			e.printStackTrace();
		}
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
}
