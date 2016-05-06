package game.states.fight.animation.collisions;

import java.awt.Color;
import java.awt.Graphics2D;

import game.states.fight.Camera;
import game.util.Box;
import game.util.Position;

public abstract class CollisionBox extends Box {
	
	private int startFrame;
	
	private int endFrame;
	
	public CollisionBox(int start, int end, Box collision) {
		super(collision);
		startFrame = start;
		endFrame = end;
	}
	
	public CollisionBox(CollisionBox copy) {
		super(copy);
		startFrame = copy.startFrame;
		endFrame = copy.endFrame;
	}

	/**
	 * Gets whether the CollisionBox is currently active
	 * 
	 * @param currentFrame - Current frame of the animation
	 * @return - Whether the CollisionBox is active
	 */
	public boolean isActive(double currentFrame) {
		return startFrame <= currentFrame && currentFrame <= endFrame;
	}
	
	public void draw(int face, Position position, Camera camera, Graphics2D g, boolean selected) {
		forOffset(face, position).draw(g, camera, getColor(), selected);
	}
	
	public int getStartFrame() {
		return startFrame;
	}
	
	public int getEndFrame() {
		return endFrame;
	}
	
	public Object[] getInfo() {
		return new Object[] {startFrame, endFrame, topLeft.getX(), topLeft.getY(), bottomRight.getX(),
				bottomRight.getY()};
	}

	public boolean isEqual(Object[] info) {
		return info != null && (int) info[0] == startFrame && (int) info[1] == endFrame
				&& (double) info[2] == topLeft.getX() && (double) info[3] == topLeft.getY()
				&& (double) info[4] == bottomRight.getX() && (double) info[5] == bottomRight.getY();
	}

	public abstract Color getColor();

	public void setStartFrame(int value) {
		startFrame = value;
	}
	
	public void setEndFrame(int value) {
		endFrame = value;
	}
	
	public Position getTopLeft() {
		return topLeft;
	}
	
	public Position getBottomRight() {
		return bottomRight;
	}

}
