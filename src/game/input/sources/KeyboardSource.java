package game.input.sources;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import game.Game;
import game.input.Input;
import game.input.InputSource;
import game.input.InputType;
import game.input.KeyStates;
import game.input.StickInput;
import game.input.StickInputType;

public class KeyboardSource extends InputSource {

	@Override
	public void poll() {
		List<StickInputType> currentInputs = new ArrayList<>();
		if (KeyStates.getState(KeyEvent.VK_W)) {
			currentInputs.add(StickInputType.UP);
		}
		if (KeyStates.getState(KeyEvent.VK_S)) {
			currentInputs.add(StickInputType.DOWN);
		}
		if (KeyStates.getState(KeyEvent.VK_A)) {
			currentInputs.add(StickInputType.LEFT);
		}
		if (KeyStates.getState(KeyEvent.VK_D)) {
			currentInputs.add(StickInputType.RIGHT);
		}
		if (KeyStates.getState(KeyEvent.VK_U) && !buttons.get(InputType.LP)) {
			fireEvent(0, InputType.LP);
		}
		buttons.put(InputType.LP, KeyStates.getState(KeyEvent.VK_U));
		if (KeyStates.getState(KeyEvent.VK_I) && !buttons.get(InputType.HP)) {
			fireEvent(0, InputType.HP);
		}
		buttons.put(InputType.HP, KeyStates.getState(KeyEvent.VK_I));
		if (KeyStates.getState(KeyEvent.VK_J) && !buttons.get(InputType.LK)) {
			fireEvent(0, InputType.LK);
		}
		buttons.put(InputType.LK, KeyStates.getState(KeyEvent.VK_J));
		if (KeyStates.getState(KeyEvent.VK_K) && !buttons.get(InputType.HK)) {
			fireEvent(0, InputType.HK);
		}
		buttons.put(InputType.HK, KeyStates.getState(KeyEvent.VK_K));
		if (KeyStates.getState(KeyEvent.VK_ESCAPE) && !buttons.get(InputType.START)) {
			fireEvent(0, InputType.START);
		}
		buttons.put(InputType.START, KeyStates.getState(KeyEvent.VK_ESCAPE));
		if (KeyStates.getState(KeyEvent.VK_Y) && !buttons.get(InputType.GRAB)) {
			fireEvent(0, InputType.GRAB);
		}
		buttons.put(InputType.GRAB, KeyStates.getState(KeyEvent.VK_Y));
		if (KeyStates.getState(KeyEvent.VK_H) && !buttons.get(InputType.CANCEL)) {
			fireEvent(0, InputType.CANCEL);
		}
		buttons.put(InputType.CANCEL, KeyStates.getState(KeyEvent.VK_H));
		if (KeyStates.getState(KeyEvent.VK_L) && !buttons.get(InputType.EX_K)) {
			fireEvent(0, InputType.EX_K);
		}
		buttons.put(InputType.EX_K, KeyStates.getState(KeyEvent.VK_L));
		if (KeyStates.getState(KeyEvent.VK_O) && !buttons.get(InputType.EX_P)) {
			fireEvent(0, InputType.EX_P);
		}
		buttons.put(InputType.EX_P, KeyStates.getState(KeyEvent.VK_O));
		if (!stickInputs.get(stickInputs.size() - 1).getValues().equals(currentInputs)) {
			stickInputs.add(new StickInput(Game.tick, currentInputs));
			if (boundTo != null) {
				boundTo.stickMoved(this);
			}
		}
	}

}
