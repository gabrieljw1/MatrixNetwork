package xyz.onerous.MatrixNetwork.visualizer.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import xyz.onerous.MatrixNetwork.util.MathUtil;

/**
 * A class that represents a Connection visually. It stores the two endpoints of the connection's line as
 * well as the size. It represents the connection's weight with a color, and it uses java's Graphics to paint
 * the line between the two endpoints.
 * 
 * @author Gabriel Wong
 */
public class VConnection extends JComponent {
	private static final long serialVersionUID = 4177170439328838344L;

	//Visuals. Floor and Ceil for color computation.
	private static final int CONNECTION_SIZE = 3;
	private static final double CONNECTION_VALUE_FLOOR = -1.0;
	private static final double CONNECTION_VALUE_CEIL = 1.0;

	//Variables
	private double weight;
	private double value;
	private int x1, y1, x2, y2;
	private Color color;
	
	//Getters
	public double getValue() { return this.value; }
	public double getWeight() { return this.weight; }
	
	//Setters
	public void setValue(double value) { this.value = value; }
	public void setWeight(double weight) { this.weight = weight; setColor( Color.getHSBColor( (float)MathUtil.mapDoubleToRange(this.getWeight(), CONNECTION_VALUE_FLOOR, CONNECTION_VALUE_CEIL, 0, (float)128/360) , 1, 1) ); repaint();  }
	public void setColor(Color color) { this.color = color; }
	
	/**
	 * Create a new connection between two visual neurons. The line's two endpoints are derived from both
	 * neurons's positions, and the weight and value are initialized to zero. 
	 * 
	 * @param color
	 * @param fromNeuron
	 * @param toNeuron
	 */
	public VConnection(Color color, VNeuron fromNeuron, VNeuron toNeuron) {
		this.x1 = (int)fromNeuron.getX() + (int)fromNeuron.getRadius();
		this.y1 = (int)fromNeuron.getY() + (int)fromNeuron.getRadius();
		this.x2 = (int)toNeuron.getX() + (int)toNeuron.getRadius();
		this.y2 = (int)toNeuron.getY() + (int)toNeuron.getRadius();
		
		weight = 0;
		value = 0;
		
		//For java Graphics painting.
		setBounds((int)Math.min(x1,  x2), (int)Math.min(y1, y2), (int)Math.abs(x1-x2), (int)Math.abs(y1-y2));
		
		//So that that bounds do not form an opaque rectangle background around each connection.
		setOpaque(false);
		
		this.color = color;
	}
	
	/**
	 * Draw the component on the screen using java's Graphics.
	 */
	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
				
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(color);
		g2.setStroke(new BasicStroke(CONNECTION_SIZE));
		
		if (y1 < y2) {
			g2.drawLine(x1-x2, 0, 0, y2-y1);
		} else {
			g2.drawLine(x1-x2, Math.abs(y2-y1), 0, 0);
		}
	}
}
