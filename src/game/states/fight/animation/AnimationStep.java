package game.states.fight.animation;

import java.util.List;

import game.util.Vector;

/**
 * A step in an Animation
 * 
 * @author Fritz
 *
 */
public class AnimationStep {

	/**
	 * List of the step's hitboxes
	 */
	private List<HitBox> hitBoxes;
	
	/**
	 * List of the step's hurtboxes
	 */
	private List<HurtBox> hurtBoxes;
	
	/**
	 * Instruction set for the bones
	 */
	private List<AnimationInstruction> instructions;
	
	/**
	 * Number of frames the step lasts
	 */
	private int duration;
	
	/**
	 * Vector the fighter moves during the step
	 */
	private Vector selfVelocity;
	
	/**
	 * Whether to use selfVector
	 */
	private boolean moveSelf;
	
	/**
	 * Creates an animation step
	 * 
	 * @param sprite - Image to draw during the step
	 * @param duration - Length of the step in ticks
	 * @param selfVelocity - Velocity the fighter will be moved during the step
	 * @param moveSelf - Whether to move the fighter during the step
	 */
	public AnimationStep(List<AnimationInstruction> instructions, int duration, Vector selfVelocity, boolean moveSelf) {
		this.instructions = instructions;
		this.duration = duration;
		this.selfVelocity = selfVelocity;
		this.moveSelf = moveSelf;
	}

	/**
	 * Gets the hitboxes
	 * 
	 * @return - Step's hitboxes
	 */
	public List<HitBox> getHitBoxes() {
		return hitBoxes;
	}
	
	/**
	 * Gets the hurtboxes
	 * 
	 * @return - Step's hurtboxes
	 */
	public List<HurtBox> getHurtBoxes() {
		return hurtBoxes;
	}
}
