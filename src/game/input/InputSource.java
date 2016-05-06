package game.input;

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
	protected List<Input> inputs;
	
	/**
	 * Whether there is a new input since last checked
	 */
	private boolean newInput;
	
	/**
	 * Initializes the input list
	 */
	public InputSource() {
		inputs = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			inputs.add(new Input(0, new ArrayList<>()));
		}
	}

	/**
	 * Polls the device
	 */
	public abstract void poll();
	
	/**
	 * Gets the latest inputs
	 * 
	 * @return - The latest inputs
	 */
	public Input getLastInput() {
		newInput = false;
		if (inputs.size() == 0) {
			return null;
		}
		return inputs.get(inputs.size() - 1);
	}
	
	public Input getPreviousInput(int idx) {
		if (idx > inputs.size()) {
			idx = inputs.size();
		}
		return inputs.get(inputs.size() - 1 - idx);
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
