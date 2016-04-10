package fighter.states.fight.animation;

import java.awt.Graphics2D;

import fighter.states.fight.Fighter;
import fighter.util.Position;
import fighter.util.Vector;

/**
 * A damaging collision box inside of an AnimationStep
 * 
 * @author Fritz
 *
 */
public class HitBox {

	/**
	 * Group of hitboxes the hitbox is in
	 */
	private String group;
	
	/**
	 * Location of the top left corner of the hitbox relative to the animation
	 */
	private Position topLeftCorner;

	/**
	 * Location of the bottom right corner of the hitbox relative to the animation
	 */
	private Position bottomRightCorner;
	
	/**
	 * Amount of damage the hitbox does
	 */
	private float damage;
	
	/**
	 * Amount of hitstun the hitbox does
	 */
	private int hitStun;
	
	/**
	 * Amount of hitstun the hitbox does if blocked
	 */
	private int blockStun;
	
	/**
	 * Distance the hitbox will push back if blocked
	 */
	private float pushBack;
	
	/**
	 * Vector a fighter hit by the hitbox will be launched at
	 */
	private Vector launchVelocity;
	
	/**
	 * Whether the hitbox knocks down
	 */
	private boolean knockDown;

	/**
	 * Creates a hit box
	 * 
	 * @param group - Name of the hitbox group it's in
	 * @param topLeftCorner - Relative position of the top left corner
	 * @param bottomRightCorner - Relative position of the bottom right corner
	 * @param damage - Amount of damage the hitbox does on hit
	 * @param hitStun - Amount of hitstun the hitbox applies
	 * @param blockStun - Amount of blockstun the hitbox applies
	 * @param pushBack - Amount the hitbox will push back a fighter
	 * @param launchVelocity - Vector which a fighter hit by this will be launched at
	 * @param knockDown - Whether the hitbox knocks down
	 */
	public HitBox(String group, Position topLeftCorner, Position bottomRightCorner, float damage, int hitStun,
			int blockStun, float pushBack, Vector launchVelocity, boolean knockDown) {
		this.group = group;
		this.topLeftCorner = topLeftCorner;
		this.bottomRightCorner = bottomRightCorner;
		this.damage = damage;
		this.hitStun = hitStun;
		this.blockStun = blockStun;
		this.pushBack = pushBack;
		this.launchVelocity = launchVelocity;
		this.knockDown = knockDown;
	}
	
	/**
	 * Gets whether this hitbox is colliding with the specified hurtbox
	 * 
	 * @param attackerPos - The attacker's position
	 * @param defenderPos - The defender's position
	 * @param hurtBox - The hurtbox being checked for collision
	 * @return - Whether a collision is taking place
	 */
	public boolean isColliding(Position attackerPos, Position defenderPos, HurtBox hurtBox) {
		return false;
	}
	
	/**
	 * Applies the hitbox's effects to the fighter hit by it and adds hit to
	 * source's hitBy list
	 * 
	 * @param source - Fighter using the hitbox
	 * @param defender - Fighter hit by the hitbox
	 */
	public void applyHit(Fighter source, Fighter defender) {
		
	}
	
	/**
	 * Draws the hitboxes
	 * 
	 * @param position - Position to draw at
	 * @param g - Graphics2D object to draw
	 */
	public void draw(Position position, Graphics2D g) {
		
	}
	
}
