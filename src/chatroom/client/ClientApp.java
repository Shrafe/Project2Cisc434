package chatroom.client;

import java.util.*;
import java.util.List;
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
	private DefaultListModel userListModel;
	private DefaultListModel roomListModel;

	private JTextField username;
	private JPasswordField password;

	private JLabel chatLabel;
	private JButton exit;
	private JScrollPane userScroll;
	private JList userList;
	private JScrollPane historyScroll;
	private JTextArea chatHistory;
	private JTextArea chatBox;
	private JButton send;

	private JList roomList;
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

	public void init() {

		components = new ArrayList<Component>();

		frame.setBounds(xdim, ydim, width, height);
		frame.setResizable(false);
		getContentPane().setBackground(Color.lightGray);

		username = new JTextField();
		username.setBounds(xspacing, height/4 , width-8*xspacing, txtheight);

		password = new JPasswordField();
		password.setBounds(xspacing, height/2, width-8*xspacing, txtheight);

		JLabel userLabel = new JLabel();
		userLabel.setText("Username");
		userLabel.setBounds(username.getX() + username.getWidth() + xspacing,
				username.getY(), 3*xspacing, txtheight);

		JLabel passLabel = new JLabel();
		passLabel.setText("Password");
		passLabel.setBounds(password.getX() + password.getWidth() + xspacing,
				password.getY(), 3*xspacing, txtheight);

		JButton login = new JButton();
		login.setText("Login");
		login.setBounds(xspacing, 3*height/4, width/3, btnheight);
		login.addActionListener(new LoginListener());

		JButton newUser = new JButton();
		newUser.setText("New User?");
		newUser.setBounds(login.getWidth() + 3*xspacing, 3*height/4, width/3, btnheight);
		newUser.addActionListener(new NewUserListener());

		components.add(username);
		components.add(password);
		components.add(userLabel);
		components.add(passLabel);
		components.add(login);
		components.add(newUser);

		for (int i = 0; i < components.size(); i++) {
			frame.add(components.get(i));
		}
	}

	// Create the chat room window
	private void chatRoomWindow() {

		//ydim = ydim/2;
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
		//getContentPane().add(frame);
	}

	private void chatSelectionWindow() {

		// I have no idea why but unless it's resized the new components don't appear
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

		////////////////////////////////////////////////////////////
		// list will eventually accept data that was returned by the server
		roomListModel = new DefaultListModel();
		roomListModel.addElement("Room1");
		roomListModel.addElement("Room2");
		roomListModel.addElement("Room3");
		roomListModel.addElement("Room4");
		////////////////////////////////////////////////////////////

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

	// Clear every component from the current frame
	private void clearComponents() {
		for (int i = 0; i < components.size(); i++) {
			frame.remove(components.get(i));
		}

		components.clear();
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

	
	// my idea with this didn't work, feel free to mess with it
	public void refreshDisplay(){
		for (Component com : components){
			com.repaint();
		}
	}	

	/**
	 * Requests a new list of chatrooms, and puts them into the JList
	 * 
	 * @author TomW7
	 *
	 */
	class RefreshListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try{
				oos.writeObject("4");
				String[] rooms = (String[])ois.readObject();
				roomList = new JList();
				roomScroll = new JScrollPane(roomList);
				refreshDisplay();
			} catch (IOException ioe){
				ioe.printStackTrace();
			} catch (ClassNotFoundException cnfe){
				cnfe.printStackTrace();
			}
		}
	}

	class JoinListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try{
				oos.writeObject("2"); // we're joining a room
				if (roomList.getSelectedIndex() < 0){
					chatRoom = JOptionPane.showInputDialog("Enter the name of the chatroom you wish to create:");
				}
				// Check that the user has selected a room
				else {
					// Set the local variable for chat room name
					chatRoom = (String)roomList.getSelectedValue();
				}
				oos.writeObject(chatRoom); // send the name of the chatroom we wanna join
				String [] users = (String[]) ois.readObject();
				userList = new JList(users);
				clearComponents();
				chatRoomWindow();
			} catch (Exception ex){
				ex.printStackTrace();
			}

		}
	}

	/**
	 * Listener class for sending messages to the server. Creates a thread to handle
	 * the actual sending
	 */
	class SendListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String message = null;
			try{
				// One whisper or "All" users selected
				oos.writeObject("3"); // we're sending a message!

				oos.writeObject(user+": "+chatBox.getText());

			} catch(Exception ex){
				ex.printStackTrace();
			}
			chatBox.setText("");
		}
	}


	// Handles the Login button click event
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

	// Handles the New User button click event
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

	// Handles the Leave Room button click event
	class ExitListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// TODO: Clear the components currently in the frame
			try{
				// stop the thread managing the chat
				clientChatHandler.interrupt();
				oos.writeObject("4");
				String [] rooms = (String[])ois.readObject();
				roomList = new JList(rooms); // dirty!
				clearComponents();
				chatSelectionWindow();
			}
			catch (SocketException ex){
				JOptionPane.showMessageDialog(frame, "The server has crashed / is not responsive.");
				ex.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} catch (ClassNotFoundException cnfe) {
				// TODO Auto-generated catch block
				cnfe.printStackTrace();
			}
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
