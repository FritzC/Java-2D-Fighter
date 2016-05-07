package game.states;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.util.ArrayList;
import java.util.List;

import game.Game;
import game.Window;
import game.states.fight.Camera;
import game.states.fight.FightResult;
import game.states.fight.Fighter;
import game.states.fight.Particle;
import game.states.fight.Stage;
import game.states.fight.animation.SharedAnimation;
import game.states.fight.animation.collisions.HitBox;
import game.states.fight.animation.collisions.HurtBox;
import game.util.Position;
import game.util.Vector;

/**
 * GameState where a fight is taking place
 * 
 * @author Fritz
 *
 */
public class FightState extends GameState {
	
	/**
	 * The first player's Fighter
	 */
	private Fighter player1;
	
	/**
	 * The second player's Fighter
	 */
	private Fighter player2;
	
	/**
	 * The stage the fight is taking place in
	 */
	private Stage stage;
	
	/**
	 * Camera looking at the fight
	 */
	private Camera camera;
	
	/**
	 * List of any other entities in the fight state
	 */
	private List<Particle> particles;
	
	private FightResult[][] results;
	
	private long tickRoundStarted;
	
	private int roundLength;
	
	private long startRoundTick;
	
	private String endRoundMessage;
	
	private boolean gameOver;
	
	private int[][] comboData;
	
	private int hitLag;
	
	/**
	 * Initializes the fight state
	 */
	public FightState(Fighter player1, Fighter player2, Stage stage, int rounds, int roundLength) {
		this.player1 = player1;
		this.player2 = player2;
		this.stage = stage;
		this.camera = new Camera(stage);
		this.camera.setFocus(new Position(2, 1.25));
		particles = new ArrayList<>();
		results = new FightResult[2][rounds / 2 + 1];
		tickRoundStarted = Game.tick;
		this.roundLength = roundLength;
		startRoundTick = Game.tick + 60 * 4;
		player2.setFace(-1);
		endRoundMessage = "";
		comboData = new int[2][2];
	}

	@Override
	public void draw(Graphics2D g) {
		/** Drawing background **/
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 10000, 10000);
		g.setColor(Color.BLACK);
		Position defaultLoc = new Position(0f, 0f);
		Position endLoc = new Position(4f, 0f);
		Position corner1 = new Position(1f, 0.3f);
		Position corner2 = new Position(3f, 0.3f);
		g.drawLine(camera.getScreenX(defaultLoc), 0, camera.getScreenX(defaultLoc), 10000);
		g.drawLine(camera.getScreenX(defaultLoc), camera.getScreenY(defaultLoc), camera.getScreenX(corner1),
				camera.getScreenY(corner1));
		g.drawLine(camera.getScreenX(corner1), 0, camera.getScreenX(corner1),
				camera.getScreenY(corner1));
		g.drawLine(camera.getScreenX(endLoc), camera.getScreenY(endLoc), camera.getScreenX(corner2),
				camera.getScreenY(corner2));
		g.drawLine(camera.getScreenX(corner2), 0, camera.getScreenX(corner2),
				camera.getScreenY(corner2));
		g.drawLine(camera.getScreenX(corner1), camera.getScreenY(corner1), camera.getScreenX(corner2),
				camera.getScreenY(corner1));
		
		/** Drawing actors **/
		if (stage != null) {
			stage.draw(g, camera);
		}
		if (player1 != null) {
			player1.draw(g, camera, stage);
		}
		if (player2 != null) {
			player2.draw(g, camera, stage);
		}
		
