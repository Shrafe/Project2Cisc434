package test.client;

import java.io.Console;
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
			oos.writeUTF("thauser"); // username
			oos.writeUTF("goofball"); // password
			HashMap<String, Chatroom> chatRooms;
			chatRooms = (HashMap<String,Chatroom>)ois.readObject(); // we recieve this from the server. new issues apparent: resend this upon creating chatroom (annoying problem)
			System.out.println(chatRooms.keySet());
			oos.writeUTF("new");
			oos.writeUTF("test message");
			System.out.println(ois.readUTF());
			


		} catch (Exception e){
			e.printStackTrace();
			System.err.println("derrrrp");
			System.exit(1);
		}
	}
}
