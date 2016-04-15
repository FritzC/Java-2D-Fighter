package game.util;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Wrapper for a BufferedImage
 * 
 * @author Fritz
 *
 */
public class Sprite {
	
	private BufferedImage image;
	private double imageScale;
	
	public Sprite(String location, double imageScale) {
		try {
			File f = new File(location);
			image = ImageIO.read(f);
			this.imageScale = imageScale;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void draw(Graphics2D g, int x, int y, double scale, double angle) {
		Graphics2D clone = (Graphics2D) g.create();
		AffineTransform a = new AffineTransform();
		a.concatenate(AffineTransform.getTranslateInstance(x, y - (image.getHeight() / 2) * imageScale * scale));
		a.concatenate(AffineTransform.getRotateInstance(Math.toRadians(angle), 0, 0));
		a.concatenate(AffineTransform.getScaleInstance(imageScale * scale, imageScale * scale));
		clone.setTransform(a);
		clone.drawImage(image, 0, 0, null);
	}

}