		/** Draw HP Bars and Meters**/
		Graphics2D g2 = (Graphics2D) g.create();
		int hpBarX1 = camera.getRelativeScreenX(0.05);
		int hpBarX2 = camera.getRelativeScreenX(0.6);
		int hpBarY = camera.getRelativeScreenY(0.05);
		int hpBarWidth = camera.getRelativeScreenX(0.35);
		int hpBarHeight = camera.getRelativeScreenY(0.075);
		int hpBarPadding = camera.getRelativeScreenX(0.004);
		int meterWidth = camera.getRelativeScreenX(0.2);
		int meterHeight = camera.getRelativeScreenY(0.04);
		int meterCurve = hpBarPadding;
		int meterX1 = hpBarX1;
		int meterX2 = hpBarX2 + hpBarWidth - meterWidth;
		int meterY = hpBarY + hpBarHeight - meterCurve;
		g2.setColor(Color.GRAY);
		g2.fillRoundRect(meterX1, meterY, (int) (meterWidth * 0.99), (int) (meterHeight * 0.92), meterCurve, meterCurve);
		g2.fillRoundRect(meterX2, meterY, (int) (meterWidth * 0.99), (int) (meterHeight * 0.92), meterCurve, meterCurve);
		GradientPaint darkBlueToBlue = new GradientPaint(meterX1, meterY, new Color(0, 128, 255), meterX1,
				meterY + meterHeight, new Color(0, 102, 204));
		GradientPaint lightBlueToBlue = new GradientPaint(meterX1, meterY, new Color(0, 255, 255), meterX1,
				meterY + meterHeight, new Color(0, 204, 204));
		for (int i = 1; i < 5; i++) {
			g2.setPaint(darkBlueToBlue);
			if (player1.getMeter() >= i * 0.25) {
				g2.setPaint(lightBlueToBlue);
			}
			if (player1.getMeter() >= (i - 1) * 0.25) {
				g2.fillRoundRect(meterX1 + (i - 1) * meterWidth / 4, meterY,
						(int) ((meterWidth * 0.95) * (player1.getMeter() - ((i - 1) * 0.25))), (int) (meterHeight * 0.95),
						meterCurve, meterCurve);
			}
			g2.setPaint(darkBlueToBlue);
			if (player2.getMeter() >= i * 0.25) {
				g2.setPaint(lightBlueToBlue);
			}
			if (player2.getMeter() >= (i - 1) * 0.25) {
				g2.fillRoundRect(meterX2 + (i - 1) * meterWidth / 4, meterY,
						(int) ((meterWidth * 0.95) * (player2.getMeter() - ((i - 1) * 0.25))), (int) (meterHeight * 0.95),
						meterCurve, meterCurve);
			}
		}
		g2.setColor(Color.DARK_GRAY);
		g2.setStroke(new BasicStroke((float) hpBarPadding));
		g2.drawRoundRect(meterX1 + hpBarPadding / 2, meterY + hpBarPadding / 2, meterWidth - hpBarPadding,
				meterHeight - hpBarPadding, meterCurve / 2, meterCurve / 2);
		g2.drawRoundRect(meterX2 + hpBarPadding / 2, meterY + hpBarPadding / 2, meterWidth - hpBarPadding,
				meterHeight - hpBarPadding, meterCurve / 2, meterCurve / 2);
		for (int i = 1; i < 4; i++) {
			g2.drawLine(meterX1 + meterWidth / 4 * i, meterY + hpBarPadding / 2, meterX1 + meterWidth / 4 * i,
					meterY + (int) (meterHeight * 0.90));
			g2.drawLine(meterX2 + meterWidth / 4 * i, meterY + hpBarPadding / 2, meterX2 + meterWidth / 4 * i,
					meterY + (int) (meterHeight * 0.90));
		}
		g2.setStroke(new BasicStroke(0f));
		GradientPaint grayToGray = new GradientPaint(hpBarX1, hpBarY, new Color(150, 150, 150), hpBarX1,
				hpBarY + hpBarHeight, new Color(100, 100, 100));
		g2.setPaint(grayToGray);
		g2.fillRect(hpBarX1 + hpBarPadding, hpBarY + hpBarPadding,
				(int) ((hpBarWidth - hpBarPadding * 2 + 1) * (player1.getHealthPercent() + player1.getGreyHealthPercent())), hpBarHeight - hpBarPadding * 2 + 1);
		g2.fillRect(hpBarX2 + hpBarPadding, hpBarY + hpBarPadding,
				(int) ((hpBarWidth - hpBarPadding * 2 + 1) * (player2.getHealthPercent() + player2.getGreyHealthPercent())), hpBarHeight - hpBarPadding * 2 + 1);
		GradientPaint redToRed = new GradientPaint(hpBarX1, hpBarY, new Color(204, 0, 0), hpBarX1,
				hpBarY + hpBarHeight, new Color(153, 0, 0));
		g2.setPaint(redToRed);
		g2.fillRect(hpBarX1 + hpBarPadding, hpBarY + hpBarPadding,
				(int) ((hpBarWidth - hpBarPadding * 2 + 1) * (player1.getHealthPercent() + player1.getComboPercent())), hpBarHeight - hpBarPadding * 2 + 1);
		g2.fillRect(hpBarX2 + hpBarPadding, hpBarY + hpBarPadding,
				(int) ((hpBarWidth - hpBarPadding * 2 + 1) * (player2.getHealthPercent() + player2.getComboPercent())), hpBarHeight - hpBarPadding * 2 + 1);
		GradientPaint greenToGreen = new GradientPaint(hpBarX1, hpBarY, new Color(0, 204, 0), hpBarX1,
				hpBarY + hpBarHeight, new Color(0, 153, 0));
		g2.setPaint(greenToGreen);
		g2.fillRect(hpBarX1 + hpBarPadding, hpBarY + hpBarPadding, (int) ((hpBarWidth - hpBarPadding * 2 + 1) * player1.getHealthPercent()), hpBarHeight - hpBarPadding * 2 + 1);
		g2.fillRect(hpBarX2 + hpBarPadding, hpBarY + hpBarPadding, (int) ((hpBarWidth - hpBarPadding * 2 + 1) * player2.getHealthPercent()), hpBarHeight - hpBarPadding * 2 + 1);
		g2.setColor(Color.DARK_GRAY);
		g2.setStroke(new BasicStroke((float) hpBarPadding));
		g2.drawRoundRect(hpBarX1 + hpBarPadding / 2, hpBarY + hpBarPadding / 2, hpBarWidth - hpBarPadding,
				hpBarHeight - hpBarPadding, hpBarPadding / 2, hpBarPadding / 2);
		g2.drawRoundRect(hpBarX2 + hpBarPadding / 2, hpBarY + hpBarPadding / 2, hpBarWidth - hpBarPadding,
				hpBarHeight - hpBarPadding, hpBarPadding / 2, hpBarPadding / 2);
		g2.setStroke(new BasicStroke((float) 0));
		
