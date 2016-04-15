package game.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import game.states.fight.Camera;
import game.states.fight.Fighter;
import game.states.fight.Stage;
import game.states.fight.fighter.Bone;
import game.states.fight.fighter.DrawMode;
import game.util.Position;

public class Editor {

	private static Fighter fighter;
	private static Bone skeleton;
	private static Stage stage;
	private static Camera camera;
	
	private static JPanel viewPort;
	private static JPanel skeletonViewPanel;
	private static String boneSelected;
	private static String boneHovered;
	
	private static JPanel editPanel;
	private static JPanel fighterPanel;
	private static JPanel animationsPanel;
	private static JPanel keyframePanel;
	private static JPanel collisionBoxPanel;
	
	private static JButton loadFighter;
	private static JButton saveFighter;
	private static JTextField fighterName;
	private static JSpinner fighterHealth;
	private static JPanel skeletonTreeDiagram;
	private static List<String> expandedBones;
	private static int listYOff;
	private static List<BoneListElement> boneListElements;
	private static JButton addBone;
	private static JButton removeBone;
	private static JSpinner boneLength;
	private static JSpinner boneAngle;
	private static JSpinner boneWidth;
	private static JCheckBox boneVisible;
	private static JComboBox<DrawMode> drawType;
	private static JButton loadSprite;
	
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
	
	public static class BoneListElement {
		
		public final static int EXPAND_WIDTH = 21;
		public String boneId;
		public int width;
		public boolean hasChildren;
		
		public BoneListElement(String boneId, int expandXOff, boolean hasChildren) {
			this.boneId = boneId;
			this.width = expandXOff + 7;
			this.hasChildren = hasChildren;
		}
	}

	public static void main(String args[]) {
		JFrame frame = new JFrame();
		stage = new Stage() {
			@Override
			public float getWidth() {
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
		initSkeletonViewPanel();
		viewPort.add(skeletonViewPanel);
	}
	
	public static void initSkeletonViewPanel() {
		skeleton = new Bone(0.2f, 0.05f, 270f, false);
		skeleton.addChild("l_arm", new Bone(0.1f, 0.05f, 0f, true));
		skeleton.addChild("r_arm", new Bone(0.1f, 0.05f, 180f, true));
		skeleton.getChildren().get("l_arm").addChild("l_hand", new Bone(0.05f, 0.05f, 90f, true));
		skeleton.getChildren().get("l_arm").addChild("l_hand2", new Bone(0.01f, 0.05f, 180f, true));
		boneSelected = "";
		boneHovered = "";
		skeletonViewPanel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				g.setColor(Color.white);
				g.fillRect(0, 0, 1000, 1000);
				g.setColor(Color.BLACK);
				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
				g.drawRect(0, camera.getScreenY(new Position(0f, 0f)), 1000, 1000);
				skeleton.draw((Graphics2D) g, new Position(0.5f, 0f), camera, true, boneSelected, boneHovered, 0);
			}
		};
	}
	
	public static void initEditPanel() {
		editPanel = new JPanel();
		editPanel.setLayout(new GridLayout(1, 4));
		initFighterPanel();
		initAnimationsPanel();
		initKeyframePanel();
		collisionBoxPanel = new JPanel();
		editPanel.add(fighterPanel);
		editPanel.add(animationsPanel);
		editPanel.add(keyframePanel);
		editPanel.add(collisionBoxPanel);
	}
	
	public static void initFighterPanel() {
		fighterPanel = new JPanel(new GridLayout(0, 2));
		JPanel rightPanel = new JPanel(new GridLayout(0, 2));
		loadFighter = new JButton("Load");
		saveFighter = new JButton("Save");
		fighterName = new JTextField();
		fighterHealth = new JSpinner(new SpinnerNumberModel(1000, 1, 2000, 1));
		addBone = new JButton("Add Child");
		removeBone = new JButton("Delete");
		boneLength = new JSpinner(new SpinnerNumberModel(0.1 , 0, 1.5, 0.005));
		boneAngle = new JSpinner(new SpinnerNumberModel(0, -360, 360, 1));
		boneWidth = new JSpinner(new SpinnerNumberModel(0.05, 0, 1.5, 0.0025));
		drawType = new JComboBox<>(DrawMode.values());
		boneVisible = new JCheckBox("  Visible");
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
				drawBoneLevel(g2, "root", skeleton, expandedBones, null, false, 0);
			}
		};
		skeletonTreeDiagram.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int index = e.getY() / 12;
				if (index < boneListElements.size()) {
					int width = boneListElements.get(index).width;
					if (e.getX() <= width) {
						String bone = boneListElements.get(index).boneId;
						if (boneListElements.get(index).hasChildren && e.getX() > width - BoneListElement.EXPAND_WIDTH) {
							if (expandedBones.contains(bone)) {
								expandedBones.remove(bone);
							} else {
								expandedBones.add(bone);
							}
						} else {
							boneSelected = bone;
						}
					}
				} else {
					boneSelected = "";
				}
				skeletonViewPanel.repaint();
				skeletonTreeDiagram.repaint();
			}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {
				boneHovered = "";
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
					boneHovered = boneListElements.get(index).boneId;
				} else {
					boneHovered = "";
				}
				skeletonViewPanel.repaint();
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
		fighterPanel.add(rightPanel);
		fighterPanel.add(skeletonTreeDiagram);
	}
	
	public static void drawBoneLevel(Graphics2D g, String name, Bone bone, List<String> expandedBones, List<Integer> lineBuilder, boolean lastElement, int xOff) {
		if (lineBuilder == null) {
			lineBuilder = new ArrayList<>();
		}
		List<String> children = new ArrayList<>(bone.getChildren().keySet());
		String line = name;
		boolean hasChildren = (children.size() > 0);
		if (hasChildren) {
			line += ((expandedBones.contains(name)) ? "[-]" : "[+]");
		}
		if (xOff > 0) {
			line = ((lastElement) ? (char) 0x2514 : (char) 0x251C) + line;
		}
		for (int i = xOff - 1; i >= 0; i--) {
			line = ((lineBuilder.contains(i)) ? (char) 0x2502 : " ") + line;
		}
		if (boneHovered.equals(name)) {
			g.setColor(Color.GREEN);
		}
		if (boneSelected.equals(name)) {
			g.setColor(Color.BLUE);
		}
		g.drawString(line, 5, listYOff * 12);
		g.setColor(Color.BLACK);
		int width = g.getFontMetrics().stringWidth(line);
		boneListElements.add(new BoneListElement(name, width, hasChildren));
		if (expandedBones.contains(name)) {
			lineBuilder.add(++xOff);
			for (int i = 0; i < children.size(); i++) {
				if (i == children.size() - 1) {
					lineBuilder.remove((Integer) (xOff));
				}
				listYOff++;
				drawBoneLevel(g, children.get(i), bone.getChildren().get(children.get(i)), expandedBones, lineBuilder, i == children.size() - 1, xOff);
			}
		}
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
