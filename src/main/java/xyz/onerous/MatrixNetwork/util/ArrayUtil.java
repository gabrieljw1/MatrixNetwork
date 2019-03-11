package xyz.onerous.MatrixNetwork.util;

import java.util.Arrays;

public class ArrayUtil {
	/**
	 * Transfer a two-dimensional array [2D] into a one-dimensional array [1D] by concatenating each layer
	 * to the end of the last one in one dimension.
	 * 
	 * @param arrayToFlatten A 2D Array
	 * @return A 1D Array
	 */
	public static int[] flattenArray(int[][] arrayToFlatten) {
		if (arrayToFlatten.length == 0 || arrayToFlatten[0].length == 0) { return null; }
		
		int[] returnArray = new int[arrayToFlatten.length * arrayToFlatten[0].length];
		
		for (int i = 0; i < arrayToFlatten.length; i++) {
			for (int j = 0; j < arrayToFlatten[i].length; j++) {
				returnArray[j + (arrayToFlatten[i].length) * i] = arrayToFlatten[i][j];
			}
		}
		
		return returnArray;
	}
	
	/**
	 * Find the mean of a given array.
	 * 
	 * @param array The array to find the mean of
	 * @return The array's mean
	 */
	public static double mean(int[] array) {		
		int sum = 0;
		
		for (int i : array) { sum += i; }
		
		return (double)sum / (double)array.length;
	}
	
	/**
	 * Find the standard deviation of a given array.
	 * 
	 * @param array The array to find the standard deviation of
	 * @return The array's standard deviation
	 */
	public static double standardDeviation(int[] array) {
		double variance = 0;
		double mean = mean(array);
		
		for (int i : array) {
			variance += Math.pow(i - mean, 2.0) / (double)array.length;
		}
		
		return Math.sqrt(variance);
	}
	
	/**
	 * Standardize a given array. Meaning, transfer it into a set where the mean is zero and the standard
	 * deviation is one.
	 * 
	 * @param array The array to standardize
	 * @return The standardized form of the given array
	 */
	public static double[] standardize(int[] array) {
		if (array.length == 0) { return new double[0]; }
		
		double[] returnArray = new double[array.length];
		
		double mean = mean(array);
		double standardDeviation = standardDeviation(array);
		
		for (int i = 0; i < returnArray.length; i++) {
			returnArray[i] = ((double)array[i] - mean) / standardDeviation;
		}
		
		return returnArray;
	}
	
	/**
	 * Clips an array to the desired range, first inclusive and out range exclusive.
	 * 
	 * @param array
	 * @param startingIndex
	 * @param endingIndex
	 * @return
	 */
	public static double[][] clipArray(double[][] array, int startingIndex, int endingIndex) {
		double[][] returnArray = new double[endingIndex - startingIndex][];
		
		for (int i = startingIndex; i < endingIndex; i++) {
			returnArray[i - startingIndex] = array[i];
		}
		
		return returnArray;
	}
	
	/**
	 * Clips an array to the desired range, first inclusive and out range exclusive.
	 * 
	 * @param array
	 * @param startingIndex
	 * @param endingIndex
	 * @return
	 */
	public static int[] clipArray(int[] array, int startingIndex, int endingIndex) {
		int[] returnArray = new int[endingIndex - startingIndex];
		
		for (int i = startingIndex; i < endingIndex; i++) {
			returnArray[i - startingIndex] = array[i];
		}
		
		return returnArray;
	}
}