		/** Draw Combo Count **/
		int comboX1 = camera.getRelativeScreenX(0.15);
		int comboX2 = camera.getRelativeScreenX(0.85);
		int comboY = hpBarHeight + camera.getRelativeScreenY(0.2);
		int fontSize = camera.getRelativeScreenX(0.03);
		Window.coggersFont = Window.coggersFont.deriveFont((float) fontSize);
		g2.setFont(Window.coggersFont);
		int fontHeight = g2.getFontMetrics().getAscent();
		GradientPaint yellowToGold = new GradientPaint(comboX1, comboY - fontHeight / 2, Color.YELLOW, comboX1,
				comboY + fontHeight / 2, Color.ORANGE);
		if (player1.getComboCount() > 0 || comboData[0][1] == 0) {
			comboData[0][0] = player1.getComboCount();
			comboData[0][1] = 120;
		}
		if (comboData[0][0] > 1) {
			Window.drawCenteredString(g2, comboData[0][0] + " COMBO!", comboX1, comboY, yellowToGold, fontSize / 15);
			comboData[0][1]--;
		}
		if (player2.getComboCount() > 0 || comboData[1][1] == 0) {
			comboData[1][0] = player2.getComboCount();
			comboData[1][1] = 120;
		}
		if (comboData[1][0] > 1) {
			Window.drawCenteredString(g2, comboData[1][0] + " COMBO!", comboX2, comboY, yellowToGold, fontSize / 15);
			comboData[1][1]--;
		}
		
