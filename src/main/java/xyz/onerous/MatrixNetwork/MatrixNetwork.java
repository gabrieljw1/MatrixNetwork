package xyz.onerous.MatrixNetwork;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.Random;

import xyz.onerous.MatrixNetwork.component.ActivationType;
import xyz.onerous.MatrixNetwork.component.LossType;
import xyz.onerous.MatrixNetwork.component.datapackage.NetworkDataPackage;
import xyz.onerous.MatrixNetwork.component.datapackage.TestResultPackage;
import xyz.onerous.MatrixNetwork.component.datapackage.WeightBiasDeltaPackage;
import xyz.onerous.MatrixNetwork.exception.ArrayNotSquareException;
import xyz.onerous.MatrixNetwork.exception.InvalidInputLengthException;
import xyz.onerous.MatrixNetwork.util.ArrayUtil;
import xyz.onerous.MatrixNetwork.util.MatrixUtil;
import xyz.onerous.MatrixNetwork.visualizer.Visualizer;

/**
 * A neural network using Matrix operations instead of thrice-nested for loops like my first network did.
 * To be initialized by some outside 'Agent' (such as the MnistAgent, all network
 * parameters are selected upon initialization such as the Activation Type (Sigmoid, TanH, etc.) and the Loss
 * type (Cross Entropy, MSE, etc.).
 * 
 * Notes:
 * 	- Neuron Activations [0, 1] for all activations but TanH which is [-1, 1]
 * 
 * To-do:
 * 	- //TODO: Graphics?
 * 
 * @author wongg19
 * @version 0.2.0
 */
public class MatrixNetwork {
	protected int[] nPerLayer; //Number of neurons in each layer
	protected int numL; //Number of layers (including input, output) in network
	
	protected double learningRate; //Learning rate of the network
	
	protected boolean usingSoftmax;
	protected ActivationType activationType; //Activation Type (Sigmoid, TanH, etc.)
	protected LossType lossType; //Loss Type (Cross Entropy, MSE, etc.)
	
	protected double[][][] w; //Connection Weights
	protected double[][] b; //Neuron Biases
	protected double[][] z; //Neuron Weighted Inputs
	protected double[][] a; //Neuron Activations
	
	protected double[][] δ; //Network error per neuron used for gradient descent
	
	private final double BIAS_INIT_CONSTANT = 0.0; //What biases should be initialized to
	
	public MatrixNetwork(int nInput, int nOutput, int[] nHidden, int lHidden, double learningRate, boolean usingSoftmax, ActivationType activationType, LossType lossType) {
		//Global all parameters
		this.numL = lHidden + 2;
		this.learningRate = learningRate;
		this.usingSoftmax = usingSoftmax;
		this.activationType = activationType;
		this.lossType = lossType;
		this.nPerLayer = new int[numL];
		this.nPerLayer[0] = nInput;
		this.nPerLayer[numL - 1] = nOutput;

		//Calculate nPerLayer for all hidden layers from the given array
		for (int l = 1; l < numL - 1; l++) {
			this.nPerLayer[l] = nHidden[l-1];
		}
		
		initialize();
	}
	
