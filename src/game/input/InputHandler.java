package game.input;

import java.util.ArrayList;
import java.util.List;

import game.input.sources.DummySource;
import game.input.sources.KeyboardSource;
import game.input.sources.XBoxControllerSource;
import net.java.games.input.Controller;
import net.java.games.input.Controller.Type;
import net.java.games.input.ControllerEnvironment;

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
	public static void initialize() {
		devices = new ArrayList<>();
		mouse = new Mouse();
		devices.add(new KeyboardSource());
		Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();

		for (int i = 0; i < ca.length; i++) {
			if (ca[i].getName().toLowerCase().contains("xbox")) {
				devices.add(new XBoxControllerSource(ca[i]));
			}
			/*if (ca[i].getType() == Type.GAMEPAD) {
				devices.add(new XBoxControllerSource(ca[i]));
			}*/
		}
		devices.add(new DummySource());
	}

	/**
	 * Polls all the input sources
	 */
	public static void poll() {
		for (InputSource source : devices) {
			source.poll();
		}
	}
	
	public static InputSource getSource(int i) {
		return devices.get(i);
	}
}