		/** Draw Round Count **/
		int roundsX1 = hpBarX1 + hpBarWidth;
		int roundsX2 = hpBarX2;
		int roundsY = hpBarY + hpBarHeight + camera.getRelativeScreenY(0.02);
		int roundWidth = camera.getRelativeScreenX(0.02);
		int padding = camera.getRelativeScreenX(0.01);
		int roundFontSize = roundWidth - 4;
		g2.setFont(new Font("Arial", 1, roundFontSize));
		for (int i = 0; i < results[0].length; i++) {
			if (results[0][i] != null) {
				g2.setColor(results[0][i].getBack());
				g2.fillOval(roundsX1 - roundWidth * (i + 1) - padding * i, roundsY, roundWidth, roundWidth);
				Window.drawCenteredString(g2, results[0][i].getMessage(),
						roundsX1 - roundWidth * (i + 1) - padding * i + roundWidth / 2, roundsY + roundWidth / 2 - 1,
						results[0][i].getColor(), 1);
			}
			g2.setColor(Color.BLACK);
			g2.drawOval(roundsX1 - roundWidth * (i + 1) - padding * i, roundsY, roundWidth, roundWidth);
		}
		for (int i = 0; i < results[1].length; i++) {
			if (results[1][i] != null) {
				g2.setColor(results[1][i].getBack());
				g2.fillOval(roundsX2 + roundWidth * (i) + padding * i, roundsY, roundWidth, roundWidth);
				Window.drawCenteredString(g2, results[1][i].getMessage(),
						roundsX2 + roundWidth * (i) + padding * i + roundWidth / 2, roundsY + roundWidth / 2 - 1,
						results[1][i].getColor(), 1);
			}
			g2.setColor(Color.BLACK);
			g2.drawOval(roundsX2 + roundWidth * (i) + padding * i, roundsY, roundWidth, roundWidth);
		}
		
		/** Fighter Names **/
		int nameX1 = hpBarX1 + camera.getRelativeScreenX(0.05);
		int nameX2 = hpBarX2  + hpBarWidth - camera.getRelativeScreenX(0.05);
		int nameY = meterY + meterHeight + camera.getRelativeScreenY(0.021);
		int nameSize = camera.getRelativeScreenX(0.015);
		Window.coggersFont = Window.coggersFont.deriveFont((float) nameSize);
		g2.setFont(Window.coggersFont);
		Window.drawCenteredString(g2, "Player 1", nameX1, nameY, Color.WHITE, 2);
		Window.drawCenteredString(g2, "Player 2", nameX2, nameY, Color.WHITE, 2);
		
		/** Timer **/
		int timerX = camera.getRelativeScreenX(0.5);
		int timerY = hpBarY + hpBarHeight / 2;
		int timerSize = camera.getRelativeScreenX(0.05);
		Window.coggersFont = Window.coggersFont.deriveFont((float) timerSize);
		g2.setFont(Window.coggersFont);
		Window.drawCenteredString(g2, "" + getTime(), timerX, timerY, Color.WHITE, 3);
		
