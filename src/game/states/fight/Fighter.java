package game.states.fight;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import game.Game;
import game.input.InputSource;
import game.input.InputType;
import game.input.sources.DummySource;
import game.states.fight.animation.Animation;
import game.states.fight.animation.Interpolation;
import game.states.fight.animation.Keyframe;
import game.states.fight.animation.KeyframeType;
import game.states.fight.animation.SharedAnimation;
import game.states.fight.animation.collisions.ECB;
import game.states.fight.animation.collisions.HitBox;
import game.states.fight.animation.collisions.HurtBox;
import game.states.fight.fighter.Bone;
import game.util.Position;
import game.util.Vector;

/**
 * A Fighter either controlled by a Player or CPU
 * 
 * @author Fritz
 *
 */
public class Fighter {
	
	/**
	 * Name of the fighter
	 */
	private String name;
	
	/**
	 * Root node of component skeleton
	 */
	private Bone skeleton;
	
	/**
	 * Root node of component skeleton
	 */
	private Bone[] editorSkeletons;

	/**
	 * Map of all of the fighter's animations with identifiers
	 */
	private List<Animation> animations;
	
	/**
	 * Current animation of the fighter
	 */
	private Animation animation;
	
	/**
	 * The current position of the fighter
	 */
	private Position position;
	
	/**
	 * The current velocity of the fighter
	 */
	private Vector velocity;
	
	/**
	 * The current health of the fighter
	 */
	private double health;
	
	/**
	 * The number of frames the fighter is still in hitstun
	 */
	private int hitStun;
	
	/**
	 * List of groups of hitboxes that the fighter has hit with
	 *  - To be cleared on each animation switch
	 */
	private List<String> hitBy;
	
	protected InputSource inputSource;
	
	private boolean hasSetVelocityX;
	
	private boolean hasSetVelocityY;
	
	protected double gravity;
	
	protected double maxFallSpeed;
	
	/**
	 * Initializes a new fighter
	 * 
	 * @param name - Name of the fighter
	 * @param health - Amount of health the fighter has
	 * @param skeleton - Root Bone of the fighter's skeleton
	 */
	public Fighter(String name, double health, Bone skeleton) {
		this.name = name;
		this.health = health;
		this.skeleton = skeleton;
		this.editorSkeletons = new Bone[3];
		updateEditorSkeletons();
		animations = new ArrayList<>();
		velocity = new Vector(0, 0);
		position = new Position(0.25, 0);
		setAnimation(SharedAnimation.IDLE.toString());
	}
	
	public Fighter(Fighter copy) {
		this.name = copy.name;
		this.health = copy.health;
		this.skeleton = new Bone(copy.skeleton);
		this.editorSkeletons = new Bone[3];
		updateEditorSkeletons();
		animations = new ArrayList<>();
		for (Animation animation : copy.animations) {
			animations.add(new Animation(animation, false));
		}
		velocity = new Vector(0, 0);
		position = new Position(0.25, 0);
		setAnimation(SharedAnimation.IDLE.toString());
	}

	/**
	 * Draws the fighter in the game window
	 * 
	 * @param g - Graphics2d object used to draw
	 */
	public void draw(Graphics2D g, Camera camera, Stage stage) {
		if (animation != null) {
			animation.stepAnimation(this, skeleton, camera.getSpeed());
			if (animation.completed()) {
				animation = null;
				handleInputs();
			}
			animation.draw(position, this, skeleton, g, camera, stage, Game.DEBUG);
		}
	}
	
	/**
	 * Sets the animation of the fighter
	 * 
	 * @param newAnim - Identifier of the new animation
	 */
	public void setAnimation(String newAnim) {
		if (animation != null && animation.getName().equalsIgnoreCase(newAnim)) {
			return;
		}
		for (Animation anim : animations) {
			if (anim.getName().equalsIgnoreCase(newAnim)) {
				animation = anim;
				hasSetVelocityX = false;
				hasSetVelocityY = false;
				animation.setFrame(this, skeleton, 0);
			}
		}
	}
	
