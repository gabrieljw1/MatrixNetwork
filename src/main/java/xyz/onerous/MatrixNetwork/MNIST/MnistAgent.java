package xyz.onerous.MatrixNetwork.MNIST;

import java.util.Arrays;
import java.util.List;

import xyz.onerous.MatrixNetwork.MatrixNetwork;
import xyz.onerous.MatrixNetwork.ActivationType;
import xyz.onerous.MatrixNetwork.LossType;
import xyz.onerous.MatrixNetwork.MatrixNetwork;
import xyz.onerous.MatrixNetwork.MNIST.MnistReader;
import xyz.onerous.MatrixNetwork.util.ArrayUtil;

public class MnistAgent {
	private List<int[][]> images;
	private int[] labels;
	private List<int[][]> testImages;
	private int[] testLabels;
	
	private MatrixNetwork matrixNetwork;
	
	private static final String imagesFilePath = "./src/main/resources/train-images.idx3-ubyte";
	private static final String labelsFilePath = "./src/main/resources/train-labels.idx1-ubyte";
	private static final String testImagesFilePath = "./src/main/resources/t10k-images.idx3-ubyte";
	private static final String testLabelsFilePath = "./src/main/resources/t10k-labels.idx1-ubyte";
	
	private static final int lHidden = 2;
	private static final int[] nHidden = new int[] { 500, 500 };
	private static final int nOutput = 10;
	
	private static final double learningRate = 0.0005;
	private static final ActivationType activationType = ActivationType.Sigmoid;
	private static final LossType lossType = LossType.CrossEntropy;
	
	public MnistAgent(String imagesFilePath, String labelsFilePath) {
		this.images = MnistReader.getImages(imagesFilePath);
		this.labels = MnistReader.getLabels(labelsFilePath);
		this.testImages = MnistReader.getImages(testImagesFilePath);
		this.testLabels = MnistReader.getLabels(testLabelsFilePath);
		
		
		if (images.size() == 0 || labels.length == 0 || lHidden != nHidden.length) { return; }
		
		int nInput = images.get(0).length * images.get(0)[0].length;
		
		this.matrixNetwork = new MatrixNetwork(nInput, nOutput, nHidden, lHidden, learningRate, activationType, lossType);
		matrixNetwork.initialize();
	}
	
	private void performEpoch(int startIndex, int endIndex, int batchSize) {
		if (startIndex < 0 || endIndex > images.size()) { return; }
		
		for (int i = startIndex; i < endIndex; i++) {
			double[] standardizedInput = ArrayUtil.standardize( ArrayUtil.flattenArray(images.get(i)) );
			
			matrixNetwork.inputData(standardizedInput);
			
			System.out.println(i + ":: R:" + matrixNetwork.inputData(standardizedInput) + ", E:" + labels[i] + ". Cost: " + matrixNetwork.getOutputError(labels[i]));
			
			matrixNetwork.gradientDescent(labels[i], batchSize);
		}
	}
	
	private double performTest(int startIndex, int endIndex) {
		if (startIndex < 0 || endIndex > testImages.size() || startIndex >= endIndex) { return -1; }
		
		int correctCount = 0;
		
		for (int i = startIndex; i < endIndex; i++) {
			double[] normalizedInput = ArrayUtil.standardize( ArrayUtil.flattenArray(testImages.get(i)) );
			
			int outputIndex = matrixNetwork.inputData(normalizedInput);
			
			System.out.println("Begin test: " + i);
			System.out.println("Network Gave: " + outputIndex + ", but supposed to be: " + labels[i]);
			System.out.println("");
			
			if (outputIndex == testLabels[i]) { correctCount++; }
		}
		
		return 100.0 * (double)correctCount / (double)(endIndex-startIndex);
	}
	
	public static void main(String[] args) {
		MnistAgent mnistAgent = new MnistAgent(imagesFilePath, labelsFilePath);
		
		mnistAgent.performEpoch(0, 10000, 1);
		System.out.println("Got " + mnistAgent.performTest(0, 200) + "% Correct!");
	}
}
