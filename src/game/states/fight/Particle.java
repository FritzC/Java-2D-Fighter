package game.states.fight;

import java.awt.Graphics2D;
import java.util.List;

import game.states.fight.animation.Animation;
import game.states.fight.animation.collisions.HitBox;
import game.states.fight.animation.collisions.HurtBox;
import game.util.Position;
import game.util.Vector;

/**
 * Any non-fighter object or animation in the FightState
 *  - TODO: Needs a better name
 *  
 * @author Fritz
 *
 */
public class Particle {
	
	/**
	 * The particle's animation
	 */
	private Animation animation;

	/**
	 * The Fighter the owns the particle, null if none
	 */
	private Fighter owner;
	
	/**
	 * Whether the particle moves with the owner
	 */
	private boolean attached;
	
	/**
	 * Position of the particle
	 */
	private Position position;
	
	/**
	 * Velocity of the particle
	 */
	private Vector velocity;
	
	/**
	 * Creates the particle 
	 * 
	 * @param animation - Animation of the particle
	 * @param owner - Owner of the particle
	 * @param attached - Whether the particle is attached to the owner
	 * @param position - The initial position of the particle
	 * @param velocity - The velocity of the particle
	 */
	public Particle(Animation animation, Fighter owner, boolean attached, Position position, Vector velocity) {
		this.animation = animation;
		this.owner = owner;
		this.attached = attached;
		this.position = position;
		this.velocity = velocity;
	}
	
	/**
	 * Draws the particle
	 * 
	 * @param g - Graphics2D object used to draw
	 */
	public void draw(Graphics2D g, Camera camera, Stage stage) {
		
	}
	
	/**
	 * Process loop of the particle
	 *  - To be overriden
	 */
	public void tick() {}
	
	/**
	 * Gets the particle's current hitboxes
	 * 
	 * @return - List of current hitboxes
	 */
	public List<HitBox> getHitBoxes() {
		return animation.getActiveHitBoxes();
	}

	/**
	 * Gets the particle's current hurtboxes
	 * 
	 * @return - List of current hurtboxes
	 */
	public List<HurtBox> getHurtBoxes() {
		return animation.getActiveHurtBoxes();
	}
}
