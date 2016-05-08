package game.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An input source (keyboard, controller, etc)
 * 
 * @author Fritz
 *
 */
public abstract class InputSource {
	
	/**
	 * List of all previous inputs
	 */
	protected List<StickInput> stickInputs;
	
	protected Map<InputType, Boolean> buttons;
	
	protected Map<InputType, Integer> bufferedInputs;
	
	protected InputTaker boundTo;
	
	/**
	 * Whether there is a new input since last checked
	 */
	private boolean newInput;
	
	/**
	 * Initializes the input list
	 */
	public InputSource() {
		stickInputs = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			stickInputs.add(new StickInput(0, new ArrayList<>()));
		}
		buttons = new HashMap<>();
		for (InputType type : InputType.values()) {
			buttons.put(type, false);
		}
		bufferedInputs = new HashMap<>();
	}
	
	public void bind(InputTaker inputTaker) {
		this.boundTo = inputTaker;
	}

	/**
	 * Polls the device
	 */
	public abstract void poll();
	
	public void handleBufferedInputs() {
		for (InputType type : bufferedInputs.keySet()) {
			fireEvent(bufferedInputs.get(type), type);
		}
	}
	
	public StickInput getStickInputs(int face, boolean acceptEmpty, int ageLimit) {
		for (int i = stickInputs.size() - 1; i >= 0; i--) {
			if (stickInputs.get(i).getAge() > ageLimit) {
				return null;
			}
			if (stickInputs.get(i).getValues().isEmpty() && !acceptEmpty) {
				continue;
			}
			StickInput copy = new StickInput(stickInputs.get(i));
			StickInputType forward = (face == 1) ? StickInputType.RIGHT : StickInputType.LEFT;
			StickInputType backward = (face == 1) ? StickInputType.LEFT : StickInputType.RIGHT;
			if (copy.getValues().remove(backward)) {
				copy.getValues().add(StickInputType.BACKWARD);
			} else if (copy.getValues().remove(forward)) {
				copy.getValues().add(StickInputType.FORWARD);
			}
			if (i > 2) {
				if (stickInputs.get(i).getValues().contains(forward)
						&& !stickInputs.get(i).getValues().contains(StickInputType.DOWN)
						&& stickInputs.get(i - 1).getValues().contains(forward)
						&& stickInputs.get(i - 1).getValues().contains(StickInputType.DOWN)
						&& !stickInputs.get(i - 2).getValues().contains(forward)
						&& stickInputs.get(i - 2).getValues().contains(StickInputType.DOWN)
						&& stickInputs.get(i - 2).getAge() < 20) {
					copy.getValues().add(StickInputType.QC_F);
				} else if (stickInputs.get(i).getValues().contains(backward)
						&& !stickInputs.get(i).getValues().contains(StickInputType.DOWN)
						&& stickInputs.get(i - 1).getValues().contains(backward)
						&& stickInputs.get(i - 1).getValues().contains(StickInputType.DOWN)
						&& !stickInputs.get(i - 2).getValues().contains(backward)
						&& stickInputs.get(i - 2).getValues().contains(StickInputType.DOWN)
						&& stickInputs.get(i - 2).getAge() < 20) {
					copy.getValues().add(StickInputType.QC_B);
				} else if (stickInputs.get(i).getValues().contains(backward)
						&& stickInputs.get(i - 1).getValues().isEmpty()
						&& stickInputs.get(i - 2).getValues().contains(backward)
						&& stickInputs.get(i - 2).getAge() < 20) {
					fireEvent(0, InputType.DASH_B);
				} else if (stickInputs.get(i).getValues().contains(forward)
						&& stickInputs.get(i - 1).getValues().isEmpty()
						&& stickInputs.get(i - 2).getValues().contains(forward)
						&& stickInputs.get(i - 2).getAge() < 20) {
					fireEvent(0, InputType.DASH_F);
				}
			}
			return copy;
		}
		return null;
	}

	public StickInput getStickInputs(int face, boolean b) {
		return getStickInputs(face, b, Integer.MAX_VALUE);
	}
	
	public void fireEvent(int attempt, InputType type) {
		boolean success = false;
		switch (type) {
			case LP:
				success = boundTo.lightPunchPressed(attempt, this);
				break;
			case HP:
				success = boundTo.heavyPunchPressed(attempt, this);
				break;
			case LK:
				success = boundTo.lightKickPressed(attempt, this);
				break;
			case HK:
				success = boundTo.heavyKickPressed(attempt, this);
				break;
			case EX_P:
				success = boundTo.exPunchPressed(attempt, this);
				break;
			case EX_K:
				success = boundTo.exKickPressed(attempt, this);
				break;
			case CANCEL:
				success = boundTo.cancelPressed(attempt, this);
				break;
			case START:
				success = boundTo.startPressed(attempt, this);
				break;
			case GRAB:
				success = boundTo.grabPressed(attempt, this);
				break;
			case DASH_B:
				success = boundTo.doubleTappedBack(attempt, this);
				break;
			case DASH_F:
				success = boundTo.doubleTappedForward(attempt, this);
				break;
		}
		if (!success) {
			bufferedInputs.put(type, ++attempt);
		} else {
			bufferedInputs.remove(type);
		}
	}

}
