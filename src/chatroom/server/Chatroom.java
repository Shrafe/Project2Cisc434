package chatroom.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

public class Chatroom extends Thread implements Serializable{
	protected String name;
	ChatServer server;
	protected Set<String> clientNames;
	protected transient HashMap<String, DataOutputStream> clients; 
	protected int numClients; 
	protected int port;

	public Chatroom(String name, ChatServer server){
		this.name = name;
		this.server = server;
		clients = new HashMap<String,DataOutputStream>();
		clientNames = clients.keySet();
		this.numClients = 0;
	}

	public void addClient(Socket socket, String clientName) throws IOException{
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		clients.put(clientName, dos);
		clientNames = clients.keySet();
	}

	public void sendToClients(String message) throws IOException{
		Set<String> keys = clients.keySet();
		for (String client : keys){
			clients.get(client).writeUTF(message);
		}
	}

	public void removeConnection(Socket socket){
		try{
			socket.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		finally{
			clients.remove(socket);
			clientNames = clients.keySet();
			/*			if (numClients == 0){
				server.removeChatroom(this.name);
			}*/
		}

	}

}

