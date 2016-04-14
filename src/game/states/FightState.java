package game.states;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import game.states.fight.Camera;
import game.states.fight.Fighter;
import game.states.fight.Particle;
import game.states.fight.Stage;
import game.states.fight.fighter.FighterBone;
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
		particles = new ArrayList<>();
	}
	
	private FighterBone root;

	@Override
	public void draw(Graphics2D g) {
		root.draw(g, new Position(0.5f, 0.0f), camera);
	}

	@Override
	public void logic() {
		
	}

}
