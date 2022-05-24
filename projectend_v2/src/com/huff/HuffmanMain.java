package com.huff;

/**
 * This is the main class of your project
 * YOU MUST NOT CHANGE THIS FILE
 */
public class HuffmanMain {

		/**
		 * Runs the Huffman program and opens the user interface
		 */
		public static void main(String[] args) {
			HuffmanModel model = new HuffmanModel();
	        javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	            	HuffmanView view = new HuffmanView();
	            	model.addPropertyChangeListener(view);
	            	HuffmanController controller = new HuffmanController(view,model);
	            	view.setListener(controller);
	            }
	        });
		}
}
