package fighter.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;

import javax.swing.JButton;
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
	private static JSpinner fighterPositionX;
	private static JSpinner fighterPositionY;

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
		animationStepPanel = new JPanel();
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
		fighterPositionX = new JSpinner(new SpinnerNumberModel(1, -10, 10, 0.05));
		fighterPositionY = new JSpinner(new SpinnerNumberModel(1, -10, 10, 0.05));
		position.add(fighterPositionX);
		position.add(fighterPositionY);
		
		animationsPanel.add(new JLabel("Animation:", SwingConstants.CENTER));
		animationsPanel.add(animationSelector);
		animationsPanel.add(newAnimation);
		animationsPanel.add(deleteAnimation);
		animationsPanel.add(new JLabel("Animation Name:", SwingConstants.CENTER));
		animationsPanel.add(animationName);
		animationsPanel.add(new JLabel("Ground Point:", SwingConstants.CENTER));
		animationsPanel.add(position);
	}

}
