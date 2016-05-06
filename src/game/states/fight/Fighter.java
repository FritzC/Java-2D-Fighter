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
import game.Sounds;
import game.input.InputSource;
import game.input.InputType;
import game.states.fight.animation.Animation;
import game.states.fight.animation.Interpolation;
import game.states.fight.animation.Keyframe;
import game.states.fight.animation.KeyframeType;
import game.states.fight.animation.SharedAnimation;
import game.states.fight.animation.collisions.ECB;
import game.states.fight.animation.collisions.HitBox;
import game.states.fight.animation.collisions.HitBoxType;
import game.states.fight.animation.collisions.HurtBox;
import game.states.fight.fighter.Bone;
import game.util.MakeSound;
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
	
	private int face;
	
	private boolean needFaceCheck;
	
	/**
	 * The current velocity of the fighter
	 */
	private Vector velocity;
	
	/**
	 * The health of the fighter
	 */
	private double health;
	
	/**
	 * The current health of the fighter
	 */
	private double currentHealth;
	
	private double comboDamage;
	
	private double greyHealth;
	
	/**
	 * The number of frames the fighter is still in hitstun
	 */
	private int hitStun;
	
	/**
	 * List of groups of hitboxes that the fighter has hit with
	 *  - To be cleared on each animation switch
	 */
	private List<String> deadHitboxes;
	
	protected InputSource inputSource;
	
	private boolean hasSetVelocityX;
	
	private boolean hasSetVelocityY;
	
	protected double gravity;
	
	protected double maxFallSpeed;
	
	private boolean knockedDown;
	
	private boolean grabbed;
	
	private int comboCount;
	
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
		this.currentHealth = health;
		this.skeleton = skeleton;
		this.editorSkeletons = new Bone[3];
		updateEditorSkeletons();
		animations = new ArrayList<>();
		velocity = new Vector(0, 0);
		position = new Position(0.25, 0);
		face = 1;
		deadHitboxes = new ArrayList<>();
		setAnimation(SharedAnimation.IDLE.toString(), false);
	}
	
	public Fighter(Fighter copy) {
		this.name = copy.name;
		this.health = copy.health;
		this.currentHealth = copy.currentHealth;
		this.skeleton = new Bone(copy.skeleton);
		this.editorSkeletons = new Bone[3];
		updateEditorSkeletons();
		animations = new ArrayList<>();
		for (Animation animation : copy.animations) {
			animations.add(new Animation(animation, false));
		}
		velocity = new Vector(0, 0);
		position = new Position(0.25, 0);
		face = copy.face;
		deadHitboxes = new ArrayList<>();
		setAnimation(SharedAnimation.IDLE.toString(), false);
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
	public void setAnimation(String newAnim, boolean reset) {
		if (animation != null && animation.getName().equalsIgnoreCase(newAnim) && !reset) {
			return;
		}
		for (Animation anim : animations) {
			if (anim.getName().equalsIgnoreCase(newAnim)) {
				deadHitboxes.clear();
				animation = anim;
				if (isGrounded() || hitStun != 0) {
					needFaceCheck = true;
				}
				hasSetVelocityX = false;
				hasSetVelocityY = false;
				animation.setFrame(this, skeleton, 0);
			}
		}
	}

	public void dealHit(Fighter defender, double pushBack, HitBox hitBox, boolean success) {
		position = position.applyVector(new Vector(-face * pushBack / 2, 0));
		if (success) {
			comboCount ++;
		}
	}
	
	public boolean applyHit(Fighter attacker, String name, HitBoxType type, int damage, int hitStun, int blockStun, double pushBack,
			Vector launchVector, String attachTo, String triggerAnimation, String triggerTargetAnimation,
			boolean release, boolean knockDown) {
		if (attacker.deadHitboxes.contains(name)) {
			return false;
		}
		attacker.deadHitboxes.add(name);
		if (release) {
			grabbed = false;
			attacker.skeleton.release();
		}
		if (knockDown) {
			this.knockedDown = knockDown;
		}
		if (isGrounded()) {
			position = position.applyVector(new Vector(-face * pushBack / 2, 0));
			if ((isBlocking(type) && type != HitBoxType.GRAB) || type == HitBoxType.AIR_GRAB) {
				this.hitStun = blockStun;
				// build guard break meter
				setAnimation(isCrouching() ? SharedAnimation.BLOCK_CR.toString() : SharedAnimation.BLOCK_ST.toString(), true);
				if (damage > currentHealth) {
					damage = (int) currentHealth;
				}
				Sounds.playSound(Sounds.BLOCK_PUNCH);
				currentHealth -= damage;
				greyHealth += damage;
				return false;
			} else {
				if (hitStun != 0) {
					comboDamage += damage;
				}
				if (!attachTo.equals("")) {
					attacker.skeleton.getBone(attachTo).attachFighter(this);
				}
				this.hitStun = hitStun;
				if (!triggerTargetAnimation.equals("")) {
					setAnimation(triggerTargetAnimation, false);
				} else {
					setAnimation(isCrouching() ? SharedAnimation.HIT_CR.toString() : SharedAnimation.HIT_ST.toString(), true);
				}
				velocity.setX(launchVector.getX());
				velocity.setY(launchVector.getY());
				position.applyVector(velocity);
				if (position.getY() > 0) {
					// setAnimation("air recoil")
					// make character rotate with trajectory
				}
				Sounds.playSound(Sounds.PUNCH);
				currentHealth -= damage;
				greyHealth = 0;
				if (currentHealth < 0) {
					currentHealth = 0;
				}
				return true;
			}
		} else {
			if (hitStun != 0) {
				comboDamage += damage;
			}
			this.hitStun = -hitStun;
			velocity.setX(launchVector.getX());
			velocity.setY(launchVector.getY());
			// setAnimation("air recoil")
			// make character rotate with trajectory
			currentHealth -= damage;
			greyHealth = 0;
			if (currentHealth < 0) {
				currentHealth = 0;
			}
			Sounds.playSound(Sounds.PUNCH);
			return true;
		}
	}

	public void handleInputs() {
		if (hitStun != 0) {
			return;
		}
		String animName = "";
		boolean hasInput = inputSource.getLastInput() != null;
		InputType forward = (face == 1) ? InputType.RIGHT : InputType.LEFT;
		InputType backward = (face == 1) ? InputType.LEFT : InputType.RIGHT;
		if (animation != null) {
			animName = animation.getName();
		}
		boolean groundMovement = animName.equalsIgnoreCase(SharedAnimation.WALK_F.toString())
				|| animName.equalsIgnoreCase(SharedAnimation.WALK_B.toString())
				|| animName.equalsIgnoreCase(SharedAnimation.IDLE.toString()) || animName.equalsIgnoreCase("")
				|| animName.equalsIgnoreCase(SharedAnimation.IN_AIR.toString())
				|| animName.equalsIgnoreCase(SharedAnimation.CROUCH.toString())
				|| animName.equalsIgnoreCase(SharedAnimation.HIT_CR.toString())
				|| animName.equalsIgnoreCase(SharedAnimation.HIT_ST.toString())
				|| animName.equalsIgnoreCase(SharedAnimation.BLOCK_CR.toString())
				|| animName.equalsIgnoreCase(SharedAnimation.BLOCK_ST.toString());
		if (hasInput && groundMovement && inputSource.getLastInput().getTypes().contains(InputType.ATTACK_1)
				&& !inputSource.getLastInput().hasBeenUsed() && isGrounded()) {
			setAnimation(SharedAnimation.LP_ST.toString(), false);
			inputSource.getLastInput().setUsed();
		} else if (hasInput && groundMovement && inputSource.getLastInput().getTypes().contains(InputType.ATTACK_2)
				&& !inputSource.getLastInput().hasBeenUsed() && isGrounded()) {
			setAnimation(SharedAnimation.MP_ST.toString(), false);
			inputSource.getLastInput().setUsed();
		} else if (hasInput && (groundMovement || animation.isSpecialCancelable() && deadHitboxes.size() > 0)
				&& inputSource.getLastInput().getTypes().contains(InputType.ATTACK_3)
				&& !inputSource.getLastInput().hasBeenUsed() && isGrounded()) {
			setAnimation(SharedAnimation.HP_ST.toString(), false);
			inputSource.getLastInput().setUsed();
		} else if (hasInput && groundMovement && inputSource.getPreviousInput(0).getTypes().contains(forward)
				&& !inputSource.getPreviousInput(1).getTypes().contains(forward)
				&& inputSource.getPreviousInput(2).getTypes().contains(forward)
				&& inputSource.getPreviousInput(2).getDelay() < 30
				&& !inputSource.getLastInput().hasBeenUsed() && isGrounded()) {
			setAnimation(SharedAnimation.DASH_F.toString(), false);
			inputSource.getLastInput().setUsed();
		} else if (hasInput && groundMovement && inputSource.getPreviousInput(0).getTypes().contains(backward)
				&& !inputSource.getPreviousInput(1).getTypes().contains(backward)
				&& inputSource.getPreviousInput(2).getTypes().contains(backward)
				&& inputSource.getPreviousInput(2).getDelay() < 30
				&& !inputSource.getLastInput().hasBeenUsed() && isGrounded()) {
			setAnimation(SharedAnimation.DASH_B.toString(), false);
			inputSource.getLastInput().setUsed();
		} else if (hasInput && groundMovement && inputSource.getLastInput().getTypes().contains(InputType.UP)
				&& isGrounded() && !inputSource.getLastInput().hasBeenUsed()) {
			if (inputSource.getLastInput().getTypes().contains(forward)) {
				setAnimation(SharedAnimation.JUMPSQUAT_F.toString(), false);
			} else if (inputSource.getLastInput().getTypes().contains(backward)) {
				setAnimation(SharedAnimation.JUMPSQUAT_B.toString(), false);
			} else {
				setAnimation(SharedAnimation.JUMPSQUAT_N.toString(), false);
			}
			inputSource.getLastInput().setUsed();
		} else if (hasInput && groundMovement && inputSource.getLastInput().getTypes().contains(InputType.DOWN) && isGrounded()) {
			setAnimation(SharedAnimation.CROUCH.toString(), false);
		} else if (hasInput && groundMovement && inputSource.getLastInput().getTypes().contains(forward) && isGrounded()) {
			setAnimation(SharedAnimation.WALK_F.toString(), false);
		} else if (hasInput && groundMovement && inputSource.getLastInput().getTypes().contains(backward) && isGrounded()) {
			setAnimation(SharedAnimation.WALK_B.toString(), false);
		} else if (hasInput && groundMovement && isGrounded()
				&& (inputSource.getLastInput().getTypes().size() == 0 || inputSource.getLastInput().hasBeenUsed())) {
			setAnimation(SharedAnimation.IDLE.toString(), false);
		} else if (!isGrounded() && !animName.equalsIgnoreCase(SharedAnimation.HP_ST.toString())) {
			if (inputSource.getLastInput().getTypes().contains(InputType.ATTACK_3)) {
				setAnimation(SharedAnimation.HP_AIR.toString(), false);
			} else if (!animName.equalsIgnoreCase(SharedAnimation.HP_AIR.toString())) {
				setAnimation(SharedAnimation.IN_AIR.toString(), false);
			}
		}
	}

	public void reset() {
		currentHealth = health;
		comboCount = 0;
		comboDamage = 0;
		greyHealth = 0;
		velocity = new Vector(0, 0);
		setAnimation(SharedAnimation.IDLE.toString(), true);
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
					velocity.setX(face * data);
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
			if (anim.getName().equalsIgnoreCase(animName)) {
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
	
	public boolean isGrabbed() {
		return grabbed;
	}

	public boolean isBlocking(HitBoxType hitType) {
		if (animation != null && inputSource != null && inputSource.getLastInput() != null
				&& (animation.getName().equalsIgnoreCase(SharedAnimation.IDLE.toString())
						|| animation.getName().equalsIgnoreCase(SharedAnimation.CROUCH.toString())
						|| animation.getName().equalsIgnoreCase(SharedAnimation.BLOCK_ST.toString())
						|| animation.getName().equalsIgnoreCase(SharedAnimation.BLOCK_CR.toString())
						|| animation.getName().equalsIgnoreCase(SharedAnimation.WALK_B.toString()))) {
			InputType direction = (face == 1) ? InputType.LEFT : InputType.RIGHT;
			if (inputSource.getLastInput().getTypes().contains(direction)) {
				return (isCrouching()) ? hitType != HitBoxType.HIGH : hitType != HitBoxType.LOW;
			}
		}
		return false;
	}

	private boolean isCrouching() {
		return animation != null && (animation.getName().toLowerCase().contains("_cr")
				|| animation.getName().toLowerCase().contains("crouch"));
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
	
	public int getFace() {
		return face;
	}
	
	public void setFace(int value) {
		face = (int) Math.signum(value);
	}
	
	public boolean needFaceCheck() {
		return needFaceCheck || animation.getName().equalsIgnoreCase(SharedAnimation.IDLE.toString());
	}

	public boolean needsKnockdown() {
		return knockedDown;
	}
	
	public boolean usingQuickGetUp() {
		return inputSource.getLastInput().getTypes().contains(InputType.UP);
	}
	
	public int getHitStun() {
		return hitStun;
	}
	
	public void setHitStun(int newStun) {
		hitStun = newStun;
	}
	
	public int getComboCount() {
		return comboCount;
	}
	
	public void setComboCount(int amount) {
		comboCount = amount;
	}
	
	public double getGreyHealthPercent() {
		return greyHealth / health;
	}
	
	public double getComboPercent() {
		return comboDamage / health;
	}

	public double getHealthPercent() {
		return currentHealth / health;
	}
	
	public void hpTick() {
		if (currentHealth < 0) {
			currentHealth = 0;
		}
		if (greyHealth > 0 && Game.tick % 2 == 0) {
			currentHealth += 1;
			greyHealth --;
		}
	}
	
	public void resetCombo() {
		comboDamage = 0;
	}
	
	public void resetFaceCheck() {
		needFaceCheck = false;
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

	public void resetKnockdown() {
		knockedDown = false;

	}
	
}
