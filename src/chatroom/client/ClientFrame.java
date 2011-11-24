package chatroom.client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class ClientFrame extends JFrame {

	public static void start(String title, String hostname, int port) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		ClientFrame f = new ClientFrame(title);
		ClientApp a = new ClientApp(f, hostname, port);
		
		
		
		a.init();
		a.start();

		
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
		//int port = Integer.parseInt(args[0]);
		//String hostname = args[1];
		int port = 4444;
		String hostname = "localhost";
		ClientFrame.start("ClientUI", hostname, port);
	}
}
