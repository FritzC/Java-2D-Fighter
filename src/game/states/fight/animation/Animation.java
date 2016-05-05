package game.states.fight.animation;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import game.states.fight.Camera;
import game.states.fight.Fighter;
import game.states.fight.Stage;
import game.states.fight.animation.collisions.CollisionBox;
import game.states.fight.animation.collisions.ECB;
import game.states.fight.animation.collisions.HitBox;
import game.states.fight.animation.collisions.HitBoxType;
import game.states.fight.animation.collisions.HurtBox;
import game.states.fight.fighter.Bone;
import game.util.Box;
import game.util.Position;
import game.util.Vector;

/**
 * A sequence of Sprites
 * 
 * @author Fritz
 *
 */
public class Animation {
	
	private String name;

	/**
	 * List of all the animation steps
	 */
	private List<Keyframe> steps;
	
	/**
	 * List of initial bone positions
	 */
	private Map<String, Map<KeyframeType, Keyframe>> initialPose;
	
	/**
	 * List of queued interpolations
	 */
	private Map<String, Map<KeyframeType, Keyframe>> queuedInterpolations;
	
	/**
	 * List of ECBs and the frame they begin on
	 *  - ECB: Enviromental Collision Box
	 */
	private List<ECB> ecbs;
	
	/**
	 * List of the animation's hitboxes
	 */
	private List<HitBox> hitboxes;
	
	/**
	 * List of the animation's hurtboxes
	 */
	private List<HurtBox> hurtboxes;
	
	/**
	 * Whether the animation should loop
	 */
	private boolean loop;
	
	/**
	 * The current frame the animation is displaying
	 */
	private double currentFrame;
	
	/**
	 * Whether the animation is special cancelable
	 */
	private boolean specialCancelable;
	
	/**
	 * Loads an animation from a file
	 * 
	 * @param filePath - File path to load the animation from
	 */
	public Animation(String name, Map<String, Map<KeyframeType, Keyframe>> initialPose, List<HitBox> hitboxes,
			List<HurtBox> hurtboxes, List<Keyframe> keyframes, List<ECB> ecbs) {
		this.name = name;
		this.initialPose = initialPose;
		this.ecbs = ecbs;
		this.hitboxes = hitboxes;
		this.hurtboxes = hurtboxes;
		this.steps = new ArrayList<>();
		this.queuedInterpolations = new HashMap<>();
		this.steps = keyframes;
	}
	
	public Animation(Animation copy, boolean flagName) {
		this.name = copy.name + ((flagName) ? "_copy" : "");
		this.loop = copy.loop;
		this.specialCancelable = copy.specialCancelable;
		this.initialPose = new HashMap<>();
		this.queuedInterpolations = new HashMap<>();
		for (String bone : copy.initialPose.keySet()) {
			this.initialPose.put(bone, new HashMap<KeyframeType, Keyframe>());
			for (KeyframeType type : copy.initialPose.get(bone).keySet()) {
				this.initialPose.get(bone).put(type, new Keyframe(copy.initialPose.get(bone).get(type)));
			}
		}
		this.steps = new ArrayList<>();
		for (Keyframe step : copy.steps) {
			this.steps.add(new Keyframe(step));
		}
		this.ecbs = new ArrayList<>();
		for (ECB ecb : copy.ecbs) {
			this.ecbs.add(new ECB(ecb));
		}
		this.hitboxes = new ArrayList<>();
		for (HitBox hitbox : copy.hitboxes) {
			this.hitboxes.add(new HitBox(hitbox));
		}
		this.hurtboxes = new ArrayList<>();
		for (HurtBox hurtbox : copy.hurtboxes) {
			this.hurtboxes.add(new HurtBox(hurtbox));
		}
	}

	/**
	 * Draws the animation's current step at a position
	 * 
	 * @param position - Position to draw the animation at
	 * @param g - Graphics2D object to draw
	 */
	public void draw(Position position, Fighter fighter, Bone root, Graphics2D g, Camera camera, Stage stage, boolean debug) {
		draw(position, fighter, root, g, camera, stage, debug, null);
	}

	/**
	 * Draws the animation's current step at a position
	 * 
	 * @param position - Position to draw the animation at
	 * @param g - Graphics2D object to draw
	 */
	public void draw(Position position, Fighter fighter, Bone root, Graphics2D g, Camera camera, Stage stage,
			boolean debug, CollisionBox selected) {
		root.draw(g, position, camera);
		if (debug) {
			if (getActiveECB() != null) {
				getActiveECB().draw(position, camera, g, getActiveECB().equals(selected));
			}
			for (HurtBox hurtbox : getActiveHurtBoxes()) {
				hurtbox.draw(position, camera, g, hurtbox.equals(selected));
			}
			for (HitBox hitbox : getActiveHitBoxes()) {
				hitbox.draw(position, camera, g, hitbox.equals(selected));
			}
		}
	}

