package game.states.fight.animation;

import game.util.Position;

/**
 * A collision box that determines where a fighter is vulnerable inside an AnimationStep
 * 
 * @author Fritz
 *
 */
public class HurtBox {

	/**
	 * Top left corner of the hurtbox relative to the animation
	 */
	private Position topLeftCorner;

	/**
	 * Bottom right corner of the hurtbox relative to the animation
	 */
	private Position bottomRightCorner;

	/**
	 * Creates a hurtbox
	 * 
	 * @param topLeftCorner - Top left corner of the hurtbox
	 * @param bottomRightCorner - Bottom right corner of the hurtbox
	 */
	public HurtBox(Position topLeftCorner, Position bottomRightCorner) {
		this.topLeftCorner = topLeftCorner;
		this.bottomRightCorner = bottomRightCorner;
	}
}
