package game.states.fight;

import java.awt.Color;
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
import game.input.InputTaker;
import game.input.InputType;
import game.input.StickInput;
import game.input.StickInputType;
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
import game.util.Position;
import game.util.Vector;

/**
 * A Fighter either controlled by a Player or CPU
 * 
 * @author Fritz
 *
 */
public class Fighter implements InputTaker {
	
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
	
	private int animFace;
	
	private int realFace;
	
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
	private double hitStun;
	
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
	
	private Color fighterColor;
	
	private int[] decrementAmts;
	
	private int slowDownRemaining;
	
	private int juggleCount;
	
	private double meter;
	
	private boolean blockInput;
	
	private int jumpsLeft;
	
	private boolean counterHit;
	
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
		animFace = 1;
		deadHitboxes = new ArrayList<>();
		fighterColor = Color.BLACK;
		decrementAmts = new int[3];
		setAnimation(SharedAnimation.IDLE.toString(), false);
		jumpsLeft = 2;
		counterHit = false;
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
		animFace = copy.animFace;
		deadHitboxes = new ArrayList<>();
		fighterColor = Color.BLACK;
		decrementAmts = new int[3];
		setAnimation(SharedAnimation.IDLE.toString(), false);
		jumpsLeft = 2;
		counterHit = false;
	}

	/**
	 * Draws the fighter in the game window
	 * 
	 * @param g - Graphics2d object used to draw
	 */
	public void draw(Graphics2D g, Camera camera, Stage stage) {
		if (animation != null) {
			animation.draw(fighterColor, position, this, skeleton, g, camera, stage, Game.DEBUG);
		}
	}
	
	public void stepAnimation(Camera camera) {
		if (animation != null) {
			animation.stepAnimation(this, skeleton, camera.getSpeed());
			if (animation.completed()) {
				animation = null;
			}
		}
	}
	
	public void setAnimation(SharedAnimation anim, boolean reset) {
		setAnimation(anim.toString(), reset);
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

	public void dealHit(Fighter defender, String name, double pushBack, HitBox hitBox, String triggeredAnimation,
			int success) {
		if (success > 0) {
			position = position.applyVector(new Vector(-realFace * pushBack / 2, 0));
			addMeter(success * 2 * hitBox.getDamage() / 6000D);
			if (success == 2) {
				if (hitBox.getType() != HitBoxType.GRAB) {
					comboCount ++;
				}
				if (triggeredAnimation != null && !triggeredAnimation.equals("")) {
					setAnimation(triggeredAnimation, true);
				}
			}
		}
	}
	
	public int applyHit(Fighter attacker, String name, HitBoxType type, int damage, int hitStun, int blockStun, double pushBack,
			Vector launchVector, String attachTo, String triggerAnimation, String triggerTargetAnimation,
			boolean release, boolean knockDown) {
		if (attacker.deadHitboxes.contains(name) || (this.hitStun != 0 && type == HitBoxType.GRAB)) {
			return 0;
		}
		attacker.deadHitboxes.add(name);
		if (release) {
			grabbed = false;
			attacker.skeleton.release();
		}
		if (attacker.comboCount > 0) {
			damage /= ((comboCount + 2) / 2);
		}
		if (isGrounded()) {
			if (isParrying() && type != HitBoxType.GRAB) {
				setAnimation(SharedAnimation.PARRY, true);
				addMeter(damage / 2000D);
				Sounds.playSound(Sounds.PARRY);
				setColor(Color.MAGENTA);
				return 1;
			}
			position = position.applyVector(new Vector(-realFace * pushBack / 2, 0));
			if ((isBlocking(type) && type != HitBoxType.GRAB) || type == HitBoxType.AIR_GRAB) {
				this.hitStun = blockStun;
				setAnimation(isCrouching() ? SharedAnimation.BLOCK_CR.toString() : SharedAnimation.BLOCK_ST.toString(), true);
				if (damage > currentHealth) {
					damage = (int) currentHealth;
				}
				Sounds.playSound(Sounds.BLOCK_PUNCH);
				setColor(Color.BLUE);
				currentHealth -= damage;
				greyHealth += damage;
				addMeter(damage / 6000D);
				return 1;
			} else {
				if (animation.getHitboxes().size() > 0) {
					counterHit = true;
					hitStun *= 1.5;
					damage *= 1.5;
				}
				if (hitStun != 0) {
					comboDamage += damage;
				}
				this.knockedDown = knockDown;
				if (!attachTo.equals("")) {
					attacker.skeleton.getBone(attachTo).attachFighter(this);
				}
				this.hitStun = hitStun;
				if (!triggerTargetAnimation.equals("")) {
					setAnimation(triggerTargetAnimation, false);
				} else {
					setAnimation(isCrouching() ? SharedAnimation.HIT_CR.toString() : SharedAnimation.HIT_ST.toString(), true);
				}
				velocity.setX(-animFace * launchVector.getX());
				velocity.setY(launchVector.getY());
				if (velocity.getY() > 0) {
					this.hitStun = -hitStun;
					setAnimation(SharedAnimation.HIT_AIR.toString(), false);
				}
				if (type != HitBoxType.GRAB) {
					setColor(Color.RED);
					Sounds.playSound(Sounds.PUNCH);
				}
				currentHealth -= damage;
				greyHealth = 0;
				if (currentHealth < 0) {
					currentHealth = 0;
				}
				addMeter(damage / 5000D);
				return 2;
			}
		} else if (type != HitBoxType.GRAB) {
			if (animation.getHitboxes().size() > 0) {
				counterHit = true;
				hitStun *= 1.5;
				damage *= 1.5;
			}
			this.knockedDown = knockDown;
			juggleCount ++;
			if (hitStun != 0) {
				comboDamage += damage;
			}
			this.hitStun = -hitStun;
			if (launchVector.getX() == 0 && launchVector.getY() == 0) {
				launchVector = new Vector(0.02, 0.02);
			}
			velocity.setX(-animFace * launchVector.getX());
			velocity.setY(launchVector.getY() / ((juggleCount + 2) / 2D));
			setAnimation(SharedAnimation.HIT_AIR.toString(), false);
			currentHealth -= damage;
			greyHealth = 0;
			if (currentHealth < 0) {
				currentHealth = 0;
			}
			setColor(Color.RED);
			Sounds.playSound(Sounds.PUNCH);
			addMeter(damage / 5000D);
			return 2;
		}
		return 0;
	}

	public void reset() {
		knockedDown = false;
		currentHealth = health;
		comboCount = 0;
		comboDamage = 0;
		greyHealth = 0;
		juggleCount = 0;
		slowDownRemaining = 0;
		velocity = new Vector(0, 0);
		setColor(Color.BLACK);
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
					if (doingAnim(SharedAnimation.WALK_B) || doingAnim(SharedAnimation.WALK_F)) {
						velocity.setX(realFace * data);
					} else {
						velocity.setX(animFace * data);
					}
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

	public Animation getAnimation() {
		return animation;
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
	

	public boolean isParrying() {
		StickInput inputs = inputSource.getStickInputs(realFace, true, 20);
		if (hitStun != 0) {
			return false;
		}
		if (animation != null
				&& inputSource != null && inputs != null && (hasNeutralGroundState()
						|| animation.getName().toLowerCase().contains("block") || doingAnim(SharedAnimation.PARRY))
				&& inputs.getValues().contains(StickInputType.FORWARD) && inputs.getValues().size() == 1) {
			return true;
		}
		return false;
	}

	public boolean isBlocking(HitBoxType hitType) {
		StickInput inputs = inputSource.getStickInputs(realFace, true);
		if (hitStun != 0) {
			return false;
		}
		if (animation != null
				&& inputSource != null && inputs != null && (hasNeutralGroundState()
						|| animation.getName().toLowerCase().contains("block") || doingAnim(SharedAnimation.PARRY))
				&& inputs.getValues().contains(StickInputType.BACKWARD)) {
			return (inputs.getValues().contains(StickInputType.DOWN)) ? hitType != HitBoxType.HIGH
					: hitType != HitBoxType.LOW;
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
		return animFace;
	}
	
	public int getRealFace() {
		return realFace;
	}
	
	public void setAnimFace(int value) {
		animFace = (int) Math.signum(value);
	}
	
	public void setRealFace(int value) {
		realFace = (int) Math.signum(value);
	}
	
	public boolean needFaceCheck() {
		return needFaceCheck
				|| animation != null && animation.getName().equalsIgnoreCase(SharedAnimation.IDLE.toString());
	}

	public boolean needsKnockdown() {
		return knockedDown;
	}
	
	public double getHitStun() {
		return hitStun;
	}
	
	public void setHitStun(double newStun) {
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
		juggleCount = 0;
	}
	
	public void resetFaceCheck() {
		needFaceCheck = false;
	}
	
	public void setColor(Color color) {
		fighterColor = color;
		decrementAmts[0] = color.getRed() / 30;
		decrementAmts[1] = color.getGreen() / 30;
		decrementAmts[2] = color.getBlue() / 30;
	}
	
	public void normalizeColor() {
		int newRed = fighterColor.getRed() - decrementAmts[0];
		if (newRed < 0) {
			newRed = 0;
			decrementAmts[0] = 0;
		}
		int newGreen = fighterColor.getGreen() - decrementAmts[1];
		if (newGreen < 0) {
			newGreen = 0;
			decrementAmts[1] = 0;
		}
		int newBlue = fighterColor.getBlue() - decrementAmts[2];
		if (newBlue < 0) {
			newBlue = 0;
			decrementAmts[2] = 0;
		}
		fighterColor = new Color(newRed, newGreen, newBlue);
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

	public boolean hasNeutralGroundState() {
		return isGrounded() && animation != null
				&& (doingAnim(SharedAnimation.IDLE) || doingAnim(SharedAnimation.WALK_B)
						|| doingAnim(SharedAnimation.WALK_F) || doingAnim(SharedAnimation.CROUCH));
	}
	
	public boolean doingAnim(SharedAnimation anim) {
		if (animation == null) {
			return false;
		}
		return animation.getName().equalsIgnoreCase(anim.toString());
	}
	
	public void updateGroundAnim() {
		List<StickInputType> inputs = inputSource.getStickInputs(realFace, true).getValues();
		if (doingAnim(SharedAnimation.KNOCKED_DOWN_FAST) || doingAnim(SharedAnimation.KNOCKED_DOWN_SLOW)) {
			return;
		}
		jumpsLeft = 2;
		if (inputs.isEmpty()) {
			setAnimation(SharedAnimation.IDLE, false);
		} else if (inputs.contains(StickInputType.UP)) {
			if (inputs.contains(StickInputType.BACKWARD)) {
				setAnimation(SharedAnimation.JUMPSQUAT_B, true);
			} else if (inputs.contains(StickInputType.FORWARD)) {
				setAnimation(SharedAnimation.JUMPSQUAT_F, true);
			} else {
				setAnimation(SharedAnimation.JUMPSQUAT_N, true);
			}
			jumpsLeft--;
		} else if (inputs.contains(StickInputType.DOWN)) {
			setAnimation(SharedAnimation.CROUCH, false);
		} else if (inputs.contains(StickInputType.FORWARD)) {
			setAnimation(SharedAnimation.WALK_F, false);
		} else if (inputs.contains(StickInputType.BACKWARD)) {
			setAnimation(SharedAnimation.WALK_B, false);
		}
	}

	@Override
	public boolean stickMoved(InputSource source) {
		if (hitStun == 0 && !blockInput) {
			List<StickInputType> inputs = source.getStickInputs(realFace, true).getValues();
			if (hasNeutralGroundState()) {
				updateGroundAnim();
			} else if (doingAnim(SharedAnimation.IN_AIR) && jumpsLeft > 0 && hitStun == 0) {
				if (inputs.contains(StickInputType.UP)) {
					if (inputs.contains(StickInputType.BACKWARD)) {
						setAnimation(SharedAnimation.JUMPSQUAT_B, true);
					} else if (inputs.contains(StickInputType.FORWARD)) {
						setAnimation(SharedAnimation.JUMPSQUAT_F, true);
					} else {
						setAnimation(SharedAnimation.JUMPSQUAT_N, true);
					}
					jumpsLeft--;
				}
			}
		}
		return true;
	}

	@Override
	public boolean doubleTappedForward(int attempt, InputSource source) {
		if (hitStun == 0 && !blockInput) {
			if (hasNeutralGroundState()) {
				setAnimation(SharedAnimation.DASH_F, false);
				return true;
			}
		}
		return attempt >= Game.INPUT_BUFFER;
	}

	@Override
	public boolean doubleTappedBack(int attempt, InputSource source) {
		if (hitStun == 0 && !blockInput) {
			if (hasNeutralGroundState()) {
				setAnimation(SharedAnimation.DASH_B, false);
				return true;
			}
		}
		return attempt >= Game.INPUT_BUFFER;
	}

	@Override
	public boolean lightPunchPressed(int attempt, InputSource source) {
		if (hitStun == 0 && !blockInput) {
			List<StickInputType> inputs = source.getStickInputs(realFace, true).getValues();
			if ((hasNeutralGroundState() || (animation.isSpecialCancelable() && deadHitboxes.size() > 0))
					&& inputs != null && inputs.contains(StickInputType.QC_F)) {
				setAnimation(SharedAnimation.LP_SPECIAL_1, true);
				return true;
			} else if (hasNeutralGroundState()) {
				setAnimation(isCrouching() ? SharedAnimation.LP_CR : SharedAnimation.LP_ST, true);
				return true;
			} else if (!isGrounded() && (doingAnim(SharedAnimation.IN_AIR) || doingAnim(SharedAnimation.JUMPSQUAT_B)
					|| doingAnim(SharedAnimation.JUMPSQUAT_F) || doingAnim(SharedAnimation.JUMPSQUAT_N))) {
				setAnimation(SharedAnimation.LP_AIR, true);
				return true;
			}
		}
		return attempt >= Game.INPUT_BUFFER;
	}

	@Override
	public boolean heavyPunchPressed(int attempt, InputSource source) {
		if (hitStun == 0 && !blockInput) {
			List<StickInputType> inputs = source.getStickInputs(realFace, true).getValues();
			if ((hasNeutralGroundState() || (animation.isSpecialCancelable() && deadHitboxes.size() > 0))
					&& inputs != null && inputs.contains(StickInputType.QC_F)) {
				setAnimation(SharedAnimation.HP_SPECIAL_1, true);
				return true;
			} else if (hasNeutralGroundState()) {
				if (source.getStickInputs(realFace, true).getValues().contains(StickInputType.FORWARD)) {
					setAnimation(SharedAnimation.MP_ST, true);
				} else {
					setAnimation(isCrouching() ? SharedAnimation.HP_CR : SharedAnimation.HP_ST, true);
				}
				return true;
			} else if (!isGrounded() && (doingAnim(SharedAnimation.IN_AIR) || doingAnim(SharedAnimation.JUMPSQUAT_B)
					|| doingAnim(SharedAnimation.JUMPSQUAT_F) || doingAnim(SharedAnimation.JUMPSQUAT_N))) {
				setAnimation(SharedAnimation.HP_AIR, true);
				return true;
			}
		}
		return attempt >= Game.INPUT_BUFFER;
	}

	@Override
	public boolean lightKickPressed(int attempt, InputSource source) {
		if (hitStun == 0 && !blockInput) {
			List<StickInputType> inputs = source.getStickInputs(realFace, true).getValues();
			if ((hasNeutralGroundState() || (animation.isSpecialCancelable() && deadHitboxes.size() > 0))
					&& inputs != null && inputs.contains(StickInputType.QC_B)) {
				setAnimation(SharedAnimation.LK_SPECIAL_1, true);
				return true;
			} else if (hasNeutralGroundState()) {
				setAnimation(isCrouching() ? SharedAnimation.LK_CR : SharedAnimation.LK_ST, true);
				return true;
			} else if (!isGrounded() && (doingAnim(SharedAnimation.IN_AIR) || doingAnim(SharedAnimation.JUMPSQUAT_B)
					|| doingAnim(SharedAnimation.JUMPSQUAT_F) || doingAnim(SharedAnimation.JUMPSQUAT_N))) {
				setAnimation(SharedAnimation.LK_AIR, true);
				return true;
			}
		}
		return attempt >= Game.INPUT_BUFFER;
	}

	@Override
	public boolean heavyKickPressed(int attempt, InputSource source) {
		if (hitStun == 0 && !blockInput) {
			List<StickInputType> inputs = source.getStickInputs(realFace, true).getValues();
			if ((hasNeutralGroundState() || (animation.isSpecialCancelable() && deadHitboxes.size() > 0))
					&& inputs != null && inputs.contains(StickInputType.QC_B)) {
				setAnimation(SharedAnimation.HK_SPECIAL_1, true);
				return true;
			} else if (hasNeutralGroundState()) {
				setAnimation(isCrouching() ? SharedAnimation.HK_CR : SharedAnimation.HK_ST, true);
				return true;
			} else if (!isGrounded() && (doingAnim(SharedAnimation.IN_AIR) || doingAnim(SharedAnimation.JUMPSQUAT_B)
					|| doingAnim(SharedAnimation.JUMPSQUAT_F) || doingAnim(SharedAnimation.JUMPSQUAT_N))) {
				setAnimation(SharedAnimation.HK_AIR, true);
				return true;
			}
		}
		return attempt >= Game.INPUT_BUFFER;
	}

	@Override
	public boolean cancelPressed(int attempt, InputSource source) {
		if (meter >= 0.5 && !blockInput) {
			addMeter(-0.5);
			hitStun = 0;
			setColor(Color.GREEN);
			if (isGrounded()) {
				updateGroundAnim();
			} else {
				setAnimation(SharedAnimation.IN_AIR, false);
			}
			slowDownRemaining = 180;
		}
		return true;
	}

	@Override
	public boolean startPressed(int attempt, InputSource source) {
		return true;		
	}

	@Override
	public boolean grabPressed(int attempt, InputSource source) {
		if (hitStun == 0 && !blockInput) {
			if (hasNeutralGroundState()) {
				setAnimation(SharedAnimation.GRAB, true);
				return true;
			}
		}
		return attempt >= Game.INPUT_BUFFER;
	}

	@Override
	public boolean exKickPressed(int attempt, InputSource source) {
		if (hitStun == 0 && !blockInput) {
			List<StickInputType> inputs = source.getStickInputs(realFace, true).getValues();
			if ((hasNeutralGroundState() || (animation.isSpecialCancelable() && deadHitboxes.size() > 0))
					&& inputs != null && inputs.contains(StickInputType.QC_B)
					&& meter >= 0.25) {
				setAnimation(SharedAnimation.EX_K_SPECIAL_1, true);
				setColor(Color.CYAN);
				meter -= 0.25;
				return true;
			}
		}
		return attempt >= Game.INPUT_BUFFER;
	}

	@Override
	public boolean exPunchPressed(int attempt, InputSource source) {
		if (hitStun == 0 && !blockInput) {
			List<StickInputType> inputs = source.getStickInputs(realFace, true).getValues();
			if ((hasNeutralGroundState() || (animation.isSpecialCancelable() && deadHitboxes.size() > 0))
					&& inputs != null && inputs.contains(StickInputType.QC_F)
					&& meter >= 0.25) {
				setAnimation(SharedAnimation.EX_P_SPECIAL_1, true);
				setColor(Color.CYAN);
				meter -= 0.25;
				return true;
			}
		}
		return attempt >= Game.INPUT_BUFFER;
	}

	public boolean usingQuickGetUp() {
		StickInput input = inputSource.getStickInputs(realFace, true, 60);
		return input != null && input.getValues().contains(StickInputType.UP);
	}
	
	public boolean hasSlowDownActive() {
		return slowDownRemaining-- > 0;
	}
	
	public void addMeter(double amt) {
		meter += amt;
		if (meter > 1) {
			meter = 1;
		}
		if (meter < 0) {
			meter = 0;
		}
	}

	public double getMeter() {
		return meter;
	}
	
	public void blockInput(boolean value) {
		blockInput = value;
	}
	
	public boolean gotCounterHit() {
		boolean ret = counterHit;
		counterHit = false;
		return ret;
	}
	
}
