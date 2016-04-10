package fighter.states.fight;

import java.awt.Graphics2D;
import java.util.List;

import fighter.states.fight.animation.Animation;
import fighter.states.fight.animation.HitBox;
import fighter.states.fight.animation.HurtBox;
import fighter.util.Position;
import fighter.util.Vector;

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
	 * @param graphics - Graphics2D object used to draw
	 */
	public void draw(Graphics2D graphics) {
		
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
		return animation.getHitBoxes();
	}

	/**
	 * Gets the particle's current hurtboxes
	 * 
	 * @return - List of current hurtboxes
	 */
	public List<HurtBox> getHurtBoxes() {
		return animation.getHurtBoxes();
	}
}
