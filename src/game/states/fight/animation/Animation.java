package game.states.fight.animation;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 * List of queued interpolations
	 */
	private Map<String, Map<KeyframeType, Keyframe>> queuedInterpolations;
	
	/**
	 * List of ECBs and the frame they begin on
	 *  - ECB: Enviromental Collision Box
	 */
	private Map<Integer, Box> ecbs;
	
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
	private int currentFrame;
	
	/**
	 * Number of ticks until currentFrame advances
	 */
	private int ticksUntilNextFrame;
	
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
	public Animation(List<HitBox> hitboxes, List<HurtBox> hurtboxes, List<Keyframe> keyframes) {
		steps = new ArrayList<>();
		queuedInterpolations = new HashMap<>();
		ecbs = new HashMap<>();
		this.hitboxes = hitboxes;
		this.hurtboxes = hurtboxes;
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
		stepAnimation(fighter, root);
		root.draw(g, position, camera);
	}
	
	/**
	 * Steps the animation forward one frame
	 * 
	 * @param root - Skeleton of the fighter
	 */
	public void stepAnimation(Fighter fighter, Bone root) {
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
		currentFrame++;
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
	public Box getECB() {
		int frame = currentFrame + 1;
		while (!ecbs.containsKey(--frame)) {}
		return ecbs.get(frame);
	}
}
