package game.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import game.Game;
import game.states.fight.animation.collisions.CollisionBox;
import game.states.fight.animation.collisions.ECB;
import game.states.fight.animation.collisions.HitBox;
import game.states.fight.animation.collisions.HitBoxType;
import game.states.fight.animation.collisions.HurtBox;
import game.util.Box;
import game.util.Position;
import game.util.Vector;

public class CollisionEditor {
	
	private static JSpinner currentFrame;
	private static JTable collisionBoxes;
	private static TableModel model;
	private static JTabbedPane tabbedPane;
	private static double frame;
	private static boolean updateData;
	private static String[] tabNames = {"ECBs", "Hurtboxes", "Hitboxes"};

	static Position start;
	static Position end;
	static JComboBox<String> grabWith;
	static JComboBox<String> triggerAnim;
	static CollisionBox selectedCollision;
	
	public static void initCollisionViewPanel() {
		Editor.collisionViewPanel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, 1000, 1000);
				g.setColor(Color.BLACK);
				g.drawRect(-1, 0, getWidth() - 1, getHeight() - 1);
				int groundY = Editor.camera.getScreenY(new Position(0f, 0f));
				g.drawLine(0, groundY, getWidth() - 1, groundY);
				if (Editor.fighter != null && selectedCollision != null && AnimationEditor.currentAnimation != null) {
					AnimationEditor.currentAnimation.setFrame(Editor.fighter, Editor.fighter.getEditorSkeleton(2),
							(int) currentFrame.getValue());
					frame = AnimationEditor.currentAnimation.getCurrentFrame();
					AnimationEditor.currentAnimation.draw(Editor.defaultLoc, Editor.fighter,
							Editor.fighter.getEditorSkeleton(2), (Graphics2D) g, Editor.camera, Editor.stage, true,
							selectedCollision);
				}
			}
		};
		Runnable gameLoop = new Runnable() {
			@Override
			public void run() {
				Editor.collisionViewPanel.repaint();
			}
		};
		ScheduledExecutorService loopExecutor = Executors.newScheduledThreadPool(1);
		loopExecutor.scheduleAtFixedRate(gameLoop, 0, 1000 / Game.LOOP_SPEED, TimeUnit.MILLISECONDS);
		Editor.collisionViewPanel.setFocusable(true);
		Editor.collisionViewPanel.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (selectedCollision != null) {
					int relativeX = e.getX() - Editor.camera.getScreenX(Editor.defaultLoc);
					int relativeY = Editor.camera.getScreenY(Editor.defaultLoc) - e.getY();
					end = new Position(Editor.camera.toGameDistance(relativeX), Editor.camera.toGameDistance(relativeY));
					if (end.getX() < start.getX()) {
						end.setX(start.getX());
					}
					if (end.getY() > start.getY()) {
						end.setY(start.getY());
					}
					if (selectedCollision != null) {
						selectedCollision.getBottomRight().setX((int) (end.getX() * 1000) / 1000d);
						selectedCollision.getBottomRight().setY((int) (end.getY() * 1000) / 1000d);
					}
					updateTable();
				}
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {}
		});
		Editor.collisionViewPanel.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				if (selectedCollision != null) {
					int relativeX = e.getX() - Editor.camera.getScreenX(Editor.defaultLoc);
					int relativeY = Editor.camera.getScreenY(Editor.defaultLoc) - e.getY();
					start = new Position(Editor.camera.toGameDistance(relativeX),
							Editor.camera.toGameDistance(relativeY));
					end = new Position(Editor.camera.toGameDistance(relativeX),
							Editor.camera.toGameDistance(relativeY));
					if (selectedCollision != null) {
						selectedCollision.getTopLeft().setX((int) (start.getX() * 1000) / 1000d);
						selectedCollision.getTopLeft().setY((int) (start.getY() * 1000) / 1000d);
						selectedCollision.getBottomRight().setX((int) (start.getX() * 1000) / 1000d);
						selectedCollision.getBottomRight().setY((int) (start.getY() * 1000) / 1000d);
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (selectedCollision != null) {
					int relativeX = e.getX() - Editor.camera.getScreenX(Editor.defaultLoc);
					int relativeY = Editor.camera.getScreenY(Editor.defaultLoc) - e.getY();
					end = new Position(Editor.camera.toGameDistance(relativeX),
							Editor.camera.toGameDistance(relativeY));
					if (end.getX() < start.getX()) {
						end.setX(start.getX());
					}
					if (end.getY() > start.getY()) {
						end.setY(start.getY());
					}
					if (selectedCollision != null) {
						selectedCollision.getBottomRight().setX((int) (end.getX() * 1000) / 1000d);
						selectedCollision.getBottomRight().setY((int) (end.getY() * 1000) / 1000d);
					}
					updateTable();
				}
			}
		});
	}

	public static void initCollisionPanel() {
		Editor.collisionPanel = new JPanel(new BorderLayout());
		JPanel north = new JPanel(new GridLayout(0, 2));
		currentFrame = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
		north.add(new JLabel("Current Frame:", SwingConstants.CENTER));
		north.add(currentFrame);
		JPanel center = new JPanel(new GridLayout(0, 1));

		JPanel tablePanel = new JPanel(new BorderLayout());
		model = new DefaultTableModel(new String[] {"Start", "End", "Top X", "Top Y", "Bottom X", "Bottom Y"}, 0);
		collisionBoxes = new JTable(model);
		collisionBoxes.getTableHeader().setReorderingAllowed(false);
		collisionBoxes.setSelectionMode(0);
		collisionBoxes.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		JSpinner startFrame = new JSpinner(new SpinnerNumberModel(1, 0, 10000, 1));
		startFrame.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!updateData && selectedCollision != null) {
					selectedCollision.setStartFrame((int) startFrame.getValue());
					currentFrame.setValue(startFrame.getValue());
				}
			}
		});
		collisionBoxes.getColumnModel().getColumn(0).setCellEditor(new SpinnerEditor(startFrame));
		JSpinner endFrame = new JSpinner(new SpinnerNumberModel(1, 0, 10000, 1));
		endFrame.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!updateData && selectedCollision != null) {
					selectedCollision.setEndFrame((int) endFrame.getValue());
				}
			}
		});
		collisionBoxes.getColumnModel().getColumn(1).setCellEditor(new SpinnerEditor(endFrame));
		JSpinner topX = new JSpinner(new SpinnerNumberModel(0d, -2d, 2d, 0.01));
		topX.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!updateData && selectedCollision != null) {
					selectedCollision.getTopLeft().setX((double) topX.getValue());
				}
			}
		});
		collisionBoxes.getColumnModel().getColumn(2).setCellEditor(new SpinnerEditor(topX));
		JSpinner topY = new JSpinner(new SpinnerNumberModel(0d, -2d, 2d, 0.01));
		topY.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!updateData && selectedCollision != null) {
					selectedCollision.getTopLeft().setY((double) topY.getValue());
				}
			}
		});
		collisionBoxes.getColumnModel().getColumn(3).setCellEditor(new SpinnerEditor(topY));
		JSpinner botX = new JSpinner(new SpinnerNumberModel(0d, -2d, 2d, 0.01));
		botX.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!updateData && selectedCollision != null) {
					selectedCollision.getBottomRight().setX((double) botX.getValue());
				}
			}
		});
		collisionBoxes.getColumnModel().getColumn(4).setCellEditor(new SpinnerEditor(botX));
		JSpinner botY = new JSpinner(new SpinnerNumberModel(0d, -2d, 2d, 0.01));
		botY.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!updateData && selectedCollision != null) {
					selectedCollision.getBottomRight().setY((double) botY.getValue());
				}
			}
		});
		collisionBoxes.getColumnModel().getColumn(5).setCellEditor(new SpinnerEditor(botY));
		JPanel tableButtons = new JPanel(new GridLayout(0, 3));
		JButton add = new JButton("+");
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int start = (int) currentFrame.getValue();
				if (AnimationEditor.currentAnimation != null) {
					switch (tabbedPane.getSelectedIndex()) {
						case 0:
							selectedCollision = new ECB(start, start + 1,
									new Box(new Position(-0.2, 0.4), new Position(0.2, 0)));
							AnimationEditor.currentAnimation.getECBs().add((ECB) selectedCollision);
							break;
						case 1:
							selectedCollision = new HurtBox(start, start + 1,
									new Box(new Position(-0.2, 0.4), new Position(0.2, 0)));
							AnimationEditor.currentAnimation.getHurtboxes().add((HurtBox) selectedCollision);
							break;
						case 2:
							selectedCollision = new HitBox(start, start + 1, "",
									new Box(new Position(-0.05, 0.2), new Position(0.05, 0.1)), 0, 1, 1, 0.05,
									new Vector(0, 0), false, HitBoxType.MID, false, "", "", "");
							AnimationEditor.currentAnimation.getHitboxes().add((HitBox) selectedCollision);
							break;
					}
					updateTable();
				}
			}
		});
		JButton delete = new JButton("-");
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (selectedCollision != null) {
					switch (tabbedPane.getSelectedIndex()) {
						case 0:
							AnimationEditor.currentAnimation.getECBs().remove((ECB) selectedCollision);
							break;
						case 1:
							AnimationEditor.currentAnimation.getHurtboxes().remove((HurtBox) selectedCollision);
							break;
						case 2:
							AnimationEditor.currentAnimation.getHitboxes().remove((HitBox) selectedCollision);
							break;
					}
					updateTable();
				}
			}
		});
		JButton copy = new JButton("Clone");
		copy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (selectedCollision != null) {
					switch (tabbedPane.getSelectedIndex()) {
						case 0:
							selectedCollision = new ECB((ECB) selectedCollision);
							AnimationEditor.currentAnimation.getECBs().add((ECB) selectedCollision);
							break;
						case 1:
							selectedCollision = new HurtBox((HurtBox) selectedCollision);
							AnimationEditor.currentAnimation.getHurtboxes().add((HurtBox) selectedCollision);
							break;
						case 2:
							selectedCollision = new HitBox((HitBox) selectedCollision);
							AnimationEditor.currentAnimation.getHitboxes().add((HitBox) selectedCollision);
							break;
					}
					updateTable();
				}
			}
		});
		tableButtons.add(add);
		tableButtons.add(delete);
		tableButtons.add(copy);
		tablePanel.add(new JScrollPane(collisionBoxes), BorderLayout.CENTER);
		tablePanel.add(tableButtons, BorderLayout.SOUTH);
		
		tabbedPane = new JTabbedPane();
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				if (Editor.fighter != null && AnimationEditor.currentAnimation != null) {
					updateTable();
				}
			}
		});
		JPanel ecbs = new JPanel(new GridLayout(0, 2));
		JPanel hurtboxes = new JPanel(new GridLayout(0, 2));
		JPanel hitboxes = new JPanel(new GridLayout(0, 2));
		JTextField group = new JTextField();
		group.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				if (!updateData && tabbedPane.getSelectedIndex() == 2 && selectedCollision != null) {
					((HitBox) selectedCollision).setGroup(group.getText());
				}
			}

			public void removeUpdate(DocumentEvent e) {
				if (!updateData && tabbedPane.getSelectedIndex() == 2 && selectedCollision != null) {
					((HitBox) selectedCollision).setGroup(group.getText());
				}
			}

			public void insertUpdate(DocumentEvent e) {
				if (!updateData && tabbedPane.getSelectedIndex() == 2 && selectedCollision != null) {
					((HitBox) selectedCollision).setGroup(group.getText());
				}
			}
		});
		JSpinner damage = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
		damage.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!updateData && tabbedPane.getSelectedIndex() == 2 && selectedCollision != null) {
					((HitBox) selectedCollision).setDamage((int) damage.getValue());
				}
			}
		});
		JSpinner hitstun = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
		hitstun.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!updateData && tabbedPane.getSelectedIndex() == 2 && selectedCollision != null) {
					((HitBox) selectedCollision).setHitstun((int) hitstun.getValue());
				}
			}
		});
		JSpinner blockstun = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
		blockstun.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!updateData && tabbedPane.getSelectedIndex() == 2 && selectedCollision != null) {
					((HitBox) selectedCollision).setBlockstun((int) blockstun.getValue());
				}
			}
		});
		JSpinner pushback = new JSpinner(new SpinnerNumberModel(0d, -2d, 2d, 0.01));
		pushback.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!updateData && tabbedPane.getSelectedIndex() == 2 && selectedCollision != null) {
					((HitBox) selectedCollision).setPushback((double) pushback.getValue());
				}
			}
		});
		JPanel launchVelocity = new JPanel(new GridLayout(0, 2));
		JSpinner launchX = new JSpinner(new SpinnerNumberModel(0d, -2d, 2d, 0.01));
		launchX.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!updateData && tabbedPane.getSelectedIndex() == 2 && selectedCollision != null) {
					((HitBox) selectedCollision).setLaunchX((double) launchX.getValue());
				}
			}
		});
		JSpinner launchY = new JSpinner(new SpinnerNumberModel(0d, -2d, 2d, 0.01));
		launchY.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!updateData && tabbedPane.getSelectedIndex() == 2 && selectedCollision != null) {
					((HitBox) selectedCollision).setLaunchY((double) launchY.getValue());
				}
			}
		});
		launchVelocity.add(launchX);
		launchVelocity.add(launchY);
		JCheckBox grab = new JCheckBox("   Grab With:");
		grab.setHorizontalAlignment(SwingConstants.CENTER);
		grab.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!updateData && tabbedPane.getSelectedIndex() == 2 && selectedCollision != null) {
					grabWith.setEnabled(grab.isSelected());
					if (!grab.isSelected()) {
						((HitBox) selectedCollision).setAttachTo("");
					} else {
						((HitBox) selectedCollision).setAttachTo(grabWith.getSelectedItem().toString());
					}
				}
			}
		});
		grabWith = new JComboBox<>();
		grabWith.setEnabled(false);
		grabWith.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!updateData && tabbedPane.getSelectedIndex() == 2 && selectedCollision != null && grabWith.isEnabled()) {
					((HitBox) selectedCollision).setAttachTo(grabWith.getSelectedItem().toString());
				}
			}
		});
		JComboBox<HitBoxType> hitboxType = new JComboBox<>(HitBoxType.values());
		hitboxType.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!updateData && tabbedPane.getSelectedIndex() == 2 && selectedCollision != null) {
					((HitBox) selectedCollision).setType((HitBoxType) hitboxType.getSelectedItem());
				}
			}
		});
		JCheckBox releaseGrab = new JCheckBox("Releases Grabs");
		releaseGrab.setHorizontalAlignment(SwingConstants.CENTER);
		releaseGrab.setHorizontalTextPosition(SwingConstants.LEFT);
		releaseGrab.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!updateData && tabbedPane.getSelectedIndex() == 2 && selectedCollision != null) {
					((HitBox) selectedCollision).setRelease(releaseGrab.isSelected());
				}
			}
		});
		JCheckBox knockdown = new JCheckBox("Knocks Down");
		knockdown.setHorizontalAlignment(SwingConstants.CENTER);
		knockdown.setHorizontalTextPosition(SwingConstants.LEFT);
		knockdown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!updateData && tabbedPane.getSelectedIndex() == 2 && selectedCollision != null) {
					((HitBox) selectedCollision).setKnockdown(knockdown.isSelected());
				}
			}
		});
		triggerAnim = new JComboBox<>();
		triggerAnim.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!updateData && tabbedPane.getSelectedIndex() == 2 && selectedCollision != null) {
					((HitBox) selectedCollision).setTriggeredAnim((String) triggerAnim.getSelectedItem());
				}
			}
		});
		JTextField triggerOtherAnim = new JTextField();
		triggerOtherAnim.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				if (!updateData && tabbedPane.getSelectedIndex() == 2 && selectedCollision != null) {
					((HitBox) selectedCollision).setTargetTriggeredAnim((String) triggerOtherAnim.getText());
				}
			}

			public void removeUpdate(DocumentEvent e) {
				if (!updateData && tabbedPane.getSelectedIndex() == 2 && selectedCollision != null) {
					((HitBox) selectedCollision).setTargetTriggeredAnim((String) triggerOtherAnim.getText());
				}
			}

			public void insertUpdate(DocumentEvent e) {
				if (!updateData && tabbedPane.getSelectedIndex() == 2 && selectedCollision != null) {
					((HitBox) selectedCollision).setTargetTriggeredAnim((String) triggerOtherAnim.getText());
				}
			}
		});
		hitboxes.add(new JLabel("Group:", SwingConstants.CENTER));
		hitboxes.add(group);
		hitboxes.add(new JLabel("Hit Stun:", SwingConstants.CENTER));
		hitboxes.add(hitstun);
		hitboxes.add(new JLabel("Block Stun:", SwingConstants.CENTER));
		hitboxes.add(blockstun);
		hitboxes.add(new JLabel("Pushback:", SwingConstants.CENTER));
		hitboxes.add(pushback);
		hitboxes.add(new JLabel("Launch Velocity:", SwingConstants.CENTER));
		hitboxes.add(launchVelocity);
		hitboxes.add(grab);
		hitboxes.add(grabWith);
		hitboxes.add(new JLabel("Type:", SwingConstants.CENTER));
		hitboxes.add(hitboxType);
		hitboxes.add(releaseGrab);
		hitboxes.add(knockdown);
		hitboxes.add(new JLabel("Trigger Animation:", SwingConstants.CENTER));
		hitboxes.add(triggerAnim);
		hitboxes.add(new JLabel("Trigger Target Anim:", SwingConstants.CENTER));
		hitboxes.add(triggerOtherAnim);
		tabbedPane.addTab(tabNames[0], ecbs);
		tabbedPane.addTab(tabNames[1], hurtboxes);
		tabbedPane.addTab(tabNames[2], new JScrollPane(hitboxes));
		center.add(tablePanel);
		center.add(tabbedPane);

		collisionBoxes.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				int row = collisionBoxes.getSelectedRow();
				if (!updateData && row >= 0) {
					Object[] info = {collisionBoxes.getValueAt(row, 0), collisionBoxes.getValueAt(row, 1),
							collisionBoxes.getValueAt(row, 2), collisionBoxes.getValueAt(row, 3),
							collisionBoxes.getValueAt(row, 4), collisionBoxes.getValueAt(row, 5)};
					selectedCollision = AnimationEditor.currentAnimation
							.getCollisionBox(tabNames[tabbedPane.getSelectedIndex()], info);
					currentFrame.setValue(selectedCollision.getStartFrame());
					if (tabbedPane.getSelectedIndex() == 2) {
						updateData = true;
						((HitBox) selectedCollision).updateUI(group, damage, hitstun, blockstun, pushback, launchX,
								launchY, grab, grabWith, hitboxType, releaseGrab, knockdown, triggerAnim,
								triggerOtherAnim);
						updateData = false;
					}
				}
			}
		});
		Editor.collisionPanel.add(north, BorderLayout.NORTH);
		Editor.collisionPanel.add(center, BorderLayout.CENTER);
	}
	
	public static void updateTable() {
		updateData = true;
	    DefaultTableModel model = (DefaultTableModel) collisionBoxes.getModel();
		model.setRowCount(0);
		List<CollisionBox> temp = new ArrayList<>();
		String tabName = tabNames[tabbedPane.getSelectedIndex()];
		switch (tabbedPane.getSelectedIndex()) {
			case 0: 
				temp.addAll(AnimationEditor.currentAnimation.getECBs());
				break;
			case 1:
				temp.addAll(AnimationEditor.currentAnimation.getHurtboxes());
				break;
			case 2:
				temp.addAll(AnimationEditor.currentAnimation.getHitboxes());
				break;
		}
		while (temp.size() > 0) {
			CollisionBox first = null;
			for (int i = 0; i < temp.size(); i++) {
				if (first == null || temp.get(i).getStartFrame() < first.getStartFrame()
						|| temp.get(i).getStartFrame() == first.getStartFrame()
								&& temp.get(i).getEndFrame() < first.getEndFrame()) {
					first = temp.get(i);
				}
			}
			model.addRow(first.getInfo());
			temp.remove(first);
		}
		collisionBoxes.revalidate();
		for (int i = 0; i < model.getRowCount(); i++) {
			Object[] info = {collisionBoxes.getValueAt(i, 0), collisionBoxes.getValueAt(i, 1),
					collisionBoxes.getValueAt(i, 2), collisionBoxes.getValueAt(i, 3), collisionBoxes.getValueAt(i, 4),
					collisionBoxes.getValueAt(i, 5)};
			if (AnimationEditor.currentAnimation.getCollisionBox(tabName, info).equals(selectedCollision)) {
				collisionBoxes.setRowSelectionInterval(i, i);
				collisionBoxes.requestFocus();
				break;
			}
		}
		if (collisionBoxes.getSelectedRow() < 0) {
			selectedCollision = null;
		}
		updateData = false;
		if (collisionBoxes.getRowCount() > 0 && selectedCollision == null) {
			collisionBoxes.setRowSelectionInterval(0, 0);
		}
	}
}
