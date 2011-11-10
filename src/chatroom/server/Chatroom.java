package chatroom.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

public class Chatroom extends Thread{
	String name;
	ChatServer server;
	HashMap<String, DataOutputStream> clients; 
	int numClients; 
	int port;

	public Chatroom(String name, ChatServer server, int port){
		this.name = name;
		this.server = server;
		clients = new HashMap<String,DataOutputStream>(1);
		this.numClients = 0;
		this.port = port;
	}

	public void run (){
		try{
				numClients++;
				System.out.println("Client connection from "+socket);
				DataOutputStream dis = new DataOutputStream(socket.getOutputStream());
				clients.put(socket, dis);
				new ChatroomThread(this, socket);			
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}


	public void addClient(Socket socket) throws IOException{
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		clients.put(socket, dos);
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
/*			if (numClients == 0){
				server.removeChatroom(this.name);
			}*/
		}

	}

}

