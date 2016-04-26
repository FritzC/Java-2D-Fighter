package game.states.fight.animation;

public enum SharedAnimation {

	UNSHARED,
	WALK_F,
	WALK_B,
	JUMPSQUAT;
	
	public static SharedAnimation forString(String s) {
		for (SharedAnimation reqAnim : values()) {
			if (reqAnim.toString().equalsIgnoreCase(s)) {
				return reqAnim;
			}
		}
		return null;
	}
	
	public String toLowerCase() {
		return toString().toLowerCase();
	}
}