	/**
	 * Applies a hit to the fighter
	 * 
	 * @param damage - Damage done
	 * @param hitStun - Hitstun if successful hit
	 * @param blockStun - Hitstun if blocked
	 * @param pushBack - Amount of pushback from the hit
	 * @param launchVector - Resulting vector of the hit
	 * @param knockDown - Whether the hit knocks down
	 */
	public void applyHit(double damage, int hitStun, int blockStun, double pushBack, Vector launchVector, boolean knockDown) {
		skeleton.release();
		if (false /* Grounded */) {
			position = position.applyVector(new Vector(pushBack, 0));
			// Pushback source as well
			if (isBlocking()) {
				this.hitStun = blockStun;
				// do grey damage;
				// build guard break meter
				// setAnimation("block");
			} else {
				this.hitStun = hitStun;
				velocity.setX(launchVector.getX());
				velocity.setY(launchVector.getY());
				// setAnimation("grounded recoil");
				if (velocity.getY() > 0) {
					// setAnimation("air recoil")
					// make character rotate with trajectory
				}
				if (knockDown) {
					// kSet knockdown flag;
				}
			}
		} else {
			this.hitStun = hitStun;
			velocity.setX(launchVector.getX());
			velocity.setY(launchVector.getY());
			// setAnimation("air recoil")
			// make character rotate with trajectory
			if (knockDown) {
				// Set knockdown flag;
			}
		}
	}
	
	public void handleInputs() {
		String animName = "";
		boolean hasInput = inputSource.getLastInput() != null;
		if (animation != null) {
			animName = animation.getName();
		}
		boolean groundMovement = animName.equalsIgnoreCase(SharedAnimation.WALK_F.toString())
				|| animName.equalsIgnoreCase(SharedAnimation.WALK_B.toString())
				|| animName.equalsIgnoreCase(SharedAnimation.IDLE.toString()) || animName.equalsIgnoreCase("")
				|| animName.equalsIgnoreCase(SharedAnimation.IN_AIR.toString())
				|| animName.equalsIgnoreCase(SharedAnimation.CROUCH.toString());
		if (hasInput && groundMovement && inputSource.getLastInput().getTypes().contains(InputType.ATTACK_1)
				&& !inputSource.getLastInput().hasBeenUsed()) {
			setAnimation(SharedAnimation.PUNCH.toString());
			inputSource.getLastInput().setUsed();
		} else if (hasInput && groundMovement && inputSource.getLastInput().getTypes().contains(InputType.UP) && isGrounded()
				&& !inputSource.getLastInput().hasBeenUsed()) {
			if (inputSource.getLastInput().getTypes().contains(InputType.RIGHT)) {
				setAnimation(SharedAnimation.JUMPSQUAT_F.toString());
			} else if (inputSource.getLastInput().getTypes().contains(InputType.LEFT)) {
				setAnimation(SharedAnimation.JUMPSQUAT_B.toString());
			} else {
				setAnimation(SharedAnimation.JUMPSQUAT_N.toString());
			}
			inputSource.getLastInput().setUsed();
		} else if (hasInput && groundMovement && inputSource.getLastInput().getTypes().contains(InputType.DOWN) && isGrounded()) {
			setAnimation(SharedAnimation.CROUCH.toString());
		} else if (hasInput && groundMovement && inputSource.getLastInput().getTypes().contains(InputType.RIGHT) && isGrounded()) {
			setAnimation(SharedAnimation.WALK_F.toString());
		} else if (hasInput && groundMovement && inputSource.getLastInput().getTypes().contains(InputType.LEFT) && isGrounded()) {
			setAnimation(SharedAnimation.WALK_B.toString());
		} else if (hasInput && groundMovement && inputSource.getLastInput().getTypes().contains(InputType.LEFT) && isGrounded()) {
			setAnimation(SharedAnimation.WALK_B.toString());
		} else if (hasInput && groundMovement && isGrounded()
				&& (inputSource.getLastInput().getTypes().size() == 0 || inputSource.getLastInput().hasBeenUsed())) {
			setAnimation(SharedAnimation.IDLE.toString());
		} else if (!isGrounded() && hitStun <= 0) {
			setAnimation(SharedAnimation.IN_AIR.toString());
		}
	}
	
