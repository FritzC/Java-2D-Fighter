package game.states.fight.fighter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

import game.states.fight.Camera;
import game.states.fight.Fighter;
import game.states.fight.animation.Interpolation;
import game.states.fight.animation.KeyframeType;
import game.util.Position;
import game.util.Sprite;
import game.util.Vector;

/**
 * Piece of a fighter
 * 
 * @author Fritz
 *
 */
public class Bone {

	/**
	 * Map of all children of the bone
	 */
	private Map<String, Bone> children;
	
	/**
	 * How to draw the bone (SPRITE or LINE)
	 */
	private DrawMode drawMode;
	
	/**
	 * Length of the bone
	 */
	private float length;
	
	/**
	 * Angle the bone is at (clockwise starting at x axis)
	 */
	private float angle;
	
	/**
	 * Width of the bone
	 */
	private float width;
	
	/**
	 * Sprite to draw for the bone
	 */
	private Sprite sprite;
	
	/**
	 * Whether to draw the bone
	 */
	private boolean visible;
	
	/**
	 * Length offset between interpolated steps
	 */
	private float interpolatedLength;

	/**
	 * Angle offset between interpolated steps
	 */
	private float interpolatedAngle;
	
	/**
	 * If defined, the fighter attached to the bone (for grabs)
	 */
	private Fighter attached;
	
	/**
	 * Whether a fighter is attached
	 */
	private boolean hasAttachedFighter;
	
	/**
	 * Initializes a LINE Fighterbone
	 * 
	 * @param length - Length of line
	 * @param width - Width of line
	 * @param angle - Angle line is at
	 * @param visible - Whether to draw the bone
	 */
	public Bone(float length, float width, float angle, boolean visible) {
		drawMode = DrawMode.LINE;
		this.length = length;
		this.width = width;
		this.angle = angle;
		children = new HashMap<>();
		this.visible = visible;
	}
	
	/**
	 * Initializes a SPRITE bone
	 * @param sprite - Sprite to draw
	 * @param length - Length of bone
	 * @param angle - Angle bone is at
	 * @param visible - Whether to draw the bone
	 */
	public Bone(Sprite sprite, float length, float angle, boolean visible) {
		drawMode = DrawMode.SPRITE;
		this.sprite = sprite;
		this.length = length;
		this.angle = angle;
		children = new HashMap<>();
		this.visible = visible;
	}
	
	/**
	 * Draws the bone and all of its children
	 *  - Call on the root of the structure to draw the entire fighter
	 *  
	 * @param g - Graphics object to draw with
	 * @param root - Position of the last node to draw from
	 * @param camera - Camera reference to get screen coordinates
	 */
	public void draw(Graphics2D g, Position root, Camera camera) {
		draw(g, root, camera, false, "", "", 0);
	}
	
