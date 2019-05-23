package xyz.onerous.MatrixNetwork.visualizer;

import java.awt.Dimension;
import java.awt.Toolkit;

import xyz.onerous.MatrixNetwork.MatrixNetwork;
import xyz.onerous.MatrixNetwork.component.datapackage.NetworkDataPackage;
import xyz.onerous.MatrixNetwork.visualizer.frame.NetworkDiagnosticsFrame;
import xyz.onerous.MatrixNetwork.visualizer.frame.TrainingDataFrame;
import xyz.onerous.MatrixNetwork.visualizer.frame.VisualizerFrame;

public class Visualizer {
	private static final Dimension TRAINING_DATA_FRAME_SIZE = new Dimension(300, 350);
	private static final int SCREEN_HEIGHT_OFFSET = 100;
	private static final int MAX_NEURONS_TO_DISPLAY = 20;
	
	private static final boolean VERBOSE = true;
	
	private TrainingDataFrame trainingDataFrame;
	private VisualizerFrame visualizerFrame;
	private NetworkDiagnosticsFrame networkDiagnosticsFrame;
	
	public Visualizer(MatrixNetwork network) {
		if (VERBOSE) System.out.println("GENERATING VISUALIZER\n");
		
		if (VERBOSE) System.out.print("Generating Training Data Frame... ");
		trainingDataFrame = new TrainingDataFrame();
		trainingDataFrame.setSize(TRAINING_DATA_FRAME_SIZE);
		trainingDataFrame.display();
		if (VERBOSE) System.out.println("Done!");
		
		if (VERBOSE) System.out.print("Generating Visualizer Frame... ");
		visualizerFrame = new VisualizerFrame(network, MAX_NEURONS_TO_DISPLAY);
		visualizerFrame.setSize((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() - trainingDataFrame.getWidth()), (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - SCREEN_HEIGHT_OFFSET));
		visualizerFrame.setLocation(trainingDataFrame.getWidth(), 0);
		visualizerFrame.display();
		if (VERBOSE) System.out.println("Done!");
		
		if (VERBOSE) System.out.print("Generating Diagnostics Frame... ");
		networkDiagnosticsFrame = new NetworkDiagnosticsFrame(network.generateNetworkDataPackage());
		networkDiagnosticsFrame.setSize(trainingDataFrame.getSize());
		networkDiagnosticsFrame.setLocation(0, (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - networkDiagnosticsFrame.getHeight() - SCREEN_HEIGHT_OFFSET));
		networkDiagnosticsFrame.display();
		if (VERBOSE) System.out.println("Done!");
		
		if (VERBOSE) System.out.println("\nDONE");
	}
	
	public void updateTrainingData(double[] pixelData, int expectedOutput) {
		//trainingDataFrame.displayTrainingData(pixelData, expectedOutput);
	}
	
	public void updateVisualizer(NetworkDataPackage networkDataPackage) {
		networkDiagnosticsFrame.updateDataPackage(networkDataPackage);
		visualizerFrame.update(networkDataPackage);
	}
}