	/**
	 * Gets the fighter's ECB
	 * 
	 * @return - Fighter's current ECB
	 */
	public ECB getECB() {
		return animation.getActiveECB();
	}
	
	/**
	 * Gets the fighter's current hitboxes
	 * 
	 * @return - List of current hitboxes
	 */
	public List<HitBox> getHitBoxes() {
		return animation.getActiveHitBoxes();
	}

	/**
	 * Gets the fighter's current hurtboxes
	 * 
	 * @return - List of current hurtboxes
	 */
	public List<HurtBox> getHurtBoxes() {
		return animation.getActiveHurtBoxes();
	}

	/**
	 * Interpolate velocity 
	 * 
	 * @param data - New velocity
	 * @param type - Type of movement
	 * @param interpolation - Type of interpolation
	 * @param completion - % complete
	 */
	public void interpolateVelocity(double data, KeyframeType type, Interpolation interpolation, double completion) {
		switch (type) {
			case VELOCITY_X:
				//velocity.setX(interpolation.getInterpolatedValue(velocity.getX(), data, completion));
				if (completion >= 1) {
					velocity.setX(data);
				}
				break;
			case VELOCITY_Y:
				//velocity.setY(interpolation.getInterpolatedValue(velocity.getY(), data, completion));
				if (completion >= 1) {
					velocity.setY(data);
				}
				break;
			case IGNORE_VELOCITY_X:
				hasSetVelocityX = data != 0;
				break;
			case IGNORE_VELOCITY_Y:
				hasSetVelocityY = data != 0;
				break;
		}
	}

	/**
	 * Attaches a fighter to a specified bone
	 *  - For grabs
	 *  
	 * @param attachTo - Bone to attach to
	 * @param defender - Fighter being attached
	 */
	public void attach(String attachTo, Fighter defender) {
		skeleton.getBone(attachTo).attachFighter(defender);
	}
	
	/**
	 * Releases all attached fighters from the skeleton
	 */
	public void release() {
		skeleton.release();
	}

	/**
	 * Sets the fighter's position
	 * 
	 * @param position - New position
	 */
	public void setPosition(Position position) {
		this.position = position;
	}
	
	/**
	 * Gets whether the fighter is blocking
	 * @return
	 */
	public boolean isBlocking() {
		return false;
	}

	/**
	 * Gets the fighter's skeleton
	 * 
	 * @return - Fighter's skeleton
	 */
	public Bone getSkeleton() {
		return skeleton;
	}
	
	public Bone getEditorSkeleton(int panel) {
		return editorSkeletons[panel];
	}
	
	public void updateEditorSkeletons() {
		for (int i = 0; i < editorSkeletons.length; i++) {
			editorSkeletons[i] = new Bone(skeleton);
		}
	}

	/**
	 * Updates UI fields with the fighter's information
	 * 
	 * @param fighterName - Name textbox
	 * @param fighterHealth - Health spinner
	 */
	public void updateUIFields(JTextField fighterName, JSpinner fighterHealth) {
		fighterName.setText(name);
		fighterHealth.setValue(health);
	}
	
	/**
	 * Updates internal values
	 *  - For editor use
	 */
	public void updateValues(String name, double health) {
		this.name = name;
		this.health = health;
	}
	
	public void updateUIAnimationList(JComboBox<String> animationSelector, boolean emptySlot) {
		animationSelector.removeAllItems();
		if (emptySlot) {
			animationSelector.addItem("");
		}
		for (Animation anim : animations) {
			animationSelector.addItem(anim.getName());
		}
	}
	
	public Animation getAnimation(String animName) {
		for (Animation anim : animations) {
			if (anim.getName().equals(animName)) {
				return anim;
			}
		}
		return null;
	}