	/**
	 * Create each array for every piece of data the network will need - weight, bias, error, activation, and
	 * weighted input storage. This is necessary because java arrays need to be initialized before getting or
	 * setting values.
	 */
	private void initialize() {		
		w = new double[numL][][]; //Every layer will have weights but the first. weights[0] will be left empty. We will not initialize weights[0].
		b = new double[numL][];
		δ = new double[numL][];
		a = new double[numL][];
		z = new double[numL][];

		for (int l = 0; l < numL; l++) {
			w[l] = new double[ nPerLayer[l] ][];
			b[l] = new double[ nPerLayer[l] ];
			δ[l] = new double[ nPerLayer[l] ];
			a[l] = new double[ nPerLayer[l] ];
			z[l] = new double[ nPerLayer[l] ];
			
			if (l != 0) {
				for (int n = 0; n < nPerLayer[l]; n++) {
					w[l][n] = new double[ nPerLayer[l-1] ];
				}
			} else {
				for (int n = 0; n < nPerLayer[l]; n++) {
					w[l][n] = new double[0];
				}
			}
		}

		Random random = new Random();

		switch (activationType) {
		case Sigmoid:
			for (int l = 1; l < numL; l++) {
				for (int n = 0; n < nPerLayer[l]; n++) {
					b[l][n] = this.BIAS_INIT_CONSTANT;
					for (int p_n = 0; p_n < nPerLayer[l-1]; p_n++) {
						w[l][n][p_n] = 2.0 * (random.nextDouble() - 0.5) * 4.0 * Math.sqrt(6.0 / ((double)nPerLayer[l-1]));
					}
				}
			}
			break;
		case ReLU: //Generate weights based on a normal distribution * sqrt(2/size of prev layer)
			for (int l = 1; l < numL; l++) {
				for (int n = 0; n < nPerLayer[l]; n++) {
					b[l][n] = this.BIAS_INIT_CONSTANT;
					for (int p_n = 0; p_n < nPerLayer[l-1]; p_n++) {
						w[l][n][p_n] = random.nextGaussian() * Math.sqrt(2.0/(double)nPerLayer[l-1]);
					}
				}
			}
			break;
		case TanH:
			for (int l = 1; l < numL; l++) {
				for (int n = 0; n < nPerLayer[l]; n++) {
					b[l][n] = this.BIAS_INIT_CONSTANT;
					for (int p_n = 0; p_n < nPerLayer[l-1]; p_n++) {
						w[l][n][p_n] = random.nextGaussian() * Math.sqrt(1.0/(double)nPerLayer[l-1]); //Xavier initialization
					}
				}
			}
			break;
		default:
			for (int l = 1; l < numL; l++) {
				for (int n = 0; n < nPerLayer[l]; n++) {
					b[l][n] = this.BIAS_INIT_CONSTANT;
					for (int p_n = 0; p_n < nPerLayer[l-1]; p_n++) {
						w[l][n][p_n] = random.nextGaussian() * Math.sqrt(1.0/(double)nPerLayer[l-1]); //Xavier initialization
					}
				}
			}
			break;
		}
	}
	
	/**
	 * Take an array of doubles and spread that across the input layer of the network
	 * 
	 * @param data Array of length number of input neurons in the network
	 * @throws InvalidInputLengthException 
	 */
	private void inputData(double[] data) throws InvalidInputLengthException {
		if (data.length != nPerLayer[0]) {
			throw new InvalidInputLengthException();
		}
		
		z[0] = data;
	}
	
	/**
	 * Take an array of doubles and spread that across the input layer of the network with the `inputData`
	 * method, then propagate and return the output of the network.
	 * 
	 * @param data Array of length number of input neurons in the network
	 * @return Network response (index of 'brightest' output neuron)
	 */
	public int inputDataAndPropagate(double[] data) {
		try {
			inputData(data);
		} catch (InvalidInputLengthException e) {
			e.printStackTrace();
		}
		
		propagate();
		
		return getDominantOutputIndex();
	}
	
	/**
	 * @param neuronZ Neuron weighted input
	 * @return the activation function performed on some input value
	 */
	public double activate(double neuronZ) {
		double neuronA;
		
		switch (activationType) {
		case Sigmoid:
			neuronA = 1.0 / (1.0 + Math.exp(-neuronZ));
			break;
		case ReLU:
			if (neuronZ >= 0) {
				neuronA = neuronZ;
			} else {
				neuronA = 0;
			}
			break;
		case Linear:
			neuronA = neuronZ;
			break;
		case TanH:
			neuronA = Math.tanh(neuronZ);
			break;
		default:
			neuronA = 1.0 / (1.0 + Math.exp(-neuronZ));
			break;
		}
		
		return neuronA;
	}
	
	/**
	 * @param layerZ A layer (array) of neuron weighted inputs
	 * @return the activation function performed on an entire layer (array) of neuron weighted inputs
	 */
	public double[] activateLayer(double[] layerZ) {
		double[] layerA = new double[layerZ.length];
	
		switch (activationType) {
		case Sigmoid:
			for (int i = 0; i < layerA.length; i++) {
				layerA[i] = 1.0 / (1.0 + Math.exp(-layerZ[i]));
			}
			break;
		case ReLU:
			for (int i = 0; i < layerA.length; i++) {
				if (layerZ[i] >= 0) {
					layerA[i] = layerZ[i];
				} else {
					layerA[i] = 0;
				}
			}
			break;
		case Linear:
			for (int i = 0; i < layerA.length; i++) {
				layerA[i] = layerZ[i];
			}
			break;
		case TanH:
			for (int i = 0; i < layerA.length; i++) {
				layerA[i] = Math.tanh(layerZ[i]);
			}
			break;
		default:
			for (int i = 0; i < layerA.length; i++) {
				layerA[i] = 1.0 / (1.0 + Math.exp(-layerZ[i]));
			}
			break;
		}
		
		return layerA;
	}
	
