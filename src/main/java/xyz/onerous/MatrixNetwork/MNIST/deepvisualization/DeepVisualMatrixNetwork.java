package xyz.onerous.MatrixNetwork.MNIST.deepvisualization;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Random;

import xyz.onerous.MatrixNetwork.MatrixNetwork;
import xyz.onerous.MatrixNetwork.component.ActivationType;
import xyz.onerous.MatrixNetwork.component.LossType;
import xyz.onerous.MatrixNetwork.component.datapackage.NeuronInputDeltaPackage;
import xyz.onerous.MatrixNetwork.component.exception.ArrayNotSquareException;
import xyz.onerous.MatrixNetwork.component.util.MatrixUtil;

public class DeepVisualMatrixNetwork extends MatrixNetwork {
	private final double NEURON_INPUT_DELTA_RATE = 1.0; //Rate at which the input neuron Z changes
	
	public DeepVisualMatrixNetwork(int nInput, int nOutput, int[] nHidden, int lHidden, double learningRate, boolean usingSoftmax, ActivationType activationType, LossType lossType) {
		super(nInput, nOutput, nHidden, lHidden, learningRate, usingSoftmax, activationType, lossType);
	}

	public BufferedImage generateImageFromInputNeurons() throws ArrayNotSquareException {
		//Check if the input vector can be represented as a square raster
		if (Math.sqrt(z[0].length) - (int)Math.sqrt(z[0].length) != 0) {
			throw new ArrayNotSquareException();
		}
		
		int imageWidth = (int)Math.sqrt(z[0].length);
		
		BufferedImage outputImage = new BufferedImage(imageWidth, imageWidth, BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster raster = outputImage.getRaster();
		
		//We need to take the neuron array from [-1,1] -> [0, 255]
		double[] dataArray = MatrixUtil.scalarMultiply(  MatrixUtil.scalarAdd(z[0], 1)  , 127.5);
		
		raster.setSamples(0, 0, imageWidth, imageWidth, 0, dataArray);
        
        return outputImage;
	}
	
	public double[] generateRandomInputData() {
		Random random = new Random();
		
		double[] data = new double[z[0].length];
		
		for (int i = 0; i < data.length; i++) {
			data[i] = -0.5;//random.nextDouble() * 2.0 - 1.0;
		}
		
		return data;
	}

	private NeuronInputDeltaPackage gradientAscent(int expectedIndex) { //Batch size needed to limit weight/bias changing over an entire batch
		propagate();
		
		backPropagate(expectedIndex);
		
		double[][] deltaZ = a.clone();
		
		deltaZ[0] = MatrixUtil.scalarMultiply(δ[0], Math.pow(this.getOutputError(expectedIndex), -1) * -NEURON_INPUT_DELTA_RATE);
		
		/*
		for (int l = numL - 1; l > 0; l--) {
			deltaW[l] = MatrixUtil.scalarMultiply(MatrixUtil.multiplyWithSecondTranspose(δ[l], a[l-1]), -learningRate);
			deltaB[l] = MatrixUtil.scalarMultiply(δ[l], -learningRate);
		}
		*/
		
		return new NeuronInputDeltaPackage(deltaZ); 
	}
	
	private void applyInputDataDelta(NeuronInputDeltaPackage neuronInputDeltaPackage) {
		z[0] = MatrixUtil.add(z[0], neuronInputDeltaPackage.deltaZ[0]);
	}
	
	@Override protected void backPropagate(int expectedIndex) {
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
		for (int l = numL - 2; l >= 0; l--) { //THIS IS THE OVERRIDDEN LINE to INCLUDE FIRST LAYER
			δ[l] = MatrixUtil.hadamard(MatrixUtil.multiplyWithFirstTranspose(w[l+1], δ[l+1]), activateLayerPrime(z[l]));
		}
	}
	
	/**
	 * Assumes already trained network
	 * 
	 * @param expectedOutput
	 * @param numberOfTrains
	 */
	public BufferedImage generateDeepNetworkVisual(int expectedOutput, int numberOfTrains) {
		inputDataAndPropagate(generateRandomInputData());
		
		for (int i = 0; i < numberOfTrains; i++) {
			NeuronInputDeltaPackage neuronInputDeltaPackage = gradientAscent(expectedOutput);
			
			System.out.println(this.getOutputError(expectedOutput));
			
			applyInputDataDelta(neuronInputDeltaPackage);
		}
		
		try {
			return generateImageFromInputNeurons();
		} catch (ArrayNotSquareException e) {
			e.printStackTrace();
			return (BufferedImage) null;
		}
	}
}
