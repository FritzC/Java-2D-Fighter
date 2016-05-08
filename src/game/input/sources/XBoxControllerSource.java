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
		List<StickInputType> currentInputs = new ArrayList<>();
		if (boundTo == null) {
			return;
		}
		if (yAxis.getPollData() < -0.4) {
			currentInputs.add(StickInputType.UP);
		}
		if (yAxis.getPollData() > 0.4) {
			currentInputs.add(StickInputType.DOWN);
		}
		if (xAxis.getPollData() < -0.4) {
			currentInputs.add(StickInputType.LEFT);
		}
		if (xAxis.getPollData() > 0.4) {
			currentInputs.add(StickInputType.RIGHT);
		}
		if (x.getPollData() == 1 && !buttons.get(InputType.LP)) {
			fireEvent(0, InputType.LP);
		}
		buttons.put(InputType.LP, x.getPollData() == 1);
		if (y.getPollData() == 1 && !buttons.get(InputType.HP)) {
			fireEvent(0, InputType.HP);
		}
		buttons.put(InputType.HP, y.getPollData() == 1);
		if (a.getPollData() == 1 && !buttons.get(InputType.LK)) {
			fireEvent(0, InputType.LK);
		}
		buttons.put(InputType.LK, a.getPollData() == 1);
		if (b.getPollData() == 1 && !buttons.get(InputType.HK)) {
			fireEvent(0, InputType.HK);
		}
		buttons.put(InputType.HK, b.getPollData() == 1);
		if (start.getPollData() == 1 && !buttons.get(InputType.START)) {
			fireEvent(0, InputType.START);
		}
		buttons.put(InputType.START, start.getPollData() == 1);
		if (lt.getPollData() == 1 && !buttons.get(InputType.GRAB)) {
			fireEvent(0, InputType.GRAB);
		}
		buttons.put(InputType.GRAB, lt.getPollData() == 1);
		if (lb.getPollData() < -0.5 && !buttons.get(InputType.CANCEL)) {
			fireEvent(0, InputType.CANCEL);
		}
		buttons.put(InputType.CANCEL, lb.getPollData() < -0.5);
		if (rb.getPollData() > 0.5 && !buttons.get(InputType.EX_K)) {
			fireEvent(0, InputType.EX_K);
		}
		buttons.put(InputType.EX_K, rb.getPollData() > 0.5);
		if (rt.getPollData() == 1 && !buttons.get(InputType.EX_P)) {
			fireEvent(0, InputType.EX_P);
		}
		buttons.put(InputType.EX_P, rt.getPollData() == 1);
		if (!stickInputs.get(stickInputs.size() - 1).getValues().equals(currentInputs)) {
			stickInputs.add(new StickInput(Game.tick, currentInputs));
			if (boundTo != null) {
				boundTo.stickMoved(this);
			}
		}
	}

}
