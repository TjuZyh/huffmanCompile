package com.huff;

import java.awt.event.*;
import java.io.*;

/**
 * This is the CONTROLLER class for your project (MVC architecture)
 * YOU MUST NOT CHANGE THIS FILE
 */
public class HuffmanController implements ActionListener {
	
	private HuffmanView view;   // the view
	private HuffmanModel model; // the model
	
	/**
	 * Creates a new Controller using the View 'view'
	 * and the Model 'model' (MVC architecture)
	 */
	public HuffmanController(HuffmanView view, HuffmanModel model)
	{
		this.view = view;
		this.model = model;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if ( e.getActionCommand().equals("compress") )
		{
			File inputFile = view.getFileToCompress();
			if ( inputFile != null )
				model.compress(inputFile);
		}
		else if ( e.getActionCommand().equals("uncompress") )
		{
			File inputFile = view.getFileToUncompress();
			if ( inputFile != null )
				model.uncompress(inputFile);
		}
		else if ( e.getActionCommand().equals("exit") )
		{
			System.exit(0);
		}
	}
}
