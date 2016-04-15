package game.states.fight.animation;

import java.awt.Color;
import java.awt.Graphics2D;

import game.states.fight.Camera;
import game.util.Box;
import game.util.Position;

/**
 * A collision box that determines where a fighter is vulnerable inside an AnimationStep
 * 
 * @author Fritz
 *
 */
public class HurtBox {
	
	/**
	 * Frame the hurtbox appears on in the animation
	 */
	private int startFrame;

	/**
	 * Frame the hurtbox disappears on in the animation
	 */
	private int endFrame;

	/**
	 * Collision box
	 */
	private Box collision;

	/**
	 * Creates a hurtbox
	 * 
	 * @param collision - Bounding box of the hurtbox
	 */
	public HurtBox(Box collision) {
		this.collision = collision;
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
	 * Draws the hurtbox
	 * 
	 * @param position - Position to draw at
	 * @param g - Graphics2D object to draw
	 */
	public void draw(Position position, Camera camera, Graphics2D g) {
		collision.forOffset(position).draw(g, camera, Color.YELLOW);
	}

	/**
	 * Gets whether the hurtbox is currently active
	 * 
	 * @param currentFrame - Current frame of the animation
	 * @return - Whether the hurtbox is active
	 */
	public boolean isActive(int currentFrame) {
		return startFrame <= currentFrame && currentFrame <= endFrame;
	}
}
