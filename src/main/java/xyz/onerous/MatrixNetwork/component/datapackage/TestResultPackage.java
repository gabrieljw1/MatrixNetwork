package xyz.onerous.MatrixNetwork.component.datapackage;

import xyz.onerous.MatrixNetwork.component.util.ArrayUtil;
import xyz.onerous.MatrixNetwork.component.util.MatrixUtil;

public class TestResultPackage {
	public int    numTests;
	public double percentageCorrect;
	
	public double[]  outputNeuronValues;
	public int[]     outputNeuronIndeces;
	public boolean[] ifCorrect;
	
	public TestResultPackage(int numTests, double percentageCorrect, double[] outputNeuronValues, int[] outputNeuronIndeces, boolean[] ifCorrect) {
		this.numTests = numTests;
		this.percentageCorrect = percentageCorrect;
		
		this.outputNeuronValues = outputNeuronValues;
		this.outputNeuronIndeces = outputNeuronIndeces;
		this.ifCorrect = ifCorrect;	
	}
	
	private double correctAverageConfidenceLevel() {
		double sum = 0;
		int num = 0;
		
		for (int i = 0; i < ifCorrect.length; i++) {
			if (ifCorrect[i]) { 
				sum += outputNeuronValues[i]; 
				num++;
			}
		}
		
		return sum / (double)num;
	}
	
	private double incorrectAverageConfidenceLevel() {
		double sum = 0;
		int num = 0;
		
		for (int i = 0; i < ifCorrect.length; i++) {
			if (!ifCorrect[i]) { 
				sum += outputNeuronValues[i]; 
				num++;
			}
		}
		
		return sum / (double)num;
	}
	
	public String toString() {
		String result = "|  Test  ||  Network Result  ||  Correct?    ||  Confidence\n";
		
		double[] percentageLevels = MatrixUtil.scalarMultiply(outputNeuronValues, 100);
		
		for (int t = 0; t < numTests; t++) {
			String testNumber = t + 1 + "";
			String networkResult;
			
			if (ifCorrect[t]) {
				networkResult = "correct  ";
			} else {
				networkResult = "incorrect";
			}
			
			for (int i = (int)(Math.log10(t+1)+1); i < 3; i++) {
				if (i < 0) { i = 1; }
				
				testNumber += " ";
			}
			
			result += "|  " + testNumber + "   ||       " + outputNeuronIndeces[t] + "          ||  " + networkResult + "   ||   " + Math.round(percentageLevels[t]) + "%\n";
		}
		
		return result + "\n\nTest Results for ID (" + this.hashCode() + ")\n"
				+ "  #Tests:   " + numTests + "\n"
				+ "  %Correct: " + 100.0 * percentageCorrect + "%\n"
				+ "  Average Confidence Level: " + ArrayUtil.mean(outputNeuronValues) + "%\n"
				+ "  Correct AVG Conf Level: " + correctAverageConfidenceLevel() + "%\n"
				+ "  Incorrect AVG Conf Lvl: " + incorrectAverageConfidenceLevel() + "%";
	}
}