	/**
	 * @param neuronZ Neuron weighted input
	 * @return the (derivative of) activation function performed on some input value
	 */
	public double activatePrime(double neuronZ) {
		double neuronA;
		
		switch (activationType) {
		case Sigmoid:
			neuronA = activate(neuronZ) * (1 - activate(neuronZ));
			break;
		case ReLU:
			if (neuronZ >= 0) {
				neuronA = 1;
			} else {
				neuronA = 0;
			}
			break;
		case Linear:
			neuronA = 1;
			break;
		case TanH:
			neuronA = Math.pow(Math.cosh(neuronZ), -2.0); //sech^2(x)
			break;
		default:
			neuronA = activate(neuronZ) * (1 - activate(neuronZ));
			break;
		}
		
		return neuronA;
	}
	
	/**
	 * @param layerZ A layer (array) of neuron weighted inputs
	 * @return the (derivative of) activation function performed on an entire layer (array) of neuron weighted inputs
	 */
	public double[] activateLayerPrime(double[] layerZ) {
		double[] layerA = new double[layerZ.length];
		
		switch (activationType) {
		case Sigmoid:
			for (int i = 0; i < layerA.length; i++) {
				layerA[i] = activate(layerZ[i]) * (1 - activate(layerZ[i]));
			}
			break;
		case ReLU:
			for (int i = 0; i < layerA.length; i++) {
				if (layerZ[i] >= 0) {
					layerA[i] = 1;
				} else {
					layerA[i] = 0;
				}
			}
			break;
		case Linear:
			for (int i = 0; i < layerA.length; i++) {
				layerA[i] = 1;
			}
			break;
		case TanH:
			for (int i = 0; i < layerA.length; i++) {
				layerA[i] = Math.pow(Math.cosh(layerZ[i]), -2.0); //sech^2(x)
			}
			break;
		default:
			for (int i = 0; i < layerA.length; i++) {
				layerA[i] = activate(layerZ[i]) * (1 - activate(layerZ[i]));
			}
			break;
		}
		
		return layerA;
	}
	
	public double[] softmaxLayer(double[] layerZ) {
		double layerSum = 0.0;
		double[] softmaxValues = new double[layerZ.length];
		
		for (double neuronZ : layerZ) {
			layerSum += Math.exp(neuronZ);
		}
		
		for (int i = 0; i < layerZ.length; i++) {
			softmaxValues[i] = Math.exp(layerZ[i]) / layerSum;
		}

		return softmaxValues;
	}
	
	public double[] softmaxLayerPrime(double[] layerZ) {
		double[] softmaxPrimeValues = new double[layerZ.length];
		double[] softmaxValues = softmaxLayer(layerZ);
		
		for (int i = 0; i < layerZ.length; i++) {
			softmaxPrimeValues[i] = softmaxValues[i] * (1 - softmaxValues[i]);
		}
		
		return softmaxPrimeValues;
	}
	
	/**
	 * Find the 'brightest' output neuron in the network. This is what amounts to the network's output.
	 * 
	 * @return the index of the network output neuron
	 */
	public int getDominantOutputIndex() {
		double maxValue = a[numL - 1][0];
		int maxValueAtIndex = 0;
		
		for (int n = 1; n < nPerLayer[numL - 1]; n++) {
			if (a[numL - 1][n] > maxValue) {
				maxValueAtIndex = n;
				maxValue = a[numL - 1][n];
			}
		}
		
		return maxValueAtIndex;
	}
	
	public int getNumL() {
		return numL;
	}
	
	public int[] getNPerLayer() {
		return nPerLayer;
	}
	
