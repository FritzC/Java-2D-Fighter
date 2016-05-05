package game.input.sources;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import game.Game;
import game.input.Input;
import game.input.InputSource;
import game.input.InputType;
import game.input.KeyStates;

public class KeyboardSource extends InputSource {

	@Override
	public void poll() {
		List<InputType> currentInputs = new ArrayList<>();
		if (KeyStates.getState(KeyEvent.VK_W)) {
			currentInputs.add(InputType.UP);
		}
		if (KeyStates.getState(KeyEvent.VK_S)) {
			currentInputs.add(InputType.DOWN);
		}
		if (KeyStates.getState(KeyEvent.VK_D)) {
			currentInputs.add(InputType.RIGHT);
		}
		if (KeyStates.getState(KeyEvent.VK_A)) {
			currentInputs.add(InputType.LEFT);
		}
		if (KeyStates.getState(KeyEvent.VK_U)) {
			currentInputs.add(InputType.ATTACK_1);
		}
		if (inputs.size() == 0 || !getLastInput().getTypes().equals(currentInputs)) {
			inputs.add(new Input(Game.tick, currentInputs));
			//System.out.println(currentInputs);
		}
	}

}
