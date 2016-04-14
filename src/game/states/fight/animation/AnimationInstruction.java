package game.states.fight.animation;

import game.states.fight.fighter.FighterBone;

/**
 * An instruction for bone animation
 * 
 * @author Fritz
 *
 */
public class AnimationInstruction {

	/**
	 * Type of instruction
	 */
	private Instruction type;
	
	/**
	 * Type of interpolation used
	 */
	private Interpolation interpolation;
	
	/**
	 * Name of the bone being acted on
	 */
	private String boneId;
	
	/**
	 * Auxiliary data for the instruction
	 */
	private float data;

	/**
	 * Initializes an instruction
	 * 
	 * @param boneId - Name of bone to be modified
	 * @param data - Auxiliary data about the move (new length, new angle, 1 or 0 for visibility
	 * @param type - Type of instruction
	 * @param interpolation - Type of interpolation to use
	 */
	public AnimationInstruction(String boneId, float data, Instruction type, Interpolation interpolation) {
		this.boneId = boneId;
		this.type = type;
		this.interpolation = interpolation;
		this.data = data;
	}
	
	/**
	 * Applies interpolation to the bone
	 * 
	 * @param root - Fighter's root bone
	 * @param completion - % completion of the movement
	 */
	public void applyStep(FighterBone root, float completion) {
		root.getBone(boneId).interpolate(data, type, interpolation, completion);
	}
	
	/**
	 * Applies the instruction
	 * 
	 * @param root - Fighter's root bone
	 */
	public void apply(FighterBone root) {
		root.getBone(boneId).applyInstruction(data, type);
	}

}