	public double getOutputNeuronValue(int index) {
		return a[numL-1][index];
	}
	
	/**
	 * Feed forward the input given by the input data all the way to the output layer through the weights and
	 * hidden layer neurons (if any).
	 */
	public void propagate() {
		//z will already be inside of the first layer array index
		a[0] = activateLayer(z[0]);
		
		for (int l = 1; l < numL - 1; l++) {
			z[l] = MatrixUtil.add(MatrixUtil.multiply(w[l], a[l-1]), b[l]);
			a[l] = activateLayer(z[l]);
		}
		
		z[numL-1] = MatrixUtil.add(MatrixUtil.multiply(w[numL-1], a[numL-2]), b[numL-1]);
		
		if (usingSoftmax) {
			a[numL-1] = softmaxLayer(z[numL-1]);
		} else {
			a[numL-1] = activateLayer(z[numL-1]);
		}
	}
	
	/**
	 * Using an expected output versus the actual network output, calculate the error in the output layer
	 * (the error is calculated using the desired loss function specified during network initialization).
	 * Then, 'backpropagate' that error through the network to approximate the individual error for every
	 * single network neuron. Finding the error for each neuron will allow the gradient descent function to find the weight and bias
	 * changes necessary to make the network perform better.
	 * 
	 * For the output layer, error is defined as the hadamard product of the derivative of the loss function
	 * and the activation of the previous layer's neurons.
	 * 
	 * For the non-output layers (also not the input layer, it does not have error), the error is given by
	 * multiplying the matrix of the weights (between the current layer and the layer closer to the output)
	 * by the matrix of the next layer's error and then taking the hadamard of that product and the current
	 * layer activations. This algorithm starts at the second to last layer and then backwards, therefore
	 * 'backpropagating'. 
	 * 
	 * @param expectedIndex The expected output of the network
	 */
	protected void backPropagate(int expectedIndex) {
		//CALCULATE OUTPUT LAYER FIRST
		double[] expectedOutput = new double[nPerLayer[numL - 1]];
		
		expectedOutput[expectedIndex] = 1.0;
		
		if (activationType == ActivationType.TanH) {
			for (int i = 0; i < expectedOutput.length; i++) {
				if (i != expectedIndex) {
					expectedOutput[i] = -1.0;
				}
			}
		}
	
		switch (lossType) {
		case MeanSquaredError: //  (actual - predicted)^2 / n    so the deriv is    (2/n)(actual-predicted)
			for (int i = 0; i < nPerLayer[numL - 1]; i++) {
				δ[numL-1][i] = 0.5 * (a[numL - 1][i] - expectedOutput[i]);
			}
			break;
		case MeanAbsoluteError:
			for (int i = 0; i < nPerLayer[numL - 1]; i++) {
				if (a[numL - 1][i] > expectedOutput[i]) {
					δ[numL-1][i] = +1.0;
				} else if (a[numL - 1][i] < expectedOutput[i]) {
					δ[numL-1][i] = -1.0;
				} else {
					δ[numL-1][i] = +0.0;
				}
			}
			break;
		case CrossEntropy:
			for (int i = 0; i < nPerLayer[numL - 1]; i++) {
				δ[numL-1][i] = (-expectedOutput[i]/a[numL - 1][i]) + (1.0 - expectedOutput[i])/(1.0 - a[numL - 1][i]);
			}
			break;
		case BinaryCrossEntropy:
			
			break;
		default: 
			System.out.println("Default switch thrown at δ calculation"); 
			break;
		}
		
		
		//Up until now, only part of δ has been stored inside.
		//We still have to hadamard the delCdelA with the derivative of the activation function for z.
		if (usingSoftmax) {
			δ[numL-1] = MatrixUtil.hadamard(δ[numL-1], softmaxLayerPrime(z[numL - 1]));
		} else {
			δ[numL-1] = MatrixUtil.hadamard(δ[numL-1], activateLayerPrime(z[numL - 1]));
		}
		
		
		//
		//Calculate rest of network
		//
		for (int l = numL - 2; l > 0; l--) {
			δ[l] = MatrixUtil.hadamard(MatrixUtil.multiplyWithFirstTranspose(w[l+1], δ[l+1]), activateLayerPrime(z[l]));
		}
	}
	
