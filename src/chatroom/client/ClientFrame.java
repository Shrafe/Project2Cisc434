package chatroom.client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;

public class ClientFrame extends JFrame {

	public static void start(String title) {
		Dimension size;	
		ClientFrame f = new ClientFrame(title);

		ClientApp a = new ClientApp(f);
		
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
		ClientFrame.start("ClientUI");
	}
}
