package xyz.onerous.MatrixNetwork.MNIST.deepvisualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import xyz.onerous.MatrixNetwork.MNIST.MnistAgent;

public class DeepVisualMnistAgent extends MnistAgent {
	protected DeepVisualMatrixNetwork matrixNetwork;
	
	public DeepVisualMnistAgent() {
		super();
	}
	
	@Override public void generateNetwork() {
		int nInput = imageData[0].length;
		int nOutput = 10;
		
		this.matrixNetwork = new DeepVisualMatrixNetwork(nInput, nOutput, nHidden, lHidden, learningRate, usingSoftmax, activationType, lossType);
		
		super.matrixNetwork = matrixNetwork;
	}
	
	private BufferedImage generateDeepNetworkVisual(int expectedOutput, int numberOfTrains) {
		return matrixNetwork.generateDeepNetworkVisual(expectedOutput, numberOfTrains);
	}
	
	private void displayDeepNetworkVisual(BufferedImage deepNetworkVisual) {
		DataFrame dataFrame = new DataFrame();
		dataFrame.setSize(new Dimension(300, 350));
		dataFrame.display();
		
		dataFrame.displayBufferedImage(deepNetworkVisual);
	}
	
	public static void main(String[] args) {
		DeepVisualMnistAgent agent = new DeepVisualMnistAgent();		
		
		agent.generateNetwork();
		
		agent.loadNetwork("1");
		
		BufferedImage initVisual = agent.generateDeepNetworkVisual(1, 0);
		agent.displayDeepNetworkVisual(initVisual);
		
		System.out.println("Generating...");
		BufferedImage visual = agent.generateDeepNetworkVisual(1, 10000);
		
		System.out.println("Displaying...");
		agent.displayDeepNetworkVisual(visual);
	}
	
	
	
	
	public class DataPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private BufferedImage currentImage;
		
		public DataPanel() {
			super();
			
			setLayout(null);
		}
		
		public void displayBufferedImage(BufferedImage bufferedImage) {
			currentImage = bufferedImage;
			
			repaint();
		}
		
		/**
		 * Using java's graphics.
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			if (currentImage != null) {
				Image scaledImage = currentImage.getScaledInstance(Math.min(this.getWidth(), this.getHeight()), Math.min(this.getWidth(), this.getHeight()), Image.SCALE_DEFAULT);
				g.drawImage(scaledImage, 0, 0, this);
			}
		}
	}
	
	public class DataFrame extends JFrame {
		private static final long serialVersionUID = 1L;
		private DataPanel dataPanel;

		public DataFrame() {
			super("Current Training Data");
			
			dataPanel = new DataPanel();
			setLayout(null);
		}

		public void display() {
			dataPanel.setSize(this.getSize());
			dataPanel.setBackground(Color.BLACK);
			
			dataPanel.setLocation(0, 0);
			dataPanel.setVisible(true);
			
			setResizable(false);
			getContentPane().add(dataPanel);
			setVisible(true);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
		}
		
		public void displayBufferedImage(BufferedImage bufferedImage) {
			dataPanel.displayBufferedImage(bufferedImage);
		}
	}
}
