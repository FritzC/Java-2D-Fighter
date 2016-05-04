package game.states.fight.animation.collisions;

import java.awt.Color;
import java.awt.Graphics2D;

import game.states.fight.Camera;
import game.util.Box;
import game.util.Position;

/**
 * An enviromental collision box
 * 
 * @author Fritz
 *
 */
public class ECB extends CollisionBox {

	/**
	 * Creates a ECB
	 * 
	 * @param collision - Bounding box of the ECB
	 */
	public ECB(int startFrame, int endFrame, Box collision) {
		super(startFrame, endFrame, collision);
	}
	
	public ECB(ECB copy) {
		super(copy);
	}

	@Override
	public Color getColor() {
		return Color.GREEN;
	}
}
