package chatroom.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ChatServerThread implements Runnable {
	Socket socket;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	public ChatServerThread(Socket socket){
		this.socket = socket;
		try {
			oos = new ObjectOutputStream(this.socket.getOutputStream());
			ois = new ObjectInputStream(this.socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run(){
		try {
			if (validUser()){

				// give the user a list of chatrooms 

				oos.writeObject(ChatServer.chatrooms); // send the client the map of available chatrooms. process clientside

				String crn = (String)ois.readObject(); // wait here for the name of the chatroom the client wants to join

				ChatServer.chatrooms.get(crn).addClient(this.socket); // add the client to the appropriate chatroom's client list
				oos.close(); // close them both to avoid annoyance with socket 
				ois.close(); // close them both to avoid annoyance with socket

				// TODO: Somehow implement a waiting mechanism for the client to return from being in the chatroom. 
				Thread.sleep(1000000);
				// The Thread.sleep is not a solution and is hacky at best.
			}
			else
				System.out.println("User validation failed for "+ socket);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		} catch (InterruptedException e){
			e.printStackTrace();
		}
	}

	public boolean validUser() throws IOException, ClassNotFoundException{
		// TODO: Implement a proper check for the existence of a user
		String userName = (String)ois.readObject();
		String password = (String)ois.readObject();

		// check the values in the hashmap. userName = key, password should = value;
		return ChatServer.users.get(userName).equals(password);

	}

}
