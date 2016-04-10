package fighter.input;

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
	private InputType type;
	
	/**
	 * Constructs an input
	 * 
	 * @param delay - Delay in ticks since previous input
	 * @param type - Type of input
	 */
	public Input(long delay, InputType type) {
		this.delay = delay;
		this.type = type;
	}
}