		/** Results **/
		if (startRoundTick - Game.tick > 0 || gameOver) {
			g2.setColor(new Color(0, 0, 0, 75));
			g2.fillRect(0, 0, camera.getScreenWidth(), camera.getScreenHeight());
		}
		int messageX = camera.getRelativeScreenX(0.5);
		int messageY = camera.getRelativeScreenY(0.5);
		int messageFontSize = camera.getRelativeScreenX(0.1);
		Window.coggersFont = Window.coggersFont.deriveFont((float) messageFontSize);
		g2.setFont(Window.coggersFont);
		if (startRoundTick - Game.tick > 60 * 4 && !gameOver) {
			Window.drawCenteredString(g2, endRoundMessage, messageX, messageY, Color.WHITE, messageFontSize / 20);
		} else if (startRoundTick - Game.tick > 60 && !gameOver) {
			Window.drawCenteredString(g2, "" + ((startRoundTick - Game.tick) / 60), messageX, messageY, Color.WHITE, messageFontSize / 10);
		} else if (startRoundTick - Game.tick > 0 && !gameOver) {
			Window.drawCenteredString(g2, "FIGHT!", messageX, messageY, Color.WHITE, messageFontSize / 20);
		} else if (gameOver) {
			int gameOverY = camera.getRelativeScreenY(0.35);
			int subMessageY = camera.getRelativeScreenY(0.65);
			Window.drawCenteredString(g2, endRoundMessage, messageX, subMessageY, Color.WHITE, messageFontSize / 20);
			int gameOverFontSize = camera.getRelativeScreenX(0.16);
			Window.coggersFont = Window.coggersFont.deriveFont((float) gameOverFontSize);
			g2.setFont(Window.coggersFont);
			Window.drawCenteredString(g2, "GAME OVER", messageX, gameOverY, Color.WHITE, messageFontSize / 20);
		}
	}

	@Override
	public void logic() {
		if (gameOver) {
			tickRoundStarted = Integer.MIN_VALUE;
			return;
		}
		if (startRoundTick - Game.tick < 0) {
			if (getTime() == 0) {
				endRound();
			} else {
				if (hitLag > 0) {
					hitLag --;
					return;
				}
				player1.blockInput(false);
				player2.blockInput(false);
				playerLogic(player1, player2);
				playerLogic(player2, player1);
				handleHitboxes(player1, player2);
				if (player1.getHealthPercent() <= 0 && player1.getGreyHealthPercent() <= 0
						|| player2.getHealthPercent() <= 0 && player2.getGreyHealthPercent() <= 0) {
					endRound();
				}
				if (camera.getSpeed() < 1) {
					camera.setSpeed(camera.getSpeed() + 0.1);
				} else {
					camera.setSpeed(1);
				}
			}
		} else if (startRoundTick - Game.tick > 60 * 4) {
			tickRoundStarted = Integer.MIN_VALUE;
			player1.blockInput(true);
			player2.blockInput(true);
		} else {
			tickRoundStarted = Game.tick;
			player1.setPosition(new Position(stage.getWidth() / 4, 0));
			player2.setPosition(new Position(stage.getWidth() / 4 * 3, 0));
			player1.setFace(1);
			player2.setFace(-1);
			player1.reset();
			player2.reset();
			player1.blockInput(true);
			player2.blockInput(true);
			camera.setFocus(new Position(stage.getWidth() / 2, 1.35));
		}
	}
	
	public void endRound() {
		FightResult result = FightResult.WIN;
		boolean p1Wins = false;
		boolean p2Wins = false;
		if (getTime() == 0) {
			result = FightResult.TIME_OUT;
		}
		if (player1.getHealthPercent() + player1.getGreyHealthPercent() < player2.getHealthPercent()
				+ player2.getGreyHealthPercent()) {
			endRoundMessage = "Player 2 Wins!";
			p2Wins = addResult(result, 1);
		} else if (player1.getHealthPercent() + player1.getGreyHealthPercent() > player2.getHealthPercent()
				+ player2.getGreyHealthPercent()) {
			endRoundMessage = "Player 1 Wins!";
			p1Wins = addResult(result, 0);
		} else {
			if (getTime() > 0) {
				result = FightResult.DRAW;
			}
			if (player1.getHealthPercent() <= 0) {
				result = FightResult.DOUBLE_KO;
			}
			endRoundMessage = "Draw!";
			p1Wins = addResult(result, 0);
			p2Wins = addResult(result, 1);
		}
		if (p1Wins || p2Wins) {
			gameOver = true;
		}
		startRoundTick = Game.tick + 60 * 6;
	}
	
	public void playerLogic(Fighter player, Fighter other) {
		/** Animations **/
		player.stepAnimation(camera);
		if (player.getAnimation() == null) {
			if (player.isGrounded()) {
				player.updateGroundAnim();
			} else {
				player.setAnimation(SharedAnimation.IN_AIR, true);
			}
		}
		if (player.hasSlowDownActive()) {
			camera.setSpeed(0.1);
		}
		
		/** Gravity **/
		if (!player.hasSetVelocityY()) {
			if (!player.isGrounded() && player.getVelocity().getY() > player.getMaxFallSpeed()) {
				player.getVelocity().setY(player.getVelocity().getY() - player.getGravity() * camera.getSpeed());
				if (player.getVelocity().getY() < player.getMaxFallSpeed()) {
					player.getVelocity().setY(player.getMaxFallSpeed());
				}
			} else if (player.isGrounded()) {
				if (player.getVelocity().getY() < 0) {
					player.getVelocity().setY(0);
				}
				player.getPosition().setY(0);
			}
		}

		/** Friction **/
		if (player.isGrounded()) {
			player.getPosition().setY(0);
			if (!player.hasSetVelocityX()) {
				player.getVelocity().setX(player.getVelocity().getX() - 0.005 * camera.getSpeed() * Math.signum(player.getVelocity().getX()) );
				if (Math.abs(player.getVelocity().getX()) < 0.005) {
					player.getVelocity().setX(0);
				}
			}
		}
		
		/** Bounce off walls (in jump) **/
		if (!player.isGrounded()) {
			if (player.getPosition().getX() < 0.2) {
				player.getVelocity().setX(-player.getVelocity().getX());
			}
			if (player.getPosition().getX() > stage.getWidth() - 0.2) {
				player.getVelocity().setX(-player.getVelocity().getX());
			}
		}
		
		/** Stage edge collisions **/
		if (player.getPosition().getX() < 0.2) {
			player.getPosition().setX(0.2);
		}
		if (player.getPosition().getX() > stage.getWidth() - 0.2) {
			player.getPosition().setX(stage.getWidth() - 0.2);
		}
		
		boolean inAir = !player.isGrounded();
		/** Apply velocities **/
		player.setPosition(player.getPosition().applyVector(new Vector(player.getVelocity().getX() * camera.getSpeed(),
				player.getVelocity().getY() * camera.getSpeed())));

		if (player.isGrounded() && inAir) {
			player.updateGroundAnim();
		}
		
		/** Knockdown **/
		if (player.isGrounded() && player.needsKnockdown()) {
			player.setAnimation(player.usingQuickGetUp() ? SharedAnimation.KNOCKED_DOWN_FAST.toString()
					: SharedAnimation.KNOCKED_DOWN_SLOW.toString(), false);
			player.resetKnockdown();
		}
		
		/** Camera movement **/
		if (player.getPosition().getX() - other.getPosition().getX() > camera.getViewportSize() - 0.35) {
			player.getPosition().setX(other.getPosition().getX() + camera.getViewportSize() - 0.35);
		}
		if (other.getPosition().getX() - player.getPosition().getX() > camera.getViewportSize() - 0.35) {
			player.getPosition().setX(other.getPosition().getX() - camera.getViewportSize() + 0.35);
		}
		if (camera.getScreenX(player.getPosition()) - camera.toPixels(0.2) < 0
				/*&& other.getPosition().getX() - player.getPosition().getX() < camera.getViewportSize() - 0.4*/) {
			camera.setFocus(new Position(
					camera.getFocus().getX()
							+ camera.toGameDistance(camera.getScreenX(player.getPosition()) - camera.toPixels(0.2)),
					camera.getFocus().getY()));
		}
		if (camera.getScreenX(player.getPosition()) + camera.toPixels(0.2) > camera.getScreenWidth()
				/*&& player.getPosition().getX() - other.getPosition().getX() < camera.getViewportSize() - 0.4*/) {
			camera.setFocus(new Position(
					camera.getFocus().getX() + camera.toGameDistance(
							camera.getScreenX(player.getPosition()) + camera.toPixels(0.2) - camera.getScreenWidth()),
					camera.getFocus().getY()));
		}
		
		/** ECB Collisions **/
		if (player.getECB().forOffset(player.getFace(), player.getPosition())
				.intersects(other.getECB().forOffset(other.getFace(), other.getPosition())) && player.isGrounded()
				&& other.isGrounded()) {
			double off;
			Position newPos = new Position(player.getPosition());
			while (player.getECB().forOffset(player.getFace(), newPos)
					.intersects(other.getECB().forOffset(other.getFace(), other.getPosition()))) {
				newPos.setX(newPos.getX() - 0.001 * (other.getPosition().getX() - player.getPosition().getX()));
			}
			off = player.getPosition().getX() - newPos.getX();
			player.setPosition(new Position(player.getPosition().getX() - off / 2, 0));
			other.setPosition(new Position(other.getPosition().getX() + off / 2, 0));
		}
		
		/** Facing **/
		if (player.needFaceCheck()) {
			player.setFace((player.getPosition().getX() < other.getPosition().getX()) ? 1 : -1);
			player.resetFaceCheck();
		}
		
		/** Hitstun **/
		if (player.getHitStun() > 0) {
			player.setHitStun(player.getHitStun() - camera.getSpeed());
			if (player.getHitStun() <= 0) {
				other.setComboCount(0);
				player.resetCombo();
				player.updateGroundAnim();
			}
		}
		if (player.isGrounded() && player.getHitStun() < 0) {
			player.setHitStun(0);
			other.setComboCount(0);
			player.resetCombo();
			player.updateGroundAnim();
		}
		
		/** Gray health **/
		player.hpTick();
		
		player.normalizeColor();
	}
	
	public void handleHitboxes(Fighter player1, Fighter player2) {
		HitBox hitPlayer1 = null;
		HitBox hitPlayer2 = null;
		loop1: {
			for (HurtBox hurtBox : player2.getHurtBoxes()) {
				for (HitBox hitBox : player1.getHitBoxes()) {
					if (hurtBox.forOffset(player2.getFace(), player2.getPosition())
							.intersects(hitBox.forOffset(player1.getFace(), player1.getPosition()))) {
						hitPlayer2 = hitBox;
						break loop1;
					}
				}
			}
		}
		loop2: {
			for (HurtBox hurtBox : player1.getHurtBoxes()) {
				for (HitBox hitBox : player2.getHitBoxes()) {
					if (hurtBox.forOffset(player1.getFace(), player1.getPosition())
							.intersects(hitBox.forOffset(player2.getFace(), player2.getPosition()))) {
						hitPlayer1 = hitBox;
						break loop2;
					}
				}
			}
		}
		boolean appliedHit = false;
		if (hitPlayer1 != null || hitPlayer2 != null) {
			if (hitPlayer1 == null && hitPlayer2 != null) {
				appliedHit = hitPlayer2.applyHit(player1, player2);
			} else if (hitPlayer2 == null && hitPlayer1 != null) {
				appliedHit = hitPlayer1.applyHit(player2, player1);
			} else {
				int damage1 = hitPlayer1.getDamage() / 10;
				int damage2 = hitPlayer2.getDamage() / 10;
				if (damage1 > damage2) {
					appliedHit = hitPlayer1.applyHit(player2, player1);
				} else if (damage1 < damage2) {
					appliedHit = hitPlayer2.applyHit(player1, player2);
				} else {
					appliedHit = hitPlayer1.applyHit(player2, player1);
					appliedHit = hitPlayer2.applyHit(player1, player2);
				}
			}
		}
		if (appliedHit) {
			hitLag = 10;
		}
	}
	
	public int getTime() {
		int time = (int) (roundLength - (Game.tick - tickRoundStarted) / 60);
		return time > 0 ? time : 0;
	}
	
	public boolean addResult(FightResult result, int winner) {
		for (int i = 0; i < results[winner].length; i++) {
			if (results[winner][i] == null) {
				results[winner][i] = result;
				if (i == results[winner].length - 1) {
					return true;
				}
				return false;
			}
		}
		return false;
	}

}
