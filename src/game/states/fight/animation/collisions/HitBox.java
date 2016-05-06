package game.states.fight.animation.collisions;

import java.awt.Color;
import java.io.File;
import java.io.PrintWriter;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import game.states.fight.Fighter;
import game.util.Box;
import game.util.Position;
import game.util.Vector;

/**
 * A damaging collision box inside of an AnimationStep
 * 
 * @author Fritz
 *
 */
public class HitBox extends CollisionBox {

	/**
	 * Group of hitboxes the hitbox is in
	 */
	private String group;
	
	/**
	 * Amount of damage the hitbox does
	 */
	private int damage;
	
	/**
	 * Amount of hitstun the hitbox does
	 */
	private int hitStun;
	
	/**
	 * Amount of hitstun the hitbox does if blocked
	 */
	private int blockStun;
	
	/**
	 * Distance the hitbox will push back if blocked
	 */
	private double pushBack;
	
	/**
	 * Vector a fighter hit by the hitbox will be launched at
	 */
	private Vector launchVelocity;
	
	/**
	 * Whether the hitbox knocks down
	 */
	private boolean knockDown;
	
	/**
	 * The bone that the hitbox attaches the other fighter to
	 */
	private String attachTo;
	
	/**
	 * Whether the hitbox releases any held fighters
	 */
	private boolean release;
	
	private HitBoxType type;
	
	/**
	 * If the hitbox connects, the fighter will switch to this animation
	 */
	private String triggerAnimation;
	
	/**
	 * If the hitbox connects, the fighter hit will switch to this animation
	 */
	private String triggerTargetAnimation;

	public HitBox(int startFrame, int endFrame, String group, Box collision, int damage, int hitStun, int blockStun,
			double pushBack, Vector launchVelocity, boolean knockDown, HitBoxType type, boolean release,
			String attachTo, String triggerAnim, String triggerTargetAnim) {
		super(startFrame, endFrame, collision);
		this.group = group;
		this.damage = damage;
		this.hitStun = hitStun;
		this.blockStun = blockStun;
		this.pushBack = pushBack;
		this.launchVelocity = launchVelocity;
		this.knockDown = knockDown;
		this.type = type;
		this.release = release;
		this.attachTo = attachTo;
		this.triggerAnimation = triggerAnim;
		this.triggerTargetAnimation = triggerTargetAnim;
	}
	
	public HitBox(HitBox copy) {
		super(copy);
		this.group = copy.group;
		this.damage = copy.damage;
		this.hitStun = copy.hitStun;
		this.blockStun = copy.blockStun;
		this.pushBack = copy.pushBack;
		this.launchVelocity = copy.launchVelocity;
		this.knockDown = copy.knockDown;
		this.type = copy.type;
		this.release = copy.release;
		this.attachTo = copy.attachTo;
		this.triggerAnimation = copy.triggerAnimation;
		this.triggerTargetAnimation = copy.triggerTargetAnimation;
	}
	
	/**
	 * Gets whether this hitbox is colliding with the specified hurtbox
	 * 
	 * @param attackerPos - The attacker's position
	 * @param defenderPos - The defender's position
	 * @param hurtBox - The hurtbox being checked for collision
	 * @return - Whether a collision is taking place
	 */
	public boolean isColliding(Fighter attacker, Fighter defender, HurtBox hurtBox) {
		return forOffset(attacker.getFace(), attacker.getPosition())
				.intersects(hurtBox.forOffset(defender.getFace(), defender.getPosition()));
	}
	
	/**
	 * Applies the hitbox's effects to the fighter hit by it and adds hit to
	 * source's hitBy list
	 * 
	 * @param source - Fighter using the hitbox
	 * @param defender - Fighter hit by the hitbox
	 */
	public void applyHit(Fighter source, Fighter defender) {
		boolean success = defender.applyHit(source, group, type, damage, hitStun, blockStun, pushBack, launchVelocity, attachTo, triggerAnimation,
				triggerTargetAnimation, release, knockDown);
		source.dealHit(defender, pushBack, this, success);
	}

	@Override
	public Color getColor() {
		return Color.RED;
	}

