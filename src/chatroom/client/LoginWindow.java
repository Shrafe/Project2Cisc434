package chatroom.client;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import java.awt.SystemColor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPasswordField;

import chatroom.server.MsgObj;
import javax.swing.SwingConstants;

public class LoginWindow {

	private JFrame frmBatChat;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private ClientManager client;

	/**
	 * Create the application.
	 */
	public LoginWindow(ClientManager client) {
		this.client = client;
		initialize();
	}
	
	public JFrame getFrame(){
		return frmBatChat;
	}
	
	public JTextField getUsernameField(){
		return usernameField;
	}
	
	public JPasswordField getPasswordField(){
		return passwordField;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmBatChat = new JFrame();
//		frmBatChat.addWindowListener(new DisconnectListener(this.client.getOos()));
		frmBatChat.setTitle("BaT Chat");
		frmBatChat.setForeground(SystemColor.textHighlight);
		frmBatChat.setBackground(SystemColor.desktop);
		frmBatChat.setResizable(false);
		frmBatChat.setBounds(100, 100, 180, 151);
		frmBatChat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		usernameField = new JTextField();
		usernameField.setColumns(10);
		
		JLabel lblUsername = new JLabel("Username");
		
		JLabel lblPassword = new JLabel("Password");
		
		JButton btnLogin = new JButton("Login");
		btnLogin.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnLogin.addActionListener(new LoginListener());
		
		JButton btnNewUser = new JButton("New User");
		btnNewUser.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnNewUser.addActionListener(new NewUserListener());
		
		JLabel lblWelcomeToChat = new JLabel("Login to BaT Chat");
		lblWelcomeToChat.setHorizontalAlignment(SwingConstants.CENTER);
		lblWelcomeToChat.setFont(new Font("Calibri", Font.BOLD, 16));
		
		passwordField = new JPasswordField();
		GroupLayout groupLayout = new GroupLayout(frmBatChat.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblWelcomeToChat, GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
							.addGroup(groupLayout.createSequentialGroup()
								.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
									.addComponent(lblPassword)
									.addComponent(lblUsername))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
									.addComponent(passwordField)
									.addComponent(usernameField, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)))
							.addGroup(groupLayout.createSequentialGroup()
								.addComponent(btnLogin)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(btnNewUser))))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(7)
					.addComponent(lblWelcomeToChat, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(usernameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblUsername))
							.addGap(26))
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblPassword)
							.addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnNewUser)
						.addComponent(btnLogin))
					.addContainerGap())
		);
		frmBatChat.getContentPane().setLayout(groupLayout);
	}
	
	/**
	 * Listener for the **Login** button on the Login window
	 */
	private class LoginListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try{
				MsgObj message = new MsgObj();
				byte type = 5; // log in type
				message.setType(type);
				message.addToPayload(usernameField.getText());
				message.addToPayload(client.charArrToString(passwordField.getPassword()));
				client.getOos().writeObject(message);
				// wait on the result from ComThread
				client.getLatch().await();
				
				if(client.getLoginSuccess()){

					client.setUser(usernameField.getText());

					//instead of this, we set this frame to invisible, then tell client to launch the next frame
					frmBatChat.setVisible(false);
					client.roomSelectionWindow();
				}
				else
					JOptionPane.showMessageDialog(frmBatChat, "Incorrect username / password. Try again .");
			} catch(Exception ex){
				ex.printStackTrace();
			}

		}
	}
	
	/**
	 * Listener for the **New User** button on the Login window
	 */
	private class NewUserListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try{
				MsgObj message = new MsgObj();
				byte type = 6;
				message.setType(type);
				message.addToPayload(usernameField.getText());				
				message.addToPayload(client.charArrToString(passwordField.getPassword())); 

				// wait for the result to be put into our object
				client.getOos().writeObject(message);
				client.getLatch().await();
				if (client.getCreationSuccess()){
					JOptionPane.showMessageDialog(frmBatChat, "Your new user was successfully registered.");
				}
				else
					JOptionPane.showMessageDialog(frmBatChat, "Your user could not be created. Likely it already exists.");
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}	
}
