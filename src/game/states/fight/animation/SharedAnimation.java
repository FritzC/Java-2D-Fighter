package game.states.fight.animation;

public enum SharedAnimation {

	UNSHARED,
	IDLE,
	CROUCH,
	WALK_F,
	WALK_B,
	DASH_F,
	DASH_B,
	JUMPSQUAT_N,
	JUMPSQUAT_F,
	JUMPSQUAT_B,
	IN_AIR,
	HIT_ST,
	HIT_CR,
	HIT_AIR,
	BLOCK_ST,
	BLOCK_CR,
	KNOCKED_DOWN_SLOW,
	KNOCKED_DOWN_FAST,
	LP_CR,
	MP_CR,
	HP_CR,
	LP_ST,
	MP_ST,
	HP_ST,
	LK_CR,
	MK_CR,
	HK_CR,
	LK_ST,
	MK_ST,
	HK_ST,
	LP_AIR,
	MP_AIR,
	HP_AIR,
	LK_AIR,
	MK_AIR,
	HK_AIR,
	GRAB,
	GRAB_CONNECT,
	GRABBED,
	LP_SPECIAL_1,
	HP_SPECIAL_1,
	EX_P_SPECIAL_1,
	LK_SPECIAL_1,
	HK_SPECIAL_1,
	EX_K_SPECIAL_1,
	PARRY;
	
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
