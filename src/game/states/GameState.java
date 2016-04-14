package game.states;

import java.awt.Graphics2D;

/**
 * Abstract class that is used to determine the behavior of the game
 * 
 * @author Fritz
 *
 */
public abstract class GameState {
	
	/**
	 * Draws the state in the window
	 * 
	 * @param g - Graphics2D object used to draw
	 */
	public abstract void draw(Graphics2D g);
	
	/**
	 * Logic step of the game loop for the state
	 */
	public abstract void logic();
}
