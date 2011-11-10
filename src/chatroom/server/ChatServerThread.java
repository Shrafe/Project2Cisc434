package chatroom.server;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class ChatServerThread extends Thread {
	private ChatServer server;
	private Socket socket;

	public ChatServerThread(ChatServer server, Socket socket){
		this.server = server;
		this.socket = socket;
		start();
	}

	public void run(){
		try{
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			while (true){
				String message = dis.readUTF();
				System.out.println("Received message from: "+socket);
				server.sendToClients(message);
			}
		}
		catch(EOFException e){}
		catch(IOException e){
			e.printStackTrace();
		}
		finally{
			server.removeConnection(socket);
		}
	}
}
