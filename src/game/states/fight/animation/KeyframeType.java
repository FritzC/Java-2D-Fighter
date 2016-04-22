package game.states.fight.animation;

import game.states.fight.fighter.DrawMode;

/**
 * Types of Bone instructions
 * 
 * @author Fritz
 *
 */
public enum KeyframeType {
	VELOCITY_X,
	VELOCITY_Y,
	ROTATE, 
	LENGTH, 
	VISIBLE;
	
	public static KeyframeType forString(String s) {
		for (KeyframeType mode : values()) {
			if (s.equalsIgnoreCase(mode.toString())) {
				return mode;
			}
		}
		return null;
	}
}