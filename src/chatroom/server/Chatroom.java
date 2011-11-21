package chatroom.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

public class Chatroom extends Thread implements Serializable{
	protected String name;
	ChatServer server;
	protected String[] clientNames; // array of HashMap keys, basically. quick sending to clients
	protected HashMap<String, ObjectOutputStream> clients; 
	protected int numClients; 
	protected int port;

	public Chatroom(String name, ChatServer server){
		this.name = name;
		this.server = server;
		clients = new HashMap<String,ObjectOutputStream>();
		clientNames = loadClientNames(clients.keySet());
		this.numClients = 0;
	}

	public void addClient(ObjectOutputStream oos, String clientName) throws IOException{
		clients.put(clientName, oos);
		clientNames = loadClientNames(clients.keySet());
	}

	public void sendToClients(String message) throws IOException{
		Set<String> keys = clients.keySet();
		for (String client : keys){
			clients.get(client).writeObject(message);
		}
	}
	
	public String[] getClientList() throws IOException{
		return clientNames;
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
			clientNames = loadClientNames(clients.keySet());
			/*			if (numClients == 0){
				server.removeChatroom(this.name);
			}*/
		}
	}
	
	public String[] loadClientNames(Set<String> keys){
		Object[] objects = keys.toArray();
		String[] returnVal = new String[objects.length];	
		for (int i = 0; i < returnVal.length; i++){
			returnVal[i] = (String) objects[i];			
		}
		return returnVal;
	}

}