	public boolean completed() {
		return currentFrame > getLength();
	}
	
	/**
	 * Steps the animation forward one frame
	 * 
	 * @param root - Skeleton of the fighter
	 */
	public void stepAnimation(Fighter fighter, Bone root, double speed) {
		currentFrame += speed;
		if (currentFrame > getLength() && loop) {
			setFrame(fighter, root, 0);
			return;
		}
		for (Keyframe keyframe : steps) {
			keyframe.attemptToRegister(currentFrame, queuedInterpolations);
		}
		Map<String, List<KeyframeType>> toRemove = new HashMap<>();
		for (String bone : queuedInterpolations.keySet()) {
			for (KeyframeType instruction : queuedInterpolations.get(bone).keySet()) {
				if (queuedInterpolations.get(bone).get(instruction).getCompletion(currentFrame) >= 1) {
					queuedInterpolations.get(bone).get(instruction).apply(fighter, root);
					if (!toRemove.containsKey(bone)) {
						toRemove.put(bone, new ArrayList<KeyframeType>());
					}
					toRemove.get(bone).add(instruction);
				}
			}
		}
		for (String bone : toRemove.keySet()) {
			for (KeyframeType instruction : toRemove.get(bone)) {
				queuedInterpolations.get(bone).remove(instruction);
			}
		}
		for (Keyframe keyframe : steps) {
			keyframe.attemptToRegister(currentFrame, queuedInterpolations);
		}
		for (String bone : queuedInterpolations.keySet()) {
			for (Keyframe keyframe : queuedInterpolations.get(bone).values()) {
				keyframe.interpolate(fighter, root, currentFrame);
			}
		}
	}
	
	public void setFrame(Fighter fighter, Bone root, double frame) {
		currentFrame = 0;
		queuedInterpolations.clear();
		for (String boneId : initialPose.keySet()) {
			for (KeyframeType instruction : initialPose.get(boneId).keySet()) {
				if (instruction != KeyframeType.VELOCITY_X && instruction != KeyframeType.VELOCITY_Y) {
					initialPose.get(boneId).get(instruction).apply(fighter, root);
				}
			}
		}
		stepAnimation(fighter, root, 0);
		for (int i = 0; i < (int) frame; i++) {
			stepAnimation(fighter, root, 1);
		}
		if (frame % 1 != 0) {
			stepAnimation(fighter, root, frame % 1);
		}
	}
	
	/**
	 * Gets the current step's hitboxes
	 * 
	 * @return - List of current hitboxes
	 */
	public List<HitBox> getActiveHitBoxes() {
		List<HitBox> active = new ArrayList<>();
		for (HitBox hitBox : hitboxes) {
			if (hitBox.isActive(currentFrame)) {
				active.add(hitBox);
			}
		}
		return active;
	}

	/**
	 * Gets the current step's hurtboxes
	 * 
	 * @return - List of current hurtboxes
	 */
	public List<HurtBox> getActiveHurtBoxes() {
		List<HurtBox> active = new ArrayList<>();
		for (HurtBox hurtBox : hurtboxes) {
			if (hurtBox.isActive(currentFrame)) {
				active.add(hurtBox);
			}
		}
		return active;
	}

	/**
	 * Gets the animation's current ECB
	 * 
	 * @return - Current ECB
	 */
	public ECB getActiveECB() {
		for (ECB ecb : ecbs) {
			if (ecb.isActive(currentFrame)) {
				return ecb;
			}
		}
		return null;
	}
	
	public double getCurrentFrame() {
		return currentFrame;
	}
	
	public List<Keyframe> getKeyframes() {
		return steps;
	}

	public Keyframe getKeyframe(Object[] info) {
		for (Keyframe frame : steps) {
			if (frame.matchesInfo(info)) {
				return frame;
			}
		}
		return null;
	}
	
	public List<HurtBox> getHurtboxes() {
		return hurtboxes;
	}
	
	public List<ECB> getECBs() {
		return ecbs;
	}
	
	public List<HitBox> getHitboxes() {
		return hitboxes;
	}

	public void setName(String newName) {
		name = newName;
	}
	
	public String getName() {
		return name;
	}

	public void updateValues(String name, boolean loop, boolean specialCancelable) {
		this.name = name;
		this.loop = loop;
		this.specialCancelable = specialCancelable;
	}
	
	public void updateUIFields(JTextField name, JCheckBox loop, JCheckBox specialCancelable) {
		name.setText(this.name);
		loop.setSelected(this.loop);
		specialCancelable.setSelected(this.specialCancelable);
	}

	public Map<String, Map<KeyframeType, Keyframe>> getBonePositions(Fighter fighter, Bone root, int startFrame) {
		setFrame(fighter, root, startFrame);
		return root.getStartPositions();
	}
	
