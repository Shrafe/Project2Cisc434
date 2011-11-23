package chatroom.client;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.*;
import javax.swing.event.*;

public class ClientApp extends JApplet{

	private JFrame frame;
	private int width = 300, height = 300;
	private int xdim = 600, ydim = 300;
	private int xspacing = 20;
	private int txtheight = 25;
	private int btnheight = 35;
	private String chatRoom = "Default";
	private String user;
	private DefaultListModel<String> userListModel;
	private DefaultListModel<String> roomListModel;

	private JTextField username;
	private JPasswordField password;

	private JLabel chatLabel;
	private JButton exit;
	private JScrollPane userScroll;
	private JList<String> userList;
	private JScrollPane historyScroll;
	private JTextArea chatHistory;
	private JTextArea chatBox;
	private JButton send;

	private JList<String> roomList;
	private JScrollPane roomScroll;

	private ArrayList<Component> components;
	
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Socket socket;
	
	private Thread clientChatHandler;

	public ClientApp (JFrame frame, String hostname, int port) {
		try{
			this.socket = new Socket(hostname, port);
			this.oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			this.ois = new ObjectInputStream(socket.getInputStream());
			this.frame = frame;
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Initial method that creates the Login screen.
	 */
	public void init() {

		components = new ArrayList<Component>();

		frame.setBounds(xdim, ydim, width, height);
		frame.setResizable(false);
		getContentPane().setBackground(Color.lightGray);

		// Create the Username text field
		username = new JTextField();
		username.setBounds(xspacing, height/4 , width-8*xspacing, txtheight);
		
		// Create the Username label
		JLabel userLabel = new JLabel();
		userLabel.setText("Username");
		userLabel.setBounds(username.getX() + username.getWidth() + xspacing,
				username.getY(), 3*xspacing, txtheight);
		
		// Create the Password text field
		password = new JPasswordField();
		password.setBounds(xspacing, height/2, width-8*xspacing, txtheight);
		
		// Create the Password label
		JLabel passLabel = new JLabel();
		passLabel.setText("Password");
		passLabel.setBounds(password.getX() + password.getWidth() + xspacing,
				password.getY(), 3*xspacing, txtheight);
		
		// Create the Login button
		JButton login = new JButton();
		login.setText("Login");
		login.setBounds(xspacing, 3*height/4, width/3, btnheight);
		login.addActionListener(new LoginListener());
		
		// Create the New User button
		JButton newUser = new JButton();
		newUser.setText("New User?");
		newUser.setBounds(login.getWidth() + 3*xspacing, 3*height/4, width/3, btnheight);
		newUser.addActionListener(new NewUserListener());
		
		// Add every new component to a list so they can be easily removed
		// later with a single method
		components.add(username);
		components.add(password);
		components.add(userLabel);
		components.add(passLabel);
		components.add(login);
		components.add(newUser);
		
		// Add every new component to the frame
		for (int i = 0; i < components.size(); i++) {
			frame.add(components.get(i));
		}
	}

	/**
	 * Creates the actual Chat Room window where users will
	 * be able to send and receive messages
	 */
	private void chatRoomWindow() {
		
		width = 500;
		height = 700;
		int[] rowHeights = {50, 550, 100};
		int[] columnWidths = {125, 125, 125, 125};

		frame.setBounds(xdim, ydim/2, width, height);
		getContentPane().setBackground(Color.lightGray);

		GridBagConstraints gbc = new GridBagConstraints();
		GridBagLayout gbl = new GridBagLayout();

		gbl.rowHeights = rowHeights;
		gbc.weightx = 0.0;
		gbl.columnWidths = columnWidths;
		frame.setLayout(gbl);

		chatLabel = new JLabel();
		chatLabel.setText("Current Room: " + chatRoom);

		exit = new JButton();
		exit.setText("Leave Room");
		exit.addActionListener(new ExitListener());

		////////////////////////////////////////////////////////////
		// list will eventually accept data that was returned by the server
		userListModel = new DefaultListModel<String>();
		//userListModel.addElement("All");
		//userListModel.addElement("User1");
		//userListModel.addElement("User2");
		//userListModel.addElement("User3");
		//userListModel.addElement("User4");
		
		// Not too sure how you were getting lists of users because you had deleted
		// this but not added anything.
		////////////////////////////////////////////////////////////

		userList = new JList(userListModel);
		userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		userList.addListSelectionListener(new UserSelectionListener());

		userScroll = new JScrollPane(userList);
		userScroll.setMinimumSize(new Dimension(250,100));
		userScroll.setMaximumSize(new Dimension(250,1000));

		chatHistory = new JTextArea();
		chatHistory.setMinimumSize(new Dimension(250,100));
		chatHistory.setMaximumSize(new Dimension(250,550));
		chatHistory.setEditable(false);
		chatHistory.setLineWrap(true);

		historyScroll = new JScrollPane(chatHistory);
		historyScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		chatBox = new JTextArea();
		chatBox.setMinimumSize(new Dimension(375, 100));

		send = new JButton();
		send.setText("Send");
		send.addActionListener(new SendListener());

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0; gbc.gridy = 0;
		gbc.gridwidth = 3;
		gbl.setConstraints(chatLabel, gbc);
		components.add(chatLabel);

		gbc.gridx = 3; gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbl.setConstraints(exit, gbc);
		components.add(exit);

		gbc.gridx = 0; gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbl.setConstraints(userScroll, gbc);
		components.add(userScroll);

		gbc.gridx = 2; gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbl.setConstraints(historyScroll, gbc);
		components.add(historyScroll);

		gbc.gridx = 0; gbc.gridy = 2;
		gbc.gridwidth = 3;
		gbl.setConstraints(chatBox, gbc);
		components.add(chatBox);

		gbc.gridx = 3; gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbl.setConstraints(send, gbc);
		components.add(send);

		for (int i = 0; i < components.size(); i++) {
			frame.add(components.get(i));
		}
		
		clientChatHandler = new Thread(new ClientChatThread(chatHistory, ois));
		clientChatHandler.start();
	}
	
	/**
	 * Creates the Chat Room Selection window where users will be able to
	 * Join Rooms, Refresh the list of rooms, and create new Rooms (maybe)
	 */
	private void chatSelectionWindow() {

		// I have no idea why but unless the frame is resized the new components don't appear
		width = 300;
		height = 400;

		int[] rowHeights = {300, 100};
		int[] columnWidths = {150, 150};

		frame.setBounds(xdim, ydim, width, height);
		getContentPane().setBackground(Color.lightGray);

		GridBagConstraints gbc = new GridBagConstraints();
		GridBagLayout gbl = new GridBagLayout();

		gbl.rowHeights = rowHeights;
		gbl.columnWidths = columnWidths;
		frame.setLayout(gbl);

		String[] rooms = {};
		MsgObj message = new MsgObj();
		
		try {
			// Ask the server for a list of rooms
			oos.writeObject(message);
			
			// Wait for the list to come back
			rooms = (String[])ois.readObject();
			
			for (int i = 0; i < rooms.length; i++) {
				roomListModel.addElement(rooms[i]);
			}
		
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		roomListModel = new DefaultListModel<String>();

		for (int i = 0; i < rooms.length; i++) {
			roomListModel.addElement(rooms[i]);
		}
		
		roomList = new JList<String>(roomListModel);
		roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		roomList.addListSelectionListener(new RoomSelectionListener());

		roomScroll = new JScrollPane(roomList);
		roomScroll.setMinimumSize(new Dimension(300,200));
		roomScroll.setMaximumSize(new Dimension(300,200));

		JButton join = new JButton();
		join.setText("Join Room");
		join.addActionListener(new JoinListener());

		JButton refresh = new JButton();
		refresh.setText("Refresh");
		refresh.addActionListener(new RefreshListener());

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0; gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbl.setConstraints(roomScroll, gbc);
		components.add(roomScroll);

		gbc.gridx = 0; gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbl.setConstraints(refresh, gbc);
		components.add(refresh);

		gbc.gridx = 1; gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbl.setConstraints(join, gbc);
		components.add(join);

		for (int i = 0; i < components.size(); i++) {
			frame.add(components.get(i));
		}
	}

	/**
	 * Clear every current component from the frame without destroying
	 * it outright
	 */
	private void clearComponents() {
		for (int i = 0; i < components.size(); i++) {
			frame.remove(components.get(i));
		}

		components.clear();
	}
	
	/**
	 * Listener for the Refresh button on the Chat Room
	 * Selection window
	 */
	class RefreshListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			MsgObj message = new MsgObj();
			
			try {
				oos.writeObject(message);
				
				String[] rooms = (String[])ois.readObject();
				
				roomScroll.remove(roomList);
				roomListModel = new DefaultListModel<String>();
				
				for (int i = 0; i < rooms.length; i++) {
					roomListModel.addElement(rooms[i]);
				}
				
				roomList = new JList<String>(roomListModel);
				roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				roomList.addListSelectionListener(new RoomSelectionListener());
				
				
				roomScroll.add(roomList);
				roomScroll.setMinimumSize(new Dimension(300,200));
				roomScroll.setMaximumSize(new Dimension(300,200));
				
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Listener for the **Join** button on the Chat Room
	 * Selection window
	 */
	class JoinListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			if (roomList.getSelectedIndex() == -1){
				chatRoom = JOptionPane.showInputDialog("Enter the name of the chatroom you wish to create:");
			}
			// Check that the user has selected a room
			else {
				// Set the local variable for chat room name
				chatRoom = roomList.getSelectedValue();
			}
			
			MsgObj message = new MsgObj(chatRoom, user, MsgObj.entering);
			
			// Send the message
			try {
				oos.writeObject(message);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			// Clear the Chat Room Selection window
			clearComponents();
			
			// Launch the Chat Room window
			chatRoomWindow();
				
		}
	}


	/**
	 * Listener class for sending messages to the server. Creates a thread to handle
	 * the actual sending
	 */
	class SendListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			int size = userList.getSelectedIndices().length;
			MsgObj message;

			// One whisper or "All" users selected
			if(size == 1) {

				if (userList.getSelectedIndex() != 0) {
					// Whisper the target
					message = new MsgObj(
							user + ": " + chatBox.getText(),
							chatRoom, user,
							userList.getSelectedValue()
							);
				}
				else {
					// Post to the room
					message = new MsgObj(user + ": " + chatBox.getText(),
							chatRoom, user);
				}
			}
			else {
				// Whisper multiple targets
				message = new MsgObj(
						user + ": " + chatBox.getText(),
						chatRoom, user,	userList.getSelectedValuesList());
			}
			
			// Send the message
			try {
				oos.writeObject(message);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			/////////////////////////////////////////////
			// Testing that the message shows up in the box
			//chatHistory.setText(chatHistory.getText() + "\n" + user + ": " + chatBox.getText());
			/////////////////////////////////////////////

			chatBox.setText("");
		}
	}

	public String charArrToString(char [] arr){
		String result = "";
		for (char a : arr){
			result+=a;
		}
		return result;
	}

	/**
	 * Listener for the **Login** button on the Login window
	 */
	class LoginListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try{
				oos.writeObject("0"); // we're trying to log in. tell server
				oos.writeObject(username.getText());
				oos.writeObject(charArrToString(password.getPassword()));

				String[] rooms = (String[])ois.readObject();
				if(rooms!=null){

					user = username.getText();

					roomList = new JList(rooms);
					clearComponents();
					chatSelectionWindow();
				}
				else
					JOptionPane.showMessageDialog(frame, "Incorrect username / password. Try again .");
			} catch(Exception ex){
				ex.printStackTrace();
			}

		}
	}

