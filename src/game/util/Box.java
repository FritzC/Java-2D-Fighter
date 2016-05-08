package game.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import game.states.fight.Camera;

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
	protected Position topLeft;
	
	/**
	 * Bottom right coordinate
	 */
	protected Position bottomRight;
	
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
	public Box(Position topLeft, double width, double height) {
		this(topLeft, new Position(topLeft.getX() + width, topLeft.getY() + height));
	}

	public Box(Box copy) {
		this.topLeft = new Position(copy.topLeft.getX(), copy.topLeft.getY());
		this.bottomRight = new Position(copy.bottomRight.getX(), copy.bottomRight.getY());
	}

	/**
	 * Gets an offset instance of the box
	 * 
	 * @param offset - Position the box is to be offset
	 * @return - New instance of the offset box
	 */
	public Box forOffset(int face, Position offset) {
		if (face < 0) {
			return new Box(new Position(-bottomRight.getX() + offset.getX(), topLeft.getY() + offset.getY()),
					new Position(-topLeft.getX() + offset.getX(), bottomRight.getY() + offset.getY()));
		}
		return new Box(new Position(topLeft.getX() + offset.getX(), topLeft.getY() + offset.getY()),
				new Position(bottomRight.getX() + offset.getX(), bottomRight.getY() + offset.getY()));
	}

	/**
	 * Checks if the two boxes are intersecting
	 * 
	 * @param box - Box checked against
	 * @return - Whether the boxes are intersecting
	 */
	public boolean intersects(Box box) {
		if (topLeft.getX() >= box.topLeft.getX() && topLeft.getX() <= box.bottomRight.getX()
				&& topLeft.getY() >= box.bottomRight.getY() && topLeft.getY() <= box.topLeft.getY()) {
			return true;
		}
		if (bottomRight.getX() >= box.topLeft.getX() && bottomRight.getX() <= box.bottomRight.getX()
					&& topLeft.getY() >= box.bottomRight.getY() && topLeft.getY() <= box.topLeft.getY()) {
			return true;
		}
		if (topLeft.getX() >= box.topLeft.getX() && topLeft.getX() <= box.bottomRight.getX()
					&& bottomRight.getY() >= box.bottomRight.getY() && bottomRight.getY() <= box.topLeft.getY()) {
			return true;
		}
		if (bottomRight.getX() >= box.topLeft.getX() && bottomRight.getX() <= box.bottomRight.getX()
				&& bottomRight.getY() >= box.bottomRight.getY() && bottomRight.getY() <= box.topLeft.getY()) {
			return true;
		}
		if (box.topLeft.getX() >= topLeft.getX() && box.topLeft.getX() <= bottomRight.getX()
				&& box.topLeft.getY() >= bottomRight.getY() && box.topLeft.getY() <= topLeft.getY()) {
			return true;
		}
		if (box.bottomRight.getX() >= topLeft.getX() && box.bottomRight.getX() <= bottomRight.getX()
				&& box.topLeft.getY() >= bottomRight.getY() && box.topLeft.getY() <= topLeft.getY()) {
			return true;
		}
		if (box.topLeft.getX() >= topLeft.getX() && box.topLeft.getX() <= bottomRight.getX()
				&& box.bottomRight.getY() >= bottomRight.getY() && box.bottomRight.getY() <= topLeft.getY()) {
			return true;
		}
		if (box.bottomRight.getX() >= topLeft.getX() && box.bottomRight.getX() <= bottomRight.getX()
				&& box.bottomRight.getY() >= bottomRight.getY() && box.bottomRight.getY() <= topLeft.getY()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Draws the box
	 * 
	 * @param g - Graphics object used to draw
	 * @param camera - Camera on the scene
	 * @param color - Color of the box
	 */
	public void draw(Graphics2D g, Camera camera, Color color, boolean selected) {
		int x = camera.getScreenX(topLeft);
		int y = camera.getScreenY(topLeft);
		int width = camera.toPixels(Math.abs(bottomRight.getX() - topLeft.getX()));
		int height = camera.toPixels(Math.abs(topLeft.getY() - bottomRight.getY()));
		//g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
		//g.fillRect(x + 1, y + 1, width - 1, height - 1);
		g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 200));
		if (selected)
			g.setColor(Color.BLACK);
		g.drawRect(x, y, width, height);
	}

	public String toString() {
		return "[" + ((int) (1000 * topLeft.getX()) / 1000d) + ", " + ((int) (1000 * topLeft.getY()) / 1000d) + ", "
				+ ((int) (1000 * bottomRight.getX()) / 1000d) + ", " + ((int) (1000 * bottomRight.getY()) / 1000d) + "]";
	}
}
