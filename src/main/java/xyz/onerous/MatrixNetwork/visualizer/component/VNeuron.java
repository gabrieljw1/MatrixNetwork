package xyz.onerous.MatrixNetwork.visualizer.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

import xyz.onerous.MatrixNetwork.component.util.MathUtil;

/**
 * A class that represents a Neuron visually. It stores the position of the Neuron as well as it's value and
 * color. It represents the value with a greyscale color, and it uses java's Graphics to paint the circle
 * that represents the neuron.
 * 
 * @author Gabriel Wong
 */
public class VNeuron extends JComponent {
	private static final long serialVersionUID = 2691490704695107160L;
	
	//Visuals for color calculation
	private static final double ALT_NEURON_FLOOR = -1; //For input neurons!
	private static final double NEURON_FLOOR = 0;
	private static final double NEURON_CEIL = 1;
	
	//Variables
	private double value, bias;
	private VConnection[] connections;
	private Color color;
	private double r; //Radius
	
	//Getters
	public double getValue() { return value; }
	public double getBias() { return bias; }
	public double getRadius() { return r; }
	public VConnection[] getConnections() { return connections; }
	public VConnection getConnection(int index) { return connections[index]; }
	
	//Setters
	public void setColor(Color color) { this.color = color; }
	public void setValue(double value) { 
		this.value = value; 
		
		if (value >= 0) {
			setColor( Color.getHSBColor(0, 0, (float)MathUtil.mapDoubleToRange(value, NEURON_FLOOR, NEURON_CEIL, 0, 1))); repaint(); 
		} else {
			setColor( Color.getHSBColor(0, 0, (float)MathUtil.mapDoubleToRange(value, ALT_NEURON_FLOOR, NEURON_CEIL, 0, 1))); repaint(); 
		}
	}
	public void setBias(double bias) { this.bias = bias; }
	public void setConnections(VConnection[] connections) { this.connections = connections; }
	public void setConnection(int index, VConnection connection) { this.connections[index] = connection; }
	public void setConnectionWeight(int index, double weight) { this.connections[index].setWeight(weight); }
	public void setConnectionValue(int index, double value) { this.connections[index].setValue(value); }

	/**
	 * Create a new visual neuron object with the four paramaters.
	 * 
	 * @param color
	 * @param x Position
	 * @param y Position
	 * @param r Radius
	 */
	public VNeuron(Color color, double x, double y, double r) {
		super();
		
		this.r = r;
		
		//For java Graphics painting.
		setBounds((int)x, (int)y, (int)r * 2, (int)r * 2);
		
		//So that that bounds do not form an opaque rectangle background around each connection.
		setOpaque(false);
		
		this.color = color;
	}
	
	/**
	 * For java Graphics.
	 */
	@Override public Dimension getPreferredSize() {
        return new Dimension((int)r * 2,(int)r * 2);
    }
	
	/**
	 * Draw the component on the screen using java's Graphics.
	 */
	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.setColor(color);
		g.fillOval(0, 0, (int)r*2, (int)r*2);
	}
}
