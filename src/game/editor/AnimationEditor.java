package game.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import game.Game;
import game.states.fight.animation.Animation;
import game.states.fight.animation.SharedAnimation;
import game.util.Position;

public class AnimationEditor {

	private static JCheckBox play;
	private static JSpinner speed;
	private static JButton newAnimation;
	private static JButton copyAnimation;
	private static JButton newAnimationFrom;
	private static JSpinner frameToCopy;
	private static JTextField name;
	private static JComboBox<SharedAnimation> sharedAnimation;
	private static JSpinner currentFrame;
	private static JCheckBox loop;
	private static JCheckBox specialCancelable;
	private static Position animLoc = new Position(0.5f, 0f);

	static JComboBox<String> animationSelector;
	static Animation currentAnimation;
	static String oldName;
	
	public static void initAnimationViewPanel() {
		Editor.animationViewPanel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        g.setColor(Color.WHITE);
				g.fillRect(0, 0, 1000, 1000);
				g.setColor(Color.BLACK);
				g.drawRect(-1, 0, getWidth() - 1, getHeight() - 1);
				if (currentAnimation != null) {
					currentAnimation.draw(animLoc, Editor.fighter, Editor.fighter.getEditorSkeleton(0), (Graphics2D) g,
							Editor.camera, Editor.stage);
					currentFrame.setValue((int) currentAnimation.getCurrentFrame());
					g.drawString("Frame: " + currentAnimation.getCurrentFrame(), 5, 15);
					if (play.isSelected()) {
						currentAnimation.stepAnimation(Editor.fighter, Editor.fighter.getEditorSkeleton(0),
								Editor.camera.getSpeed());
					} else {
						currentAnimation.setFrame(Editor.fighter, Editor.fighter.getEditorSkeleton(0), 0);
					}
				}
			}
		};
		Runnable gameLoop = new Runnable() {

			@Override
			public void run() {
				Editor.animationViewPanel.repaint();
			}
			
		};
		ScheduledExecutorService loopExecutor = Executors.newScheduledThreadPool(1);
		loopExecutor.scheduleAtFixedRate(gameLoop, 0, 1000 / Game.LOOP_SPEED, TimeUnit.MILLISECONDS);
	}
	
	public static void initAnimationsPanel() {
		Editor.animationsPanel = new JPanel(new GridLayout(0, 2));
		play = new JCheckBox("Play: ");
		play.setHorizontalAlignment(SwingConstants.CENTER);
		play.setHorizontalTextPosition(SwingConstants.LEFT);
		JPanel speedPanel = new JPanel(new GridLayout(0, 2));
		speed = new JSpinner(new SpinnerNumberModel(1d, 0.1, 2d, 0.1));
		speedPanel.add(new JLabel("Speed:", SwingConstants.CENTER));
		speedPanel.add(speed);
		animationSelector = new JComboBox<>();
		animationSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentAnimation = Editor.fighter.getAnimation((String) animationSelector.getSelectedItem());
				updateFields();
				currentFrame.setValue(0);
				name.setText((String) animationSelector.getSelectedItem());
				currentAnimation.setFrame(Editor.fighter, Editor.fighter.getEditorSkeleton(0), 0);
			}
		});
		newAnimation = new JButton("New");
		newAnimation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentAnimation = Editor.fighter.newAnimation();
				updateFields();
			}
		});
		copyAnimation = new JButton("Copy");
		newAnimation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentAnimation = Editor.fighter.newAnimation(currentAnimation);
				updateFields();
			}
		});
		newAnimationFrom = new JButton("New from");
		newAnimationFrom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentAnimation = Editor.fighter.newAnimationFrom(currentAnimation, (int) frameToCopy.getValue());
				updateFields();
			}
		});
		frameToCopy = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
		JPanel namePanel = new JPanel(new GridLayout(0, 2));
		name = new JTextField();
		sharedAnimation = new JComboBox<>(SharedAnimation.values());
		namePanel.add(name);
		namePanel.add(sharedAnimation);
		currentFrame = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
		loop = new JCheckBox("Loop: ");
		loop.setHorizontalAlignment(SwingConstants.CENTER);
		loop.setHorizontalTextPosition(SwingConstants.LEFT);
		specialCancelable = new JCheckBox("Special Cancelable: ");
		specialCancelable.setHorizontalAlignment(SwingConstants.CENTER);
		specialCancelable.setHorizontalTextPosition(SwingConstants.LEFT);
		
		Editor.animationsPanel.add(play);
		Editor.animationsPanel.add(speedPanel);
		Editor.animationsPanel.add(newAnimation);
		Editor.animationsPanel.add(copyAnimation);
		Editor.animationsPanel.add(newAnimationFrom);
		Editor.animationsPanel.add(frameToCopy);
		Editor.animationsPanel.add(new JLabel("Animation:", SwingConstants.CENTER));
		Editor.animationsPanel.add(animationSelector);
		Editor.animationsPanel.add(new JLabel("Name:", SwingConstants.CENTER));
		Editor.animationsPanel.add(namePanel);
		Editor.animationsPanel.add(new JLabel("Current Frame:", SwingConstants.CENTER));
		Editor.animationsPanel.add(currentFrame);
		Editor.animationsPanel.add(loop);
		Editor.animationsPanel.add(specialCancelable);
	}
	
	public static void updateFields() {
		currentAnimation.updateUIFields(loop, specialCancelable);
	}
	
}
