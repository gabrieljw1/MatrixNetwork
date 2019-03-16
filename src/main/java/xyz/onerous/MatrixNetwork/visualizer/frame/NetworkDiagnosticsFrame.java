package xyz.onerous.MatrixNetwork.visualizer.frame;

import java.awt.Color;

import javax.swing.JFrame;

import xyz.onerous.MatrixNetwork.component.datapackage.NetworkDataPackage;
import xyz.onerous.MatrixNetwork.visualizer.panel.NetworkDiagnosticsPanel;


/**
 * A frame that displays useful information about the network. This is just a wrapper for the panel that will
 * perform this action.
 * 
 * @author Gabriel Wong
 */
public class NetworkDiagnosticsFrame extends JFrame {
	private static final long serialVersionUID = 1442893998717329579L;
	
	private NetworkDiagnosticsPanel networkDiagnosticsPanel;
	
	/**
	 * Create a new Frame with a data package. This creates the panel which is displayed on call.
	 * 
	 * @param networkDataPackage
	 */
	public NetworkDiagnosticsFrame(NetworkDataPackage networkDataPackage) {
		super("Network Diagnostics");
		
		networkDiagnosticsPanel = new NetworkDiagnosticsPanel(networkDataPackage);
		setLayout(null);
	}
	
	/**
	 * Display the frame.
	 */
	public void display() {
		networkDiagnosticsPanel.setSize(this.getSize());
		networkDiagnosticsPanel.setBackground(Color.BLACK);
		
		networkDiagnosticsPanel.setLocation(0, 0);
		networkDiagnosticsPanel.setVisible(true);
		
		setResizable(false);
		getContentPane().add(networkDiagnosticsPanel);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	/**
	 * Update the data package with new data from the network.
	 * 
	 * @param networkDataPackage
	 */
	public void updateDataPackage(NetworkDataPackage networkDataPackage) {
		networkDiagnosticsPanel.updateDataPackage(networkDataPackage);
	}
}