	/**
	 * Calculate the bias and weight deltas for each neuron using the previously calculated errors (see the
	 * `backpropagate()` function). 
	 * 
	 * Weight deltas are given by multiplying the layer error by the activations of the previous layer
	 * transposed then multiplying scalarly with the negaative learning rate. Bias deltas are given by
	 * multiplying the layer error scalarly by the negative learning rate.
	 * 
	 * Deltas are not directly applied in case the network is being used in a batch context.
	 * 
	 * @param expectedIndex The expected network output
	 * @return the bias and weight deltas
	 */
	public WeightBiasDeltaPackage gradientDescent(int expectedIndex) { //Batch size needed to limit weight/bias changing over an entire batch
		backPropagate(expectedIndex);
		
		double[][][] deltaW = w.clone(); //Cloned so the dimensions are the same without having to re-init.
		double[][] deltaB = b.clone();
		
		for (int l = numL - 1; l > 0; l--) {
			deltaW[l] = MatrixUtil.scalarMultiply(MatrixUtil.multiplyWithSecondTranspose(δ[l], a[l-1]), -learningRate);
			deltaB[l] = MatrixUtil.scalarMultiply(δ[l], -learningRate);
		}
		
		return new WeightBiasDeltaPackage(deltaW, deltaB);
	}
	
	/**
	 * Apply a given weight-bias delta package to the network.
	 * 
	 * @param deltaPackage The weight-bias delta package to be applied to the network
	 */
	public void applyDeltaPackage(WeightBiasDeltaPackage deltaPackage) {
		for (int l = numL - 1; l > 0; l--) {
			w[l] = MatrixUtil.add(w[l], deltaPackage.deltaW[l]);
			b[l] = MatrixUtil.add(b[l], deltaPackage.deltaB[l]);
		}
	}
	
	/**
	 * @param expectedIndex The expected output of the network
	 * @return the total output layer error specified by the selected loss function.
	 */
	public double getOutputError(int expectedIndex) {
		double[] expectedOutput = new double[nPerLayer[numL - 1]];
		
		expectedOutput[expectedIndex] = 1.0;
		
		switch (lossType) {
		case MeanSquaredError: //  (actual - predicted)^2 / n
			double sumSquaredError = 0.0;
			for (int i = 0; i < nPerLayer[numL - 1]; i++) {
				sumSquaredError += Math.pow(a[numL - 1][i] - expectedOutput[i], 2.0);
			}
			return sumSquaredError / (double)(nPerLayer[numL - 1]);
		case MeanAbsoluteError:
			double sumAbsoluteError = 0.0;
			for (int i = 0; i < nPerLayer[numL - 1]; i++) {
				sumAbsoluteError += Math.abs(a[numL - 1][i] - expectedOutput[i]);
			}
			return sumAbsoluteError / (double)(nPerLayer[numL - 1]);
		case CrossEntropy:
			double totalCrossEntropyError = 0.0;
			for (int i = 0; i < nPerLayer[numL -1]; i++) {
				totalCrossEntropyError += expectedOutput[i]*Math.log(a[numL - 1][i]);
			}
			return -totalCrossEntropyError;
		case BinaryCrossEntropy:
			return 0.0;
		default: 
			System.out.println("Default switch thrown at network error calculation"); 
			return 0.0;
		}
	}
	
	/**
	 * Perform one training iteration with three steps:
	 * 	1. Input Data
	 * 	2. Propagate
	 * 	3. Backpropagation / Gradient Descent
	 * 
	 * @param trainData Data to be inputted
	 * @param expectedOutput The expected result of the network
	 * @return The weight-bias deltas generated by the gradient descent process
	 */
	public WeightBiasDeltaPackage performTrainAndGetDelta(double[] trainData, int expectedOutput) {
		inputDataAndPropagate(trainData);
		return gradientDescent(expectedOutput);
	}
	
