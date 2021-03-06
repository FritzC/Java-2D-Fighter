package game;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import game.input.InputHandler;
import game.states.FightState;
import game.states.GameState;
import game.states.fight.Stage;
import game.states.fight.fighters.BasicFighter;
import game.util.Position;

/**
 * Main class of the game
 * 
 * @author Fritz
 *
 */
public class Game {
	
	public final static int INPUT_BUFFER = 5;
	
	/**
	 * Number of times the game loop is ran per second
	 */
	public final static int LOOP_SPEED = 60;

	/**
	 * Whether to display hitboxes
	 */
	public static final boolean DEBUG = false;

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
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		try {
			System.setProperty("java.library.path", "./dlls");
			Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputHandler.initialize();
		state = new FightState(new BasicFighter(new Position(1, 0)),
				new BasicFighter(new Position(2, 0)), new Stage(), 3, 99);
		window = new Window();
		window.setState(state);
		gameLoop = new Runnable() {

			@Override
			public void run() {
				tick++;
				InputHandler.poll();
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
		return window.getDrawPanel().getHeight();
	}
	
	/**
	 * Gets the window's width
	 * 
	 * @return - Window's width
	 */
	public static int getScreenWidth() {
		return window.getDrawPanel().getWidth();
	}
	
}
