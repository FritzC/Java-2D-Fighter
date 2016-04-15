package game.states.fight;

import game.Game;
import game.util.Position;

/**
 * Defines what portion of screen is drawn
 * 
 * @author Fritz
 *
 */
public class Camera {

	public Position focus;
	public double zoom;
	public Stage stage;
	
	public Camera(Stage stage) {
		this.stage = stage;
		focus = new Position(0.5f, 0.25f);
		zoom = 1.0f;
	}
	
	public void setFocus(Position newFocus) {
		focus = new Position(newFocus.getX(), newFocus.getY());
	}
	
	public void setZoom(double newZoom) {
		zoom = newZoom;
	}

	public double getScale() {
		return 1 / zoom;
	}
	
	public int getScreenX(Position position) {
		double screenRight = focus.getX() - zoom / 2;
		return (int) (((position.getX() - screenRight) / zoom) * getScreenWidth());
	}
	
	public int getScreenY(Position position) {
		double screenBottom = focus.getY() - zoom / 2;
		return getScreenHeight() - (int) (((position.getY() - screenBottom) / zoom) * getScreenWidth());
	}
	
	public int toPixels(double size) {
		return (int) (size * getScreenWidth() * getScale());
	}
	
	public double toGameDistance(double size) {
		return size / (double) getScreenWidth() / (double) getScale();
	}
	
	public int getScreenWidth() {
		return Game.getScreenWidth();
	}
	
	public int getScreenHeight() {
		return Game.getScreenHeight();
	}
}
