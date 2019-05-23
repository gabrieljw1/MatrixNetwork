package xyz.onerous.MatrixNetwork.visualizer;

import xyz.onerous.MatrixNetwork.MatrixNetwork;
import xyz.onerous.MatrixNetwork.component.ActivationType;
import xyz.onerous.MatrixNetwork.component.LossType;
import xyz.onerous.MatrixNetwork.component.datapackage.WeightBiasDeltaPackage;
import xyz.onerous.MatrixNetwork.component.util.ArrayUtil;

public class VisualMatrixNetwork extends MatrixNetwork {
	private Visualizer networkVisualizer;
	
	public VisualMatrixNetwork(int nInput, int nOutput, int[] nHidden, int lHidden, double learningRate, boolean usingSoftmax, ActivationType activationType, LossType lossType) {
		super(nInput, nOutput, nHidden, lHidden, learningRate, usingSoftmax, activationType, lossType);
		
		this.networkVisualizer = new Visualizer(this);
	}
	
	@Override public WeightBiasDeltaPackage performTrainAndGetDelta(double[] trainData, int expectedOutput) {
		inputDataAndPropagate(trainData);
	
		double[] visualData = ArrayUtil.rangeTranslation(trainData, 0, 255);
		
		networkVisualizer.updateTrainingData(visualData, expectedOutput);
		networkVisualizer.updateVisualizer(this.generateNetworkDataPackage());
		
		return gradientDescent(expectedOutput);
	}
	
}
