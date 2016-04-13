package fighter.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import fighter.states.fight.Fighter;

public class Editor {

	private static Fighter fighter;
	private static JPanel viewPort;
	private static JPanel editPanel;
	private static JPanel fighterPanel;
	private static JPanel animationsPanel;
	private static JPanel animationStepPanel;
	private static JPanel collisionBoxPanel;
	
	private static JButton loadFighter;
	private static JButton saveFighter;
	private static JTextField fighterName;
	private static JSpinner fighterHealth;
	
	private static JComboBox<String> animationSelector;
	private static JButton newAnimation;
	private static JButton deleteAnimation;
	private static JTextField animationName;
	private static JSpinner ecbTopLeftX;
	private static JSpinner ecbTopLeftY;
	private static JSpinner ecbBottomRightX;
	private static JSpinner ecbBottomRightY;
	private static JSpinner centerY;

	private static JSpinner stepSelecter;
	private static JButton newStep;
	private static JButton deleteStep;
	private static JButton duplicateStep;
	private static JButton setSprite;
	private static JSpinner stepDuration;
	private static JCheckBox stepMovesSelf;
	private static JSpinner stepVelocityX;
	private static JSpinner stepVelocityY;

	public static void main(String args[]) {
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new GridLayout(2, 1));
		viewPort = new JPanel() {
			
			@Override
			public void paint(Graphics g) {
				g.setColor(Color.RED);
				g.fillRect(0, 0, 1000, 1000);
				if (fighter != null) {
					fighter.draw((Graphics2D) g);
				}
			}
		};
		initEditPanel();
		frame.getContentPane().add(viewPort);
		frame.getContentPane().add(editPanel);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(1000, 500));
		frame.pack();
		frame.setVisible(true);
	}
	
	public static void initEditPanel() {
		editPanel = new JPanel();
		editPanel.setLayout(new GridLayout(1, 4));
		initFighterPanel();
		initAnimationsPanel();
		initAnimationStepsPanel();
		collisionBoxPanel = new JPanel();
		editPanel.add(fighterPanel);
		editPanel.add(animationsPanel);
		editPanel.add(animationStepPanel);
		editPanel.add(collisionBoxPanel);
	}
	
	public static void initFighterPanel() {
		fighterPanel = new JPanel(new GridLayout(0, 2));
		loadFighter = new JButton("Load");
		saveFighter = new JButton("Save");
		fighterName = new JTextField();
		fighterHealth = new JSpinner(new SpinnerNumberModel(1000, 1, 2000, 1));
		fighterPanel.add(loadFighter);
		fighterPanel.add(saveFighter);
		fighterPanel.add(new JLabel("Name:", SwingConstants.CENTER));
		fighterPanel.add(fighterName);
		fighterPanel.add(new JLabel("Health:", SwingConstants.CENTER));
		fighterPanel.add(fighterHealth);
	}
	
	public static void initAnimationsPanel() {
		animationsPanel = new JPanel(new GridLayout(0, 2));
		animationSelector = new JComboBox<>();
		newAnimation = new JButton("New");
		deleteAnimation = new JButton("Delete");
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
		
		animationsPanel.add(new JLabel("Animation:", SwingConstants.CENTER));
		animationsPanel.add(animationSelector);
		animationsPanel.add(newAnimation);
		animationsPanel.add(deleteAnimation);
		animationsPanel.add(new JLabel("Animation Name:", SwingConstants.CENTER));
		animationsPanel.add(animationName);
		animationsPanel.add(new JLabel("ECB:", SwingConstants.CENTER));
		animationsPanel.add(position);
		animationsPanel.add(new JLabel("Center:", SwingConstants.CENTER));
		animationsPanel.add(centerY);
	}
	
	public static void initAnimationStepsPanel() {
		animationStepPanel = new JPanel(new GridLayout(0, 2));
		stepSelecter = new JSpinner(new SpinnerNumberModel(0, 0, 1, 1));
		JPanel addRemove = new JPanel(new GridLayout(0, 2));
		newStep = new JButton("+");
		deleteStep = new JButton("-");
		addRemove.add(newStep);
		addRemove.add(deleteStep);
		duplicateStep = new JButton("Copy");
		setSprite = new JButton("Choose...");
		stepDuration = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
		stepMovesSelf = new JCheckBox("   Velocity:");
		JPanel velocity = new JPanel(new GridLayout(0, 2));
		stepVelocityX = new JSpinner(new SpinnerNumberModel(0, -100, 100, 0.05));
		stepVelocityY = new JSpinner(new SpinnerNumberModel(0, -100, 100, 0.05));
		velocity.add(stepVelocityX);
		velocity.add(stepVelocityY);
		
		animationStepPanel.add(new JLabel("Step:", SwingConstants.CENTER));
		animationStepPanel.add(stepSelecter);
		animationStepPanel.add(addRemove);
		animationStepPanel.add(duplicateStep);
		animationStepPanel.add(new JLabel("Sprite:", SwingConstants.CENTER));
		animationStepPanel.add(setSprite);
		animationStepPanel.add(new JLabel("Duration:", SwingConstants.CENTER));
		animationStepPanel.add(stepDuration);
		animationStepPanel.add(stepMovesSelf);
		animationStepPanel.add(velocity);
	}

}
