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
import game.states.fight.fighter.Bone;
import game.states.fight.fighter.DrawMode;
import game.util.Position;
import javafx.scene.input.KeyCode;

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
	private static JFileChooser fighterFileChooser;
	
	public static void initSkeletonViewPanel() {
		Position skeletonLoc = new Position(0.5f, 0f);
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
				if (Editor.skeleton != null) {
					Editor.skeleton.draw((Graphics2D) g, skeletonLoc, Editor.camera, true, selected, hovered, 0);
				}
			}
		};
		Editor.skeletonViewPanel.setFocusable(true);
		Editor.skeletonViewPanel.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (boneSelected != null && Editor.skeleton != null) {
					Position pos = Editor.skeleton.getPosition(boneSelected.getName(), skeletonLoc, Editor.camera);
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
					boneSelected.setAngle(angle);
					boneSelected.setLength(Editor.camera.toGameDistance(length));
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
				if (boneSelected != null && Editor.skeleton != null) {
					Position pos = Editor.skeleton.getPosition(boneSelected.getName(), skeletonLoc, Editor.camera);
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
					boneSelected.setAngle(angle);
					boneSelected.setLength(Editor.camera.toGameDistance(length));
					Editor.skeletonViewPanel.repaint();
					updateBoneFields();
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
						loadFile(fighterFileChooser.getSelectedFile());
						Editor.skeletonViewPanel.repaint();
						skeletonTreeDiagram.repaint();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		saveFighter = new JButton("Save");
		fighterName = new JTextField();
		fighterHealth = new JSpinner(new SpinnerNumberModel(1000, 1, 2000, 1));
		ActionListener updateValues = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (updateBone) {
					updateBoneValues();
				}
			}
		};
		ChangeListener updateValues2 = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (updateBone) {
					updateBoneValues();
				}
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
				if (boneSelected != null && Editor.skeleton != null) {
					Editor.skeleton.removeBone(boneSelected.getName());
				}
			}
		});
		boneName = new JTextField();
		boneName.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				if (updateBone) {
					updateBoneValues();
				}
			}

			public void removeUpdate(DocumentEvent e) {
				if (updateBone) {
					updateBoneValues();
				}
			}

			public void insertUpdate(DocumentEvent e) {
				if (updateBone) {
					updateBoneValues();
				}
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
		boneVisible = new JCheckBox("  Visible");
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
				if (Editor.skeleton != null) {
					drawBoneLevel(g2, Editor.skeleton, null, false, 0);
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
	
	public static void updateBoneFields() {
		updateBone = false;
		if (boneSelected != null) {
			boneName.setText(boneSelected.getName());
			boneLength.setValue(boneSelected.getLength());
			boneAngle.setValue(boneSelected.getAngle());
			boneWidth.setValue(boneSelected.getWidth());
			boneVisible.setSelected(boneSelected.isVisible());
			drawType.setSelectedIndex(boneSelected.getDrawMode().ordinal());
		}
		updateBone = true;
	}

	public static void updateBoneValues() {
		if (boneSelected != null) {
			boneSelected.updateValues(boneName.getText(), (DrawMode) drawType.getSelectedItem(),
					(double) boneLength.getValue(), (double) boneWidth.getValue(), (double) boneAngle.getValue(),
					boneVisible.isSelected());
			Editor.skeletonViewPanel.repaint();
			skeletonTreeDiagram.repaint();
		}
	}
	
	public static void loadFile(File f) throws FileNotFoundException {
		Scanner s = new Scanner(f);
		while(s.hasNextLine()) {
			String line = s.nextLine().trim();
			if (!line.contains(":")) {
				continue;
			}
			String type = line.substring(0, line.indexOf(":"));
			String data = line.substring(line.indexOf(":") + 1).replaceAll(",", "").replaceAll("\"", "").trim();
			//System.out.println(data);
			if (type.contains("name")) {
				//fighterName.setText(data);
				System.out.println("NAME: " + data);
			} else if (type.contains("health")) {
				//fighterHealth.setValue(Double.parseDouble(data));
				System.out.println("HEALTH: " + data);
			} else if (type.contains("skeleton")) {
				s.nextLine();
				Editor.skeleton = loadBone(s);
			}
		}
	}
	
	public static Bone loadBone(Scanner s) {
		Bone bone = new Bone(null, 0, 0, 0, false);
		String name = null;
		DrawMode drawMode = null;
		double length = 0, width = 0, angle = 0;
		boolean visible = false;
		int skip = 0;
		while (s.hasNextLine()) {
			String line = s.nextLine();
			if (line.contains("}") &&  skip-- == 0) {
				if (line.contains("children")) {
					s.nextLine();
				}
				break;
			}
			String type = line.substring(0, line.indexOf(":"));
			String data = line.substring(line.indexOf(":") + 1).replaceAll(",", "").replaceAll("\"", "").trim();
			if (type.contains("children")) {
				while (!line.contains("}")) {
					line = s.nextLine();
					Bone b = loadBone(s);
					if (b.getName() != null) {
						bone.addChild(b);
					}
				}
				break;
			} else if (type.contains("drawMode")) {
				drawMode = DrawMode.forString(data);
			} else if (type.contains("length")) {
				length = Double.parseDouble(data);
			} else if (type.contains("width")) {
				width = Double.parseDouble(data);
			} else if (type.contains("angle")) {
				angle = Double.parseDouble(data);
			} else if (type.contains("visible")) {
				visible = Boolean.parseBoolean(data);
			} else if (type.contains("name")) {
				name = data;
			}
		}
		bone.updateValues(name, drawMode, length, width, angle, visible);
		return bone;
	}

}
