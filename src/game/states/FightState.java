package game.states;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import game.states.fight.Camera;
import game.states.fight.Fighter;
import game.states.fight.Particle;
import game.states.fight.Stage;
import game.states.fight.animation.Animation;
import game.states.fight.animation.Keyframe;
import game.states.fight.animation.KeyframeType;
import game.states.fight.animation.SharedAnimation;
import game.states.fight.animation.Interpolation;
import game.states.fight.fighter.Bone;
import game.util.Position;
import game.util.Sprite;

/**
 * GameState where a fight is taking place
 * 
 * @author Fritz
 *
 */
public class FightState extends GameState {
	
	/**
	 * The first player's Fighter
	 */
	private Fighter player1;
	
	/**
	 * The second player's Fighter
	 */
	private Fighter player2;
	
	/**
	 * The stage the fight is taking place in
	 */
	private Stage stage;
	
	/**
	 * Camera looking at the fight
	 */
	private Camera camera;
	
	/**
	 * List of any other entities in the fight state
	 */
	private List<Particle> particles;
	
	/**
	 * Initializes the fight state
	 */
	public FightState(Fighter player1, Fighter player2, Stage stage) {
		this.player1 = player1;
		this.player2 = player2;
		this.stage = stage;
		this.camera = new Camera(stage);
		this.camera.setFocus(new Position(2, 1.25));
		particles = new ArrayList<>();
		
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 10000, 10000);
		if (stage != null) {
			stage.draw(g, camera);
		}
		if (player1 != null) {
			player1.draw(g, camera, stage);
		}
		if (player2 != null) {
			player2.draw(g, camera, stage);
		}
		g.setColor(Color.BLACK);
		Position defaultLoc = new Position(0f, 0f);
		Position endLoc = new Position(4f, 0f);
		Position corner1 = new Position(1f, 0.3f);
		Position corner2 = new Position(3f, 0.3f);
		//g.drawLine(0, camera.getScreenY(defaultLoc), 10000, camera.getScreenY(defaultLoc));
		g.drawLine(camera.getScreenX(defaultLoc), 0, camera.getScreenX(defaultLoc), 10000);
		g.drawLine(camera.getScreenX(defaultLoc), camera.getScreenY(defaultLoc), camera.getScreenX(corner1),
				camera.getScreenY(corner1));
		g.drawLine(camera.getScreenX(corner1), 0, camera.getScreenX(corner1),
				camera.getScreenY(corner1));
		g.drawLine(camera.getScreenX(endLoc), camera.getScreenY(endLoc), camera.getScreenX(corner2),
				camera.getScreenY(corner2));
		g.drawLine(camera.getScreenX(corner2), 0, camera.getScreenX(corner2),
				camera.getScreenY(corner2));
		g.drawLine(camera.getScreenX(corner1), camera.getScreenY(corner1), camera.getScreenX(corner2),
				camera.getScreenY(corner1));
	}

	@Override
	public void logic() {
		playerLogic(player1, player2);
		playerLogic(player2, player1);
	}
	
	public void playerLogic(Fighter player, Fighter other) {
		player.handleInputs();
		if (!player.hasSetVelocityY()) {
			if (!player.isGrounded() && player.getVelocity().getY() > player.getMaxFallSpeed()) {
				player.getVelocity().setY(player.getVelocity().getY() - player.getGravity());
				if (player.getVelocity().getY() < player.getMaxFallSpeed()) {
					player.getVelocity().setY(player.getMaxFallSpeed());
				}
			} else if (player.isGrounded()) {
				player.getVelocity().setY(0);
				player.getPosition().setY(0);
			}
		}
		if (player.isGrounded()) {
			player.getPosition().setY(0);
		}
		if (!player.isGrounded()) {
			if (player.getPosition().getX() < 0.2) {
				player.getVelocity().setX(-player.getVelocity().getX());
			}
			if (player.getPosition().getX() > stage.getWidth() - 0.2) {
				player.getVelocity().setX(-player.getVelocity().getX());
			}
		}
		if (player.getPosition().getX() < 0.2) {
			player.getPosition().setX(0.2);
		}
		if (player.getPosition().getX() > stage.getWidth() - 0.2) {
			player.getPosition().setX(stage.getWidth() - 0.2);
		}
		if (player.getPosition().getX() - other.getPosition().getX() > camera.getViewportSize() - 0.3) {
			player.getPosition().setX(other.getPosition().getX() + camera.getViewportSize());
		}
		if (other.getPosition().getX() - player.getPosition().getX() > camera.getViewportSize() - 0.3) {
			player.getPosition().setX(other.getPosition().getX() - camera.getViewportSize());
		}
		if (camera.getScreenX(player.getPosition()) - camera.toPixels(0.2) < 0
				&& other.getPosition().getX() - player.getPosition().getX() < camera.getViewportSize() - 0.4) {
			camera.setFocus(new Position(camera.getFocus().getX() + camera.toGameDistance(camera.getScreenX(player.getPosition()) - camera.toPixels(0.2)),
					camera.getFocus().getY()));
		}
		if (camera.getScreenX(player.getPosition()) + camera.toPixels(0.2) > camera.getScreenWidth()
				&& player.getPosition().getX() - other.getPosition().getX() < camera.getViewportSize() - 0.4) {
			camera.setFocus(new Position(
					camera.getFocus().getX() + camera.toGameDistance(
							camera.getScreenX(player.getPosition()) + camera.toPixels(0.2) - camera.getScreenWidth()),
					camera.getFocus().getY()));
		}
		player.setPosition(player.getPosition().applyVector(player.getVelocity()));
	}

}
