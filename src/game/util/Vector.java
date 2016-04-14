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
	private float x;
	
	/**
	 * Y component
	 */
	private float y;
	
	/**
	 * Creates a vector
	 * 
	 * @param x - X component
	 * @param y - Y component
	 */
	public Vector(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Translates the vector by x and y
	 * 
	 * @param x - X translation
	 * @param y - Y translation
	 */
	public void transform(float x, float y) {
		this.x += x;
		this.y += y;
	}
	
	/**
	 * Sets X
	 * 
	 * @param x - What X will be set to
	 */
	public void setX(float x) {
		this.x = x;
	}
	
	/**
	 * Sets Y
	 * 
	 * @param y - What Y will be set to
	 */
	public void setY(float y) {
		this.y = y;
	}
	
	/**
	 * Gets X
	 * 
	 * @return - X location
	 */
	public float getX() {
		return x;
	}

	/**
	 * Gets Y
	 * 
	 * @return - Y location
	 */
	public float getY() {
		return y;
	}
}
