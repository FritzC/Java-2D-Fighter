package game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;

import game.states.GameState;

/**
 * Desktop window where the game is drawn
 * 
 * @author Fritz
 *
 */
public class Window extends JFrame {
	
	/**
	 * Current state being drawn
	 */
	private GameState state;

	/**
	 * Constructs the Window and binds input listeners
	 */
	public Window() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(1000, 500));
		setLocationRelativeTo(null);
		pack();
		setVisible(true);
	}
	
	@Override
	public void paint(Graphics g) {
		state.draw((Graphics2D) g);
	}
	
	public void setState(GameState state) {
		this.state = state;
	}
}
