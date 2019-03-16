package xyz.onerous.MatrixNetwork.visualizer.frame;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;


import xyz.onerous.MatrixNetwork.MatrixNetwork;
import xyz.onerous.MatrixNetwork.component.datapackage.NetworkDataPackage;
import xyz.onerous.MatrixNetwork.visualizer.panel.NeuronsPanel;

/**
 * A frame that will house the panel that displays all neurons and connections of a neural network.
 * 
 * @author Gabriel Wong
 */
public class VisualizerFrame extends JFrame {
	private static final long serialVersionUID = 8708072835816254043L;
	
	private NeuronsPanel neuronsPanel;
	
	/**
	 * Create a new frame and the panel that contains the VNeurons and VConnections.
	 * 
	 * @param network The network to visualize.
	 */
	public VisualizerFrame(MatrixNetwork network, int maxNeuronsToDisplay) {
		super("Neural Network Visualizer");
		
		neuronsPanel = new NeuronsPanel(network, maxNeuronsToDisplay);
	}
	
	/**
	 * Display the frame.
	 */
	public void display() {		
		neuronsPanel.setBackground(Color.BLUE);
		
		neuronsPanel.setSize(getSize());
		
		neuronsPanel.generateAll();
		neuronsPanel.addAll();
		
		neuronsPanel.setVisible(true);
		
		setResizable(false);
		getContentPane().add(neuronsPanel, BorderLayout.CENTER);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/**
	 * Update the data that is being visualized.
	 * 
	 * @param networkDataPackage
	 */
	public void update(NetworkDataPackage networkDataPackage) {
		neuronsPanel.updateData(networkDataPackage);
	}
}
