package xyz.onerous.MatrixNetwork.visualizer.panel;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import xyz.onerous.MatrixNetwork.MatrixNetwork;
import xyz.onerous.MatrixNetwork.component.datapackage.NetworkDataPackage;
import xyz.onerous.MatrixNetwork.visualizer.component.VConnection;
import xyz.onerous.MatrixNetwork.visualizer.component.VNeuron;

/**
 * The panel that will visualize all neurons and connections of a network.
 * 
 * @author Gabriel Wong
 */
public class NeuronsPanel extends JPanel {
	private static final long serialVersionUID = -6962839743258148205L;

	//Constants
	private static final int DEFAULT_NEURON_RADIUS = 25;
	private static final Color DEFAULT_NEURON_COLOR = Color.GREEN;
	private static final Color DEFAULT_CONNECTION_COLOR = Color.BLACK;

	//Variables
	private Color neuronColor = DEFAULT_NEURON_COLOR;
	private Color connectionColor = DEFAULT_CONNECTION_COLOR;
	private int neuronRadius = DEFAULT_NEURON_RADIUS;
	private int maxNeuronsToDisplay;
	private VNeuron[][] neurons;
	
	//Network data variables
	private int numL;
	private int[] nPerLayer;
	private NetworkDataPackage networkDataPackage;
	
	//Spacing variables
	private int xStep;
	private int[] yStep;
	
	/**
	 * Create a new panel with the network and its data.
	 * 
	 * @param network
	 * @param networkDataPackage
	 */
	public NeuronsPanel(MatrixNetwork network, int maxNeuronsToDisplay) {
		super();
		
		this.networkDataPackage = network.generateNetworkDataPackage();
		
		setLayout(null);
		
		this.numL = network.getNumL();
		this.nPerLayer = network.getNPerLayer();
		this.maxNeuronsToDisplay = maxNeuronsToDisplay;
		
		
		//Create the neurons array
		this.neurons = new VNeuron[this.numL][];
		
		for (int l = 0; l < this.numL; l++) {
			this.neurons[l] = new VNeuron[Math.min(this.nPerLayer[l], maxNeuronsToDisplay)];
		}
	}
	
	/**
	 * Generate the constants required to space the visual components evenly.
	 */
	public void generateSpacingConstants() {
		xStep = this.getWidth() / (this.numL + 1);
		yStep = new int[this.numL];
		
		for (int l = 0; l < this.numL; l++) {
			yStep[l] = this.getHeight() / (Math.min(this.nPerLayer[l], maxNeuronsToDisplay) + 1);
		}
		
		if (neuronRadius * nPerLayer[0] > this.getHeight()) {
			neuronRadius = 10;
		}
	}
	
	/**
	 * Generate all of the VNeurons from the network data.
	 */
	public void generateVNeurons() {
		generateSpacingConstants();
		
		for (int l = 0; l < this.numL; l++) {
			for (int n = 0; n < this.nPerLayer[l] && n < maxNeuronsToDisplay; n++) {
				int xPos = this.xStep * (l + 1) - neuronRadius;
				int yPos = this.yStep[l] * (n + 1) - neuronRadius;
				
				neurons[l][n] = new VNeuron(neuronColor, xPos, yPos, neuronRadius);
				
				if (networkDataPackage != null) {
					neurons[l][n].setValue( networkDataPackage.getNeuronActivations()[l][n] );
					neurons[l][n].setBias( networkDataPackage.getNeuronBiases()[l][n] );
				}
			}
		}
	}
	
	/**
	 * Generate all of the VConnections from the network data and add them to corresponding VNeurons.
	 */
	public void generateVConnections() {				
		for (int l = 1; l < this.numL; l++) {
			for (int n = 0; n < this.nPerLayer[l] && n < maxNeuronsToDisplay; n++) {
				neurons[l][n].setConnections( new VConnection[Math.min(this.nPerLayer[l-1], maxNeuronsToDisplay)] );
				for (int pn = 0; pn < this.nPerLayer[l-1] && pn < maxNeuronsToDisplay; pn++) {
					VConnection connection = new VConnection(connectionColor, neurons[l][n], neurons[l-1][pn]);
					neurons[l][n].setConnection(pn, connection);
					
					if (networkDataPackage != null) {
						neurons[l][n].setConnectionValue(pn, networkDataPackage.getConnectionValues()[l][n][pn]);
						neurons[l][n].setConnectionWeight(pn, networkDataPackage.getConnectionWeights()[l][n][pn]);
					}
				}
			}
		}
	}
	
	/**
	 * Add VNeurons to the view.
	 */
	public void addVNeurons() {		
		for (VNeuron[] layer : neurons) {
			for (VNeuron neuron : layer) {
				add(neuron);
			}
		}
	}
	
	/**
	 * Add VConnections to the view.
	 */
	public void addVConnections() {
		for (int l = 1; l < this.numL; l++) {
			for (VNeuron neuron : neurons[l]) {	
				for (VConnection connection : neuron.getConnections()) {
					add(connection);
				}
			}
		}
	}
	
	/**
	 * Convenience method to generate both VNeurons and VConnections at once.
	 */
	public void generateAll() {
		generateVNeurons();
		generateVConnections();
	}
	
	/**
	 * Convenience method to add both VNeurons and VConnections to the view at once.
	 */
	public void addAll() {
		addVNeurons();
		addVConnections();
	}
	
	/**
	 * Update the data package with a new one from the network.
	 * 
	 * @param networkDataPackage
	 */
	public void updateData(NetworkDataPackage networkDataPackage) {	
		this.networkDataPackage = networkDataPackage;
		
		for (int l = 0; l < neurons.length; l++) {
			for (int n = 0; n < neurons[l].length && n < maxNeuronsToDisplay; n++) {
				neurons[l][n].setValue(networkDataPackage.getNeuronActivations()[l][n]);
				neurons[l][n].repaint();
				
				if (l != 0) {
					for (int c = 0; c < neurons[l][n].getConnections().length; c++) {
						neurons[l][n].setConnectionWeight(c, networkDataPackage.getConnectionWeights()[l][n][c]);
					}
				}
			}
		}
	}
	
	/**
	 * Drawing code to bring in the gradient background.
	 */
	@Override protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		//Gradient code taken from StackOverflow
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		Color color1 = Color.BLUE;
        Color color2 = Color.CYAN;
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
	}
}
