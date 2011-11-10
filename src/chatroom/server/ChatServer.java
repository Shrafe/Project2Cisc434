package chatroom.server;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Set;

public class ChatServer {
	ServerSocket serverSocket;
	HashMap<Socket, DataOutputStream> clients; 
	public ChatServer(int port) throws IOException{
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

		System.out.println("Server accepting connections on "+serverSocket);

		while(true){
			Socket socket = serverSocket.accept();
			System.out.println("Client connection from "+socket);
			DataOutputStream dis = new DataOutputStream(socket.getOutputStream());
			clients.put(socket, dis);
			new ChatServerThread(this, socket);			
		}
	}

	public void sendToClients(String message) throws IOException{
		Set<Socket> keys = clients.keySet();
		for (Socket client : keys){
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
		}

	}


}
