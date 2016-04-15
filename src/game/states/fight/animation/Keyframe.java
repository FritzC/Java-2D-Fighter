package game.states.fight.animation;

import java.util.HashMap;
import java.util.Map;

import game.states.fight.Fighter;
import game.states.fight.fighter.Bone;

/**
 * An instruction for bone animation
 * 
 * @author Fritz
 *
 */
public class Keyframe {

	/**
	 * Type of instruction
	 */
	private KeyframeType type;
	
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
	private double data;
	
	/**
	 * Frame that interpolating begins
	 */
	private int beginInterpolatingFrame;
	
	/**
	 * Frame the keyframe ends
	 */
	private int endFrame;

	/**
	 * Initializes an instruction
	 * 
	 * @param frame - Frame the keyframe ends
	 * @param boneId - Name of bone to be modified
	 * @param data - Auxiliary data about the move (new length, new angle, 1 or 0 for visibility
	 * @param type - Type of instruction
	 * @param interpolation - Type of interpolation to use
	 */
	public Keyframe(int frame, String boneId, double data, KeyframeType type, Interpolation interpolation) {
		this.endFrame = frame;
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
	public void interpolate(Fighter fighter, Bone root, int currentFrame) {
		if (type == KeyframeType.VELOCITY_X || type == KeyframeType.VELOCITY_Y) {
			fighter.interpolateVelocity(data, type, interpolation, getCompletion(currentFrame));
			return;
		}
		root.getBone(boneId).interpolate(data, type, interpolation, getCompletion(currentFrame));
	}
	
	/**
	 * Applies the instruction
	 * 
	 * @param root - Fighter's root bone
	 */
	public void apply(Bone root) {
		root.getBone(boneId).applyInstruction(data, type);
	}
	
	/**
	 * Attempts to register the keyframe to begin interpolating
	 * 
	 * @param currentFrame - Current frame animation is displaying
	 * @param table - Queued keyframe table
	 * @return - Whether registration was successful
	 */
	public boolean attemptToRegister(int currentFrame, Map<String, Map<KeyframeType, Keyframe>> table) {
		if (table.containsKey(boneId) && table.get(boneId).containsKey(type) || getCompletion(currentFrame) >= 1) {
			return false;
		}
		if (table.get(boneId) == null) {
			table.put(boneId, new HashMap<>());
		}
		beginInterpolatingFrame = currentFrame;
		table.get(boneId).put(type, this);
		System.out.println("Successfully added instruction: " + type.toString() + "[" + data + "] for " + boneId);
		return true;
	}
	
	/**
	 * Gets the current completion %
	 * 
	 * @param currentFrame - Current frame animation is on
	 * @return - % complete
	 */
	public double getCompletion(int currentFrame) {
		return (currentFrame - beginInterpolatingFrame) / (double) (endFrame - beginInterpolatingFrame);
	}
	
	/**
	 * Gets the end frame of the keyframe
	 * 
	 * @return - End frame of the keyframe
	 */
	public int getEndFrame() {
		return endFrame;
	}

}
