package xyz.onerous.MatrixNetwork.weightvisualizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import xyz.onerous.MatrixNetwork.component.util.ArrayUtil;
import xyz.onerous.MatrixNetwork.component.util.MatrixUtil;

public class WeightVisualizer {
	private double[][][] weights;
	
	private static final Dimension WEIGHT_DATA_FRAME_SIZE = new Dimension(300, 350);
	
	private List<WeightVisualizerFrame> weightVisualizerFrames;
	
	public WeightVisualizer(double[][][] weights) {
		this.weights = weights;
		
		weightVisualizerFrames = new ArrayList<WeightVisualizerFrame>();
	}
	
	public void displayWeightsToNeuron(int layer, int neuron) {
		double[] neuronWeights = weights[layer][neuron];
		
		int width, height;
		
		if (Math.sqrt((double)neuronWeights.length) % 1 == 0) {
			width = (int)Math.sqrt((double)neuronWeights.length);
			height = width;
		} else {
			width = (int)Math.floor(Math.sqrt((double)neuronWeights.length)); //TODO: Make sure this code works
			height = width + 1;
		}
		
		WeightVisualizerFrame weightVisualizerFrame = new WeightVisualizerFrame();
		
		weightVisualizerFrame.setSize(WEIGHT_DATA_FRAME_SIZE);
		weightVisualizerFrame.setLocation(0, 0);
		weightVisualizerFrame.display();
		
		weightVisualizerFrame.displayWeightData(neuronWeights, "l" + layer + ", n" + neuron, width, height);
		
		weightVisualizerFrames.add(weightVisualizerFrame);
	}
	
	public void displayWeightsFromNeuron(int layer, int neuron) {
		double[][] neuronWeights = weights[layer + 1];
		
		double[] weightsFromNeuron = MatrixUtil.transpose(neuronWeights)[neuron];
		
		int width, height;
		
		if (Math.sqrt((double)neuronWeights.length) % 1 == 0) {
			width = (int)Math.sqrt((double)neuronWeights.length);
			height = width;
		} else {
			width = (int)Math.floor(Math.sqrt((double)neuronWeights.length)); //TODO: Make sure this code works
			height = width + 1;
		}
		
		WeightVisualizerFrame weightVisualizerFrame = new WeightVisualizerFrame();
		
		weightVisualizerFrame.setSize(WEIGHT_DATA_FRAME_SIZE);
		weightVisualizerFrame.setLocation(0, 0);
		weightVisualizerFrame.display();
		
		weightVisualizerFrame.displayWeightData(weightsFromNeuron, "l" + layer + ", n" + neuron, width, height);
		
		weightVisualizerFrames.add(weightVisualizerFrame);
	}
	
	public class WeightVisualizerFrame extends JFrame {
		private static final long serialVersionUID = 8985650273263084070L;
		private WeightVisualizerPanel weightVisualizerPanel;
		
		/**
		 * Create a new frame and the panel that has all of the training data built in.
		 * 
		 * @param images
		 * @param labels
		 */
		public WeightVisualizerFrame() {
			super("Current Training Data");
			
			weightVisualizerPanel = new WeightVisualizerPanel();
			setLayout(null);
		}
		
		/**
		 * Display the Frame.
		 */
		public void display() {
			weightVisualizerPanel.setSize(this.getSize());
			weightVisualizerPanel.setBackground(Color.BLACK);
			
			weightVisualizerPanel.setLocation(0, 0);
			weightVisualizerPanel.setVisible(true);
			
			setResizable(false);
			getContentPane().add(weightVisualizerPanel);
			setVisible(true);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
		}
		
		/**
		 * Display the training data (An image and a label) associated with a certain index.
		 * 
		 * @param index
		 */
		public void displayWeightData(double[] pixelData, String weightLabel, int width, int height) {
			weightVisualizerPanel.displayWeightData(pixelData, weightLabel, width, height);
		}
		
		
		public class WeightVisualizerPanel extends JPanel {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1893435727149453093L;
			
			//Constants
			private static final int FONT_SIZE = 10;
			
			//Image and Label storage
			private BufferedImage currentImage;
			private String currentLabel;
		
			public WeightVisualizerPanel() {
				super();
				
				setLayout(null);
			}

			public void displayWeightData(double[] pixelData, String weightLabel, int width, int height) {
				currentImage = getImageFromArray(pixelData, width, height);
				currentLabel = weightLabel;
				
				repaint();
			}
			
			public BufferedImage getImageFromArray(double[] data, int width, int height) {				
				BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
				WritableRaster raster = outputImage.getRaster();
			
				double[] translatedData = ArrayUtil.rangeTranslation(ArrayUtil.absArray(ArrayUtil.standardize(data)), 0, 255);
				
				raster.setSamples(0, 0, width, height, 0, translatedData);
		        
		        return outputImage;
		    }

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				if (currentImage != null) {
					Image scaledImage = currentImage.getScaledInstance(Math.min(this.getWidth(), this.getHeight()), Math.min(this.getWidth(), this.getHeight()), Image.SCALE_DEFAULT);
					g.drawImage(scaledImage, 0, 0, this);
					
					if (this.getWidth() <= this.getHeight()) {
						g.setColor(Color.WHITE);
						g.setFont(new Font("Values", 0, FONT_SIZE));
						g.drawString("Value: " + String.valueOf(currentLabel), 0, this.getHeight() - 3 * FONT_SIZE);
					}
				}
			}
		}
	}
}
