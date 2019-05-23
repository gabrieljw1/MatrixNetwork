package xyz.onerous.MatrixNetwork.demerit;


import java.util.Arrays;

import xyz.onerous.MatrixNetwork.MatrixNetwork;
import xyz.onerous.MatrixNetwork.component.ActivationType;
import xyz.onerous.MatrixNetwork.component.LossType;
import xyz.onerous.MatrixNetwork.component.datapackage.TestResultPackage;
import xyz.onerous.MatrixNetwork.component.util.ArrayUtil;
import xyz.onerous.MatrixNetwork.component.util.FileUtil;
import xyz.onerous.MatrixNetwork.component.util.MatrixUtil;
import xyz.onerous.MatrixNetwork.weightvisualizer.WeightVisualizer;

public class DemeritAgent {
	protected double[][] studentData; //gender, sport, GPAW, teacher connection in that order
	protected int[] studentContent;
	
	protected double[][] testData; 
	protected int[] testContent;
	
	private static String PATH_DATA_FILE = "./src/main/resources/StudentData";
	private static String PATH_TEST_FILE = "./src/main/resources/TestData";
	
	public MatrixNetwork matrixNetwork;
	
	protected final int lHidden = 2;
	protected final int[] nHidden = new int[] { 49, 49 };
	
	protected final double learningRate = 0.001;
	protected final boolean usingSoftmax = true;
	protected final ActivationType activationType = ActivationType.ReLU;
	protected final LossType lossType = LossType.CrossEntropy;
	
	public DemeritAgent() {
		processStudentData();
		processTestData();

		if (studentData.length == 0 || studentContent.length == 0 || lHidden != nHidden.length) { return; }
	}
	
	private void processStudentData() {
		double[][] allData = FileUtil.loadStudentData(PATH_DATA_FILE); //Student is first 4, student content is last column
		
		studentData = ArrayUtil.splitArray(allData, 0, 4);
		studentContent = ArrayUtil.doubleToInt(MatrixUtil.scalarAdd(ArrayUtil.transposeColumnArray(ArrayUtil.splitArray(allData, 4, 5)), 0.0));
	}
	
	private void processRandomData() {
		double[][] allData = ArrayUtil.generateRandomArray(53, 5, 6);
		
		studentData = ArrayUtil.splitArray(allData, 0, 4);
		studentContent = ArrayUtil.doubleToInt(MatrixUtil.scalarAdd(ArrayUtil.transposeColumnArray(ArrayUtil.splitArray(allData, 4, 5)), 0.0));
	}
	
	private void processTestData() {
		double[][] allData = FileUtil.loadStudentData(PATH_TEST_FILE); //Student is first 4, student content is last column
		
		testData = ArrayUtil.splitArray(allData, 0, 4);
		testContent = ArrayUtil.doubleToInt(MatrixUtil.scalarAdd(ArrayUtil.transposeColumnArray(ArrayUtil.splitArray(allData, 4, 5)), 0.0));
	}
	
	public void generateNetwork() {
		int nInput = 4;
		int nOutput = 6;
		
		this.matrixNetwork = new MatrixNetwork(nInput, nOutput, nHidden, lHidden, learningRate, usingSoftmax, activationType, lossType);
	}

	
	public void performEpoch(int batchSize) {		
		matrixNetwork.performEpoch(studentData, studentContent, batchSize);
	}
	
	public TestResultPackage performTestWithStudentData(int startIndex, int endIndex) {
		if (startIndex < 0 || endIndex > studentData.length || startIndex >= endIndex) { return (TestResultPackage) null; }
		
		TestResultPackage testResults = matrixNetwork.performTest(ArrayUtil.clipArray(studentData, startIndex, endIndex), ArrayUtil.clipArray(studentContent, startIndex, endIndex));
		
		return testResults;
	}
	
	public TestResultPackage performTest(int startIndex, int endIndex) {
		if (startIndex < 0 || endIndex > testData.length || startIndex >= endIndex) { return (TestResultPackage) null; }
		
		TestResultPackage testResults = matrixNetwork.performTest(ArrayUtil.clipArray(testData, startIndex, endIndex), ArrayUtil.clipArray(testContent, startIndex, endIndex));
		
		return testResults;
	}
	
	public void saveNetwork(String identifier) {
		FileUtil.writeNetworkWeightBiasPackage(matrixNetwork.generateNetworkWeightBiasPackage(), "network" + identifier);
	}
	
	public void loadNetwork(String identifier) {
		matrixNetwork.applyNetworkWeightBiasPackage(FileUtil.loadNetworkWeightBiasPackage("network" + identifier));
	}
	
	public static void main(String[] args) {
		DemeritAgent demeritAgent = new DemeritAgent();
		
		demeritAgent.generateNetwork();
		
		demeritAgent.loadNetwork("ContentPredictor");

		/*
		for (int i = 0; i < 6000; i++) {
			demeritAgent.performEpoch(1);
		}
		
		demeritAgent.saveNetwork("ContentPredictor");
		*/
		
		
		System.out.println(demeritAgent.performTestWithStudentData(0, 53));
		System.out.println("\n\n\n");
		System.out.println(demeritAgent.performTest(0, 5));
		
		WeightVisualizer weightVisualizer = new WeightVisualizer(demeritAgent.matrixNetwork.generateNetworkWeightBiasPackage().getConnectionWeights());
		
		for (int i = 0; i < 4; i++) {
			weightVisualizer.displayWeightsFromNeuron(0, i);
		}
	}
}
