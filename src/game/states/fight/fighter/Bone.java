package game.states.fight.fighter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import game.states.fight.Camera;
import game.states.fight.Fighter;
import game.states.fight.animation.Interpolation;
import game.states.fight.animation.Keyframe;
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
	private List<Bone> children;
	
	/**
	 * Name of the bone
	 */
	private String name;
	
	/**
	 * How to draw the bone (SPRITE or LINE)
	 */
	private DrawMode drawMode;
	
	/**
	 * Length of the bone
	 */
	private double length;
	
	/**
	 * Original length of the bone
	 */
	private double originalLength;
	
	/**
	 * Angle the bone is at (clockwise starting at x axis)
	 */
	private double angle;
	
	/**
	 * Original angle of the bone
	 */
	private double originalAngle;
	
	/**
	 * Width of the bone
	 */
	private double width;
	
	/**
	 * Original width of the bone
	 */
	private double originalWidth;
	
	/**
	 * Sprite to draw for the bone
	 */
	private Sprite sprite;
	
	/**
	 * Whether to draw the bone
	 */
	private boolean visible;
	
	/**
	 * Original visiblity of the bone
	 */
	private boolean originalVisibility;
	
	/**
	 * Length offset between interpolated steps
	 */
	private double interpolatedLength;

	/**
	 * Angle offset between interpolated steps
	 */
	private double interpolatedAngle;
	
	/**
	 * If defined, the fighter attached to the bone (for grabs)
	 */
	private Fighter attached;
	
	/**
	 * Initializes a LINE Fighterbone
	 * 
	 * @param length - Length of line
	 * @param width - Width of line
	 * @param angle - Angle line is at
	 * @param visible - Whether to draw the bone
	 */
	public Bone(String name, double length, double width, double angle, boolean visible) {
		drawMode = DrawMode.LINE;
		this.name = name;
		this.length = length;
		this.width = width;
		this.angle = angle;
		children = new ArrayList<>();
		this.visible = visible;
		originalLength = length;
		originalAngle = angle;
		originalWidth = width;
		originalVisibility = visible;
	}
	
	/**
	 * Initializes a SPRITE bone
	 * @param sprite - Sprite to draw
	 * @param length - Length of bone
	 * @param angle - Angle bone is at
	 * @param visible - Whether to draw the bone
	 */
	public Bone(String name, Sprite sprite, double length, double angle, boolean visible) {
		drawMode = DrawMode.SPRITE;
		this.name = name;
		this.sprite = sprite;
		this.length = length;
		this.angle = angle;
		children = new ArrayList<>();
		this.visible = visible;
		originalLength = length;
		originalAngle = angle;
		originalWidth = width;
		originalVisibility = visible;
	}
	
	public Bone(Bone copy) {
		this.drawMode = copy.drawMode;
		this.name = copy.name;
		this.sprite = copy.sprite;
		this.length = copy.length;
		this.width = copy.width;
		this.angle = copy.angle;
		this.visible = copy.visible;
		this.originalLength = copy.originalLength;
		this.originalAngle = copy.originalAngle;
		this.originalWidth = copy.originalWidth;
		this.originalVisibility = copy.originalVisibility;
		this.children = new ArrayList<>();
		for (Bone copyChild : copy.children) {
			this.children.add(new Bone(copyChild));
		}
	}

	/**
	 * Draws the bone and all of its children
	 *  - Call on the root of the structure to draw the entire fighter
	 * @param fighterColor 
	 *  
	 * @param g - Graphics object to draw with
	 * @param root - Position of the last node to draw from
	 * @param camera - Camera reference to get screen coordinates
	 */
	public void draw(Color fighterColor, Graphics2D g, int face, Position pos, Camera camera) {
		draw(fighterColor, g, face, pos, camera, false, "", "", 0);
	}
	
	/**
	 * Draws the bone and all of its children
	 *  - Call on the root of the structure to draw the entire fighter
	 * @param fighterColor 
	 *  
	 * @param g - Graphics object to draw with
	 * @param pos - Position of the last node to draw from
	 * @param camera - Camera reference to get screen coordinates
	 * @param drawHidden - Whether to draw hidden bones
	 * @param selected - Selected bone
	 */
	public void draw(Color fighterColor, Graphics2D g, int face, Position pos, Camera camera, boolean drawHidden, String selectedBone, String hoveredBone, int selectType) {
		double currentAngle = angle + interpolatedAngle;
		double currentLength = length + interpolatedLength;
		if (face < 0) {
			currentAngle = 180 - currentAngle;
		}
		if (selectedBone.equals("root")) {
			selectType = 2;
			selectedBone = "";
		} else if (hoveredBone.equals("root")) {
			selectType = 1;
			hoveredBone = "";
		}
		g.setColor(fighterColor);
		if (visible || drawHidden) {
			int screenX = camera.getScreenX(pos);
			int screenY = camera.getScreenY(pos);
			switch (drawMode) {
				case SPRITE:
					if (sprite != null) {
						sprite.draw(g, screenX, screenY, camera.getScale(), currentAngle);
					}
					break;
				case LINE:
					Graphics2D clone = (Graphics2D) g.create();
					AffineTransform a = new AffineTransform();
					a.concatenate(AffineTransform.getRotateInstance(Math.toRadians(-currentAngle), screenX, screenY));
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
				case CIRCLE:
					Graphics2D clone2 = (Graphics2D) g.create();
					AffineTransform a2 = new AffineTransform();
					a2.concatenate(AffineTransform.getRotateInstance(Math.toRadians(-currentAngle), screenX, screenY));
					clone2.setTransform(a2);
					int circleLength = camera.toPixels(currentLength);
					int circleWidth = camera.toPixels(width);
					if (selectType != 0) {
						clone2.setColor((selectType == 1) ? Color.GREEN : Color.BLUE);
					}
					if (!visible) {
						clone2.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, new float[] {5.0f}, 0.0f));
						clone2.drawOval(screenX - circleLength / 2, screenY - circleWidth / 2, circleLength, circleLength);
					} else {
						clone2.setStroke(new BasicStroke(circleWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
						clone2.drawOval(screenX, screenY - circleLength / 2, circleLength, circleLength);
					}
					break;
			}
		}
		Position tail = pos.applyVector(new Vector((float) (currentLength * Math.cos(Math.toRadians(currentAngle))),
				(float) (currentLength * Math.sin(Math.toRadians(currentAngle)))));
		if (attached != null) {
			attached.setPosition(tail); 
		}
		for (Bone bone : children) {
			int drawType = (bone.name.equals(selectedBone)) ? 2 : ((bone.name.equals(hoveredBone)) ? 1 : 0);
			bone.draw(fighterColor, g, face, tail, camera, drawHidden, selectedBone, hoveredBone, drawType);
		}
	}
	
	/**
	 * Adds a child node to the bone
	 * 
	 * @param identifier - Name of the bone (Must be unique)
	 * @param child - bone to add as a child
	 */
	public void addChild(Bone child) {
		children.add(child);
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
		for (Bone child : children) {
			if (child.name.equals(identifier)) {
				return child;
			} else if (child.getBone(identifier) != null) {
				return child.getBone(identifier);
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
	public void interpolate(double data, KeyframeType type, Interpolation interpolation, double completion) {
		switch (type) {
			case LENGTH:
				interpolatedLength = interpolation.getInterpolatedValue(length, data, completion);
				break;
			case ROTATE:
				if (data - angle >= 180) {
					data = (data - 360);
				} else if (angle - data >= 180) {
					data = (360 + data);
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
	public void applyInstruction(double data, KeyframeType type) {
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
			case VELOCITY_X:
				break;
			case VELOCITY_Y:
				break;
		}
	}

	/**
	 * Attaches a fighter to the bone
	 * 
	 * @param defender - Fighter being attached
	 */
	public void attachFighter(Fighter defender) {
		attached = defender;
	}

	/**
	 * Detaches the fighter from the bone
	 */
	public void release() {
		attached = null;
		for (Bone child : children) {
			child.release();
		}
	}
	
	/**
	 * Gets the children of the bone
	 * 
	 * @return - Children of the bone
	 */
	public List<Bone> getChildren() {
		return children;
	}
	
	/**
	 * Gets the position of a bone
	 * 
	 * @param boneId - Bone name
	 * @param root - Root position
	 * @param camera - Camera object
	 * @return - Position of bone
	 */
	public Position getPosition(String boneId, Position root, Camera camera) {
		if (name.equals(boneId)) {
			return root;
		}
		Position tail = root.applyVector(new Vector((float) (length * Math.cos(Math.toRadians(angle))),
				(float) (length * Math.sin(Math.toRadians(angle)))));
		for (Bone child : children) {
			if (child.getPosition(boneId, tail, camera) != null) {
				return child.getPosition(boneId, tail, camera); 
			}
		}
		return null;
	}

	/**
	 * Get the bone's name
	 * 
	 * @return - Bone name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Updates UI fields with the bone's data
	 * 
	 * @param nameField - Name textbox 
	 * @param lengthSpinner - Length spinner
	 * @param angleSpinner - Angle spinner
	 * @param widthSpinner - Width spinner
	 * @param visibleCheckbox - Visibility checkbox
	 * @param drawModeSelector - DrawMode combobox
	 */
	public void updateUIFields(JTextField nameField, JSpinner lengthSpinner, JSpinner angleSpinner,
			JSpinner widthSpinner, JCheckBox visibleCheckbox, JComboBox<DrawMode> drawModeSelector) {
		nameField.setText(name);
		lengthSpinner.setValue(length);
		angleSpinner.setValue(angle);
		widthSpinner.setValue(width);
		visibleCheckbox.setSelected(visible);
		drawModeSelector.setSelectedIndex(drawMode.ordinal());
	}
	
	/**
	 * Updates the internal values
	 *  - For the Editor
	 */
	public void updateValues(String name, DrawMode drawMode, double length, double width, double angle, boolean visible) {
		this.drawMode = drawMode;
		this.name = name;
		this.length = length;
		this.width = width;
		this.angle = angle;
		this.visible = visible;
		this.originalAngle = angle;
		this.originalLength = length;
		this.originalVisibility = visible;
		this.originalWidth = width;
	}

	/**
	 * Removes a bone from the skeleton
	 * 
	 * @param boneId - Bone to remove
	 * @return - Whether removal was successful
	 */
	public boolean removeBone(String boneId) {
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i).name.equals(boneId)) {
				children.remove(i);
				return true;
			} else if (children.get(i).removeBone(boneId)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Restores the bone to its original position
	 */
	public void restore() {
		angle = originalAngle;
		interpolatedAngle = 0;
		length = originalLength;
		interpolatedLength = 0;
		width = originalWidth;
		visible = originalVisibility;
		for (Bone child : children) {
			child.restore();
		}
	}
	
	public Map<String, Map<KeyframeType, Keyframe>> getStartPositions() {
		Map<String, Map<KeyframeType, Keyframe>> instructionSet = new HashMap<>();
		addStartInstructions(instructionSet);
		return instructionSet;
	}
	
	public Map<String, Map<KeyframeType, Keyframe>> getDefaultStartPositions() {
		restore();
		return getStartPositions();
	}

	private void addStartInstructions(Map<String, Map<KeyframeType, Keyframe>> list) {
		Map<KeyframeType, Keyframe> instructions = new HashMap<>();
		instructions.put(KeyframeType.LENGTH,
				new Keyframe(0, name, length + interpolatedLength, KeyframeType.LENGTH, Interpolation.NONE));
		instructions.put(KeyframeType.ROTATE,
				new Keyframe(0, name, angle + interpolatedAngle, KeyframeType.ROTATE, Interpolation.NONE));
		instructions.put(KeyframeType.VISIBLE,
				new Keyframe(0, name, visible ? 1 : 0, KeyframeType.VISIBLE, Interpolation.NONE));
		instructions.put(KeyframeType.VELOCITY_X,
				new Keyframe(0, name, 0, KeyframeType.VELOCITY_X, Interpolation.NONE));
		instructions.put(KeyframeType.VELOCITY_Y,
				new Keyframe(0, name, 0, KeyframeType.VELOCITY_Y, Interpolation.NONE));
		instructions.put(KeyframeType.IGNORE_VELOCITY_X,
				new Keyframe(0, name, 0, KeyframeType.IGNORE_VELOCITY_X, Interpolation.NONE));
		instructions.put(KeyframeType.IGNORE_VELOCITY_Y,
				new Keyframe(0, name, 0, KeyframeType.IGNORE_VELOCITY_Y, Interpolation.NONE));
		list.put(name, instructions);
		for (Bone child : children) {
			child.addStartInstructions(list);
		}
	}
	
	public List<String> getIdentifiers() {
		List<String> ids = new ArrayList<>();
		ids.add(name);
		for (Bone child : children) {
			ids.addAll(child.getIdentifiers());
		}
		return ids;
	}

	/**
	 * Loads a bone from a fighter file
	 * 
	 * @param s
	 *            - Fighter file scanner
	 * @return - Bone loaded
	 */
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

	public void save(PrintWriter pw, String whiteSpace, boolean endOfList) {
		pw.println(whiteSpace + "\"bone\": {");
		pw.println(whiteSpace + "\t\"name\": \"" + name + "\",");
		pw.println(whiteSpace + "\t\"drawMode\": " + drawMode.toString() + ",");
		pw.println(whiteSpace + "\t\"length\": " + length + ",");
		pw.println(whiteSpace + "\t\"width\": " + width + ",");
		pw.println(whiteSpace + "\t\"angle\": " + angle + ",");
		pw.println(whiteSpace + "\t\"visible\": " + visible + ",");
		pw.println(whiteSpace + "\t\"sprite\": \"\",");
		pw.print(whiteSpace + "\t\"children\": {");
		for(int i = 0; i < children.size(); i++) {
			pw.println();
			children.get(i).save(pw, whiteSpace + "\t\t", i == children.size() - 1);
		}
		pw.println(((children.size() == 0) ? "" : (whiteSpace + "\t")) + "}");
		if (endOfList) {
			pw.println(whiteSpace + "}");
		} else {
			pw.print(whiteSpace + "},");
		}
	}

	public double getAngle() {
		// TODO Auto-generated method stub
		return angle;
	}

	public double getOriginalAngle() {
		// TODO Auto-generated method stub
		return originalAngle;
	}
}
