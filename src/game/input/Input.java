package game.input;

import java.util.List;

/**
 * An input from an InputSource
 * 
 * @author Fritz
 *
 */
public class Input {

	/**
	 * Ticks since previous input
	 */
	private long delay;
	
	/**
	 * Type of input
	 */
	private List<InputType> types;
	
	/**
	 * Constructs an input
	 * 
	 * @param delay - Delay in ticks since previous input
	 * @param type - Type of input
	 */
	public Input(long delay, InputType ... types) {
		this.delay = delay;
		for (InputType type : types) {
			this.types.add(type);
		}
	}
}
