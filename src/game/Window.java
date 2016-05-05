package game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import game.input.KeyStates;
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
	
	JPanel drawPanel = new JPanel() {
		
		@Override
		public void paintComponent(Graphics g) {
			state.draw((Graphics2D) g);
		}
	};

	/**
	 * Constructs the Window and binds input listeners
	 */
	public Window() {
		addKeyListener(new KeyStates());
		add(drawPanel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(1000, 500));
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void setState(GameState state) {
		this.state = state;
	}
}
