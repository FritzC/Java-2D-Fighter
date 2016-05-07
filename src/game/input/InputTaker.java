package game.input;

public interface InputTaker {

	public void stickMoved(InputSource source);
	
	public void lightPunchPressed(InputSource source);
	
	public void heavyPunchPressed(InputSource source);
	
	public void lightKickPressed(InputSource source);
	
	public void heavyKickPressed(InputSource source);
	
	public void cancelPressed(InputSource source);
	
	public void startPressed(InputSource source);

	public void grabPressed(InputSource source);

	public void exKickPressed(InputSource source);

	public void exPunchPressed(InputSource source);
}
