package net.PRP.MCAI.GUI;

import javax.swing.*;

public class Window {
	
	JFrame frame;
	
	public Window() {
		frame = new JFrame("-------------------------------");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(500,500);
	    frame.setVisible(true);
	    DefaultListModel<String> l = new DefaultListModel<>();
	    l.addElement("huy");
	    JList<String> b = new JList<>(l);
	    b.setBounds(100,100,75,75);
	    frame.add(b);
	    l.addElement("pizda");
	    l.removeElement("huy");
	    
	}
}
