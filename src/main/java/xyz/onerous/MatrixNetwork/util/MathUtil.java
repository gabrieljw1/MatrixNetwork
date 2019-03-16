package xyz.onerous.MatrixNetwork.util;

/**
 * A collection of useful utility methods for manipulating data and numbers. These methods include:
 * 	mapDoubleToRange
 * 
 * @author Gabriel Wong
 * @version 1.0
 */
public class MathUtil {
	/**
	 * Change the range of a double into another range. For example, a double with range [-1, 1] can be
	 * converted into one with a range of [0, 1]. Essentially, the method scales and shifts values.
	 * 
	 * @param value
	 * @param inRangeFloor
	 * @param inRangeCeil
	 * @param outRangeFloor
	 * @param outRangeCeil
	 * @return
	 */
	public static double mapDoubleToRange(double value, double inRangeFloor, double inRangeCeil, double outRangeFloor, double outRangeCeil) {
		double newValue = value - inRangeFloor;
		newValue /= inRangeCeil - inRangeFloor;
		newValue *= outRangeCeil - outRangeFloor;
		newValue += outRangeFloor;
		
		return newValue;
	}
}
