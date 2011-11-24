package chatroom.client;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JFormattedTextField;
import javax.swing.JTextArea;
import javax.swing.JList;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.ScrollPaneConstants;
import java.awt.TextArea;

public class HelpWindow {

	private JFrame frame;
	/**
	 * Create the application.
	 */
	public HelpWindow() {
		initialize();
	}
	
	public JFrame getFrame(){
		return this.frame;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 463, 617);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMinimum());
		JLabel lblWelcomeToBat = new JLabel("Welcome to BaT Chat");
		lblWelcomeToBat.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblWelcomeToBat.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(153)
							.addComponent(lblWelcomeToBat)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblWelcomeToBat)
					.addPreferredGap(ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 539, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		JTextArea txtrThisChatProgram = new JTextArea();
		txtrThisChatProgram.setLineWrap(true);
		txtrThisChatProgram.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtrThisChatProgram.setText("This chat program is very simple. The server contains a list of chatrooms which can be joined or created by an authenticated user. \r\n\r\nLogging in:\r\n       - Enter your username and password, and click login!\r\n\r\nCreating a user:\r\n       - Fill in the desired username and password, and click create!\r\n\r\nChatrooms:\r\nAfter being validated, you will be displayed with a list of rooms currently\r\non the server. \r\n\r\nJoining a chatroom:\r\n       - Click the chatroom you wish to join, and click Join!\r\n       - Click join and enter the name of the chatroom you want to join!\r\n\r\nCreating a chatroom:\r\n       - Click the Create button; you will be prompted for the name of a \r\n       chatroom. Enter it in (cannot be a duplicate of another room) and \r\n       your chatroom will appear to all other users connected. It can be\r\n       joined immediately.\r\n\r\n\r\nChatting\r\nOnce you have joined a chatroom of your choice, you will be able to see\r\nthe users in the chatroom you have joined, as well as the messages being \r\npassed back and forth. \r\n\r\nSending a message:\r\n        - Enter the desired message content into the text field to the left \r\n          of the send button. When you are done, click send!\r\n\r\nLeaving a room: \r\n        - Pressing the \"Leave room\" button will take you back to the room \r\n           selection dialog, to join a new room and continue chatting!\r\n\r\n\r\nHappy Chatting!");
		txtrThisChatProgram.setCaretPosition(0);
		scrollPane.setViewportView(txtrThisChatProgram);
		frame.getContentPane().setLayout(groupLayout);
	}
}
