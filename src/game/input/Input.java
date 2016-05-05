package game.input;

import java.util.List;

import game.Game;

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
	
	private boolean used;
	
	/**
	 * Constructs an input
	 * 
	 * @param delay - Delay in ticks since previous input
	 * @param type - Type of input
	 */
	public Input(long delay, List<InputType> inputTypes) {
		this.delay = delay;
		this.used = false;
		this.types = inputTypes;
	}
	
	public long getDelay() {
		return Game.tick - delay;
	}
	
	public List<InputType> getTypes() {
		return types;
	}
	
	public boolean hasBeenUsed() {
		return used;
	}
	
	public void setUsed() {
		used = true;
	}
}
