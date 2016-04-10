package fighter.input;

import java.util.ArrayList;
import java.util.List;

/**
 * An input source (keyboard, controller, etc)
 * 
 * @author Fritz
 *
 */
public abstract class InputSource {
	
	/**
	 * List of all previous inputs
	 */
	private List<Input> inputs;
	
	/**
	 * Whether there is a new input since last checked
	 */
	private boolean newInput;
	
	/**
	 * Initializes the input list
	 */
	public InputSource() {
		inputs = new ArrayList<>();
	}

	/**
	 * Polls the device
	 */
	public abstract void poll();
	
	/**
	 * Gets the latest input
	 * 
	 * @return - The latest input
	 */
	public Input getLastInput() {
		newInput = false;
		return inputs.get(inputs.size() - 1);
	}
	
	/**
	 * Gets whether there is a new input
	 * 
	 * @return - Whether there is a new input
	 */
	public boolean hasNewInput() {
		return newInput;
	}
}
