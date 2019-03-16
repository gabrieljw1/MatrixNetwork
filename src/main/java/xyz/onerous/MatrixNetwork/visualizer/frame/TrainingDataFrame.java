package xyz.onerous.MatrixNetwork.visualizer.frame;

import java.awt.Color;

import javax.swing.JFrame;

import xyz.onerous.MatrixNetwork.visualizer.panel.TrainingDataPanel;

/**
 * A frame that will house the panel that shows what the network is currently training with. In the case of
 * this MNIST neural network, it is the image that it is training on and the corresponding label.
 * 
 * @author Gabriel Wong
 */
public class TrainingDataFrame extends JFrame {
	private static final long serialVersionUID = -4142193521457753998L;
	
	private TrainingDataPanel trainingDataPanel;
	
	/**
	 * Create a new frame and the panel that has all of the training data built in.
	 * 
	 * @param images
	 * @param labels
	 */
	public TrainingDataFrame() {
		super("Current Training Data");
		
		trainingDataPanel = new TrainingDataPanel();
		setLayout(null);
	}
	
	/**
	 * Display the Frame.
	 */
	public void display() {
		trainingDataPanel.setSize(this.getSize());
		trainingDataPanel.setBackground(Color.BLACK);
		
		trainingDataPanel.setLocation(0, 0);
		trainingDataPanel.setVisible(true);
		
		setResizable(false);
		getContentPane().add(trainingDataPanel);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	/**
	 * Display the training data (An image and a label) associated with a certain index.
	 * 
	 * @param index
	 */
	public void displayTrainingData(double[] pixelData, int expectedOutput) {
		trainingDataPanel.displayTrainingData(pixelData, expectedOutput);
	}
}
