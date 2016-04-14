package game.input;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles different types of inputs and polls the sources
 * 
 * @author Fritz
 *
 */
public class InputHandler {
	
	/**
	 * List of all supported input devices
	 */
	private static List<InputSource> devices;
	
	/**
	 * Mouse input source
	 */
	private static Mouse mouse;
	
	/**
	 * Initializes variables
	 */
	static {
		devices = new ArrayList<>();
		mouse = new Mouse();
	}
	
	/**
	 * Polls all the input sources
	 */
	public static void poll() {
		for (InputSource source : devices) {
			source.poll();
		}
	}
}
