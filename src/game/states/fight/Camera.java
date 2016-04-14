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
	public float zoom;
	public Stage stage;
	
	public Camera(Stage stage) {
		this.stage = stage;
		focus = new Position(0.5f, 0.25f);
		zoom = 1.0f;
	}
	
	public void setFocus(Position newFocus) {
		focus = new Position(newFocus.getX(), newFocus.getY());
	}
	
	public void setZoom(float newZoom) {
		zoom = newZoom;
	}

	public float getScale() {
		return 1 / zoom;
	}
	
	public int getScreenX(Position position) {
		float screenRight = focus.getX() - zoom / 2;
		return (int) (((position.getX() - screenRight) / zoom) * Game.getScreenWidth());
	}
	
	public int getScreenY(Position position) {
		float screenBottom = focus.getY() - zoom / 2;
		return Game.getScreenHeight() - (int) (((position.getY() - screenBottom) / zoom) * Game.getScreenWidth());
	}
	
	public int toPixels(float size) {
		return (int) (size * Game.getScreenWidth() * getScale());
	}
}
