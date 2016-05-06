package game.states.fight;

import java.awt.Color;

public enum FightResult {

	WIN("W", Color.YELLOW, Color.ORANGE),
	DRAW("D", new Color(50, 205, 50), new Color(34, 139, 34)),
	TIME_OUT("T", new Color(135, 206, 250), new Color(70, 130, 180)),
	DOUBLE_KO("K", new Color(210, 105, 30), new Color(165, 42, 42));

	private String message;
	private Color color;
	private Color back;
	
	private FightResult(String message, Color color, Color back) {
		this.message = message;
		this.color = color;
		this.back = back;
	}
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @return the back
	 */
	public Color getBack() {
		return back;
	}
}
