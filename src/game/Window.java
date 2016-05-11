package game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.io.File;

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
	
	public static Font coggersFont;
	
	/**
	 * Current state being drawn
	 */
	private GameState state;
	
	private JPanel drawPanel = new JPanel() {
		
		@Override
		public void paintComponent(Graphics g) {
			state.draw((Graphics2D) g);
		}
	};

	/**
	 * Constructs the Window and binds input listeners
	 */
	public Window() {
		loadAssets();
		addKeyListener(new KeyStates());
		add(drawPanel);
		setTitle("Stick Fighter");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(1000, 500));
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public JPanel getDrawPanel() {
		return drawPanel;
	}
	
	public void setState(GameState state) {
		this.state = state;
	}
	
	public void loadAssets() {
		try {
			coggersFont = Font.createFont(Font.TRUETYPE_FONT, new File("assets/CoggersTariqa.ttf")).deriveFont(30f);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(coggersFont);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void drawCenteredString(Graphics2D g2, String line, int x, int y, Paint paint, int strokeSize) {
		Graphics2D g = (Graphics2D) g2.create();
		x = x - g.getFontMetrics().stringWidth(line) / 2;
		y = y + g.getFontMetrics().getAscent() / 2;
		if (strokeSize > 0) {
			GlyphVector gv = g.getFont().createGlyphVector(g.getFontRenderContext(), line);
			Shape shape = gv.getOutline();
			g.translate(x, y);
			g.setColor(Color.BLACK);
			g.setStroke(new BasicStroke((float) (strokeSize) * 2));
			g.draw(shape);
			g.setPaint(paint);
			g.fill(shape);
		}
	}
}
