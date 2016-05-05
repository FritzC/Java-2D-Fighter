package game.states.fight.fighter;

/**
 * Different modes for drawing a FighterComponent
 * 
 * @author Fritz
 *
 */
public enum DrawMode {

	SPRITE,
	LINE,
	CIRCLE;
	
	/**
	 * Gets a value for a string
	 * 
	 * @param s - Input string
	 * @return - DrawMode for string
	 */
	public static DrawMode forString(String s) {
		for (DrawMode mode : values()) {
			if (s.equalsIgnoreCase(mode.toString())) {
				return mode;
			}
		}
		return LINE;
	}
	
}
