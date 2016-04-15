package game;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
	 * Whether to display hitboxes
	 */
	public static final boolean DEBUG = true;

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
	private static ScheduledExecutorService loopExecutor;
	
	/**
	 * The game loop, currently running 60 times per second
	 */
	private static Runnable gameLoop;
	
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
		gameLoop = new Runnable() {

			@Override
			public void run() {
				tick++;
				state.logic();
				window.repaint();
			}
			
		};
		loopExecutor = Executors.newScheduledThreadPool(1);
		loopExecutor.scheduleAtFixedRate(gameLoop, 0, 1000 / LOOP_SPEED, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Gets the window's height
	 * 
	 * @return - Window's height
	 */
	public static int getScreenHeight() {
		return window.getBounds().height;
	}
	
	/**
	 * Gets the window's width
	 * 
	 * @return - Window's width
	 */
	public static int getScreenWidth() {
		return window.getBounds().width;
	}
	
}