	public String newAnimation() {
		if (animations.size() == 0) {
			animations.add(new Animation("animation_0", skeleton.getDefaultStartPositions(), new ArrayList<HitBox>(),
					new ArrayList<HurtBox>(), new ArrayList<Keyframe>(), new ArrayList<ECB>()));
			return "animation_0";
		}
		for (int i = 0;; i++) {
			String name = "animation_" + i;
			for (Animation anim : animations) {
				if (!anim.getName().equals(name)) {
					animations.add(new Animation(name, skeleton.getDefaultStartPositions(), new ArrayList<HitBox>(),
							new ArrayList<HurtBox>(), new ArrayList<Keyframe>(), new ArrayList<ECB>()));
					return name;
				}
			}
		}
	}

	public String newAnimation(Animation copy) {
		for (int i = 0;; i++) {
			String name = "animation_" + i;
			for (Animation anim : animations) {
				if (!anim.getName().equals(name)) {
					animations.add(new Animation(copy, true));
					return animations.get(animations.size() - 1).getName();
				}
			}
		}
	}

	public String newAnimationFrom(Animation startFrom, int startFrame) {
		for (int i = 0;; i++) {
			String name = "animation_" + i;
			for (Animation anim : animations) {
				if (!anim.getName().equals(name)) {
					animations.add(new Animation(name, startFrom.getBonePositions(this, editorSkeletons[0], startFrame),
							new ArrayList<HitBox>(), new ArrayList<HurtBox>(), new ArrayList<Keyframe>(),
							new ArrayList<ECB>()));
					return name;
				}
			}
		}
	}
	
	public boolean isGrounded() {
		return position.getY() <= 0;
	}

	public List<Animation> getAnimations() {
		return animations;
	}
	
	public Vector getVelocity() {
		return velocity;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public boolean hasSetVelocityX() {
		return hasSetVelocityX;
	}
	
	public boolean hasSetVelocityY() {
		return hasSetVelocityY;
	}
	
	public double getGravity() {
		return gravity;
	}
	
	public double getMaxFallSpeed() {
		return maxFallSpeed;
	}

	/**
	 * Loads a fighter from a file
	 * 
	 * @param f - File to load from
	 * @return - Fighter loaded
	 * @throws FileNotFoundException
	 */
	public static Fighter load(File f) throws FileNotFoundException {
		Scanner s = new Scanner(f);
		String name = "";
		double health = 0;
		Bone skeleton = null;
		while(s.hasNextLine()) {
			String line = s.nextLine().trim();
			if (!line.contains(":")) {
				continue;
			}
			String type = line.substring(0, line.indexOf(":"));
			String data = line.substring(line.indexOf(":") + 1).replaceAll(",", "").replaceAll("\"", "").trim();
			if (type.contains("name")) {
				name = data;
			} else if (type.contains("health")) {
				health = Double.parseDouble(data);
			} else if (type.contains("skeleton")) {
				s.nextLine();
				skeleton = Bone.loadBone(s);
			}
		}
		Fighter fighter = new Fighter(name, health, skeleton);
		File animDir = new File(f.getParent() + "/animations/");
		if (animDir.exists()) {
			for (File animFile : animDir.listFiles()) {
				try {
					fighter.animations.add(Animation.load(animFile));
				} catch (Exception e) {
					System.out.println("Error loading animation " + animFile.getName());
					e.printStackTrace();
				}
			}
		} else {
			animDir.mkdirs();
		}
		return fighter;
	}
	
	/**
	 * Saves the fighter to a file
	 * 
	 * @param f - File to save to
	 * @throws IOException 
	 */
	public void save(File f) throws IOException {
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			f.createNewFile();
		}
		PrintWriter pw = new PrintWriter(new FileWriter(f));
		pw.println("{");
		pw.println("\t\"name\": \"" + name + "\",");
		pw.println("\t\"health\": " + health + ",");
		pw.println("\t\"skeleton\": {");
		skeleton.save(pw, "\t\t", true);
		pw.println("\t}");
		pw.print("}");
		pw.close();
		for (Animation animation : animations) {
			animation.save(new File(f.getParent() + "/animations/" + animation.getName() + ".json"));
		}
	}
	
}
