package game;

import java.util.concurrent.ScheduledExecutorService;

import game.states.FightState;
import game.states.GameState;
import game.states.fight.Stage;

/**
 * Main class of the game
 * 
 * @author Fritz
 *
 */
public class Game {
	
	/**
	 * Number of times the game loop is ran per second
	 */
	private final static int LOOP_SPEED = 60;

	/**
	 * The current GameState
	 */
	private static GameState state;
	
	/**
	 * The game desktop window
	 */
	private static Window window;
	
	/**
	 * Executor of the game loop
	 */
	private ScheduledExecutorService loopExecutor;
	
	/**
	 * The game loop, currently running 60 times per second
	 */
	private Runnable gameLoop;
	
	/**
	 * Current game tick, incremented each loop
	 */
	public static long tick;
	
	
	/**
	 * Main method, runs on startup
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		state = new FightState(null, null, new Stage());
		window = new Window();
		window.setState(state);
	}
	
	public static int getScreenHeight() {
		return window.getBounds().height;
	}
	
	public static int getScreenWidth() {
		return window.getBounds().width;
	}
	
}