	/**
	 * Listener for the **New User** button on the Login window
	 */
	class NewUserListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try{
				oos.writeObject("1"); // we're creating a user, tell it that!
				oos.writeObject(username.getText()); 
				oos.writeObject(charArrToString(password.getPassword()));

				String success = (String)ois.readObject();

				if (success != null && success.equals("success")){
					JOptionPane.showMessageDialog(frame, "Your new user was successfully registered.");
				}
				else if (success != null && success.equals("dup")){
					JOptionPane.showMessageDialog(frame, "The username already existed.");
				}
				else
					JOptionPane.showMessageDialog(frame, "Cataclysmic error of some kind.");
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Listener for the **Leave Room** button on the
	 * Chat Room window
	 */
	class ExitListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			MsgObj message = new MsgObj(chatRoom, user, MsgObj.exiting);
			
			// Send message
			try {
				// stop the thread managing the chat
				clientChatHandler.interrupt();
				oos.writeObject(message);
			} catch (SocketException ex){
				JOptionPane.showMessageDialog(frame, "The server has crashed / is not responsive.");
				ex.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			
			clearComponents();
			chatSelectionWindow();
		}
	}

	class RoomSelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			// Probably a useless listening class
		}
	}
	/**
	 * Listener class for when the user clicks on a any user in the
	 * list of users. Includes shift and ctrl selection, but does not
	 * simply add or remove selections by left clicking without modifiers.
	 */
	class UserSelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() == false) {

				int size = userList.getSelectedIndices().length;

				// I swear I can do something here...
				if (userList.getSelectedIndex() == 0 && size != 1) {
					//userList.clearSelection();
				}
			}
		}
	}
}
