package game.util;

/**
 * A 2 dimensional vector
 * 
 * @author Fritz
 *
 */
public class Vector {

	/**
	 * X component
	 */
	private double x;
	
	/**
	 * Y component
	 */
	private double y;
	
	/**
	 * Creates a vector
	 * 
	 * @param x - X component
	 * @param y - Y component
	 */
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Translates the vector by x and y
	 * 
	 * @param x - X translation
	 * @param y - Y translation
	 */
	public void transform(double x, double y) {
		this.x += x;
		this.y += y;
	}
	
	/**
	 * Sets X
	 * 
	 * @param x - What X will be set to
	 */
	public void setX(double x) {
		this.x = x;
	}
	
	/**
	 * Sets Y
	 * 
	 * @param y - What Y will be set to
	 */
	public void setY(double y) {
		this.y = y;
	}
	
	/**
	 * Gets X
	 * 
	 * @return - X location
	 */
	public double getX() {
		return x;
	}

	/**
	 * Gets Y
	 * 
	 * @return - Y location
	 */
	public double getY() {
		return y;
	}
}