	public int getLength() {
		int length = 0;
		for (Keyframe step : steps) {
			if (step.getEndFrame() > length) {
				length = step.getEndFrame();
			}
		}
		return length;
	}
	
	public CollisionBox getCollisionBox(String type, Object[] data) {
		switch (type) {
			case "ECBs":
				for (ECB ecb : ecbs) {
					if (ecb.isEqual(data)) {
						return ecb;
					}
				}
				break;
			case "Hurtboxes":
				for (HurtBox hurtbox : hurtboxes) {
					if (hurtbox.isEqual(data)) {
						return hurtbox;
					}
				}
				break;
			case "Hitboxes":
				for (HitBox hitbox : hitboxes) {
					if (hitbox.isEqual(data)) {
						return hitbox;
					}
				}
				break;
		}
		return null;
	}
	
	public static Animation load(File f) throws FileNotFoundException {
		Scanner s = new Scanner(f);
		List<String> lines = new ArrayList<>();
		while(s.hasNextLine()) {
			lines.add(s.nextLine());
		}
		boolean loop = false;
		boolean specialCancelable = false;
		Map<String, Map<KeyframeType, Keyframe>> initialPose = new HashMap<>();
		List<Keyframe> keyframes = new ArrayList<>();
		List<ECB> ecbs = new ArrayList<>();
		List<HitBox> hitboxes = new ArrayList<>();
		List<HurtBox> hurtboxes = new ArrayList<>();
		for (int i = 1; i < lines.size(); i++) {
			if (lines.get(i).contains("}")) {
				continue;
			}
			String id = lines.get(i).split(":")[0].replaceAll("\"", "").trim();
			String data = lines.get(i).split(":")[1].replaceAll(",", "").replace("\"", "").trim();
			switch (id) {
				case "loop":
					loop = Boolean.parseBoolean(data);
					break;
				case "specialCancelable":
					specialCancelable = Boolean.parseBoolean(data);
					break;
				case "initialPose":
					while (lines.get(++i).contains("bonePose")) {
						String bone = lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", "").replaceAll("\"", "");
						double boneLength = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						double boneAngle = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						boolean boneVisible = Boolean.parseBoolean(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2));
						Map<KeyframeType, Keyframe> bonePose = new HashMap<>();
						bonePose.put(KeyframeType.LENGTH, new Keyframe(0, bone, boneLength, KeyframeType.LENGTH, Interpolation.NONE));
						bonePose.put(KeyframeType.ROTATE, new Keyframe(0, bone, boneAngle, KeyframeType.ROTATE, Interpolation.NONE));
						bonePose.put(KeyframeType.VISIBLE, new Keyframe(0, bone, boneVisible ? 1 : 0, KeyframeType.VISIBLE, Interpolation.NONE));
						initialPose.put(bone, bonePose);
						i++;
					}
					break;
				case "steps":
					while (lines.get(++i).contains("keyframe")) {
						String bone = lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", "").replaceAll("\"", "");
						int frame = Integer.parseInt(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						KeyframeType type = KeyframeType.forString(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						Interpolation interpolation = Interpolation.forString(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						double frameData = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						keyframes.add(new Keyframe(frame, bone, frameData, type, interpolation));
						i++;
					}
					break;
				case "ecbs":
					while (lines.get(++i).contains("ecb")) {
						int startFrame = Integer.parseInt(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						int endFrame = Integer.parseInt(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						i+=2;
						double topX = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						double topY = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						double botX = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						double botY = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						ecbs.add(new ECB(startFrame, endFrame, new Box(new Position(topX, topY), new Position(botX, botY))));
						i+=3;
					}
					break;
				case "hurtboxes":
					while (lines.get(++i).contains("hurtbox")) {
						int startFrame = Integer.parseInt(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						int endFrame = Integer.parseInt(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						i+=2;
						double topX = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						double topY = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						double botX = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						double botY = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						hurtboxes.add(new HurtBox(startFrame, endFrame, new Box(new Position(topX, topY), new Position(botX, botY))));
						i+=3;
					}
					break;
				case "hitboxes":
					while (lines.get(++i).contains("hitbox")) {
						String group = lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", "").replaceAll("\"", "");
						int startFrame = Integer.parseInt(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						int endFrame = Integer.parseInt(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						int damage = Integer.parseInt(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						int blockStun = Integer.parseInt(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						int hitStun = Integer.parseInt(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						double pushBack = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						boolean knockDown = Boolean.parseBoolean(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						HitBoxType type = HitBoxType.forString(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						boolean release = Boolean.parseBoolean(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						String attachTo = lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", "").replaceAll("\"", "");
						String triggerAnim = lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", "").replaceAll("\"", "");
						String triggerTargetAnim = lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", "").replaceAll("\"", "");
						i+=2;
						double topX = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						double topY = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						double botX = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						double botY = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						i+=4;
						double velX = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						double velY = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(": ") + 2).replace(",", ""));
						hitboxes.add(new HitBox(startFrame, endFrame, group,
								new Box(new Position(topX, topY), new Position(botX, botY)), damage, hitStun, blockStun,
								pushBack, new Vector(velX, velY), knockDown, type, release, attachTo, triggerAnim,
								triggerTargetAnim));
						i += 3;
					}
					break;
			}
		}
		Animation anim = new Animation(f.getName().replace(".json", ""), initialPose, hitboxes, hurtboxes, keyframes,
				ecbs);
		anim.loop = loop;
		anim.specialCancelable = specialCancelable;
		s.close();
		return anim;
	}
	
	public void save(File f) throws IOException {
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			f.createNewFile();
		}
		PrintWriter pw = new PrintWriter(new FileWriter(f));
		pw.println("{");
		pw.println("\t\"loop\": " + loop + ",");
		pw.println("\t\"specialCancelable\": " + specialCancelable + ",");
		pw.println("\t\"initialPose\": {");
		for (String bone : initialPose.keySet()) {
			pw.println("\t\t\"bonePose\": {");
			pw.println("\t\t\t\"name\": \"" + bone + "\",");
			pw.println("\t\t\t\"length\": " + initialPose.get(bone).get(KeyframeType.LENGTH).getInfo()[4] + ",");
			pw.println("\t\t\t\"angle\": " + initialPose.get(bone).get(KeyframeType.ROTATE).getInfo()[4] + ",");
			pw.println("\t\t\t\"visible\": " + ((double) initialPose.get(bone).get(KeyframeType.VISIBLE).getInfo()[4] == 1));
			pw.println("\t\t},");
		}
		pw.println("\t},");
		pw.println("\t\"steps\": {");
		for (Keyframe keyframe : steps) {
			pw.println("\t\t\"keyframe\": {");
			pw.println("\t\t\t\"bone\": \"" + keyframe.getInfo()[1] + "\",");
			pw.println("\t\t\t\"frame\": " + keyframe.getInfo()[0] + ",");
			pw.println("\t\t\t\"type\": " + keyframe.getInfo()[2] + ",");
			pw.println("\t\t\t\"interpolation\": " + keyframe.getInfo()[3] + ",");
			pw.println("\t\t\t\"data\": " + keyframe.getInfo()[4]);
			pw.println("\t\t},");
		}
		pw.println("\t},");
		pw.println("\t\"ecbs\": {");
		for (ECB ecb : ecbs) {
			pw.println("\t\t\"ecb\": {");
			pw.println("\t\t\t\"startFrame\": " + ecb.getStartFrame() + ",");
			pw.println("\t\t\t\"endFrame\": " + ecb.getEndFrame() + ",");
			pw.println("\t\t\t\"collision\": {");
			pw.println("\t\t\t\t\"box\": {");
			pw.println("\t\t\t\t\t\"top_l x\": " + ecb.getTopLeft().getX() + ",");
			pw.println("\t\t\t\t\t\"top_l y\": " + ecb.getTopLeft().getY() + ",");
			pw.println("\t\t\t\t\t\"bottom_r x\": " + ecb.getBottomRight().getX() + ",");
			pw.println("\t\t\t\t\t\"bottom_r y\": " + ecb.getBottomRight().getY());
			pw.println("\t\t\t\t}");
			pw.println("\t\t\t}");
			pw.println("\t\t},");
		}
		pw.println("\t},");
		pw.println("\t\"hurtboxes\": {");
		for (HurtBox hurtbox : hurtboxes) {
			pw.println("\t\t\"hurtbox\": {");
			pw.println("\t\t\t\"startFrame\": " + hurtbox.getStartFrame() + ",");
			pw.println("\t\t\t\"endFrame\": " + hurtbox.getEndFrame() + ",");
			pw.println("\t\t\t\"collision\": {");
			pw.println("\t\t\t\t\"box\": {");
			pw.println("\t\t\t\t\t\"top_l x\": " + hurtbox.getTopLeft().getX() + ",");
			pw.println("\t\t\t\t\t\"top_l y\": " + hurtbox.getTopLeft().getY() + ",");
			pw.println("\t\t\t\t\t\"bottom_r x\": " + hurtbox.getBottomRight().getX() + ",");
			pw.println("\t\t\t\t\t\"bottom_r y\": " + hurtbox.getBottomRight().getY());
			pw.println("\t\t\t\t}");
			pw.println("\t\t\t}");
			pw.println("\t\t},");
		}
		pw.println("\t},");
		pw.println("\t\"hitboxes\": {");
		for (HitBox hitbox : hitboxes) {
			hitbox.save(f, pw);
		}
		pw.println("\t},");
		pw.println("}");
		pw.close();
	}
}
