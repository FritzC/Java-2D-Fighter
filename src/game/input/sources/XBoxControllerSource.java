package game.input.sources;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import game.Game;
import game.input.Input;
import game.input.InputSource;
import game.input.InputType;
import game.input.KeyStates;
import net.java.games.input.Component;
import net.java.games.input.Controller;

public class XBoxControllerSource extends InputSource {
	
	private Controller controller;
	
	private Component a;
	private Component b;
	private Component x;
	private Component y;
	private Component rb;
	private Component rt;
	private Component lb;
	private Component lt;
	private Component start;
	private Component xAxis;
	private Component yAxis;
	
	public XBoxControllerSource(Controller controller) {
		this.controller = controller;
		for (Component component : controller.getComponents()) {
			switch (component.getIdentifier().getName()) {
				case "y":
					yAxis = component;
					break;
				case "x":
					xAxis = component;
					break;
				case "0":
					a = component;
					break;
				case "1":
					b = component;
					break;
				case "2":
					x = component;
					break;
				case "3":
					y = component;
					break;
				case "5":
					rt = component;
					break;
				case "z":
					rb = lb = component;
					break;
				case "4":
					lt = component;
					break;
				case "7":
					start = component;
					break;
			}
		}
	}

	@Override
	public void poll() {
		controller.poll();
		List<InputType> currentInputs = new ArrayList<>();
		if (yAxis.getPollData() < -0.4) {
			currentInputs.add(InputType.UP);
		}
		if (yAxis.getPollData() > 0.4) {
			currentInputs.add(InputType.DOWN);
		}
		if (xAxis.getPollData() < -0.4) {
			currentInputs.add(InputType.LEFT);
		}
		if (xAxis.getPollData() > 0.4) {
			currentInputs.add(InputType.RIGHT);
		}
		if (x.getPollData() == 1) {
			currentInputs.add(InputType.ATTACK_1);
		}
		if (y.getPollData() == 1) {
			currentInputs.add(InputType.ATTACK_2);
		}
		if (rt.getPollData() == 1) {
			currentInputs.add(InputType.ATTACK_3);
		}
		if (inputs.size() == 0 || !getLastInput().getTypes().equals(currentInputs)) {
			inputs.add(new Input(Game.tick, currentInputs));
			//System.out.println(currentInputs);
		}
	}

}
