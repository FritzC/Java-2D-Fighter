package game.input;

import java.util.ArrayList;
import java.util.List;

import game.Game;

/**
 * An input from an InputSource
 * 
 * @author Fritz
 *
 */
public class StickInput {

	/**
	 * Ticks since previous input
	 */
	private long delay;
	
	/**
	 * Type of input
	 */
	private List<StickInputType> types;
	
	private boolean used;
	
	/**
	 * Constructs an input
	 * 
	 * @param delay - Delay in ticks since previous input
	 * @param type - Type of input
	 */
	public StickInput(long delay, List<StickInputType> inputTypes) {
		this.delay = delay;
		this.used = false;
		this.types = inputTypes;
	}
	
	public StickInput(StickInput copy) {
		this.delay = copy.delay;
		this.used = copy.used;
		this.types = new ArrayList<>();
		for (StickInputType type : copy.types) {
			this.types.add(type);
		}
	}
	
	public long getAge() {
		return Game.tick - delay;
	}
	
	public List<StickInputType> getValues() {
		return types;
	}
	
	public boolean hasBeenUsed() {
		return used;
	}
	
	public void setUsed() {
		used = true;
	}
}
