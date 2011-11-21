package chatroom.client;

import java.util.List;

public class ClientMsgThread implements Runnable {

	private String message;
	private String user;
	private String room;
	private String target;
	private List<String> targets;
	private int msgType;
	
	public ClientMsgThread (String msg, String room, String user) {
		
		message = msg;
		this.room = room;
		this.user = user;
		msgType = 0;
	}
	
	public ClientMsgThread (String msg, String room, String user, String target) {
		
		message = msg;
		this.room = room;
		this.user = user;
		this.target = target;
		msgType = 1;
	}
	
	public ClientMsgThread (String msg, String room, String user, List<String> targets) {
		
		message = msg;
		this.room = room;
		this.user = user;
		this.targets = targets;
		msgType = 2;
	}
	
	public ClientMsgThread (String room, String user) {
		
		this.room = room;
		this.user = user;
		msgType = 3;
	}
	
	public void run(){
		
		//TODO: Send the message. Probably need to set up sockets before hand and pass them with the thread creation too.
		switch (msgType){
			case 0:
				break;
			case 1:
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				break;
			default:
				break;
		}
	}
}
