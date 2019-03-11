package xyz.onerous.MatrixNetwork.MNIST;

import java.util.List;

import xyz.onerous.MatrixNetwork.MatrixNetwork;
import xyz.onerous.MatrixNetwork.ActivationType;
import xyz.onerous.MatrixNetwork.LossType;
import xyz.onerous.MatrixNetwork.MNIST.MnistReader;
import xyz.onerous.MatrixNetwork.util.ArrayUtil;

public class MnistAgent {
	private List<int[][]> images;
	private int[] labels;
	private List<int[][]> testImages;
	private int[] testLabels;
	
	private double[][] imageData;
	private double[][] testImageData;
	
	private MatrixNetwork matrixNetwork;
	
	private static final String imagesFilePath = "./src/main/resources/train-images.idx3-ubyte";
	private static final String labelsFilePath = "./src/main/resources/train-labels.idx1-ubyte";
	private static final String testImagesFilePath = "./src/main/resources/t10k-images.idx3-ubyte";
	private static final String testLabelsFilePath = "./src/main/resources/t10k-labels.idx1-ubyte";
	
	private static final int lHidden = 1;
	private static final int[] nHidden = new int[] { 800 };
	private static final int nOutput = 10;
	
	private static final double learningRate = 0.001;
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
		
		processImageData();
	}
	
	private void processImageData() {
		imageData = new double[images.size()][];
		testImageData = new double[testImages.size()][];
		
		for (int i = 0; i < imageData.length; i++) {
			imageData[i] = ArrayUtil.standardize( ArrayUtil.flattenArray(images.get(i)) );
		}
		
		for (int i = 0; i < testImageData.length; i++) {
			testImageData[i] = ArrayUtil.standardize( ArrayUtil.flattenArray(testImages.get(i)) );
		}
	}
	
	private void performEpoch(int batchSize) {
		matrixNetwork.performEpoch(imageData, labels, batchSize);
	}
	
	private double performTest(int startIndex, int endIndex) {
		if (startIndex < 0 || endIndex > testImages.size() || startIndex >= endIndex) { return -1; }
		
		int correctCount = 0;
		
		for (int i = startIndex; i < endIndex; i++) {
			int outputIndex = matrixNetwork.inputDataAndPropagate(testImageData[i]);
			
			System.out.println("Begin test: " + i);
			System.out.println("Network Gave: " + outputIndex + ", but supposed to be: " + testLabels[i]);
			System.out.println("");
			
			if (outputIndex == testLabels[i]) { correctCount++; }
		}
		
		return 100.0 * (double)correctCount / (double)(endIndex-startIndex);
	}
	
	public static void main(String[] args) {
		MnistAgent mnistAgent = new MnistAgent(imagesFilePath, labelsFilePath);
		
		mnistAgent.performEpoch(50);
		System.out.println("Got " + mnistAgent.performTest(0, 200) + "% Correct!");
	}
}
