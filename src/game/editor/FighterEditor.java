package game.editor;

import java.awt.Color;
import java.awt.Font;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import game.editor.Editor.BoneListElement;
import game.states.fight.Fighter;
import game.states.fight.fighter.Bone;
import game.states.fight.fighter.DrawMode;
import game.util.Position;

public class FighterEditor {
	
	private final static int SHIFT = 16;

	private static Bone boneSelected;
	private static Bone boneHovered;
	private static JButton loadFighter;
	private static JButton saveFighter;
	private static JTextField fighterName;
	private static JSpinner fighterHealth;
	private static JPanel skeletonTreeDiagram;
	private static List<Bone> expandedBones;
	private static int listYOff;
	private static List<BoneListElement> boneListElements;
	private static JButton addBone;
	private static JButton removeBone;
	private static JTextField boneName;
	private static JSpinner boneLength;
	private static JSpinner boneAngle;
	private static JSpinner boneWidth;
	private static JCheckBox boneVisible;
	private static JComboBox<DrawMode> drawType;
	private static JButton loadSprite;
	private static boolean updateBone;
	private static boolean updateFighter;
	private static JFileChooser fighterFileChooser;
	
	public static void initSkeletonViewPanel() {
		final Position skeletonLoc = new Position(0.5f, 0f);
		Editor.skeletonViewPanel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				g.setColor(Color.white);
				g.fillRect(0, 0, 1000, 1000);
				g.setColor(Color.BLACK);
				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
				g.drawRect(0, Editor.camera.getScreenY(new Position(0f, 0f)), 1000, 1000);
				String selected = (boneSelected != null) ? boneSelected.getName() : "";
				String hovered = (boneHovered != null) ? boneHovered.getName() : "";
				if (Editor.fighter != null) {
					Editor.fighter.getSkeleton().draw((Graphics2D) g, skeletonLoc, Editor.camera, true, selected, hovered, 0);
				}
			}
		};
		Editor.skeletonViewPanel.setFocusable(true);
		Editor.skeletonViewPanel.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				AnimationEditor.play.setSelected(false);
				if (boneSelected != null && Editor.fighter != null && Editor.fighter.getSkeleton() != null) {
					Position pos = Editor.fighter.getSkeleton().getPosition(boneSelected.getName(), skeletonLoc, Editor.camera);
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
					boneAngle.setValue(angle);
					boneLength.setValue(Editor.camera.toGameDistance(length));
					Editor.skeletonViewPanel.repaint();
					updateBoneFields();
				}
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
				Editor.skeletonViewPanel.requestFocus();
			}
		});
		Editor.skeletonViewPanel.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				AnimationEditor.play.setSelected(false);
				if (boneSelected != null && Editor.fighter != null && Editor.fighter.getSkeleton() != null) {
					Position pos = Editor.fighter.getSkeleton().getPosition(boneSelected.getName(), skeletonLoc, Editor.camera);
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
					boneAngle.setValue(angle);
					boneLength.setValue(Editor.camera.toGameDistance(length));
					Editor.skeletonViewPanel.repaint();
					updateBoneData();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {}
			
		});
		Editor.skeletonViewPanel.addKeyListener(new KeyListener() {

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

	public static void initFighterPanel() {
		Editor.fighterPanel = new JPanel(new GridLayout(0, 2));
		JPanel rightPanel = new JPanel(new GridLayout(0, 2));
		fighterFileChooser = new JFileChooser(".");
		loadFighter = new JButton("Load");
		loadFighter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Fighter files", "json");
				fighterFileChooser.setFileFilter(filter);
				int returnVal = fighterFileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						boneSelected = null;
						Editor.fighter = Fighter.load(fighterFileChooser.getSelectedFile());
						updateFighterFields();
						Editor.skeletonViewPanel.repaint();
						skeletonTreeDiagram.repaint();
						Editor.fighter.updateUIAnimationList(AnimationEditor.animationSelector);
						Editor.fighterDirectory = fighterFileChooser.getSelectedFile().getParentFile();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		saveFighter = new JButton("Save");
		saveFighter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Editor.fighter.save(new File(
							Editor.fighterDirectory.getAbsolutePath() + "/fighter.json"));
					File animDir = new File(Editor.fighterDirectory.getAbsolutePath() + "/animations");
					if (!animDir.exists()) {
						animDir.mkdir();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				/*
				 * FileNameExtensionFilter filter = new FileNameExtensionFilter(
				 * "Fighter files", "json");
				 * fighterFileChooser.setFileFilter(filter); int returnVal =
				 * fighterFileChooser.showSaveDialog(null); if (returnVal ==
				 * JFileChooser.APPROVE_OPTION) { try {
				 * Editor.fighter.save(fighterFileChooser.getSelectedFile()); }
				 * catch (IOException e1) { e1.printStackTrace(); } }
				 */
			}
		});
		fighterName = new JTextField();
		fighterName.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				updateFighterData();
			}

			public void removeUpdate(DocumentEvent e) {
				updateFighterData();
			}

			public void insertUpdate(DocumentEvent e) {
				updateFighterData();
			}
		});
		fighterHealth = new JSpinner(new SpinnerNumberModel(1000d, 1d, 2000d, 1d));
		fighterHealth.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateFighterData();
			}
		});
		ActionListener updateValues = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateBoneData();
			}
		};
		ChangeListener updateValues2 = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				updateBoneData();
			}
		};
		addBone = new JButton("Add Child");
		addBone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Bone newBone = new Bone(boneName.getText() + "_2", (double) boneLength.getValue(),
						(double) boneWidth.getValue(), (double) boneAngle.getValue(), boneVisible.isSelected());
				boneSelected.addChild(newBone);
				boneSelected = newBone;
			}
		});
		removeBone = new JButton("Delete");
		removeBone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (boneSelected != null && Editor.fighter != null && Editor.fighter.getSkeleton() != null) {
					Editor.fighter.getSkeleton().removeBone(boneSelected.getName());
				}
			}
		});
		boneName = new JTextField();
		boneName.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				updateBoneData();
			}

			public void removeUpdate(DocumentEvent e) {
				updateBoneData();
			}

			public void insertUpdate(DocumentEvent e) {
				updateBoneData();
			}
		});
		boneLength = new JSpinner(new SpinnerNumberModel(0.1 , 0, 1.5, 0.005));
		boneLength.addChangeListener(updateValues2);
		boneAngle = new JSpinner(new SpinnerNumberModel(0d, -360d, 360d, 1d));
		boneAngle.addChangeListener(updateValues2);
		boneWidth = new JSpinner(new SpinnerNumberModel(0.05, 0, 1.5, 0.0025));
		boneWidth.addChangeListener(updateValues2);
		drawType = new JComboBox<>(DrawMode.values());
		drawType.addActionListener(updateValues);
		boneVisible = new JCheckBox("Visible: ");
		boneVisible.setHorizontalAlignment(SwingConstants.CENTER);
		boneVisible.setHorizontalTextPosition(SwingConstants.LEFT);
		boneVisible.addActionListener(updateValues);
		loadSprite = new JButton("Load Img");
		boneListElements = new ArrayList<>();
		expandedBones = new ArrayList<>();
		skeletonTreeDiagram = new JPanel() {
			@Override
			public void paint(Graphics g) {
				g.setColor(Color.white);
				g.fillRect(0, 0, 1000, 1000);
				g.setColor(Color.BLACK);
				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
				Graphics2D g2 = (Graphics2D) g;
				Font stringFont = new Font("monospaced", Font.PLAIN, 12);
				g2.setFont(stringFont);
				listYOff = 1;
				boneListElements.clear();
				if (Editor.fighter != null && Editor.fighter.getSkeleton() != null) {
					drawBoneLevel(g2, Editor.fighter.getSkeleton(), null, false, 0);
				}
			}
		};
		skeletonTreeDiagram.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = e.getY() / 12;
				if (index < boneListElements.size()) {
					int width = boneListElements.get(index).width;
					if (e.getX() <= width) {
						Bone bone = boneListElements.get(index).bone;
						if (boneListElements.get(index).hasChildren && e.getX() > width - BoneListElement.EXPAND_WIDTH) {
							if (expandedBones.contains(bone)) {
								expandedBones.remove(bone);
							} else {
								expandedBones.add(bone);
							}
						} else {
							boneSelected = bone;
							updateBoneFields();
						}
					}
				} else {
					boneSelected = null;
				}
				Editor.skeletonViewPanel.repaint();
				skeletonTreeDiagram.repaint();
			}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {
				boneHovered = null;
				skeletonTreeDiagram.repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
			
		});
		skeletonTreeDiagram.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				int index = e.getY() / 12;
				if (index < boneListElements.size()
						&& boneListElements.get(index).width >= e.getX()) {
					boneHovered = boneListElements.get(index).bone;
				} else {
					boneHovered = null;
				}
				Editor.skeletonViewPanel.repaint();
				skeletonTreeDiagram.repaint();
			}
		});
		rightPanel.add(loadFighter);
		rightPanel.add(saveFighter);
		rightPanel.add(new JLabel("Name:", SwingConstants.CENTER));
		rightPanel.add(fighterName);
		rightPanel.add(new JLabel("Health:", SwingConstants.CENTER));
		rightPanel.add(fighterHealth);
		rightPanel.add(new JSeparator());
		rightPanel.add(new JSeparator());
		rightPanel.add(addBone);
		rightPanel.add(removeBone);
		rightPanel.add(new JLabel("Name:", SwingConstants.CENTER));
		rightPanel.add(boneName);
		rightPanel.add(new JLabel("Length:", SwingConstants.CENTER));
		rightPanel.add(boneLength);
		rightPanel.add(new JLabel("Angle:", SwingConstants.CENTER));
		rightPanel.add(boneAngle);
		rightPanel.add(new JLabel("Width:", SwingConstants.CENTER));
		rightPanel.add(boneWidth);
		rightPanel.add(new JLabel("Drawing Type:", SwingConstants.CENTER));
		rightPanel.add(drawType);
		rightPanel.add(boneVisible);
		rightPanel.add(loadSprite);
		Editor.fighterPanel.add(rightPanel);
		Editor.fighterPanel.add(skeletonTreeDiagram);
	}
	
	public static void drawBoneLevel(Graphics2D g, Bone bone, List<Integer> lineBuilder, boolean lastElement, int xOff) {
		if (lineBuilder == null) {
			lineBuilder = new ArrayList<>();
		}
		List<Bone> children = bone.getChildren();
		String name = bone.getName();
		String line = name;
		boolean hasChildren = (children.size() > 0);
		if (hasChildren) {
			line += ((expandedBones.contains(bone)) ? "[-]" : "[+]");
		}
		if (xOff > 0) {
			line = ((lastElement) ? (char) 0x2514 : (char) 0x251C) + line;
		}
		for (int i = xOff - 1; i >= 0; i--) {
			line = ((lineBuilder.contains(i)) ? (char) 0x2502 : " ") + line;
		}
		if (boneHovered != null && boneHovered.equals(bone)) {
			g.setColor(Color.GREEN);
		}
		if (boneSelected != null && boneSelected.equals(bone)) {
			g.setColor(Color.BLUE);
		}
		g.drawString(line, 5, listYOff * 12);
		g.setColor(Color.BLACK);
		int width = g.getFontMetrics().stringWidth(line);
		boneListElements.add(new BoneListElement(bone, width, hasChildren));
		if (expandedBones.contains(bone)) {
			lineBuilder.add(++xOff);
			for (int i = 0; i < children.size(); i++) {
				if (i == children.size() - 1) {
					lineBuilder.remove((Integer) (xOff));
				}
				listYOff++;
				drawBoneLevel(g, children.get(i), lineBuilder, i == children.size() - 1, xOff);
			}
		}
	}
	
	public static void updateFighterFields() {
		updateFighter = false;
		if (Editor.fighter != null) {
			Editor.fighter.updateUIFields(fighterName, fighterHealth);
		}
		updateFighter = true;
	}
	
	public static void updateFighterData() {
		if (Editor.fighter != null && updateFighter) {
			Editor.fighter.updateValues(fighterName.getText(), (double) fighterHealth.getValue());
		}
	}
	
	public static void updateBoneFields() {
		updateBone = false;
		if (boneSelected != null) {
			boneSelected.updateUIFields(boneName, boneLength, boneAngle, boneWidth, boneVisible, drawType);
		}
		updateBone = true;
	}

	public static void updateBoneData() {
		if (boneSelected != null && updateBone) {
			boneSelected.updateValues(boneName.getText(), (DrawMode) drawType.getSelectedItem(),
					(double) boneLength.getValue(), (double) boneWidth.getValue(), (double) boneAngle.getValue(),
					boneVisible.isSelected());
			Editor.fighter.updateEditorSkeletons();
			Editor.skeletonViewPanel.repaint();
			skeletonTreeDiagram.repaint();
		}
	}
	
	public void saveFighter(File f) {
		
	}

}
