package chatroom.client;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.JOptionPane;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;

import chatroom.server.MsgObj;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class RoomSelectionWindow {

	private JFrame frmChatroom;
	private JList roomList;
	private ClientManager client;
	private JLabel lblTitle;

	/**
	 * Create the application.
	 */
	public RoomSelectionWindow(ClientManager client) {
		this.client = client;
		initialize();
	}

	public JFrame getFrame(){
		return frmChatroom;
	}

	public JList getRoomList(){
		return roomList;
	}
	
	public JLabel getTitle(){
		return this.lblTitle;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmChatroom = new JFrame();
		frmChatroom.setTitle("Select");
		frmChatroom.setBounds(100, 100, 207, 318);
		frmChatroom.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		roomList = new JList();

		JButton btnJoin = new JButton("Join / Create");
		btnJoin.addActionListener(new JoinListener());

		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new RefreshListener());

		lblTitle = new JLabel();
		GroupLayout groupLayout = new GroupLayout(frmChatroom.getContentPane());
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(roomList, GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup()
										.addComponent(btnJoin, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(btnRefresh))
										.addComponent(lblTitle))
										.addContainerGap())
				);
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addGap(8)
						.addComponent(lblTitle, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(roomList, GroupLayout.PREFERRED_SIZE, 206, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(btnRefresh, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnJoin))
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);
		frmChatroom.getContentPane().setLayout(groupLayout);
	}

	/**
	 * Listener for the Refresh button on the Chat Room
	 * Selection window
	 */
	class RefreshListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			MsgObj message = new MsgObj();
			byte type = 0;
			message.setType(type);

			try {
				client.getOos().writeObject(message);
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
			String joinChatroom = null;
			if (roomList.getSelectedIndex() == -1){
				while (joinChatroom == null){
					joinChatroom = JOptionPane.showInputDialog("Enter the name of the chatroom you wish to create:");
				}
			}
			// Check that the user has selected a room
			else {
				// Set the local variable for chat room name
				joinChatroom = (String)roomList.getSelectedValue();
			}
			MsgObj message = new MsgObj();
			message.addToPayload(joinChatroom);
			byte type = 2;
			message.setType(type);

			// Send the message
			try {
				client.getOos().writeObject(message);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			// Launch the Chat Room window
			// set us invisible
			client.setRoom(joinChatroom);
			frmChatroom.setVisible(false);
			client.chatroomWindow();
		}
	}
}
