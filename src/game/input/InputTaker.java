package game.input;

public interface InputTaker {

	public boolean stickMoved(InputSource source);
	
	public boolean lightPunchPressed(int attempt, InputSource source);
	
	public boolean heavyPunchPressed(int attempt, InputSource source);
	
	public boolean lightKickPressed(int attempt, InputSource source);
	
	public boolean heavyKickPressed(int attempt, InputSource source);
	
	public boolean cancelPressed(int attempt, InputSource source);
	
	public boolean startPressed(int attempt, InputSource source);

	public boolean grabPressed(int attempt, InputSource source);

	public boolean exKickPressed(int attempt, InputSource source);

	public boolean exPunchPressed(int attempt, InputSource source);
	
	public boolean doubleTappedForward(int attempt, InputSource source);

	public boolean doubleTappedBack(int attempt, InputSource source);
}
