package chatroom.client;

import java.util.List;

public class ClientMsgThread implements Runnable {

	private String message;
	private String user;
	private List<String> users;
	private int msgType;
	
	public ClientMsgThread (String msg) {
		
		message = msg;
		msgType = 0;
	}
	
	public ClientMsgThread (String msg, String usr) {
		
		message = msg;
		user = usr;
		msgType = 1;
	}
	
	public ClientMsgThread (String msg, List<String> usr) {
		
		message = msg;
		users = usr;
		msgType = 2;
	}
	
	public void run(){
		
		//TODO: Send the message. Probably need to set up sockets before hand and pass them with the thread creation too.
	}
}
