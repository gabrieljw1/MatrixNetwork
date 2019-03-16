package xyz.onerous.MatrixNetwork.visualizer.panel;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

/**
 * The panel that shows what the network is currently training with. In the case of
 * this MNIST neural network, it is the image that it is training on and the corresponding label.
 * 
 * @author Gabriel Wong
 */
public class TrainingDataPanel extends JPanel {
	private static final long serialVersionUID = 6021307757364611864L;
	
	//Constants
	private static final int IMAGE_WIDTH = 28;
	private static final int IMAGE_HEIGHT = 28;
	private static final int FONT_SIZE = 10;
	
	//Image and Label storage
	private BufferedImage currentImage;
	private int currentLabel;
	
	/**
	 * Create a new panel with the training data list and array.
	 * 
	 * @param images
	 * @param labels
	 */
	public TrainingDataPanel() {
		super();
		
		setLayout(null);
	}
	
	/**
	 * Display the training data associated with a certain index.
	 * 
	 * @param index
	 */
	public void displayTrainingData(double[] pixelData, int expectedOutput) {
		currentImage = getImageFromArray(pixelData, IMAGE_WIDTH, IMAGE_HEIGHT);
		currentLabel = expectedOutput;
		
		repaint();
	}
	
	/**
	 * Generate a buffered image from and double[] of pixels data.
	 * 
	 * @param pixels
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage getImageFromArray(double[] data, int width, int height) {
		BufferedImage outputImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster raster = outputImage.getRaster();
		
		raster.setSamples(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, 0, data);
        
        return outputImage;
    }
	
	/**
	 * Using java's graphics.
	 */
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
