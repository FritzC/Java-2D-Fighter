package game.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyStates implements KeyListener {

	private static boolean[] keys = new boolean[222];

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent arg0) {}

	public static boolean getState(int keyCode) {
		return keys[keyCode];
	}
	
	public static void reset(int keyCode) {
		keys[keyCode] = false;
	}
}