	public void updateUI(JTextField group, JSpinner damage, JSpinner hitstun, JSpinner blockstun, JSpinner pushback,
			JSpinner launchX, JSpinner launchY, JCheckBox grab, JComboBox<String> grabWith,
			JComboBox<HitBoxType> hitboxType, JCheckBox releaseGrab, JCheckBox knockdown, JComboBox<String> triggerAnim,
			JTextField triggerOtherAnim) {
		group.setText(this.group);
		damage.setValue(this.damage); 
		hitstun.setValue(this.hitStun);
		blockstun.setValue(this.blockStun);
		pushback.setValue(this.pushBack);
		launchX.setValue(this.launchVelocity.getX());
		launchY.setValue(this.launchVelocity.getY());
		grab.setSelected(!this.attachTo.equals(""));
		grabWith.setEnabled(grab.isSelected());
		grabWith.setSelectedItem(this.attachTo);
		hitboxType.setSelectedItem(this.type);
		releaseGrab.setSelected(this.release);
		knockdown.setSelected(this.knockDown);
		triggerAnim.setSelectedItem(this.triggerAnimation);
		triggerOtherAnim.setText(this.triggerTargetAnimation);
	}

	public void setAttachTo(String value) {
		attachTo = value;
	}

	public void setKnockdown(boolean value) {
		knockDown = value;
	}

	public void setRelease(boolean value) {
		release = value;
	}
	
	public void setHitstun(int value) {
		hitStun = value;
	}
	
	public void setBlockstun(int value) {
		blockStun = value;
	}
	
	public void setPushback(double value) {
		pushBack = value;
	}

	public void setLaunchX(double value) {
		launchVelocity.setX(value);
	}

	public void setLaunchY(double value) {
		launchVelocity.setY(value);
	}

	public void setTriggeredAnim(String value) {
		triggerAnimation = value;
	}

	public void setTargetTriggeredAnim(String value) {
		triggerTargetAnimation = value;
	}

	public void setGroup(String value) {
		group = value;
	}

	public void setType(HitBoxType value) {
		type = value;
	}

	public void setDamage(int value) {
		damage = value;
	}
	
	public int getDamage() {
		return damage;
	}

	public void save(File f, PrintWriter pw) {
		pw.println("\t\t\"hitbox\": {");
		pw.println("\t\t\t\"group\": \"" + group + "\",");
		pw.println("\t\t\t\"startFrame\": " + getStartFrame() + ",");
		pw.println("\t\t\t\"endFrame\": " + getEndFrame() + ",");
		pw.println("\t\t\t\"damage\": " + damage + ",");
		pw.println("\t\t\t\"blockstun\": " + blockStun + ",");
		pw.println("\t\t\t\"hitstun\": " + hitStun + ",");
		pw.println("\t\t\t\"pushback\": " + pushBack + ",");
		pw.println("\t\t\t\"knockdown\": " + knockDown + ",");
		pw.println("\t\t\t\"type\": " + type.toString() + ",");
		pw.println("\t\t\t\"release\": " + release + ",");
		pw.println("\t\t\t\"attach\": \"" + attachTo + "\",");
		pw.println("\t\t\t\"triggerAnim\": \"" + triggerAnimation + "\",");
		pw.println("\t\t\t\"triggerTargetAnim\": \"" + triggerTargetAnimation + "\",");
		pw.println("\t\t\t\"collision\": {");
		pw.println("\t\t\t\t\"box\": {");
		pw.println("\t\t\t\t\t\"top_l x\": " + getTopLeft().getX() + ",");
		pw.println("\t\t\t\t\t\"top_l y\": " + getTopLeft().getY() + ",");
		pw.println("\t\t\t\t\t\"bottom_r x\": " + getBottomRight().getX() + ",");
		pw.println("\t\t\t\t\t\"bottom_r y\": " + getBottomRight().getY());
		pw.println("\t\t\t\t},");
		pw.println("\t\t\t},");
		pw.println("\t\t\t\"velocity\": {");
		pw.println("\t\t\t\t\"vector\": {");
		pw.println("\t\t\t\t\t\"x\": " + launchVelocity.getX() + ",");
		pw.println("\t\t\t\t\t\"y\": " + launchVelocity.getY());
		pw.println("\t\t\t\t}");
		pw.println("\t\t\t}");
		pw.println("\t\t},");
	}
	
}
