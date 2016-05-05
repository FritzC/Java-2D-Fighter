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
	private double beginInterpolatingFrame;
	
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
	
	public Keyframe(Keyframe keyframe) {
		this.endFrame = keyframe.endFrame;
		this.boneId = keyframe.boneId;
		this.type = keyframe.type;
		this.interpolation = keyframe.interpolation;
		this.data = keyframe.data;
	}

	/**
	 * Applies interpolation to the bone
	 * 
	 * @param root - Fighter's root bone
	 * @param completion - % completion of the movement
	 */
	public void interpolate(Fighter fighter, Bone root, double currentFrame) {
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
	public void apply(Fighter fighter, Bone root) {
		if (type == KeyframeType.VELOCITY_X || type == KeyframeType.VELOCITY_Y || type == KeyframeType.IGNORE_VELOCITY_X
				|| type == KeyframeType.IGNORE_VELOCITY_Y) {
			fighter.interpolateVelocity(data, type, interpolation, 1);
			return;
		}
		root.getBone(boneId).applyInstruction(data, type);
	}
	
	/**
	 * Attempts to register the keyframe to begin interpolating
	 * 
	 * @param currentFrame - Current frame animation is displaying
	 * @param table - Queued keyframe table
	 * @return - Whether registration was successful
	 */
	public boolean attemptToRegister(double currentFrame, Map<String, Map<KeyframeType, Keyframe>> table) {
		if (table.containsKey(boneId) && table.get(boneId).containsKey(type)
				&& table.get(boneId).get(type).endFrame <= endFrame
				|| (getCompletion(currentFrame) >= 1 && currentFrame != 0)) {
			return false;
		}
		if (table.get(boneId) == null) {
			table.put(boneId, new HashMap<KeyframeType, Keyframe>());
		}
		beginInterpolatingFrame = currentFrame;
		table.get(boneId).put(type, this);
		return true;
	}
	
	/**
	 * Gets the current completion %
	 * 
	 * @param currentFrame - Current frame animation is on
	 * @return - % complete
	 */
	public double getCompletion(double currentFrame) {
		if (endFrame == 0) {
			return 1;
		}
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

	public boolean matchesInfo(Object[] info) {
		return (int) info[0] == endFrame && ((String) info[1]).equals(boneId)
				&& (KeyframeType) info[2] == type;
	}
	
	public Object[] getInfo() {
		return new Object[] {endFrame, boneId, type, interpolation, data};
	}

	public void setEndFrame(int value) {
		endFrame = value;
	}
	
	public void setData(double value) {
		data = value;
	}

	public void setBone(String bone) {
		boneId = bone;
	}

	public void setType(KeyframeType type) {
		this.type = type;
	}

	public void setInterpolation(Interpolation interp) {
		this.interpolation = interp;
	}

}
