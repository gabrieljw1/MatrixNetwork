package xyz.onerous.MatrixNetwork.demerit;

import xyz.onerous.MatrixNetwork.visualizer.VisualMatrixNetwork;
import xyz.onerous.MatrixNetwork.visualizer.Visualizer;

public class VisualDemeritAgent extends DemeritAgent {

	public VisualDemeritAgent() {
		super();
		
		generateNetwork();
	}
	
	@Override public void generateNetwork() {
		int nInput = 4;
		int nOutput = 6;
		
		this.matrixNetwork = new VisualMatrixNetwork(nInput, nOutput, nHidden, lHidden, learningRate, usingSoftmax, activationType, lossType);
	}
	
	public static void main(String[] args) {
		VisualDemeritAgent demeritAgent = new VisualDemeritAgent();

		for (int i = 0; i < 5000; i++)
			demeritAgent.performEpoch(1);
		
		System.out.println(demeritAgent.performTest(0, 53));
	}
}
