package src;

import javax.swing.*;
import java.awt.event.*;
import java.awt.Taskbar;
import java.awt.Image;

public class Main {

	static int pageWidth = 700;
	static int pageHeight = 700;

	public static void main(String[] args){
		createWindow();
	}

	private static void createWindow(){
		JFrame frame = new JFrame("LogicSim");
		frame.setBounds(150, 50, pageWidth, pageHeight);
		frame.setExtendedState(JFrame.NORMAL);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Image img = new ImageIcon("src/assets/logo.png").getImage();
		frame.setIconImage(img);
  	final Taskbar taskbar = Taskbar.getTaskbar();
    try { taskbar.setIconImage(img);}
		catch (final UnsupportedOperationException e) {System.out.println("The os does not support: 'taskbar.setIconImage'");}
		catch (final SecurityException e) {System.out.println("There was a security exception for: 'taskbar.setIconImage'");}

		Display display = new Display(pageWidth, pageHeight);
		frame.add(display);
		frame.setVisible(true);
	}

}
