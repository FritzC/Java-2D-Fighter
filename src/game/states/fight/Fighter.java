package game.states.fight;

import java.awt.Graphics2D;
import java.util.List;
import java.util.Map;

import game.states.fight.animation.Animation;
import game.states.fight.animation.HitBox;
import game.states.fight.animation.HurtBox;
import game.states.fight.animation.KeyframeType;
import game.states.fight.animation.Interpolation;
import game.states.fight.fighter.Bone;
import game.util.Position;
import game.util.Vector;

/**
 * A Fighter either controlled by a Player or CPU
 * 
 * @author Fritz
 *
 */
public class Fighter {
	
	/**
	 * Root node of component skeleton
	 */
	private Bone skeleton;

	/**
	 * Map of all of the fighter's animations with identifiers
	 */
	private Map<String, Animation> animations;
	
	/**
	 * Current animation of the fighter
	 */
	private Animation animation;
	
	/**
	 * The current position of the fighter
	 */
	private Position position;
	
	/**
	 * The current velocity of the fighter
	 */
	private Vector velocity;
	
	/**
	 * The current health of the fighter
	 */
	private float health;
	
	/**
	 * The number of frames the fighter is still in hitstun
	 */
	private int hitStun;
	
	/**
	 * List of groups of hitboxes that the fighter has hit with
	 *  - To be cleared on each animation switch
	 */
	private List<String> hitBy;
	
	/**
	 * Loads the fighter's data from the file specified
	 * 
	 * @param filePath - Path to fighter data location
	 */
	public Fighter(String filePath) {
		
	}
	
	/**
	 * Draws the fighter in the game window
	 * 
	 * @param g - Graphics2d object used to draw
	 */
	public void draw(Graphics2D g, Camera camera, Stage stage) {
		if (animation != null) {
			animation.draw(position, this, skeleton, g, camera, stage);
		}
	}
	
	/**
	 * Sets the animation of the fighter
	 * 
	 * @param newAnim - Identifier of the new animation
	 */
	public void setAnimation(String newAnim) {
		animation = animations.get(newAnim);
	}
	
	/**
	 * Applies a hit to the fighter
	 * 
	 * @param damage - Damage done
	 * @param hitStun - Hitstun if successful hit
	 * @param blockStun - Hitstun if blocked
	 * @param pushBack - Amount of pushback from the hit
	 * @param launchVector - Resulting vector of the hit
	 * @param knockDown - Whether the hit knocks down
	 */
	public void applyHit(float damage, int hitStun, int blockStun, float pushBack, Vector launchVector, boolean knockDown) {
		
	}
	
	/**
	 * Gets the fighter's current hitboxes
	 * 
	 * @return - List of current hitboxes
	 */
	public List<HitBox> getHitBoxes() {
		return animation.getHitBoxes();
	}

	/**
	 * Gets the fighter's current hurtboxes
	 * 
	 * @return - List of current hurtboxes
	 */
	public List<HurtBox> getHurtBoxes() {
		return animation.getHurtBoxes();
	}

	/**
	 * Interpolate velocity 
	 * 
	 * @param data - New velocity
	 * @param type - Type of movement
	 * @param interpolation - Type of interpolation
	 * @param completion - % complete
	 */
	public void interpolateVelocity(float data, KeyframeType type, Interpolation interpolation, float completion) {
		switch (type) {
			case VELOCITY_X:
				velocity.setX(interpolation.getInterpolatedValue(velocity.getX(), data, completion));
				break;
			case VELOCITY_Y:
				velocity.setY(interpolation.getInterpolatedValue(velocity.getY(), data, completion));
				break;
		}
	}
}
