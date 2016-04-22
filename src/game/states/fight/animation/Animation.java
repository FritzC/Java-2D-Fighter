package game.states.fight.animation;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import game.util.Vector;
import game.states.fight.Camera;
import game.states.fight.Fighter;
import game.states.fight.Stage;
import game.states.fight.fighter.Bone;
import game.util.Box;
import game.util.Position;

/**
 * A sequence of Sprites
 * 
 * @author Fritz
 *
 */
public class Animation {

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
	 * Length in frames of the animation
	 */
	private int length;
	
	/**
	 * Loads an animation from a file
	 * 
	 * @param filePath - File path to load the animation from
	 */
	public Animation(Map<String, Map<KeyframeType, Keyframe>> initialPose, List<HitBox> hitboxes,
			List<HurtBox> hurtboxes, List<Keyframe> keyframes, List<ECB> ecbs) {
		this.initialPose = initialPose;
		this.ecbs = ecbs;
		this.hitboxes = hitboxes;
		this.hurtboxes = hurtboxes;
		this.steps = new ArrayList<>();
		this.queuedInterpolations = new HashMap<>();
		for (Keyframe frame : keyframes) {
			steps.add(frame);
			if (frame.getEndFrame() > length) {
				length = frame.getEndFrame();
			}
		}
	}
	
	/**
	 * Draws the animation's current step at a position
	 * 
	 * @param position - Position to draw the animation at
	 * @param g - Graphics2D object to draw
	 */
	public void draw(Position position, Fighter fighter, Bone root, Graphics2D g, Camera camera, Stage stage) {
		if (currentFrame > length) {
			currentFrame = 0;
		}
		stepAnimation(fighter, root, camera.getSpeed());
		root.draw(g, position, camera);
	}
	
	/**
	 * Steps the animation forward one frame
	 * 
	 * @param root - Skeleton of the fighter
	 */
	public void stepAnimation(Fighter fighter, Bone root, double speed) {
		Map<String, List<KeyframeType>> toRemove = new HashMap<>();
		for (String bone : queuedInterpolations.keySet()) {
			for (KeyframeType instruction : queuedInterpolations.get(bone).keySet()) {
				if (queuedInterpolations.get(bone).get(instruction).getCompletion(currentFrame) >= 1) {
					queuedInterpolations.get(bone).get(instruction).apply(root);
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
		currentFrame += speed;
	}
	
	public void setFrame(Fighter fighter, double frame) {
		currentFrame = frame;
		queuedInterpolations.clear();
		for (String boneId : queuedInterpolations.keySet()) {
			for (KeyframeType instruction : queuedInterpolations.get(boneId).keySet()) {
				initialPose.get(boneId).get(instruction).apply(fighter.getSkeleton());
			}
		}
		for (int i = 0; i < frame; i++) {
			stepAnimation(fighter, fighter.getSkeleton(), 1);
		}
		if (frame % 1 != 0) {
			stepAnimation(fighter, fighter.getSkeleton(), frame % 1);
		}
	}
	
	/**
	 * Gets the current step's hitboxes
	 * 
	 * @return - List of current hitboxes
	 */
	public List<HitBox> getHitBoxes() {
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
	public List<HurtBox> getHurtBoxes() {
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
	public ECB getECB() {
		for (ECB ecb : ecbs) {
			if (ecb.isActive(currentFrame)) {
				return ecb;
			}
		}
		return null;
	}
	
	public static Animation load(File f) throws FileNotFoundException {
		Scanner s = new Scanner(f);
		List<String> lines = new ArrayList<>();
		while(s.hasNextLine()) {
			lines.add(s.nextLine());
		}
		int length = 0;
		boolean loop = false;
		boolean specialCancelable = false;
		Map<String, Map<KeyframeType, Keyframe>> initialPose = new HashMap<>();
		List<Keyframe> keyframes = new ArrayList<>();
		List<ECB> ecbs = new ArrayList<>();
		List<HitBox> hitboxes = new ArrayList<>();
		List<HurtBox> hurtboxes = new ArrayList<>();
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).contains("}")) {
				continue;
			}
			String id = lines.get(i).split(":")[0].replaceAll("\"", "").trim();
			String data = lines.get(i).split(":")[1].replaceAll(",", "").replace("\"", "").trim();
			switch (id) {
				case "length":
					length = Integer.parseInt(data);
					break;
				case "loop":
					loop = Boolean.parseBoolean(data);
					break;
				case "specialCancelable":
					specialCancelable = Boolean.parseBoolean(data);
					break;
				case "initialPose":
					while (lines.get(++i).contains("bonePose")) {
						String bone = lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", "").replaceAll("\"", "");
						double boneLength = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						double boneAngle = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						boolean boneVisible = Boolean.parseBoolean(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1));
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
						String bone = lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", "").replaceAll("\"", "");
						int frame = Integer.parseInt(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						KeyframeType type = KeyframeType.forString(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						Interpolation interpolation = Interpolation.forString(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						double frameData = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						keyframes.add(new Keyframe(frame, bone, frameData, type, interpolation));
						i++;
					}
					break;
				case "ecbs":
					while (lines.get(++i).contains("ecb")) {
						int startFrame = Integer.parseInt(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						int endFrame = Integer.parseInt(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						i+=2;
						double topX = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						double topY = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						double botX = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						double botY = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						ecbs.add(new ECB(new Box(new Position(topX, topY), new Position(botX, botY))));
						i+=2;
					}
					break;
				case "hurtboxes":
					while (lines.get(++i).contains("hurtbox")) {
						int startFrame = Integer.parseInt(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						int endFrame = Integer.parseInt(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						i+=2;
						double topX = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						double topY = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						double botX = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						double botY = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						hurtboxes.add(new HurtBox(new Box(new Position(topX, topY), new Position(botX, botY))));
						i+=2;
					}
					break;
				case "hitboxes":
					while (lines.get(++i).contains("hitbox")) {
						String group = lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", "").replaceAll("\"", "");
						int startFrame = Integer.parseInt(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						int endFrame = Integer.parseInt(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						double damage = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						int blockStun = Integer.parseInt(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						int hitStun = Integer.parseInt(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						double pushBack = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						boolean knockDown = Boolean.parseBoolean(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1));
						i+=2;
						double topX = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						double topY = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						double botX = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						double botY = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						i+=4;
						double velX = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						double velY = Double.parseDouble(lines.get(++i).substring(lines.get(i).indexOf(" ") + 1).replace(",", ""));
						hitboxes.add(new HitBox(startFrame, endFrame, group, new Box(new Position(topX, topY), new Position(botX, botY)),
								damage, hitStun, blockStun, pushBack, new Vector(velX, velY), knockDown));
						i+=3;
					}
					break;
			}
		}
		Animation anim = new Animation(initialPose, hitboxes, hurtboxes, keyframes, ecbs);
		anim.ecbs = ecbs;
		return anim;
	}
	
	public void save(File f) {
		
	}
}
