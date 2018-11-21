package Ex_04;

import javax.swing.JFrame;

public class ServerGUI {
	public ServerGUI(){
		   final JFrame window = new JFrame("Chat");
		    window.getContentPane().setLayout(null);
		    window.setSize(700, 500);
		    window.setResizable(false);
		    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}