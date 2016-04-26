package game.states.fight.animation;

import java.awt.Color;
import java.awt.Graphics2D;

import game.states.fight.Camera;
import game.util.Box;
import game.util.Position;

/**
 * An enviromental collision box
 * 
 * @author Fritz
 *
 */
public class ECB {
	
	/**
	 * Frame the ECB appears on in the animation
	 */
	private int startFrame;

	/**
	 * Frame the ECB disappears on in the animation
	 */
	private int endFrame;

	/**
	 * Collision box
	 */
	private Box collision;

	/**
	 * Creates a ECB
	 * 
	 * @param collision - Bounding box of the ECB
	 */
	public ECB(int startFrame, int endFrame, Box collision) {
		this.startFrame = startFrame;
		this.endFrame = endFrame;
		this.collision = collision;
	}
	
	public ECB(ECB copy) {
		this.startFrame = copy.startFrame;
		this.endFrame = copy.endFrame;
		this.collision = new Box(copy.collision);
	}

	/**
	 * Gets the collision box
	 * 
	 * @return - Collision box
	 */
	public Box getCollision() {
		return collision;
	}
	
	/**
	 * Draws the ECB
	 * 
	 * @param position - Position to draw at
	 * @param g - Graphics2D object to draw
	 */
	public void draw(Position position, Camera camera, Graphics2D g) {
		collision.forOffset(position).draw(g, camera, Color.GREEN);
	}

	/**
	 * Gets whether the ECB is currently active
	 * 
	 * @param currentFrame - Current frame of the animation
	 * @return - Whether the ECB is active
	 */
	public boolean isActive(double currentFrame) {
		return startFrame <= currentFrame && currentFrame <= endFrame;
	}
}
