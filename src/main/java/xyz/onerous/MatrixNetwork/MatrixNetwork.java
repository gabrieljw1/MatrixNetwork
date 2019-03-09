package xyz.onerous.MatrixNetwork;

import java.util.Arrays;
import java.util.Random;

import xyz.onerous.MatrixNetwork.ActivationType;
import xyz.onerous.MatrixNetwork.LossType;
import xyz.onerous.MatrixNetwork.util.MatrixUtil;

/**
 * All neuron activations are locked between activation of 0 and 1
 * @author wongg19
 *
 */
public class MatrixNetwork {
	private int[] nLayer;
	private int lTotal;
	
	private double learningRate;
	
	private ActivationType activationType;
	private LossType lossType;
	
	private double[][][] w; //Connection Weights
	private double[][] b; //Neuron Biases
	private double[][] z; //Neuron Weighted Inputs
	private double[][] a; //Neuron Activations
	
	private double[][] δ; // δ
	
	private final double BIAS_INIT_CONSTANT = 0.0;
	
	
	public MatrixNetwork(int nInput, int nOutput, int[] nHidden, int lHidden, double learningRate, ActivationType activationType, LossType lossType) {
		this.lTotal = lHidden + 2;

		this.learningRate = learningRate;

		this.activationType = activationType;
		this.lossType = lossType;

		this.nLayer = new int[lTotal];

		this.nLayer[0] = nInput;
		this.nLayer[lTotal - 1] = nOutput;

		for (int l = 1; l < lTotal - 1; l++) {
			this.nLayer[l] = nHidden[l-1];
		}
	}
	
	public void initialize() {
		int nOutput = nLayer[nLayer.length - 1];
		
		//
		//Weights
		//
		w = new double[lTotal][][]; //Every layer will have weights but the first. weights[0] will be left empty. We will not initialize weights[0].
		w[lTotal-1] = new double[nOutput][nLayer[lTotal-1-1]]; //lTotal-1-1 = weights in last hidden layer

		for (int l = 1; l < lTotal - 1; l++) {
			w[l] = new double[ nLayer[l] ][];

			for (int n = 0; n < nLayer[l]; n++) {
				w[l][n] = new double[ nLayer[l-1] ];
			}
		}

		Random random = new Random();

		switch (activationType) {
		case Sigmoid:
			for (int l = 1; l < lTotal; l++) {
				for (int n = 0; n < nLayer[l]; n++) {
					for (int p_n = 0; p_n < nLayer[l-1]; p_n++) {
						w[l][n][p_n] = 2.0 * (random.nextDouble() - 0.5) * 4.0 * Math.sqrt(6.0 / ((double)nLayer[l-1]));
					}
				}
			}
			break;
		case ReLU: //Generate weights based on a normal distribution * sqrt(2/size of prev layer)
			for (int l = 1; l < lTotal; l++) {
				for (int n = 0; n < nLayer[l]; n++) {
					for (int p_n = 0; p_n < nLayer[l-1]; p_n++) {
						w[l][n][p_n] = random.nextGaussian() * Math.sqrt(2.0/(double)nLayer[l-1]);
					}
				}
			}
			break;
		//case Linear:
			//	break;
		case TanH:
			for (int l = 1; l < lTotal; l++) {
				for (int n = 0; n < nLayer[l]; n++) {
					for (int p_n = 0; p_n < nLayer[l-1]; p_n++) {
						w[l][n][p_n] = random.nextGaussian() * Math.sqrt(1.0/(double)nLayer[l-1]); //Xavier initialization
					}
				}
			}
			break;
		default:
			for (int l = 1; l < lTotal; l++) {
				for (int n = 0; n < nLayer[l]; n++) {
					for (int p_n = 0; p_n < nLayer[l-1]; p_n++) {
						w[l][n][p_n] = random.nextGaussian() * Math.sqrt(1.0/(double)nLayer[l-1]); //Xavier initialization
					}
				}
			}
			break;
		}
		
		
		
		//
		//Biases and error
		//
		b = new double[lTotal][];
		δ = new double[lTotal][];
		
		for (int l = 0; l < lTotal; l++) {
			b[l] = new double[ nLayer[l] ];
			δ[l] = new double[ nLayer[l] ];
		}

		switch (activationType) {
		case ReLU:
			for (int l = 0; l < lTotal; l++) {
				for (int n = 0; n < nLayer[l]; n++) {
					b[l][n] = this.BIAS_INIT_CONSTANT;
				}
			}
			break;
		case Sigmoid:
			for (int l = 0; l < lTotal; l++) {
				for (int n = 0; n < nLayer[l]; n++) {
					b[l][n] = this.BIAS_INIT_CONSTANT;
				}
			}
			break;
		default:
			for (int l = 0; l < lTotal; l++) {
				for (int n = 0; n < nLayer[l]; n++) {
					b[l][n] = this.BIAS_INIT_CONSTANT;
				}
			}
			break;
		}
		
		
		
		//
		//Misc
		//
		a = new double[lTotal][];
		z = new double[lTotal][];

		for (int l = 0; l < lTotal; l++) {
			a[l] = new double[nLayer[l]];
			z[l] = new double[nLayer[l]];
		}
	}

	public int inputData(double[] data) {
		if (data.length != nLayer[0]) {
			System.out.println("Data length mismatch error");
		}
		
		z[0] = data;
		
		propagate();
		
		return getDominantOutputIndex();
	}
	
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
	public double[] activateLayer(double[] layerZ) {
		double[] layerA = new double[layerZ.length];
		
		for (int i = 0; i < layerA.length; i++) {
			layerA[i] = activate(layerZ[i]);
		}
		
		return layerA;
	}
	
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
	public double[] activateLayerPrime(double[] layerZ) {
		double[] layerA = new double[layerZ.length];
		
		for (int i = 0; i < layerA.length; i++) {
			layerA[i] = activatePrime(layerZ[i]);
		}
		
		return layerA;
	}
	
