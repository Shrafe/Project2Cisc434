package chatroom.client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;

public class ClientFrame extends JFrame {

	public static void start(String title, String hostname, int port) {
		Dimension size;	
		ClientFrame f = new ClientFrame(title);

		ClientApp a = new ClientApp(f, hostname, port);
		
		a.init();
		a.start();
		
		Container contentPane = f.getContentPane();
		contentPane.add( a, BorderLayout.CENTER);

		f.setVisible(true);
	} 
	
	public ClientFrame(String name) {
		super(name);
		addWindowListener(closer);
	}
	
	WindowListener closer = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			dispose();
			System.exit(0);
		}
	};
	
	public static void main(String args[]) {
		int port = Integer.parseInt(args[0]);
		String hostname = args[1];
		ClientFrame.start("ClientUI", hostname, port);
	}
}
