package chatroom.client;

import java.util.ArrayList;
import java.util.List;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.*;

public class ClientManager {

	private String user;
	private String room;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Socket socket;
	private LoginWindow loginWindow;
	private RoomSelectionWindow roomSelectionWindow;
	private ChatroomWindow chatroomWindow;
	private ClientComThread clientComHandler;
	private Latch latch = new Latch(); 
	// in the communication handler
	private boolean loginSuccess;
	private boolean creationSuccess;
	private boolean roomCreationSuccess;
	private boolean roomJoinSuccess;

	public ClientManager (String hostname, int port) {
		try{
			// connect to the server
			this.socket = new Socket(hostname, port);
			this.loginSuccess = false; // initial value
			// get the streams
			this.oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			this.ois = new ObjectInputStream(socket.getInputStream());
			clientComHandler = new ClientComThread(this);
			clientComHandler.start();
			// create the frames
			loginWindow = new LoginWindow(this);
			roomSelectionWindow = new RoomSelectionWindow(this);
			chatroomWindow = new ChatroomWindow(this);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public ObjectInputStream getOis(){
		return this.ois;
	}
	
	public ObjectOutputStream getOos(){
		return this.oos;
	}

	public void loginWindow() {
		this.loginWindow.getFrame().setVisible(true);
	}

	public void chatroomWindow() {
		this.chatroomWindow.getFrame().setVisible(true);
	}

	public void roomSelectionWindow() {
		this.roomSelectionWindow.getFrame().setVisible(true);
	}

	public List<String> makeList(Object[] in){
		List<String> retVal = new ArrayList<String>();
		for (Object a : in){
			retVal.add((String)a);
		}
		return retVal;
	}

	public String charArrToString(char [] arr){
		String result = "";
		for (char a : arr){
			result+=a;
		}
		return result;
	}

	public void await() throws InterruptedException{
		latch.await();
	}
	
	public Latch getLatch(){
		return this.latch;
	}
	
	public void setLoginSuccess(String result){
		this.loginSuccess = evaluateTrueFalse(result);
	}

	public boolean getLoginSuccess(){
		return this.loginSuccess;	
	}
	
	public void setCreationSuccess(String result){
		this.creationSuccess = evaluateTrueFalse(result);
	}
	
	public boolean getCreationSuccess(){
		return this.creationSuccess;
	}
	
	public void updateRoomList(String[] rooms){
		roomSelectionWindow.getRoomListModel().clear();
		for (String room : rooms){
			roomSelectionWindow.getRoomListModel().addElement(room);			
		}
	}
	
	public void updateUserList(String[] users){
		chatroomWindow.getUserListModel().clear();
		for (String user : users){
			chatroomWindow.getUserListModel().addElement(user);
		}
	}
	
	public void updateChatHistory(String message){
		chatroomWindow.getChatHistory().setText(chatroomWindow.getChatHistory().getText() + message +"\n");
	}
	
	public void setUser(String username){
		this.user = username;
		this.roomSelectionWindow.getTitle().setText("Logged in as '"+user+"'");
	}
	
	public void setRoom(String roomname){
		this.room = roomname;
		this.chatroomWindow.getFrame().setTitle("Chatting in room '"+room+"' as user '"+user+"'");
	}
	
	public String getUser(){
		return this.user;
	}
	
	public void setRoomCreationSuccess(String result){
		this.roomCreationSuccess = evaluateTrueFalse(result);
	}
	
	public boolean getRoomCreationSuccess(){
		return this.roomCreationSuccess;
	}
	
	public void setRoomJoinSuccess(String result){
		this.roomJoinSuccess = evaluateTrueFalse(result);
	}
	
	public boolean getRoomJoinSuccess(){
		return this.roomJoinSuccess;
	}
	
	private boolean evaluateTrueFalse(String result){
		if (result.equals("t"))
			return true;
		else
			return false;
	}
	
	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		int port = Integer.parseInt(args[0]);
		String hostname = args[1];
		ClientManager manager = new ClientManager(hostname, port);
		manager.loginWindow();
	}
}
