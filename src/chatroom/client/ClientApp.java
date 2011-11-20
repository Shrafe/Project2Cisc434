package chatroom.client;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
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
	private DefaultListModel list;
	
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
	
	private ArrayList<Component> components;
	
	public ClientApp (JFrame frame) {
		this.frame = frame;
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
	
	// Create the window for choosing the chat room
	private void chatRoomWindow() {
		
		ydim = ydim/2;
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
		list = new DefaultListModel();
		list.addElement("All");
		list.addElement("User1");
		list.addElement("User2");
		list.addElement("User3");
		list.addElement("User4");
		////////////////////////////////////////////////////////////
		
		userList = new JList(list);
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
		//getContentPane().add(frame);
	}

	private void chatSelectionWindow() {
		
	}
	
	// Clear every component from the current frame
	private void clearComponents() {
		for (int i = 0; i < components.size(); i++) {
			frame.remove(components.get(i));
		}
		
		components.clear();
	}
	
	/**
	 * Listener class for sending messages to the server. Creates a thread to handle
	 * the actual sending
	 */
	class SendListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			int size = userList.getSelectedIndices().length;
			ClientMsgThread thread;
			
			// One whisper or "All" users selected
			if(size == 1) {
				
				if (userList.getSelectedIndex() != 0) {
					// Whisper the target
					thread = new ClientMsgThread(
							user + ": " + chatBox.getText(),
							(String)userList.getSelectedValue()
							);
				}
				else {
					// Post to the room
					thread = new ClientMsgThread(user + ": " + chatBox.getText());
				}
			}
			else {
				// Whisper multiple targets
				List<String> users = makeList(userList.getSelectedValues());
				thread = new ClientMsgThread(
						user + ": " + chatBox.getText(),
						users);
			}
			
			// Send the message
			thread.run();
			
			/////////////////////////////////////////////
			// Testing that the message shows up in the box
			chatHistory.setText(chatHistory.getText() + "\n" + user + ": " + chatBox.getText());
			/////////////////////////////////////////////
			
			chatBox.setText("");
		}
	}
	
	public List<String> makeList(Object[] in){
		List<String> retVal = new ArrayList<String>();
		for (Object a : in){
			retVal.add((String)a);
		}
		return retVal;
	}
	
	// Handles the Login button click event
	class LoginListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			if (!username.getText().equals("")) {
				
				// TODO: Send the textfield data to the server then wait for a reply.
				user = username.getText();
				
				clearComponents();
				
				chatRoomWindow();
			}
		}
	}
	
	// Handles the New User button click event
	class NewUserListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// TODO: Clear the components currently in the frame
		}
	}
	
	// Handles the Leave Room button click event
	class ExitListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// TODO: Clear the components currently in the frame
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
