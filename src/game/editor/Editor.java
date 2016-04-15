package game.editor;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

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

import game.states.fight.Camera;
import game.states.fight.Fighter;
import game.states.fight.Stage;
import game.states.fight.fighter.Bone;
import game.util.Position;

public class Editor {

	static Fighter fighter;
	static Stage stage;
	static Camera camera;
	
	private static JPanel viewPort;
	static JPanel skeletonViewPanel;
	
	private static JPanel editPanel;
	private static JPanel animationsPanel;
	private static JPanel keyframePanel;
	private static JPanel collisionBoxPanel;
	static JPanel fighterPanel;
	
	
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
	
	static List<Integer> keysDown;
	
	public static class BoneListElement {
		
		public final static int EXPAND_WIDTH = 21;
		public Bone bone;
		public int width;
		public boolean hasChildren;
		
		public BoneListElement(Bone bone, int expandXOff, boolean hasChildren) {
			this.bone = bone;
			this.width = expandXOff + 7;
			this.hasChildren = hasChildren;
		}
	}

	public static void main(String args[]) {
		final JFrame frame = new JFrame();
		stage = new Stage() {
			@Override
			public double getWidth() {
				return 1f;
			}
			
		};
		camera = new Camera(stage) {
			@Override
			public int getScreenWidth() {
				return frame.getBounds().width / 4;
			}
			@Override
			public int getScreenHeight() {
				return frame.getBounds().height / 2;
			}
		};
		camera.setFocus(new Position(0.5f, 0.4f));
		keysDown = new ArrayList<>();
		frame.getContentPane().setLayout(new GridLayout(2, 1));
		initViewPortPanel();
		initEditPanel();
		frame.getContentPane().add(viewPort);
		frame.getContentPane().add(editPanel);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(1500, 750));
		frame.pack();
		frame.setVisible(true);
	}
	
	public static void initViewPortPanel() {
		viewPort = new JPanel(new GridLayout(0, 4));
		FighterEditor.initSkeletonViewPanel();
		viewPort.add(skeletonViewPanel);
	}
	
	public static void initEditPanel() {
		editPanel = new JPanel();
		editPanel.setLayout(new GridLayout(1, 4));
		FighterEditor.initFighterPanel();
		initAnimationsPanel();
		initKeyframePanel();
		collisionBoxPanel = new JPanel();
		editPanel.add(fighterPanel);
		editPanel.add(animationsPanel);
		editPanel.add(keyframePanel);
		editPanel.add(collisionBoxPanel);
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
	
	public static void initKeyframePanel() {
		keyframePanel = new JPanel(new GridLayout(0, 2));
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
		
		keyframePanel.add(new JLabel("Step:", SwingConstants.CENTER));
		keyframePanel.add(stepSelecter);
		keyframePanel.add(addRemove);
		keyframePanel.add(duplicateStep);
		keyframePanel.add(new JLabel("Sprite:", SwingConstants.CENTER));
		keyframePanel.add(setSprite);
		keyframePanel.add(new JLabel("Duration:", SwingConstants.CENTER));
		keyframePanel.add(stepDuration);
		keyframePanel.add(stepMovesSelf);
		keyframePanel.add(velocity);
	}

}
