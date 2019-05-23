package xyz.onerous.MatrixNetwork.component.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import xyz.onerous.MatrixNetwork.component.datapackage.NetworkWeightBiasPackage;

public class FileUtil {
	public static void writeNetworkWeightBiasPackage(NetworkWeightBiasPackage networkWeightBiasPackage, String fileName) {
		try {
			FileWriter fileWriter = new FileWriter(fileName);
			
			fileWriter.write("w" + Arrays.deepToString(networkWeightBiasPackage.getConnectionWeights()) + "\n");
			fileWriter.write("b" + Arrays.deepToString(networkWeightBiasPackage.getNeuronBiases()));
			
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static NetworkWeightBiasPackage loadNetworkWeightBiasPackage(String fileName) {
		try {
			List<String> lines = Files.readAllLines(Paths.get(fileName));
			
			return new NetworkWeightBiasPackage(
					fromString2D(lines.get(1).substring(1, lines.get(1).length())),
					fromString3D(lines.get(0).substring(1, lines.get(0).length()))
					);
		} catch (IOException e) {
			e.printStackTrace();
			
			return (NetworkWeightBiasPackage) null;
		}
	}
	
	public static double[][] loadStudentData(String fileName) {
		try {
			List<String> lines = Files.readAllLines(Paths.get(fileName));
			
			double[][] returnArray = new double[lines.size()][];
			
			for (int i = 0; i < returnArray.length; i++) {
				double[] dataArray = new double[5];
				
				String[] strings = lines.get(i).replace(";", "").split(" ");
				
				for (int j = 0; j < dataArray.length; j++) {
					dataArray[j] = Double.parseDouble(strings[j]);
				}
				
				returnArray[i] = dataArray;
			}
			
			return returnArray;
		} catch (IOException e) {
			e.printStackTrace();
			
			return null;
		}
	}
	
	
	/**
	 * Take a 3D array in string and return an array of doubles. Assumed to be a string made by Arrays.toString()
	 * 
	 * @param string
	 * @return
	 */
	private static double[][][] fromString3D(String string) {
		String[] strings = string.replace("]], ", "]];").replace("[[[", "[[").replace("]]]", "]]").replace("", "").split(";");
		
		double[][][] result = new double[strings.length][][];
	
		
		for (int i = 0; i < result.length; i++) {
			result[i] = fromString2D(strings[i]);
		}
		
		return result;
	}
	
	/**
	 * Take a 2D array in string and return an array of doubles. Assumed to be a string made by Arrays.toString()
	 * 
	 * @param string
	 * @return
	 */
	private static double[][] fromString2D(String string) {
		String[] strings = string.replace("], ", "];").replace("[[", "[").replace("]]", "]").replace("", "").split(";");
		
		double[][] result = new double[strings.length][];
		
		for (int i = 0; i < result.length; i++) {
			result[i] = fromString1D(strings[i]);
		}
		
		return result;
	}
	
	/**
	 * Take a 1D array in stringand return an array of doubles. Assumed to be a string made by Arrays.toString()
	 * 
	 * @param string
	 * @return
	 */
	private static double[] fromString1D(String string) {
	    String[] strings = string.replace("[]", "[0]").replace("[", "").replace("]", "").split(", ");
	    
	    double[] result = new double[strings.length];
	    
	    for (int i = 0; i < result.length; i++) {
	      result[i] = Double.parseDouble(strings[i]);
	    }
	    
	    return result;
	  }
}
