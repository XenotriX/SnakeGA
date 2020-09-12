package org.xenotrix.snakega;

import java.util.concurrent.ThreadLocalRandom;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

/**
 * Implementation of the game
 * @author XenotriX
 *
 */
public class Game {
	private static final int GRID_SIZE = 20;

	private final int id;

	private Player player = new Player(GRID_SIZE);
	
	public Player getPlayer() {
		return player;
	}
	
	private int[] applePosition = {0, 0};
	
	public int[] getApplePosition() {
		return applePosition;
	}
	
	Game(int id){
	    this.id = id;
		resetApple();
	}

	public int getId() {
		return this.id;
	}

	/**
	 * The game loop
	 */
	public void next() {
		checkCollision();
		checkScore();
		player.takeDecision(applePosition);
	}
	
	/**
	 * Reset the position of the apple to random coordinates.
	 */
	public void resetApple(){
		ThreadLocalRandom rand = ThreadLocalRandom.current();
		this.applePosition[0] = rand.nextInt(0, GRID_SIZE);
		this.applePosition[1] = rand.nextInt(0, GRID_SIZE);
	}
	
	/**
	 * Checks whether the player has scored a point.
	 */
	public void checkScore(){
		if (player.getPosition()[0] == applePosition[0] &&
			player.getPosition()[1] == applePosition[1]){
			player.incrementScore();
			player.eat();
			resetApple();
		}
	}
	
	/**
	 * Check if the player is colliding with himself or the wall.
	 */
	private void checkCollision(){
		int[] pos = player.getPosition();
		if (pos[0] < 0 || pos[0] == GRID_SIZE - 1 ||
			pos[1] < 0 || pos[1] == GRID_SIZE - 1){
			player.die();
		}
		for (int i = 1; i < player.getBody().size(); i++){
			if (pos[0] == player.getBody().get(i)[0] &&
				pos[1] == player.getBody().get(i)[1]){
				player.die();
				break;
			}
		}
	}
}
