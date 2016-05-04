package game.states.fight.animation.collisions;

public enum HitBoxType {

	LOW,
	MID,
	HIGH,
	GRAB,
	AIR_GRAB;
	
	public static HitBoxType forString(String value) {
		for (HitBoxType type : values()) {
			if (type.toString().equalsIgnoreCase(value)) {
				return type;
			}
		}
		return MID;
	}
}
