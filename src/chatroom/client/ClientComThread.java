package chatroom.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import javax.swing.JTextArea;

import chatroom.server.MsgObj;

/**
 * This thread is started once the user enters the "chat" state, and ends once he exits it. 
 * This is needed because otherwise the "Stream noise" will confuse the program.
 * Responsible for displaying any and all messages received after the user successfully joins 
 * a room
 * @author TomW7
 *
 */
public class ClientComThread extends Thread {
	ClientApp client; // we need to be able to call functions here
	Object lock;

	public ClientComThread(ClientApp client, Object lock){
		this.client = client;
		this.lock = lock;
	}

	/**
	 * This thread handles all incoming communication on behalf of the ClientApp, so we don't have GUI 
	 * hangups, or StreamCorruption errors by ClientApp trying to read the stream while we're in a chatroom
	 * 
	 */

	public void run(){
		ObjectInputStream ois = client.getOis();

		/**
		 * This functions analogously to ChatServerThread, but with different meanings for 
		 * the getType in the MsgObj 
		 * Case 0: Received a list of chatrooms 
		 * Case 1: Received a list of users
		 * Case 2: Received failed login attempt
		 * Case 3: Received result of create user attempt 
		 * Case 4: Received a message 
		 * 
		 */

		while(true){
			try{
			MsgObj message = (MsgObj)ois.readObject(); 
			switch (message.getType()){
			case 0:	
				updateChatroomList(message);
				break;
			case 1:
				updateUserList(message);
				break;
			case 2:
				reportLoginResult(message);
				break;
			case 3:
				postMessage(message);
				break;
			}
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * we know that we received a list of chatrooms from the server. the payload will contain this 
	 * list with no other data. Thus, we can just assign the JList of rooms to be made with this 
	 * String[]
	 * @param message
	 */
	
	private void updateChatroomList(MsgObj message){
		client.updateChatroomList(message.getPayload());
	}
	
	/**
	 * Identical to updateChatroomList, but we update the UserList instead of the Chatroom list
	 * @param message
	 */
	private void updateUserList(MsgObj message){
		client.updateUserList(message.getPayload());
	}
	
	/** 
	 * Need to signal the client that its login either succeeded or failed. 
	 * Maybe use a latch or something like that 
	 * @param message
	 */
	
	private void reportLoginResult(MsgObj message){
		client.setLoginResult()
	}
	
}