	/**
	 * Perform one training batch consisting of many single training iterations whose weight-bias deltas are
	 * summed and applied in one go. This is different than running many single training iterations by
	 * themselves because deltas are not applied after every iteration.
	 * 
	 * Batch training is sometimes more efficient than 'online' training (applying deltas after each
	 * iteration) operation-wise.
	 * 
	 * @param batchData Data to be inputted
	 * @param expectedOutputs The expected results of the network per the batch data
	 * @return The combined weight-bias deltas generated by the training iterations
	 */
	public WeightBiasDeltaPackage performBatchAndGetDelta(double[][] batchData, int[] expectedOutputs) {
		int batchSize = batchData.length;
		
		WeightBiasDeltaPackage[] deltaPackages = new WeightBiasDeltaPackage[batchSize];
		
		for (int i = 0; i < batchSize; i++) {
			deltaPackages[i] = performTrainAndGetDelta(batchData[i], expectedOutputs[i]);
		}
		
		return WeightBiasDeltaPackage.concatPackages(deltaPackages);
	}
	
	/**
	 * Perform an entire epoch of training. An epoch is when the network is trained through the entire data
	 * set once. If batch training is not desired, a batchSize of one (1) can be specified. If batch training
	 * is desired, the epoch trainer will split the training data into batches. Should the training data not 
	 * divide evenly by the batch size, the last batch will have less training data than the previous ones.
	 * 
	 * @param trainingData Data to be inputted
	 * @param expectedOutputs The expected results of the network per the training data
	 * @param batchSize Size of the batches to be performed. 1 if batch training is not desired.
	 */
	public void performEpoch(double[][] trainingData, int[] expectedOutputs, int batchSize) {
		//Translate numbers into readable variables for code readability
		int numDataPoints = trainingData.length - 50000;
		int numBatches = (int)Math.ceil(numDataPoints / batchSize);

		
		//Find the individual batch sizes (accounting for if num batches does not divide evenly into training data)
		int[] batchSizes = new int[numBatches];
		
		if (numDataPoints % numBatches == 0) {
			for (int i = 0; i < numBatches; i++) {
				batchSizes[i] = batchSize;
			}
		} else {
			for (int i = 0; i < numBatches - 1; i++) {
				batchSizes[i] = batchSize;
			}
			
			batchSizes[numBatches - 1] = numDataPoints - ((numBatches - 1) * batchSize);
		}
		
		
		//Find what chunk of data each batch will have
		double[][][] batchDataSets = new double[numBatches][][];
		int[][] batchExpectedOutputs = new int[numBatches][];
		int nextBatchStartIndex = 0;
		
		for (int b = 0; b < numBatches; b++) {
			batchDataSets[b] = ArrayUtil.clipArray(trainingData, nextBatchStartIndex, nextBatchStartIndex+batchSizes[b]);
			batchExpectedOutputs[b] = ArrayUtil.clipArray(expectedOutputs, nextBatchStartIndex, nextBatchStartIndex+batchSizes[b]);
			nextBatchStartIndex += batchSizes[b];
		}
		
		
		//Run each batch and apply deltas
		for (int b = 0; b < numBatches; b++) {
			System.out.println("Starting batch " + b);
			applyDeltaPackage(performBatchAndGetDelta(batchDataSets[b], batchExpectedOutputs[b]));
		}
	}
	
	public TestResultPackage performTest(double[][] testData, int[] expectedOutputs) {
		if (testData.length != expectedOutputs.length) { return (TestResultPackage) null; }
		
		int numTests = testData.length;
		int correctCount = 0;
		
		double[]  outputNeuronValues = new double[numTests];
		int[]     outputNeuronIndeces = new int[numTests];
		boolean[] ifCorrect = new boolean[numTests];
		
		for (int t = 0; t < numTests; t++) {
			int outputIndex = inputDataAndPropagate(testData[t]);
			if (outputIndex == expectedOutputs[t]) { 
				correctCount++; 
				ifCorrect[t] = true;
			} else {
				ifCorrect[t] = false;
			}
			
			outputNeuronValues[t] = getOutputNeuronValue(outputIndex); 
			outputNeuronIndeces[t] = outputIndex;
		}
		
		double percentageCorrect = (double)correctCount / (double)numTests;
		
		return new TestResultPackage(numTests, percentageCorrect, outputNeuronValues, outputNeuronIndeces, ifCorrect);
	}
	
	public NetworkDataPackage generateNetworkDataPackage() {
		System.out.println("Network output: " + getDominantOutputIndex());
		
		return new NetworkDataPackage(getDominantOutputIndex(), a, z, b, w);
	}
}
