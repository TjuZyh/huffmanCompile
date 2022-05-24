package com.huff;

import java.io.*;
import java.beans.*;

/**
 * This is the MODEL class for your project (MVC architecture)
 * You must complete this file but you must not change the profile
 * of the public methods (except adding throws clause)
 */
public class HuffmanModel {
	
	private PropertyChangeSupport support;
	
	/**
	 * Creates a HuffmanModel instance
	 */
	public HuffmanModel() {
		support = new PropertyChangeSupport(this);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }
	
	/**
	 * Compress the file 'inputFile'
	 */
	public void compress(File inputFile) {
		CompressFile cf = new CompressFile();
		cf.compress(inputFile);
		support.firePropertyChange("msg",null , inputFile.getName() + "the file has been compressed");
	}
	
	/**
	 * Uncompress the file 'inputFile'
	 */
	public void uncompress(File inputFile) {
		UncompressFile cf = new UncompressFile();
		cf.uncompress(inputFile);
		support.firePropertyChange("msg",null , inputFile.getName() + " has been uncompressed");
	}
}
