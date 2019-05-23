package xyz.onerous.MatrixNetwork.MNIST.visual;

import java.util.Arrays;

import xyz.onerous.MatrixNetwork.MatrixNetwork;
import xyz.onerous.MatrixNetwork.MNIST.MnistAgent;
import xyz.onerous.MatrixNetwork.component.ActivationType;
import xyz.onerous.MatrixNetwork.component.LossType;
import xyz.onerous.MatrixNetwork.component.datapackage.WeightBiasDeltaPackage;
import xyz.onerous.MatrixNetwork.component.util.ArrayUtil;
import xyz.onerous.MatrixNetwork.visualizer.VisualMatrixNetwork;
import xyz.onerous.MatrixNetwork.visualizer.Visualizer;

public class VisualMnistAgent extends MnistAgent {
	private final int lHidden = 2;
	private final int[] nHidden = new int[] { 500, 500 };
	
	private final double learningRate = 0.001;
	private final boolean usingSoftmax = true;
	private final ActivationType activationType = ActivationType.Sigmoid;
	private final LossType lossType = LossType.CrossEntropy;
	
	public VisualMnistAgent() {
		super();
		
		generateNetwork();
	}
	
	@Override public void generateNetwork() {
		int nInput = imageData[0].length;
		int nOutput = 10;
		
		this.matrixNetwork = new VisualMatrixNetwork(nInput, nOutput, nHidden, lHidden, learningRate, usingSoftmax, activationType, lossType);
	}
	
	public static void main(String[] args) {
		VisualMnistAgent agent = new VisualMnistAgent();
		
		agent.performEpoch(1);
	}
}
