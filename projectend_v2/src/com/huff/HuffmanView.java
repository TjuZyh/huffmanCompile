package com.huff;

import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.GridLayout;
import java.awt.event.*;

import java.beans.*;

/**
 * This is the VIEW class for your project (MVC architecture)
 * YOU MUST NOT CHANGE THIS FILE
 */
public class HuffmanView implements PropertyChangeListener {
	
   	private JFrame appFrame;
   	
   	private JLabel jlbMessage;
   	private JButton jbnCompress, jbnUncompress, jbnExit;
   	private JFileChooser jfcCompress, jfcUncompress;
   	
   	/**
   	 * Creates and open the view of the Huffman program
   	 */
   	public HuffmanView() {
   		createGUI();
   	}
	
   	/**
   	 * Gets the file to compress
   	 * selected by the user
   	 */
	public File getFileToCompress() {
		return getFile(jfcCompress);
	}
	
   	/**
   	 * Gets the file to uncompress
   	 * selected by the user
   	 */
	public File getFileToUncompress() {
		return getFile(jfcUncompress);
	}
	
	/**
	 * Sets the listener 'listener' to listen this view
	 */
	public void setListener(ActionListener listener) {
		jbnCompress.addActionListener(listener);
		jbnUncompress.addActionListener(listener);
   		jbnExit.addActionListener(listener);		
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		jlbMessage.setText((String) evt.getNewValue());
    }
   	
   	private void createGUI() {
   		appFrame = new JFrame("Huffman");
   		
   		jfcCompress = new JFileChooser();
   		jfcUncompress = new JFileChooser();
   		FileNameExtensionFilter filter = new FileNameExtensionFilter("Huffman compressed files", "hu");
   		jfcUncompress.setFileFilter(filter);

   		creatAndPlaceComponents();
   		
   		appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   		appFrame.setSize(300,100);
   		appFrame.setResizable(false);
   		appFrame.setVisible(true);
   	}
   	
	private void creatAndPlaceComponents() {
   		jlbMessage = new JLabel("Choose your action",SwingConstants.CENTER);
   		
   		jbnCompress    = new JButton("Compress");
   		jbnUncompress  = new JButton("Uncompress");
   		jbnExit        = new JButton("Exit");
 
		JPanel buttons = new JPanel(new GridLayout(1,3));
		buttons.add(jbnCompress);
		buttons.add(jbnUncompress);
		buttons.add(jbnExit);
		
		JPanel jpMain = new JPanel(new GridLayout(2,1));
		jpMain.add(jlbMessage);
		jpMain.add(buttons);
   		
		jbnCompress.setActionCommand("compress");
		jbnUncompress.setActionCommand("uncompress");
   		jbnExit.setActionCommand("exit");
		
		appFrame.setContentPane(jpMain);   		
	}
	
	private File getFile(JFileChooser chooser) {
		int returnVal = chooser.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION)
	    	return chooser.getSelectedFile();
	    return null;		
	}
}
