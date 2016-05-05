package game.states.fight.animation;

public enum SharedAnimation {

	UNSHARED,
	IDLE,
	CROUCH,
	WALK_F,
	WALK_B,
	JUMPSQUAT_N,
	JUMPSQUAT_F,
	JUMPSQUAT_B,
	IN_AIR,
	HIT_ST,
	HIT_CR,
	BLOCK_ST,
	BLOCK_CR,
	KNOCKED_DOWN_SLOW,
	KNOCKED_DOWN_FAST,
	PUNCH,
	KICK;
	
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
