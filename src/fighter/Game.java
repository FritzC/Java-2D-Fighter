package fighter;

import java.util.concurrent.ScheduledExecutorService;

import fighter.states.GameState;

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
	private GameState state;
	
	/**
	 * The game desktop window
	 */
	private Window window;
	
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
		
	}
	
}
