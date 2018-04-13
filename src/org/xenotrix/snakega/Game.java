package org.xenotrix.snakega;

import java.util.concurrent.ThreadLocalRandom;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

/**
 * Implementation of the game including rendering
 * @author XenotriX
 *
 */
public class Game {
	private static final int GRID_SIZE = 20;
	
	private Player player = new Player(GRID_SIZE);
	
	public Player getPlayer() {
		return player;
	}
	
	private int[] applePosition = {0, 0};
	
	public int[] getApplePosition() {
		return applePosition;
	}
	
	Game(){
		resetApple();
	}
	
	/**
	 * Renders the game on the given canvas.
	 * @param canvas The Canvas the game is drawn on
	 */
	public void renderGame(Canvas canvas){
		double size = canvas.getWidth();
		float ratio = (float)size / GRID_SIZE;
		GraphicsContext ctx = canvas.getGraphicsContext2D();
		// Draw background
		ctx.setFill(Color.rgb(42, 42, 42));
		ctx.fillRect(0, 0, size, size);
		ctx.setStroke(Color.rgb(50, 50, 50));
		for (int y = 0; y < GRID_SIZE; y++){
			for (int x = 0; x < GRID_SIZE; x++){
				ctx.beginPath();
				ctx.rect(x * ratio, y * ratio, ratio, ratio);
				ctx.stroke();
			}
		}
		// Draw apple
		ctx.setFill(Color.RED);
		ctx.fillRect(this.applePosition[0] * ratio, this.applePosition[1] * ratio, ratio, ratio);
		// Draw player
		ctx.setFill(Color.WHITE);
		for (int[] pos: player.getBody()){
			ctx.fillRect(pos[0] * ratio, pos[1] * ratio, ratio, ratio);
		}
	}
	
	/**
	 * Renders the Neural Network on the given canvas
	 * @param canvas The canvas the network is drawn on.
	 */
	public void renderNetwork(Canvas canvas) {
		float[] inputs = player.inputs;
		float[] outputs = player.outputs;
		float[][] weights = player.getGenotype().getChromosome();

		float circleSize = ((float)canvas.getHeight() / inputs.length) * 0.75f;
		int gap = 10;
		
		GraphicsContext ctx = canvas.getGraphicsContext2D();
	
		//// Clear Background
		ctx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		// Measurements
		float height = (float)canvas.getHeight();
		int numberOfInputs = inputs.length;
		float diameterOfCircle = circleSize;
		float offsetTopInputs = (height - (numberOfInputs * diameterOfCircle + (numberOfInputs - 1) * gap)) / 2;
		int numberOfOuputs = outputs.length;
		float offsetTopOutputs = (height - (numberOfOuputs * diameterOfCircle + (numberOfOuputs - 1) * gap)) / 2;
		
		//// Draw lines
		for(int o = 0; o < weights.length; o++){
			for(int i = 0; i < weights[0].length; i++){
				double greyScale = 1 / (1 + Math.exp(-weights[o][i]));
				ctx.setStroke(new Color(greyScale, greyScale, greyScale, 1));
				ctx.beginPath();
				ctx.moveTo(20 + circleSize / 2, i * (circleSize + gap) + offsetTopInputs + circleSize / 2);
				ctx.lineTo(canvas.getWidth() - 70 + circleSize / 2, o * (circleSize + gap) + offsetTopOutputs + circleSize / 2);
				ctx.stroke();
			}
		}

		//// Draw Inputs
		
		for (int i = 0; i < inputs.length; i++){
			double greyScale = 1 / (1 + Math.exp(-inputs[i]));
//			ctx.setFill(new Color(1 - greyScale, greyScale, 0, 1));
			ctx.setFill(new Color(1 - greyScale, 1 - greyScale, 1 - greyScale, 1));
			ctx.setStroke(new Color(0.75, 0.75, 0.75, 1));
			ctx.setLineWidth(2);
			ctx.fillArc(20, i * (circleSize + gap) + offsetTopInputs, circleSize, circleSize, 0, 360, ArcType.OPEN);
			ctx.strokeArc(20, i * (circleSize + gap) + offsetTopInputs, circleSize, circleSize, 0, 360, ArcType.OPEN);
		}
		
		//// Draw outputs	
		for (int i = 0; i < outputs.length; i++){
//			double greyScale = 1 / (1 + Math.exp(-outputs[i]));
			double greyScale = outputs[i];
			ctx.setFill(new Color(1 - greyScale, 1 - greyScale, 1 - greyScale, 1));
			ctx.setStroke(new Color(0.75, 0.75, 0.75, 1));
			ctx.setLineWidth(2);
			ctx.fillArc(canvas.getWidth() - 70, i * (circleSize + gap) + offsetTopOutputs, circleSize, circleSize, 0, 360, ArcType.OPEN);
			ctx.strokeArc(canvas.getWidth() - 70, i * (circleSize + gap) + offsetTopOutputs, circleSize, circleSize, 0, 360, ArcType.OPEN);
		}
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
