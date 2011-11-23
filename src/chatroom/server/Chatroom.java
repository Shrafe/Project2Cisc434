package chatroom.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
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

	public void addClient(ObjectOutputStream oos, String clientName){
		clients.put(clientName, oos);
		clientNames = loadClientNames(clients.keySet());
	}
	
	public void removeClient(String clientName) {
		clients.remove(clientName);
		clientNames = loadClientNames(clients.skeySet());
	}

	/** 
	 * Sends a message to all clients in the client list. constructs a MsgObj with the type 
	 * value of 3, and sends this same object to all clients
	 * @param message
	 * @throws IOException
	 */
	public void sendToClients(String message, List<String> whisperClients) throws IOException{
		MsgObj sendMessage = new MsgObj();
		sendMessage.addToPayload(message);
		byte type = 3;
		sendMessage.setType(type);
		Set<String> keys = clients.keySet();
		if (whisperClients != null){
			// we have clients to whisper, use those values instead
			for (String client : whisperClients){
				clients.get(client).writeObject(sendMessage);
			}
		}
		else {
			// this message isn't a whisper; send to all clients in the chatroom
			for (String client : keys){
				clients.get(client).writeObject(sendMessage);
			}
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

