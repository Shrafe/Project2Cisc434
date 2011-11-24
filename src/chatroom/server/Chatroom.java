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
		updateUsers();
	}

	public void removeClient(String clientName) {
		clients.remove(clientName);
		clientNames = loadClientNames(clients.keySet());
		updateUsers();
	}

	/** 
	 * Method to send a user list every time a new user joins the chatroom
	 * everyone needs to know this great news!
	 */

	public void updateUsers(){
		MsgObj message = new MsgObj();
		byte type = 1;
		message.setType(type);
		message.addToPayload(clientNames);
		Set<String> keys = clients.keySet();
		for (String client : keys){
			try{
				clients.get(client).writeObject(message);
				System.out.println("Sent user list to: "+client);
			}catch (IOException ioe){
				ioe.printStackTrace();
			}

		}
	}

	/** 
	 * Sends a message to all clients in the client list. constructs a MsgObj with the type 
	 * value of 3, and sends this same object to all clients
	 * @param message
	 * @throws IOException
	 */
	public void sendToClients(String message, String sender, List<String> whisperClients) throws IOException{
		MsgObj sendMessage = new MsgObj();
		byte type = 4;
		sendMessage.setType(type);
		if (whisperClients != null){
			// we have clients to whisper, use those values instead
			// need to modify message in this case
			sendMessage.addToPayload("( Whisper from "+sender+" ): "+message);
			String senderMessage = "( Whisper to ";
			for (int i = 0; i < whisperClients.size(); i++){
				clients.get(whisperClients.get(i)).writeObject(sendMessage);
				if (i == (whisperClients.size() - 1)){ // it's the last one
					senderMessage+=whisperClients.get(i);
				}
				else{
					senderMessage+=whisperClients.get(i)+", ";
				}				
			}
			// now we need to send another message with a different payload
			sendMessage.getPayload().clear();
			sendMessage.addToPayload(senderMessage+" ): "+message);
			clients.get(sender).writeObject(sendMessage); // send the whisper
		}
		else {
			Set<String> keys = clients.keySet();
			// this message isn't a whisper; send to all clients in the chatroom
			sendMessage.addToPayload(sender+": "+message);
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

