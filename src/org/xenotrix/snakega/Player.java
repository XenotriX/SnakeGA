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
		
		setBody(new LinkedList<int[]>());
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
	 * Moves according to the given inputs.
	 * The output are calculated through a weighted sum of the inputs.
	 * @param applePosition Position of the apple
	 */
	public void takeDecision(int[] applePosition) {
		if (timeToLive-- <= 0){
			alive = false;
			return;
		}
		
		inputs = calcInputs(applePosition);
		
		outputs = calculateOutputs(inputs);
		
		// Find index with highest value
		int index = 0;
		for (int i = 0; i < outputs.length; i++)
			if (outputs[i] < outputs[index])
				index = i;
		
		// Choose direction to move
		if (index == 0)
			this.move(DIR.UP);
		else if (index == 2)
			this.move(DIR.DOWN);
		else if (index == 1)
			this.move(DIR.RIGHT);
		else if (index == 3)
			this.move(DIR.LEFT);
	}

	/**
	 * Proceeds the Neural network
	 * @param Position of the apple
	 */
	private float[] calcInputs(int[] applePosition){
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
	 * Processes the neural network and returns the output layer.
	 * @param inputs Inputs
	 * @return Outputs as softmax distribution
	 */
	public float[] calculateOutputs(float[] inputs) {
		// Weighted sum
		float[] wheightedSums = new float[genotype.getChromosome().length];
		for (int i = 0; i < wheightedSums.length; i++) {
			wheightedSums[i] = weightedSum(inputs, genotype.getChromosome()[i]);
		}
		
		// Apply softmax
		float [] softmaxDistribution = softmax(wheightedSums);
		return softmaxDistribution;
	}

	/**
	 * Returns the weighted sum of the inputs and weights.
	 * @param Inputs
	 * @param Weights
	 * @return Weighted sum
	 */
	private float weightedSum(float[] inputs, float[] weights) {
		float sum = 0;
		for (int i = 0; i < inputs.length; i++)
			sum += inputs[i] * weights[i];
		
		return sum;
	}
	
	/**
	 * Returns the softmax distribution of the inputs.
	 * @param Values
	 * @return Distribution
	 */
	private float[] softmax(float[] values) {
		// Calculate exp for every output
		float[] exp = new float[values.length];
		for (int i = 0; i < exp.length; i++)
			exp[i] = (float)Math.exp(values[i]);
		
		// Sum up all exps
		float sum = 0;
		for (int i = 0; i < exp.length; i++)
			sum += exp[i];
		
		// Calculate individual probability
		float[] result = new float[exp.length];
		for (int i = 0; i < result.length; i++)
			result[i] = exp[i] / sum;
		
		return result;
	}
	
	/**
	 * Directions
	 */
	public enum DIR {
		UP, DOWN, RIGHT, LEFT;
	}
	
	/**
	 * Moves the player in the specified direction.
	 * @param direction Direction
	 */
	public void move(DIR direction){
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
	 * @return Score
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
