package game.states.fight.fighter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

import game.states.fight.Camera;
import game.states.fight.animation.Instruction;
import game.states.fight.animation.Interpolation;
import game.util.Position;
import game.util.Sprite;
import game.util.Vector;

/**
 * Piece of a fighter
 * 
 * @author Fritz
 *
 */
public class FighterBone {

	/**
	 * Map of all children of the bone
	 */
	private Map<String, FighterBone> children;
	
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
	 * Initializes a LINE Fighterbone
	 * 
	 * @param length - Length of line
	 * @param width - Width of line
	 * @param angle - Angle line is at
	 * @param visible - Whether to draw the bone
	 */
	public FighterBone(float length, float width, float angle, boolean visible) {
		drawMode = DrawMode.LINE;
		this.length = length;
		this.width = width;
		this.angle = angle;
		children = new HashMap<>();
		children.put("root", this);
		this.visible = visible;
	}
	
	/**
	 * Initializes a SPRITE Fighterbone
	 * @param sprite - Sprite to draw
	 * @param length - Length of bone
	 * @param angle - Angle bone is at
	 * @param visible - Whether to draw the bone
	 */
	public FighterBone(Sprite sprite, float length, float angle, boolean visible) {
		drawMode = DrawMode.SPRITE;
		this.sprite = sprite;
		this.length = length;
		this.angle = angle;
		children = new HashMap<>();
		children.put("root", this);
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
		float currentAngle = angle + interpolatedAngle;
		float currentLength = length + interpolatedLength;
		g.setColor(Color.BLACK);
		if (visible) {
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
					clone.fillRoundRect(screenX - lineWidth / 2, screenY - lineWidth / 2, lineLength + lineWidth,
							lineWidth, lineWidth, lineWidth);
					break;
			}
		}
		Position tail = root.applyVector(new Vector((float) (currentLength * Math.cos(Math.toRadians(currentAngle))),
				-(float) (currentLength * Math.sin(Math.toRadians(currentAngle)))));
		for (String key : children.keySet()) {
			if (!key.equals("root")) {
				children.get(key).draw(g, tail, camera);
			}
		}
	}
	
	/**
	 * Adds a child node to the bone
	 * 
	 * @param identifier - Name of the bone (Must be unique)
	 * @param child - bone to add as a child
	 */
	public void addChild(String identifier, FighterBone child) {
		children.put(identifier, child);
	}
	
	/**
	 * Gets a bone by it's name
	 * 
	 * @param identifier - Name of the bone
	 * @return - Bone with the given identifier
	 */
	public FighterBone getBone(String identifier) {
		if (identifier.equals("root")) {
			return this;
		}
		for (String key : children.keySet()) {
			if (key.equals("root")) {
				continue;
			}
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
	public void interpolate(float data, Instruction type, Interpolation interpolation, float completion) {
		switch (type) {
			case LENGTH:
				interpolatedLength = interpolation.getInterpolatedValue(length, data, completion);
				break;
			case ROTATE:
				interpolatedAngle = interpolation.getInterpolatedValue(length, data, completion);
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
	public void applyInstruction(float data, Instruction type) {
		switch (type) {
			case LENGTH:
				interpolatedLength = data;
				break;
			case ROTATE:
				interpolatedAngle = data;
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

}
