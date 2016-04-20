package game.editor;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import game.states.fight.animation.Animation;
import game.util.Position;

public class AnimationEditor {
	
	private static JComboBox<String> animationSelector;
	private static JButton newAnimation;
	private static JButton copyAnimation;
	private static JTextField animationName;
	private static JSpinner ecbTopLeftX;
	private static JSpinner ecbTopLeftY;
	private static JSpinner ecbBottomRightX;
	private static JSpinner ecbBottomRightY;
	private static JSpinner centerY;
	
	static Animation currentAnimation;
	static int currentFrame;
	
	public static void initAnimationViewPanel() {
		final Position animLoc = new Position(0.5f, 0f);
		Editor.camera.setSpeed(0);
		Editor.animationViewPanel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				if (currentAnimation != null) {
					currentAnimation.draw(animLoc, Editor.fighter, Editor.fighter.getSkeleton(), (Graphics2D) g,
							Editor.camera, Editor.stage);
				}
			}
		};
	}
	
	public static void initAnimationsPanel() {
		Editor.animationsPanel = new JPanel(new GridLayout(0, 2));
		animationSelector = new JComboBox<>();
		newAnimation = new JButton("New");
		copyAnimation = new JButton("Copy");
		animationName = new JTextField();
		JPanel position = new JPanel(new GridLayout(0, 2));
		ecbTopLeftX = new JSpinner(new SpinnerNumberModel(0, -2, 2, 0.05));
		ecbTopLeftY = new JSpinner(new SpinnerNumberModel(1, -2, 2, 0.05));
		ecbBottomRightX = new JSpinner(new SpinnerNumberModel(1, -2, 2, 0.05));
		ecbBottomRightY = new JSpinner(new SpinnerNumberModel(0, -2, 2, 0.05));
		position.add(ecbTopLeftX);
		position.add(ecbTopLeftY);
		position.add(ecbBottomRightX);
		position.add(ecbBottomRightY);
		centerY = new JSpinner(new SpinnerNumberModel(0.5, -2, 2, 0.05));
		
		Editor.animationsPanel.add(new JLabel("Animation:", SwingConstants.CENTER));
		Editor.animationsPanel.add(animationSelector);
		Editor.animationsPanel.add(newAnimation);
		Editor.animationsPanel.add(copyAnimation);
		Editor.animationsPanel.add(new JLabel("Animation Name:", SwingConstants.CENTER));
		Editor.animationsPanel.add(animationName);
		Editor.animationsPanel.add(new JLabel("ECB:", SwingConstants.CENTER));
		Editor.animationsPanel.add(position);
		Editor.animationsPanel.add(new JLabel("Center:", SwingConstants.CENTER));
		Editor.animationsPanel.add(centerY);
	}
	
}
