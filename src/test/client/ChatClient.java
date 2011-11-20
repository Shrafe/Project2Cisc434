package test.client;

import java.io.Console;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

import chatroom.server.Chatroom;

public class ChatClient {
	public static void main (String[] args){
		try{
			Socket socket = new Socket("localhost", 4444);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			oos.writeObject("thauser"); // username
			oos.writeObject("goofball"); // password
			String[] chatRooms = (String[])ois.readObject(); // we receive this from the server. new issues apparent: resend this upon creating chatroom (annoying problem)
			System.out.println("Rooms available: "+printArray(chatRooms));
			oos.writeObject("new");
			String[] userlist = (String[])ois.readObject();
			System.out.println("Joined room new with "+printArray(userlist) + " users.");
			oos.writeObject("test message");
			System.out.println((String)ois.readObject());
			ois.close();
			oos.close();
			Thread.sleep(100000);
			
			


		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static String printArray(String [] a){
		StringBuffer buf = new StringBuffer();
		for (String b : a){
			buf.append(b);
		}
		return buf.toString();
	}
}
