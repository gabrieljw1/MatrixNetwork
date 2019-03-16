package xyz.onerous.MatrixNetwork.visualizer.panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

import xyz.onerous.MatrixNetwork.component.datapackage.NetworkDataPackage;

/**
 * A panel that displays useful information about the network that may include but may not be limited to the
 * network's output, the error, and other data.
 * 
 * @author Gabriel Wong
 */
public class NetworkDiagnosticsPanel extends JPanel {
	private static final long serialVersionUID = -911893963392160214L;
	
	private static final int NUMBER_OF_DATA_POINTS = 2;
	private static final int FONT_SIZE = 10;
	
	private NetworkDataPackage networkDataPackage;
	
	/**
	 * Create a new panel with the network's data.
	 * 
	 * @param networkDataPackage
	 */
	public NetworkDiagnosticsPanel(NetworkDataPackage networkDataPackage) {
		super();
		
		this.networkDataPackage = networkDataPackage;
	}
	
	/**
	 * Update the visualized data.
	 * 
	 * @param extendedNetworkDataPackage
	 */
	public void updateDataPackage(NetworkDataPackage networkDataPackage) {
		this.networkDataPackage = networkDataPackage;
		repaint();
	}
	
	/**
	 * Show the component on screen using java's Graphics.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.setColor(Color.WHITE);
		g.setFont(new Font("Values", 0, FONT_SIZE));
		
		g.drawString("Network Output: " + networkDataPackage.getNetworkResult(), 0, (int)(this.getHeight()/((double)NUMBER_OF_DATA_POINTS+1.0) ));
	}
}
