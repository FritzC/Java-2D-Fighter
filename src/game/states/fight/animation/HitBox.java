package game.states.fight.animation;

import java.awt.Color;
import java.awt.Graphics2D;

import game.states.fight.Camera;
import game.states.fight.Fighter;
import game.util.Box;
import game.util.Position;
import game.util.Vector;

/**
 * A damaging collision box inside of an AnimationStep
 * 
 * @author Fritz
 *
 */
public class HitBox {
	
	/**
	 * Frame the hitbox appears on in the animation
	 */
	private int startFrame;

	/**
	 * Frame the hitbox disappears on in the animation
	 */
	private int endFrame;

	/**
	 * Group of hitboxes the hitbox is in
	 */
	private String group;
	
	/**
	 * Collision box
	 */
	private Box collision;
	
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
	 * The bone that the hitbox attaches the other fighter to
	 */
	private String attachTo;
	
	/**
	 * Whether the hitbox releases any held fighters
	 */
	private boolean release;
	
	/**
	 * If the hitbox connects, the fighter will switch to this animation
	 */
	private String triggerAnimation;
	
	/**
	 * If the hitbox connects, the fighter hit will switch to this animation
	 */
	private String triggerTargetAnimation;

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
	public HitBox(int startFrame, int endFrame, String group, Box collision, float damage, int hitStun,
			int blockStun, float pushBack, Vector launchVelocity, boolean knockDown) {
		this.startFrame = startFrame;
		this.endFrame = endFrame;
		this.group = group;
		this.collision = collision;
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
		return collision.forOffset(attackerPos).intersects(hurtBox.getCollision().forOffset(defenderPos));
	}
	
	/**
	 * Applies the hitbox's effects to the fighter hit by it and adds hit to
	 * source's hitBy list
	 * 
	 * @param source - Fighter using the hitbox
	 * @param defender - Fighter hit by the hitbox
	 */
	public void applyHit(Fighter source, Fighter defender) {
		if (!attachTo.equals("")) {
			source.attach(attachTo, defender);
		}
		if (!triggerAnimation.equals("")) {
			source.setAnimation(triggerAnimation);
		}
		if (!triggerTargetAnimation.equals("")) {
			source.setAnimation(triggerTargetAnimation);
		}
		defender.applyHit(damage, hitStun, blockStun, pushBack, launchVelocity, knockDown);
	}
	
	/**
	 * Draws the hitboxes
	 * 
	 * @param position - Position to draw at
	 * @param g - Graphics2D object to draw
	 */
	public void draw(Position position, Camera camera, Graphics2D g) {
		collision.forOffset(position).draw(g, camera, Color.RED);
	}

	/**
	 * Gets whether the hitbox is currently active
	 * 
	 * @param currentFrame - Current frame of the animation
	 * @return - Whether the hitbox is active
	 */
	public boolean isActive(int currentFrame) {
		return startFrame <= currentFrame && currentFrame <= endFrame;
	}
	
}
