package chatroom.client;

import javax.swing.DefaultListModel;
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
	private DefaultListModel roomListModel;
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

	public DefaultListModel getRoomListModel(){
		return roomListModel;
	}

	public JLabel getTitle(){
		return this.lblTitle;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmChatroom = new JFrame();
		frmChatroom.setTitle("Room Select");
//		frmChatroom.addWindowListener(new DisconnectListener(this.client.getOos()));
		frmChatroom.setResizable(false);
		frmChatroom.setBounds(100, 100, 250, 304);
		frmChatroom.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.roomListModel = new DefaultListModel();
		this.roomList = new JList(roomListModel);

		JButton btnJoin = new JButton("Join");
		btnJoin.setToolTipText("Join the selected room. If no room is selected, a new room is created with a name given.");
		btnJoin.addActionListener(new JoinListener());

		JButton btnRefresh = new JButton("Create");
		btnRefresh.addActionListener(new CreateListener());

		lblTitle = new JLabel();
		GroupLayout groupLayout = new GroupLayout(frmChatroom.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(roomList, GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnJoin, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnRefresh, GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
						.addComponent(lblTitle, GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE))
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
						.addComponent(btnJoin)
						.addComponent(btnRefresh, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		frmChatroom.getContentPane().setLayout(groupLayout);
	}

	/**
	 * Listener for the Refresh button on the Chat Room
	 * Selection window
	 */
	class CreateListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			MsgObj message = new MsgObj();
			byte type = 0;
			message.setType(type);
			String crn = JOptionPane.showInputDialog("Enter the name of the chatroom you wish to create:");
			if (crn!=null){ // if the user doesn't click cancel
				try {
					message.addToPayload(crn);
					client.getOos().writeObject(message);
					client.getLatch().await(); // wait for the result of our attempt to make the room
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (InterruptedException ie){
					ie.printStackTrace();
				}
				if (client.getRoomCreationSuccess()){
					JOptionPane.showMessageDialog(frmChatroom, "Your room named '"+crn+"' was created successfully.");
				}
				else {
					JOptionPane.showMessageDialog(frmChatroom, "Room creation failed. There is already a room with this name.");
				}
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
				joinChatroom = JOptionPane.showInputDialog("Enter the name of the chatroom you wish to join:");
			}
			// Check that the user has selected a room
			else {
				// Set the local variable for chat room name
				joinChatroom = (String)roomList.getSelectedValue();
			}
			if (joinChatroom!=null){
				MsgObj message = new MsgObj();
				message.addToPayload(joinChatroom);
				byte type = 2;
				message.setType(type);

				// Send the message
				try {
					client.getOos().writeObject(message);
					client.getLatch().await();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (InterruptedException ie){
					ie.printStackTrace();
				}
				// Launch the Chat Room window
				// set us invisible
				if (client.getRoomJoinSuccess()){
					client.setRoom(joinChatroom);
					frmChatroom.setVisible(false);
					client.chatroomWindow();
				} 
				else
					JOptionPane.showMessageDialog(frmChatroom, "Could not join the chatroom '"+joinChatroom+"'. It does not exist");
			}
		}
	}
}
