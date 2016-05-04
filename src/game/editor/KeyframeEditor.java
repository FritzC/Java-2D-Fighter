package game.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import java.util.EventObject;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import game.Game;
import game.states.fight.animation.Interpolation;
import game.states.fight.animation.Keyframe;
import game.states.fight.animation.KeyframeType;
import game.states.fight.fighter.Bone;
import game.util.Position;

public class KeyframeEditor {

	private static JTable keyframes;
	private static TableModel model;
	private static JButton newKeyframe;
	private static JButton deleteKeyframe;
	private static JButton refresh;
	private final static int SHIFT = 16;
	private static double frame;
	
	static JComboBox<String> bones;
	static Keyframe currentKeyframe;
	static boolean updateData;
	
	public static void initKeyframeViewPanel() {
		Editor.keyframeViewPanel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, 1000, 1000);
				g.setColor(Color.BLACK);
				g.drawRect(-1, 0, getWidth() - 1, getHeight() - 1);
				int groundY = Editor.camera.getScreenY(new Position(0f, 0f));
				g.drawLine(0, groundY, getWidth() - 1, groundY);
				if (currentKeyframe != null) {
					AnimationEditor.currentAnimation.setFrame(Editor.fighter, Editor.fighter.getEditorSkeleton(1),
							currentKeyframe.getEndFrame());
					Editor.fighter.getEditorSkeleton(1).draw((Graphics2D) g, Editor.defaultLoc, Editor.camera, true,
							(String) currentKeyframe.getInfo()[1], "", 0);
				}
			}
		};
		Runnable gameLoop = new Runnable() {
			@Override
			public void run() {
				Editor.keyframeViewPanel.repaint();
			}
		};
		ScheduledExecutorService loopExecutor = Executors.newScheduledThreadPool(1);
		loopExecutor.scheduleAtFixedRate(gameLoop, 0, 1000 / Game.LOOP_SPEED, TimeUnit.MILLISECONDS);
		Editor.keyframeViewPanel.setFocusable(true);
		Editor.keyframeViewPanel.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (currentKeyframe != null && Editor.fighter != null && Editor.fighter.getEditorSkeleton(1) != null) {
					Position pos = Editor.fighter.getSkeleton().getPosition((String) currentKeyframe.getInfo()[1], Editor.defaultLoc, Editor.camera);
					if (pos == null) {
						return;
					}
					int boneX = Editor.camera.getScreenX(pos);
					int boneY = Editor.camera.getScreenY(pos);
					double difX = e.getX() - boneX;
					double difY = boneY - e.getY();
					double angle = Math.toDegrees(Math.atan(difY / difX));
					double length = Math.sqrt(Math.pow(difX, 2) + Math.pow(difY, 2));
					if (difX < 0) {
						angle += 180;
					}
					if (difX > 0 && difY < 0) {
						angle += 360;
					}
					if (Editor.keysDown.contains(SHIFT)) {
						angle = angle - (angle % 5);
					}
					if ((KeyframeType) currentKeyframe.getInfo()[2] == KeyframeType.ROTATE) {
						currentKeyframe.setData(angle);
						keyframes.setValueAt(angle, keyframes.getSelectedRow(), 4);
					} else if ((KeyframeType) currentKeyframe.getInfo()[2] == KeyframeType.LENGTH) {
						currentKeyframe.setData(Editor.camera.toGameDistance(length));
						keyframes.setValueAt(Editor.camera.toGameDistance(length), keyframes.getSelectedRow(), 4);
					}
				}
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
				Editor.skeletonViewPanel.requestFocus();
			}
		});
		Editor.keyframeViewPanel.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				if (currentKeyframe != null && Editor.fighter != null && Editor.fighter.getEditorSkeleton(1) != null) {
					Position pos = Editor.fighter.getSkeleton().getPosition((String) currentKeyframe.getInfo()[1], Editor.defaultLoc, Editor.camera);
					if (pos == null) {
						return;
					}
					int boneX = Editor.camera.getScreenX(pos);
					int boneY = Editor.camera.getScreenY(pos);
					double difX = e.getX() - boneX;
					double difY = boneY - e.getY();
					double angle = Math.toDegrees(Math.atan(difY / difX));
					double length = Math.sqrt(Math.pow(difX, 2) + Math.pow(difY, 2));
					if (difX < 0) {
						angle += 180;
					}
					if (difX > 0 && difY < 0) {
						angle += 360;
					}
					if (Editor.keysDown.contains(SHIFT)) {
						angle = angle - (angle % 5);
					}
					if ((KeyframeType) currentKeyframe.getInfo()[2] == KeyframeType.ROTATE) {
						currentKeyframe.setData(angle);
						keyframes.setValueAt(angle, keyframes.getSelectedRow(), 4);
					} else if ((KeyframeType) currentKeyframe.getInfo()[2] == KeyframeType.LENGTH) {
						currentKeyframe.setData(Editor.camera.toGameDistance(length));
						keyframes.setValueAt(Editor.camera.toGameDistance(length), keyframes.getSelectedRow(), 4);
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {}
			
		});
		Editor.keyframeViewPanel.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				Editor.keysDown.add(e.getKeyCode());
			}

			@Override
			public void keyReleased(KeyEvent e) {
				Editor.keysDown.remove((Integer) e.getKeyCode());
			}

			@Override
			public void keyTyped(KeyEvent arg0) {}
		});
	}

	public static void initKeyframeEditPanel() {
		Editor.keyframePanel = new JPanel(new BorderLayout());
		model = new DefaultTableModel(new String[] {"Frame", "Bone", "Type", "Interpolation", "Data"}, 0);
		keyframes = new JTable(model);
		keyframes.getTableHeader().setReorderingAllowed(false);
		keyframes.setSelectionMode(0);
		keyframes.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		keyframes.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent event) {
				int row = keyframes.getSelectedRow();
				if (!updateData && row >= 0) {
					Object[] info = {keyframes.getValueAt(row, 0), keyframes.getValueAt(row, 1),
							keyframes.getValueAt(row, 2), keyframes.getValueAt(row, 3)};
					currentKeyframe = AnimationEditor.currentAnimation.getKeyframe(info);
				}
			}
		});
		JSpinner frame = new JSpinner(new SpinnerNumberModel(1, 0, 10000, 1));
		frame.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!updateData && currentKeyframe != null) {
					currentKeyframe.setEndFrame((int) frame.getValue());
				}
			}
		});
		keyframes.getColumnModel().getColumn(0).setCellEditor(new SpinnerEditor(frame));
		JSpinner data = new JSpinner(new SpinnerNumberModel(0d, -360d, 360d, 0.01d));
		data.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!updateData && currentKeyframe != null) {
					currentKeyframe.setData((double) data.getValue());
				}
			}
		});
		keyframes.getColumnModel().getColumn(4).setCellEditor(new SpinnerEditor(data));
		bones = new JComboBox<>();
		bones.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!updateData && currentKeyframe != null) {
					currentKeyframe.setBone((String) bones.getSelectedItem());
				}
			}
		});
		keyframes.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(bones));
		JComboBox<KeyframeType> type = new JComboBox<>(KeyframeType.values());
		type.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!updateData && currentKeyframe != null) {
					currentKeyframe.setType((KeyframeType) type.getSelectedItem());
				}
			}
		});
		keyframes.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(type));
		JComboBox<Interpolation> interpolation = new JComboBox<>(Interpolation.values());
		interpolation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!updateData && currentKeyframe != null) {
					currentKeyframe.setInterpolation((Interpolation) interpolation.getSelectedItem());
				}
			}
		});
		keyframes.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(interpolation));
		JPanel bottom = new JPanel(new GridLayout(0, 3));
		newKeyframe = new JButton("+");
		newKeyframe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentKeyframe = null;
				JPanel panel = new JPanel();
				panel.add(frame);
				panel.add(bones);
				panel.add(type);
				if (JOptionPane.showConfirmDialog(Editor.frame, panel, "Pull data from:",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == 0) {
					double oldVal = AnimationEditor.currentAnimation.getCurrentFrame();
					AnimationEditor.currentAnimation.setFrame(Editor.fighter, Editor.fighter.getEditorSkeleton(1),
							(int) frame.getValue());
					Keyframe copy = Editor.fighter.getEditorSkeleton(1).getStartPositions().get(bones.getSelectedItem()).get(type.getSelectedItem());
					copy.setInterpolation(Interpolation.LINEAR);
					copy.setEndFrame((int) frame.getValue()); 
					AnimationEditor.currentAnimation.setFrame(Editor.fighter, Editor.fighter.getEditorSkeleton(0),
							oldVal);
					AnimationEditor.currentAnimation.getKeyframes().add(copy);
					updateKeyframeTable();
					currentKeyframe = copy;
				}
			}
		});
		deleteKeyframe = new JButton("-");
		deleteKeyframe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(Editor.frame, "Are you sure you want to delete this?", "", 
                        JOptionPane.YES_NO_OPTION) == 0) {
					AnimationEditor.currentAnimation.getKeyframes().remove(currentKeyframe);
					updateKeyframeTable();
				}
			}
		});
		refresh = new JButton("Refresh");
		refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateKeyframeTable();
			}
		});
		bottom.add(newKeyframe);
		bottom.add(deleteKeyframe);
		bottom.add(refresh);
		Editor.keyframePanel.add(new JScrollPane(keyframes), BorderLayout.CENTER);
		Editor.keyframePanel.add(bottom, BorderLayout.SOUTH);
	}
	
	public static void updateBones() {
		bones.removeAllItems();
		CollisionEditor.grabWith.removeAllItems();
		if (Editor.fighter != null) {
			for (String bone : Editor.fighter.getEditorSkeleton(1).getIdentifiers()) {
				bones.addItem(bone);
				CollisionEditor.grabWith.addItem(bone);
			}
		}
	}

	public static void updateKeyframeTable() {
		updateData = true;
	    DefaultTableModel model = (DefaultTableModel) keyframes.getModel();
	    model.setRowCount(0);
		List<Keyframe> temp = new ArrayList<>();
		if (AnimationEditor.currentAnimation != null) {
			for(Keyframe keyframe : AnimationEditor.currentAnimation.getKeyframes()) {
				temp.add(keyframe);
			}
			while (temp.size() > 0) {
				Keyframe first = null;
				for (int i = 0; i < temp.size(); i++) {
					if (first == null || temp.get(i).getEndFrame() < first.getEndFrame()) {
						first = temp.get(i);
					}
				}
				model.addRow(first.getInfo());
				temp.remove(first);
			}
			keyframes.revalidate();
			for (int i = 0; i < model.getRowCount(); i++) {
				Object[] info = {keyframes.getValueAt(i, 0), keyframes.getValueAt(i, 1),
						keyframes.getValueAt(i, 2), keyframes.getValueAt(i, 3)};
				if (AnimationEditor.currentAnimation.getKeyframe(info).equals(currentKeyframe)) {
					keyframes.setRowSelectionInterval(i, i);
					keyframes.requestFocus();
					break;
				}
			}
		}
		updateData = false;
	}
}
