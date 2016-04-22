package game.states.fight;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JSpinner;
import javax.swing.JTextField;

import game.Game;
import game.states.fight.animation.Animation;
import game.states.fight.animation.ECB;
import game.states.fight.animation.HitBox;
import game.states.fight.animation.HurtBox;
import game.states.fight.animation.Interpolation;
import game.states.fight.animation.KeyframeType;
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
	 * Map of all of the fighter's animations with identifiers
	 */
	private Map<String, Animation> animations;
	
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
	}
	
	/**
	 * Draws the fighter in the game window
	 * 
	 * @param g - Graphics2d object used to draw
	 */
	public void draw(Graphics2D g, Camera camera, Stage stage) {
		if (animation != null) {
			animation.draw(position, this, skeleton, g, camera, stage);
			if (Game.DEBUG) {
				getECB().draw(position, camera, g);
				for (HurtBox hurtbox : getHurtBoxes()) {
					hurtbox.draw(position, camera, g);
				}
				for (HitBox hitbox : getHitBoxes()) {
					hitbox.draw(position, camera, g);
				}
			}
		}
	}
	
	/**
	 * Sets the animation of the fighter
	 * 
	 * @param newAnim - Identifier of the new animation
	 */
	public void setAnimation(String newAnim) {
		animation = animations.get(newAnim);
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
	
	/**
	 * Gets the fighter's ECB
	 * 
	 * @return - Fighter's current ECB
	 */
	public ECB getECB() {
		return animation.getECB();
	}
	
	/**
	 * Gets the fighter's current hitboxes
	 * 
	 * @return - List of current hitboxes
	 */
	public List<HitBox> getHitBoxes() {
		return animation.getHitBoxes();
	}

	/**
	 * Gets the fighter's current hurtboxes
	 * 
	 * @return - List of current hurtboxes
	 */
	public List<HurtBox> getHurtBoxes() {
		return animation.getHurtBoxes();
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
				velocity.setX(interpolation.getInterpolatedValue(velocity.getX(), data, completion));
				break;
			case VELOCITY_Y:
				velocity.setY(interpolation.getInterpolatedValue(velocity.getY(), data, completion));
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
		return new Fighter(name, health, skeleton);
	}
	
	/**
	 * Saves the fighter to a file
	 * 
	 * @param f - File to save to
	 * @throws IOException 
	 */
	public void save(File f) throws IOException {
		if (!f.exists()) {
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
	}
	
}