	/**
	 * Draws the bone and all of its children
	 *  - Call on the root of the structure to draw the entire fighter
	 *  
	 * @param g - Graphics object to draw with
	 * @param root - Position of the last node to draw from
	 * @param camera - Camera reference to get screen coordinates
	 * @param drawHidden - Whether to draw hidden bones
	 * @param selected - Selected bone
	 */
	public void draw(Graphics2D g, Position root, Camera camera, boolean drawHidden, String selectedBone, String hoveredBone, int selectType) {
		float currentAngle = (interpolatedAngle != 0) ? interpolatedAngle : angle;
		float currentLength = (interpolatedLength != 0) ? interpolatedLength : length;
		if (selectedBone.equals("root")) {
			selectType = 2;
			selectedBone = "";
		} else if (hoveredBone.equals("root")) {
			selectType = 1;
			hoveredBone = "";
		}
		g.setColor(Color.BLACK);
		if (visible || drawHidden) {
			int screenX = camera.getScreenX(root);
			int screenY = camera.getScreenY(root);
			switch (drawMode) {
				case SPRITE:
					sprite.draw(g, screenX, screenY, camera.getScale(), currentAngle);
					break;
				case LINE:
					Graphics2D clone = (Graphics2D) g.create();
					AffineTransform a = new AffineTransform();
					a.concatenate(AffineTransform.getRotateInstance(Math.toRadians(currentAngle), screenX, screenY));
					clone.setTransform(a);
					int lineLength = camera.toPixels(currentLength);
					int lineWidth = camera.toPixels(width);
					if (selectType != 0) {
						clone.setColor((selectType == 1) ? Color.GREEN : Color.BLUE);
					}
					if (!visible) {
						clone.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, new float[] {5.0f}, 0.0f));
						clone.drawRoundRect(screenX - lineWidth / 2, screenY - lineWidth / 2, lineLength + lineWidth,
								lineWidth, lineWidth, lineWidth);
					} else {
						clone.fillRoundRect(screenX - lineWidth / 2, screenY - lineWidth / 2, lineLength + lineWidth,
								lineWidth, lineWidth, lineWidth);
					}
					break;
			}
		}
		Position tail = root.applyVector(new Vector((float) (currentLength * Math.cos(Math.toRadians(currentAngle))),
				-(float) (currentLength * Math.sin(Math.toRadians(currentAngle)))));
		if (hasAttachedFighter) {
			attached.setPosition(tail);
		}
		for (String key : children.keySet()) {
			int drawType = (key.equals(selectedBone)) ? 2 : ((key.equals(hoveredBone)) ? 1 : 0);
			children.get(key).draw(g, tail, camera, drawHidden, selectedBone, hoveredBone, drawType);
		}
	}
	
	/**
	 * Adds a child node to the bone
	 * 
	 * @param identifier - Name of the bone (Must be unique)
	 * @param child - bone to add as a child
	 */
	public void addChild(String identifier, Bone child) {
		children.put(identifier, child);
	}
	
	/**
	 * Gets a bone by it's name
	 * 
	 * @param identifier - Name of the bone
	 * @return - Bone with the given identifier
	 */
	public Bone getBone(String identifier) {
		if (identifier.equals("root")) {
			return this;
		}
		for (String key : children.keySet()) {
			if (key.equals(identifier)) {
				return children.get(key);
			} else if (children.get(key).getBone(identifier) != null) {
				return children.get(key).getBone(identifier);
			}
		}
		return null;
	}

	/**
	 * Interpolates the bone
	 * 
	 * @param data - Auxiliary data
	 * @param type - Type of instruction
	 * @param interpolation - Type of interpolation
	 * @param completion - % completed
	 */
	public void interpolate(float data, KeyframeType type, Interpolation interpolation, float completion) {
		switch (type) {
			case LENGTH:
				if (data == length) {
					break;
				}
				interpolatedLength = interpolation.getInterpolatedValue(length, data, completion);
				break;
			case ROTATE:
				if (data == angle) {
					break;
				}
				interpolatedAngle = interpolation.getInterpolatedValue(angle, data, completion);
				break;
			case VISIBLE:
				visible = (data == 1);
				break;
		}
	}
	
	/**
	 * Applies an instruction to the bone
	 * 
	 * @param data - Auxiliary data
	 * @param type - Type of the instruction
	 */
	public void applyInstruction(float data, KeyframeType type) {
		switch (type) {
			case LENGTH:
				length = data;
				interpolatedLength = 0;
				break;
			case ROTATE:
				angle = data;
				interpolatedAngle = 0;
				break;
			case VISIBLE:
				visible = (data == 1);
				break;
		}
	}

	/**
	 * Sets the angle of the bone
	 * 
	 * @param angle - Angle to set to
	 */
	public void setAngle(float angle) {
		this.angle = angle;
	}
	
	/**
	 * Sets the length of the bone
	 * 
	 * @param length - Length to set to
	 */
	public void setLength(float length) {
		this.length = length;
	}
	
	/**
	 * Sets the visibility of the bone
	 * 
	 * @param visible - Visibility of bone
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Attaches a fighter to the bone
	 * 
	 * @param defender - Fighter being attached
	 */
	public void attachFighter(Fighter defender) {
		attached = defender;
		hasAttachedFighter = true;
	}

	/**
	 * Detaches the fighter from the bone
	 */
	public void release() {
		hasAttachedFighter = false;
	}
	
	public Map<String, Bone> getChildren() {
		return children;
	}

}
