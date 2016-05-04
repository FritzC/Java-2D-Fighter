package game.editor;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import game.states.fight.Camera;
import game.states.fight.Fighter;
import game.states.fight.Stage;
import game.states.fight.fighter.Bone;
import game.util.Position;

public class Editor {

	static JFrame frame;
	static File fighterDirectory;
	static Fighter fighter;
	static Stage stage;
	static Camera camera;
	static Position defaultLoc = new Position(0.5f, 0f);
	
	private static JPanel viewPort;
	static JPanel skeletonViewPanel;
	static JPanel animationViewPanel;
	static JPanel keyframeViewPanel;
	static JPanel collisionViewPanel;
	
	private static JPanel editPanel;
	static JPanel fighterPanel;
	static JPanel animationsPanel;
	static JPanel keyframePanel;
	static JPanel collisionPanel;
	
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
		frame = new JFrame();
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
		AnimationEditor.initAnimationViewPanel();
		KeyframeEditor.initKeyframeViewPanel();
		CollisionEditor.initCollisionViewPanel();
		viewPort.add(skeletonViewPanel);
		viewPort.add(animationViewPanel);
		viewPort.add(keyframeViewPanel);
		viewPort.add(collisionViewPanel);
	}
	
	public static void initEditPanel() {
		editPanel = new JPanel();
		editPanel.setLayout(new GridLayout(1, 4));
		FighterEditor.initFighterPanel();
		AnimationEditor.initAnimationsPanel();
		KeyframeEditor.initKeyframeEditPanel();
		CollisionEditor.initCollisionPanel();
		editPanel.add(fighterPanel);
		editPanel.add(animationsPanel);
		editPanel.add(keyframePanel);
		editPanel.add(collisionPanel);
	}

}