	public int getDominantOutputIndex() {
		double maxValue = a[lTotal - 1][0];
		int maxValueAtIndex = 0;
		
		for (int n = 1; n < nLayer[lTotal - 1]; n++) {
			if (a[lTotal - 1][n] > maxValue) {
				maxValueAtIndex = n;
				maxValue = a[lTotal - 1][n];
			}
		}
		
		return maxValueAtIndex;
	}
	
	/**
	 * Also known as the network feed forward.
	 * 
	 * z(l)=w(l)a(l−1)+b(l)
	 */
	public void propagate() {
		//z will already be inside of the first layer array index
		a[0] = activateLayer(z[0]);
		
		for (int l = 1; l < lTotal; l++) {
			z[l] = MatrixUtil.add(MatrixUtil.multiply(w[l], a[l-1]), b[l]);
			a[l] = activateLayer(z[l]);
		}
	}
	
	private void backPropagate(int expectedIndex) {
		//CALCULATE OUTPUT LAYER FIRST
		double[] expectedOutput = new double[nLayer[lTotal - 1]];
		
		for (int i = 0; i < expectedOutput.length; i++) {
			if (i == expectedIndex) {
				expectedOutput[i] = 1.0;
			} else {
				expectedOutput[i] = 0.0;
			}
		}
		
		switch (lossType) {
		case MeanSquaredError: //  (actual - predicted)^2 / n    so the deriv is    (2/n)(actual-predicted)
			for (int i = 0; i < nLayer[lTotal - 1]; i++) {
				δ[lTotal-1][i] = 0.5 * (a[lTotal - 1][i] - expectedOutput[i]);
			}
			break;
		case MeanAbsoluteError:
			for (int i = 0; i < nLayer[lTotal - 1]; i++) {
				if (a[lTotal - 1][i] > expectedOutput[i]) {
					δ[lTotal-1][i] = +1.0;
				} else if (a[lTotal - 1][i] < expectedOutput[i]) {
					δ[lTotal-1][i] = -1.0;
				} else {
					δ[lTotal-1][i] = +0.0;
				}
			}
			break;
		case CrossEntropy:
			for (int i = 0; i < nLayer[lTotal - 1]; i++) {
				//δ[lTotal-1][i] = (a[lTotal - 1][i] - expectedOutput[i]) / ((a[lTotal - 1][i] * (1 - a[lTotal - 1][i])));
				δ[lTotal-1][i] = (-expectedOutput[i]/a[lTotal - 1][i]) + (1.0 - expectedOutput[i])/(1.0 - a[lTotal - 1][i]);
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
		δ[lTotal-1] = MatrixUtil.hadamard(δ[lTotal-1], activateLayerPrime(z[lTotal - 1]));
		
		
		//
		//Calculate rest of network
		//
		for (int l = lTotal - 2; l > 0; l--) {
			δ[l] = MatrixUtil.hadamard(MatrixUtil.multiplyWithFirstTranspose(w[l+1], δ[l+1]), activateLayerPrime(z[l]));
		}
	}
	
	public void gradientDescent(int expectedIndex, int batchSize) { //Batch size needed to limit weight/bias changing over an entire batch
		backPropagate(expectedIndex);
		
		double[][][] deltaW = w.clone(); //Cloned so the dimensions are the same without having to re-init.
		double[][] deltaB = b.clone();
		
		for (int l = lTotal - 1; l > 0; l--) {
			deltaW[l] = MatrixUtil.scalarMultiply(MatrixUtil.multiplyWithSecondTranspose(δ[l], a[l-1]), -learningRate / (double)batchSize);
			deltaB[l] = MatrixUtil.scalarMultiply(δ[l], -learningRate / (double)batchSize);
			
			w[l] = MatrixUtil.add(w[l], deltaW[l]);
			b[l] = MatrixUtil.add(b[l], deltaB[l]);
		}
	}
	
	public double getOutputError(int expectedIndex) {
		double[] expectedOutput = new double[nLayer[lTotal - 1]];
		
		for (int i = 0; i < expectedOutput.length; i++) {
			if (i == expectedIndex) {
				expectedOutput[i] = 1.0;
			} else {
				expectedOutput[i] = 0.0;
			}
		}
		
		switch (lossType) {
		case MeanSquaredError: //  (actual - predicted)^2 / n
			double sumSquaredError = 0.0;
			for (int i = 0; i < nLayer[lTotal - 1]; i++) {
				sumSquaredError += Math.pow(a[lTotal - 1][i] - expectedOutput[i], 2.0);
			}
			return sumSquaredError / (double)(nLayer[lTotal - 1]);
		case MeanAbsoluteError:
			double sumAbsoluteError = 0.0;
			for (int i = 0; i < nLayer[lTotal - 1]; i++) {
				sumAbsoluteError += Math.abs(a[lTotal - 1][i] - expectedOutput[i]);
			}
			return sumAbsoluteError / (double)(nLayer[lTotal - 1]);
		case CrossEntropy:
			double totalCrossEntropyError = 0.0;
			for (int i = 0; i < nLayer[lTotal -1]; i++) {
				totalCrossEntropyError += expectedOutput[i]*Math.log(a[lTotal - 1][i]) + (1 - expectedOutput[i])*Math.log(1 - a[lTotal - 1][i]);
			}
			return -totalCrossEntropyError / (double)(nLayer[lTotal - 1]);
		case BinaryCrossEntropy:
			
			break;
		default: 
			System.out.println("Default switch thrown at network error calculation"); 
			break;
		}
		
		return 0.0;
	}
}
