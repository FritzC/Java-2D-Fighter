package game.util;

import java.awt.Rectangle;

/**
 * A box
 * 
 * @author Fritz
 *
 */
public class Box {

	/**
	 * Top left coordinate
	 */
	private Position topLeft;
	
	/**
	 * Bottom right coordinate
	 */
	private Position bottomRight;
	
	/**
	 * Initializes a box
	 * 
	 * @param topLeft - Top left corner
	 * @param bottomRight - Bottom right corner
	 */
	public Box(Position topLeft, Position bottomRight) {
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
	}
	
	/**
	 * Initializes a box
	 * 
	 * @param topLeft - Top left coordinate
	 * @param width - Width of box
	 * @param height - Height of box
	 */
	public Box(Position topLeft, float width, float height) {
		this(topLeft, new Position(topLeft.getX() + width, topLeft.getY() + height));
	}

	/**
	 * Checks if the two boxes are intersecting
	 * 
	 * @param box - Box checked against
	 * @return - Whether the boxes are intersecting
	 */
	public boolean intersects(Box box) {
		int multiplier = 10000;
		Rectangle r1 = new Rectangle((int) (topLeft.getX() * multiplier), (int) (topLeft.getY() * multiplier),
				(int) ((bottomRight.getX() - topLeft.getX()) * multiplier),
				(int) ((topLeft.getY() - bottomRight.getY()) * multiplier));
		Rectangle r2 = new Rectangle((int) (box.topLeft.getX() * multiplier), (int) (box.topLeft.getY() * multiplier),
				(int) ((box.bottomRight.getX() - box.topLeft.getX()) * multiplier),
				(int) ((box.topLeft.getY() - box.bottomRight.getY()) * multiplier));
		return r1.intersects(r2);
	}
}
