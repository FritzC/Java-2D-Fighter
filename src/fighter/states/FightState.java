package fighter.states;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import fighter.states.fight.Fighter;
import fighter.states.fight.Particle;
import fighter.states.fight.Stage;

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
		particles = new ArrayList<>();
	}

	@Override
	public void draw(Graphics2D g) {
		
	}

	@Override
	public void logic() {
		
	}

}
