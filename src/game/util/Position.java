package game.util;

/**
 * A position in the fight
 * 
 * @author Fritz
 *
 */
public class Position {

	/**
	 * X location
	 */
	private float x;
	
	/**
	 * Y location
	 */
	private float y;
	
	/**
	 * Creates a position
	 * 
	 * @param x - X location
	 * @param y - Y location
	 */
	public Position(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Gets a new position by applying a vector to the location
	 * 
	 * @param vector - Vector to be applied
	 * @return - Resulting position
	 */
	public Position applyVector(Vector vector) {
		return new Position(x + vector.getX(), y + vector.getY());
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
