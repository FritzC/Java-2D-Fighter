package game.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import game.Game;
import game.states.fight.animation.Animation;
import game.states.fight.animation.SharedAnimation;
import game.util.Position;

public class AnimationEditor {

	private static JSpinner speed;
	private static JButton newAnimation;
	private static JButton copyAnimation;
	private static JButton newAnimationFrom;
	private static JButton deleteAnimation;
	private static JTextField name;
	private static JComboBox<SharedAnimation> sharedAnimation;
	private static JSpinner currentFrame;
	private static JCheckBox loop;
	private static JCheckBox specialCancelable;
	private static double frame;

	static boolean updateFrame;
	static boolean updateFields;
	static JCheckBox play;
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
				int groundY = Editor.camera.getScreenY(new Position(0f, 0f));
				g.drawLine(0, groundY, getWidth() - 1, groundY);
				if (currentAnimation != null) {
					if (play.isSelected()) {
						currentAnimation.setFrame(Editor.fighter, Editor.fighter.getEditorSkeleton(0), frame);
						currentAnimation.stepAnimation(Editor.fighter, Editor.fighter.getEditorSkeleton(0),
								Editor.camera.getSpeed());
						frame = currentAnimation.getCurrentFrame();
						updateFrame = true;
						currentFrame.setValue((int) frame);
						updateFrame = false;
					} else {
						frame = (int) currentFrame.getValue();
						currentAnimation.setFrame(Editor.fighter, Editor.fighter.getEditorSkeleton(0), (int) currentFrame.getValue());
					}
					currentAnimation.draw(Editor.defaultLoc, Editor.fighter, Editor.fighter.getEditorSkeleton(0), (Graphics2D) g,
							Editor.camera, Editor.stage, true);
					g.setColor(Color.BLACK);
					g.drawString("Frame: " + Math.round(currentAnimation.getCurrentFrame() * 100d) / 100d, 5, 15);
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
		speed.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Editor.camera.setSpeed((double) speed.getValue());
			}
		});
		speedPanel.add(new JLabel("Speed:", SwingConstants.CENTER));
		speedPanel.add(speed);
		animationSelector = new JComboBox<>();
		animationSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!updateFields && animationSelector.getSelectedItem() != null) {
					currentAnimation = Editor.fighter.getAnimation((String) animationSelector.getSelectedItem());
					updateFields();
					currentFrame.setValue(0);
					currentAnimation.setFrame(Editor.fighter, Editor.fighter.getEditorSkeleton(0), 0);
					KeyframeEditor.updateKeyframeTable();
					CollisionEditor.updateTable();
					KeyframeEditor.currentKeyframe = null;
				}
			}
		});
		newAnimation = new JButton("New");
		newAnimation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String newAnim = Editor.fighter.newAnimation();
				updateFields = true;
				Editor.fighter.updateUIAnimationList(animationSelector, false);
				Editor.fighter.updateUIAnimationList(CollisionEditor.triggerAnim, true);
				sharedAnimation.setSelectedIndex(0);
				updateFields = false;
				animationSelector.setSelectedItem(newAnim);
			}
		});
		copyAnimation = new JButton("Clone");
		copyAnimation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String newAnim = Editor.fighter.newAnimation(currentAnimation);
				updateFields = true;
				Editor.fighter.updateUIAnimationList(animationSelector, false);
				Editor.fighter.updateUIAnimationList(CollisionEditor.triggerAnim, true);
				updateNameComboBox();
				updateFields = false;
				animationSelector.setSelectedItem(newAnim);
			}
		});
		newAnimationFrom = new JButton("New from current frame");
		newAnimationFrom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String newAnim = Editor.fighter.newAnimationFrom(currentAnimation, (int) currentFrame.getValue());
				updateFields = true;
				Editor.fighter.updateUIAnimationList(animationSelector, false);
				Editor.fighter.updateUIAnimationList(CollisionEditor.triggerAnim, true);
				sharedAnimation.setSelectedIndex(0);
				updateFields = false;
				animationSelector.setSelectedItem(newAnim);
			}
		});
		deleteAnimation = new JButton("Delete");
		deleteAnimation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(Editor.frame, "Are you sure you want to delete this?", "", 
                        JOptionPane.YES_NO_OPTION) == 0) {
					Editor.fighter.getAnimations().remove(currentAnimation);
					(new File(Editor.fighterDirectory.getAbsolutePath() + "/animations/" + currentAnimation.getName() + ".json")).delete();
					currentAnimation = null;
					KeyframeEditor.currentKeyframe = null;
					CollisionEditor.selectedCollision = null;
					KeyframeEditor.updateKeyframeTable();
					CollisionEditor.updateTable();
					updateFields = true;
					Editor.fighter.updateUIAnimationList(animationSelector, false);
					Editor.fighter.updateUIAnimationList(CollisionEditor.triggerAnim, true);
					updateFields = false;
					if (animationSelector.getItemCount() > 0) {
						animationSelector.setSelectedIndex(0);
					}
				}
			}
		});
		JPanel namePanel = new JPanel(new GridLayout(0, 2));
		name = new JTextField();
		name.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				if (!updateFields) {
					updateFields = true;
					currentAnimation.setName(name.getText());
					Editor.fighter.updateUIAnimationList(animationSelector, false);
					Editor.fighter.updateUIAnimationList(CollisionEditor.triggerAnim, true);
					animationSelector.setSelectedItem(name.getText());
					updateNameComboBox();
					updateFields = false;
				}
			}

			public void removeUpdate(DocumentEvent e) {
				if (!updateFields) {
					updateFields = true;
					currentAnimation.setName(name.getText());
					Editor.fighter.updateUIAnimationList(animationSelector, false);
					Editor.fighter.updateUIAnimationList(CollisionEditor.triggerAnim, true);
					animationSelector.setSelectedItem(name.getText());
					updateNameComboBox();
					updateFields = false;
				}
			}

			public void insertUpdate(DocumentEvent e) {
				if (!updateFields) {
					updateFields = true;
					currentAnimation.setName(name.getText());
					Editor.fighter.updateUIAnimationList(animationSelector, false);
					Editor.fighter.updateUIAnimationList(CollisionEditor.triggerAnim, true);
					animationSelector.setSelectedItem(name.getText());
					updateNameComboBox();
					updateFields = false;
				}
			}
		});
		sharedAnimation = new JComboBox<>(SharedAnimation.values());
		sharedAnimation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!updateFields && (SharedAnimation.forString(name.getText()) != null
						&& sharedAnimation.getSelectedItem() == SharedAnimation.UNSHARED
						|| sharedAnimation.getSelectedItem() != SharedAnimation.UNSHARED)) {
					name.setText(sharedAnimation.getSelectedItem().toString().toLowerCase());
				}
				name.setEnabled(sharedAnimation.getSelectedItem() == SharedAnimation.UNSHARED);
			}
		});
		namePanel.add(name);
		namePanel.add(sharedAnimation);
		currentFrame = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
		currentFrame.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!updateFrame) {
					play.setSelected(false);
				}
			}
		});
		loop = new JCheckBox("Loop: ");
		loop.setHorizontalAlignment(SwingConstants.CENTER);
		loop.setHorizontalTextPosition(SwingConstants.LEFT);
		loop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateValues();
			}
		});
		specialCancelable = new JCheckBox("Special Cancelable: ");
		specialCancelable.setHorizontalAlignment(SwingConstants.CENTER);
		specialCancelable.setHorizontalTextPosition(SwingConstants.LEFT);
		specialCancelable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateValues();
			}
		});
		
		Editor.animationsPanel.add(play);
		Editor.animationsPanel.add(speedPanel);
		Editor.animationsPanel.add(new JLabel("Current Frame:", SwingConstants.CENTER));
		Editor.animationsPanel.add(currentFrame);
		Editor.animationsPanel.add(newAnimation);
		Editor.animationsPanel.add(copyAnimation);
		Editor.animationsPanel.add(newAnimationFrom);
		Editor.animationsPanel.add(deleteAnimation);
		Editor.animationsPanel.add(new JLabel("Animation:", SwingConstants.CENTER));
		Editor.animationsPanel.add(animationSelector);
		Editor.animationsPanel.add(new JLabel("Name:", SwingConstants.CENTER));
		Editor.animationsPanel.add(namePanel);
		Editor.animationsPanel.add(loop);
		Editor.animationsPanel.add(specialCancelable);
	}
	
	public static void updateFields() {
		if (!updateFields && currentAnimation != null) {
			currentAnimation.updateUIFields(name, loop, specialCancelable);
			updateNameComboBox();
		}
	}
	
	public static void updateValues() {
		currentAnimation.updateValues(name.getText(), loop.isSelected(), specialCancelable.isSelected());
	}
	
	public static void updateNameComboBox() {
		if (SharedAnimation.forString(name.getText()) != null) {
			sharedAnimation.setSelectedItem(SharedAnimation.forString(name.getText()));
		} else {
			sharedAnimation.setSelectedIndex(0);
		}
	}
	
}
