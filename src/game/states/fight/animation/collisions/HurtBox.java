package game.states.fight.animation.collisions;

import java.awt.Color;
import java.awt.Graphics2D;

import game.states.fight.Camera;
import game.util.Box;
import game.util.Position;

/**
 * A collision box that determines where a fighter is vulnerable inside an AnimationStep
 * 
 * @author Fritz
 *
 */
public class HurtBox extends CollisionBox {

	public HurtBox(int startFrame, int endFrame, Box collision) {
		super(startFrame, endFrame, collision);
	}
	
	public HurtBox(HurtBox copy) {
		super(copy);
	}

	@Override
	public Color getColor() {
		return Color.YELLOW;
	}
}
