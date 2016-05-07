package game.states.fight.fighters;

import java.io.File;
import java.io.FileNotFoundException;

import game.input.InputSource;
import game.states.fight.Fighter;
import game.util.Position;

public class BasicFighter extends Fighter {

	public BasicFighter(InputSource input, Position pos) throws FileNotFoundException {
		super(load(new File("fighters/Rob/fighter.json")));
		inputSource = input;
		input.bind(this);
		setPosition(pos);
		gravity = 0.005;
		maxFallSpeed = -0.045;
	}
	
	

}
