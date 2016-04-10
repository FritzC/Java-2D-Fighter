package fighter.states.fight.animation;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import fighter.util.Position;

/**
 * A sequence of Sprites
 * 
 * @author Fritz
 *
 */
public class Animation {

	/**
	 * List of all the animation steps
	 */
	private List<AnimationStep> steps;
	
	/**
	 * Whether the animation should loop
	 */
	private boolean loop;
	
	/**
	 * The current frame the animation is displaying
	 */
	private int currentFrame;
	
	/**
	 * Number of ticks until currentFrame advances
	 */
	private int ticksUntilNextFrame;
	
	/**
	 * Loads an animation from a file
	 * 
	 * @param filePath - File path to load the animation from
	 */
	public Animation(String filePath) {
		steps = new ArrayList<>();
	}
	
	/**
	 * Draws the animation's current step at a position
	 * 
	 * @param position - Position to draw the animation at
	 * @param g - Graphics2D object to draw
	 */
	public void draw(Position position, Graphics2D g) {
		
	}
	
	/**
	 * Gets the current step's hitboxes
	 * 
	 * @return - List of current hitboxes
	 */
	public List<HitBox> getHitBoxes() {
		return steps.get(currentFrame).getHitBoxes();
	}

	/**
	 * Gets the current step's hurtboxes
	 * 
	 * @return - List of current hurtboxes
	 */
	public List<HurtBox> getHurtBoxes() {
		return steps.get(currentFrame).getHurtBoxes();
	}
}
