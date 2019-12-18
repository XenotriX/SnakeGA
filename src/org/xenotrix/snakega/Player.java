package org.xenotrix.snakega;

import java.util.LinkedList;

/**
 * Entity that playes the game based on its genotype
 * @author XenotriX
 *
 */
public class Player {
	
	/**
	 * Position of every body part.
	 */
	private LinkedList<int[]> body = new LinkedList<>();
	

	public LinkedList<int[]> getBody() {
		return body;
	}

	public void setBody(LinkedList<int[]> body) {
		this.body = body;
	}

	private int arenaSize;

	Player(int arenaSize){
		this.arenaSize = arenaSize;
		this.reset();
	}
	
	/**
	 * Position of the head.
	 */
	private int[] position = {0, 0};

	public int[] getPosition(){
		return this.position;
	}
	
	private Genotype genotype;
	
	public void setGenotype(Genotype genotype){
		this.genotype = genotype;
	}

	public Genotype getGenotype(){
		return this.genotype;
	}
	
	/**
	 * Represents the status of the player. (true: alive and false: dead)
	 */
	private boolean alive = true;
	
	public boolean getLife(){
		return this.alive;
	}

	/**
	 * Number of actions the player can take before dying.
	 */
	private int timeToLive = 100;
	
	/**
	 * Adds 50 to the timeToLive
	 */
	public void eat() {
		timeToLive += 50;
	}
	
	/**
	 * Resets the player.
	 * This includes position, body length, status and score.
	 */
	public void reset() {
		this.position[0] = Math.round(arenaSize/(float)2);
		this.position[1] = Math.round(arenaSize/(float)2);
		
		setBody(new LinkedList<>());
		getBody().add(new int[]{position[0], position[1]});
		getBody().add(new int[]{position[0], position[1] + 1});
		getBody().add(new int[]{position[0], position[1] + 2});
		
		alive = true;
		timeToLive = 100;
		score = 0;
	}
	
	public float[] inputs;
	public float[] outputs;

	/**
	 * Proceeds the Neural network
	 * @param applePosition Position of the apple
	 */
	public float[] calcInputs(int[] applePosition){
		float[] inputs = new float[6];
		// Distance to apple
		inputs[0] = (float)(applePosition[0] - position[0]) / (float)arenaSize;
		inputs[1] = (float)(applePosition[1] - position[1]) / (float)arenaSize;
		// Body contact
		inputs[2] = -1;
		inputs[3] = -1;
		inputs[4] = -1;
		inputs[5] = -1;
		for (int[] bodyPart: body) {
			if (bodyPart[0] == position[0] &&
				bodyPart[1] == position[1] - 1)
					inputs[2] = 1;
			else if (bodyPart[0] == position[0] + 1 &&
					 bodyPart[1] == position[1])
						inputs[3] = 1;
			else if (bodyPart[0] == position[0] &&
					 bodyPart[1] == position[1] + 1)
						inputs[4] = 1;
			else if (bodyPart[0] == position[0] - 1 &&
					 bodyPart[1] == position[1])
						inputs[5] = 1;
		}
		return inputs;
	}

	/**
	 * Directions
	 */
	public enum DIR {
		UP, DOWN, RIGHT, LEFT
	}
	
	/**
	 * Moves the player in the specified direction.
	 */
	public void move(DIR direction){
		if (timeToLive-- <= 0){
			alive = false;
			return;
		}

		switch (direction) {
		case UP:
			this.position[1]--;
			break;
		case DOWN:
			this.position[1]++;
			break;
		case RIGHT:
			this.position[0]++;
			break;
		case LEFT:
			this.position[0]--;
			break;
		}
				
		/* Add a body part at new position.
		 * If the body is longer the it should be according to the score, pop the last part. */
		getBody().add(0, new int[]{this.position[0], this.position[1]});
		if (getBody().size() - 4 == score){
			getBody().removeLast();			
		}
	}
	
	/**
	 * Current score
	 */
	private int score;
	
	/**
	 * Increments the score by one.
	 */
	public void incrementScore(){
		this.score++;
	}
	
	/**
	 * Returns the current score.
	 */
	public int getScore(){
		return this.score;
	}
	
	/**
	 * Makes the player dead and assigns the fitness.
	 */
	public void die(){
		this.alive = false;
		genotype.setFitness(this.score);
	}
}
