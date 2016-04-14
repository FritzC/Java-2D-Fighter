package game.states.fight.animation;

import game.util.Box;

/**
 * A collision box that determines where a fighter is vulnerable inside an AnimationStep
 * 
 * @author Fritz
 *
 */
public class HurtBox {

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
}
