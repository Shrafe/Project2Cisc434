package chatroom.client;

import java.lang.InterruptedException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;

import javax.swing.JTextArea;

/**
 * This thread is started once the user enters the "chat" state, and ends once he exits it. 
 * This is needed because otherwise the "Stream noise" will confuse the program.
 * Responsible for displaying any and all messages received after the user successfully joins 
 * a room
 * @author TomW7
 *
 */
public class ClientChatThread extends Thread {

	private ObjectInputStream ois;
	private JTextArea chatHistory;
	private boolean chatting;

	public ClientChatThread(JTextArea chatHistory, ObjectInputStream ois){
		this.chatHistory = chatHistory;
		this.ois = ois;
		this.chatting = true;
	}

	/**
	 * when we run, we take control of the ois.
	 * assumption: all subsequent objects on the stream are messages to be displayed.
	 */

	public void run(){

		while(chatting){
			try{
				String message = (String)ois.readObject();
				this.chatHistory.setText(this.chatHistory.getText() + "\n" + message);
			} catch (InterruptedIOException ioe) {
				this.chatting=false;
			} catch (ClassNotFoundException cnfe) {
				// TODO Auto-generated catch block
				cnfe.printStackTrace();
				chatting = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				chatting = false;
			} 
		}

	}
}
