package xyz.onerous.MatrixNetwork.MNIST;

import java.util.List;

import xyz.onerous.MatrixNetwork.MatrixNetwork;
import xyz.onerous.MatrixNetwork.MNIST.MnistReader;
import xyz.onerous.MatrixNetwork.component.ActivationType;
import xyz.onerous.MatrixNetwork.component.LossType;
import xyz.onerous.MatrixNetwork.component.datapackage.TestResultPackage;
import xyz.onerous.MatrixNetwork.util.ArrayUtil;

public class MnistAgent {
	private List<int[][]> images;
	protected int[] labels;
	private List<int[][]> testImages;
	protected int[] testLabels;
	
	protected double[][] imageData;
	protected double[][] testImageData;
	
	protected MatrixNetwork matrixNetwork;
	
	private static final String IMAGES_FILE_PATH = "./src/main/resources/train-images.idx3-ubyte";
	private static final String LABELS_FILE_PATH = "./src/main/resources/train-labels.idx1-ubyte";
	private static final String TEST_IMAGES_FILE_PATH = "./src/main/resources/t10k-images.idx3-ubyte";
	private static final String TEST_LABELS_FILE_PATH = "./src/main/resources/t10k-labels.idx1-ubyte";
	
	private final int lHidden = 2;
	private final int[] nHidden = new int[] { 500, 500 };
	
	private final double learningRate = 0.001;
	private final boolean usingSoftmax = true;
	private final ActivationType activationType = ActivationType.Sigmoid;
	private final LossType lossType = LossType.CrossEntropy;
	
	public MnistAgent() {
		this.images = MnistReader.getImages(IMAGES_FILE_PATH);
		this.labels = MnistReader.getLabels(LABELS_FILE_PATH);
		this.testImages = MnistReader.getImages(TEST_IMAGES_FILE_PATH);
		this.testLabels = MnistReader.getLabels(TEST_LABELS_FILE_PATH);
		
		processImageData();
		
		if (images.size() == 0 || labels.length == 0 || lHidden != nHidden.length) { return; }
	}
	
	public void generateNetwork() {
		int nInput = imageData[0].length;
		int nOutput = 10;
		
		this.matrixNetwork = new MatrixNetwork(nInput, nOutput, nHidden, lHidden, learningRate, usingSoftmax, activationType, lossType);
	}
	
	protected void processImageData() {
		imageData = new double[images.size()][];
		testImageData = new double[testImages.size()][];
		
		for (int i = 0; i < imageData.length; i++) {
			imageData[i] = ArrayUtil.standardize( ArrayUtil.flattenArray(images.get(i)) );
		}
		
		for (int i = 0; i < testImageData.length; i++) {
			testImageData[i] = ArrayUtil.standardize( ArrayUtil.flattenArray(testImages.get(i)) );
		}
	}
	
	public void performEpoch(int batchSize) {
		matrixNetwork.performEpoch(imageData, labels, batchSize);
	}
	
	public TestResultPackage performTest(int startIndex, int endIndex) {
		if (startIndex < 0 || endIndex > testImages.size() || startIndex >= endIndex) { return (TestResultPackage) null; }
		
		TestResultPackage testResults = matrixNetwork.performTest(ArrayUtil.clipArray(testImageData, startIndex, endIndex), ArrayUtil.clipArray(testLabels, startIndex, endIndex));
		
		return testResults;
	}
	
	public static void main(String[] args) {
		MnistAgent mnistAgent = new MnistAgent();
		
		mnistAgent.generateNetwork();
		mnistAgent.performEpoch(10);
		System.out.println(mnistAgent.performTest(0, 500));
	}
}
