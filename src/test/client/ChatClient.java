package test.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

import chatroom.server.Chatroom;

public class ChatClient {
	public static void main (String[] args){
		try{
			Socket socket = new Socket("localhost", 4444);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			oos.writeObject("thauser"); // username
			oos.writeObject("goofball"); // password
			HashMap<String, Chatroom> chatRooms;
			chatRooms = (HashMap<String,Chatroom>)ois.readObject(); // we recieve this from the server
			
			
			
			
			


		} catch (Exception e){
			e.printStackTrace();
			System.err.println("derrrrp");
			System.exit(1);
		}
	}
}
