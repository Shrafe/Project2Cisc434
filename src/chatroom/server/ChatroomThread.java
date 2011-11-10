package chatroom.server;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class ChatroomThread extends Thread {
	private Chatroom chatroom;
	private Socket socket;

	public ChatroomThread(Chatroom server, Socket socket){
		this.chatroom = server;
		this.socket = socket;
		start();
	}

	public void run(){
		try{
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			while (true){
				String message = dis.readUTF();
				System.out.println("Received message from: "+socket);
				chatroom.sendToClients(message);
			}
		}
		catch(EOFException e){}
		catch(IOException e){
			e.printStackTrace();
		}
		finally{
			chatroom.removeConnection(socket);
		}
	}
}
