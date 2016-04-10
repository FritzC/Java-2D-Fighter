package fighter.input;

/**
 * Handles and contains mouse inputs
 * 
 * @author Fritz
 *
 */
public class Mouse {

	/**
	 * Current X coordinate of the mouse in the game window
	 */
	private int x;
	
	/**
	 * Current Y coordinate of the mouse in the game window
	 */
	private int y;

	/**
	 * X Coordinate of the last mouse click
	 */
	private int clickX;
	
	/**
	 * Y Coordinate of the last mouse click
	 */
	private int clickY;
	
	/**
	 * The button used in the last mouse click (0 ~ Left, 1 ~ Middle, 2 ~ Right)
	 */
	private int clickButton;
	
	/**
	 * Update mouse values
	 */
	public void poll() {
		
	}
}
