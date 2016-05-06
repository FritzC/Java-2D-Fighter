package game;

import game.util.MakeSound;

public class Sounds {

	public final static String PUNCH = "assets/Synth_hit_punch_m_wav.aax_0000.wav";
	public final static String BLOCK_PUNCH = "assets/Synth_guard0_wav.aax_0000.wav";
	
	public final static void playSound(String location) {
		(new Thread(new Runnable() {
			@Override
			public void run() {
				(new MakeSound()).playSound(location);
			}
		})).start();
	}
}
