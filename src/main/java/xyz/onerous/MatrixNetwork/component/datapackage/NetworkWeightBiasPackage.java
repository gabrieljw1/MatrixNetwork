package xyz.onerous.MatrixNetwork.component.datapackage;

public class NetworkWeightBiasPackage {
	protected double[][] b;
	protected double[][][] w;
	
	public double[][] getNeuronBiases() {
		return b;
	}
	public double[][][] getConnectionWeights() {
		return w;
	}
	
	public NetworkWeightBiasPackage(double[][] b, double[][][] w) {
		this.b = b;
		this.w = w;
	}
}
