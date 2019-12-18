
package org.xenotrix.snakega;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Main class
 * @author XenotriX
 *
 */
public class SnakeGA {
	private static final float MUTATION_RATE = 0.2f;
	private static final int POPULATION_SIZE = 1000;

	// Statistics
	ArrayList<Integer> highScoreList = new ArrayList<>();
	ArrayList<Float> avgScoreList = new ArrayList<>();
	int generation = 1;
	
	static int timeMultiplier = 1;
	GameLoop loop;
	
	Game[] games = initializeGames(POPULATION_SIZE);
	Genotype[] population = initializePopulation(POPULATION_SIZE);

	public SnakeGA() {
		assignGenotypes(games, population);
	}
	
	/**
	 * Main loop, this updates the logic and triggers the rendering.
	 * This method is called by the gameloop every frame
	 */
	public void update() {
		// This gets executed multiple times based on the multiplier slider.
		for (int i = 0; i < timeMultiplier; i++) {
			/*
			 * Check if at least on player is still alive
			 */
			boolean isRunning = false;
			for (int j = 0; j < games.length; j++) {
				if (games[j].getPlayer().getLife()) {
					games[j].next();
					isRunning = true;
				}
			}

			// Gets executed when every player is dead
			if (!isRunning) {
				int highestFitness = getHighestFitness(population);

				highScoreList.add(highestFitness);
				avgScoreList.add(calcAverage(population));

				// Genetic operations
				Genotype[] newPopulation = crossoverPopulation(population);
				newPopulation = mutatePopulation(MUTATION_RATE, newPopulation);

				population = newPopulation;
				generation++;
				assignGenotypes(games, population);
			} 
		}
	}

	/**
	 * Creates the initial population
	 */
	private static Genotype[] initializePopulation(int populationSize) {
		Genotype[] population = new Genotype[populationSize];
		
		for (int i = 0; i < population.length; i++) {
			Genotype genotype = new Genotype();
			population[i] = genotype;
		}
		
		return population;
	}

	/**
	 * Initializes the games and adds them to the window grid.
	 */
	private static Game[] initializeGames(int populationSize) {
		// Initiate game
		Game[] games = new Game[populationSize];
		
		for (int i = 0; i < games.length; i++) {
			Game game = new Game(i);
			Genotype genotype = new Genotype();
			game.getPlayer().setGenotype(genotype);
			games[i] = game;
		}
		
		return games;
	}

	/**
	 * Calculates the average fitness of the given population
	 */
	private static float calcAverage(Genotype[] population) {
		int sum = 0;
		for (Genotype genotype: population){
			sum += genotype.getFitness();
		}
		return (float)sum / population.length;
	}
	
	/**
	 * Finds the highest fitness in the population.
	 */
	private static int getHighestFitness(Genotype[] population) {
		int highest = 0;
		for (Genotype genotype: population){
			int fitness = genotype.getFitness();
			if (fitness > highest)
				highest = fitness;
		}
		return highest;
	}
	
	/**
	 * Assigns a genotype to every game
	 */
	private static void assignGenotypes(Game[] games, Genotype[] population) {
		for (int i = 0; i < games.length; i++) {
			games[i].getPlayer().setGenotype(population[i]);
			games[i].resetApple();
			games[i].getPlayer().reset();
		}
	}
	/**
	 * Generates new generation by crossover.
	 * The Parents are selected via a Accept-Reject Selection.
	 */
	private static Genotype[] crossoverPopulation(Genotype[] population) {
		int sum = 0;
		
		for (Genotype genotype : population) {
			sum += genotype.getFitness();
		}
		
		Genotype[] newPopulation = new Genotype[population.length];
		
		ThreadLocalRandom rand = ThreadLocalRandom.current();
		
		// Accept-Reject selection
		for (int i = 0; i < population.length; i += 2) {
			Genotype parent1 = null;
			Genotype parent2 = null;
			int indexParent1 = 0;
			int indexParent2;
			
			// Select first parent
			while (parent1 == null) {
				indexParent1 = rand.nextInt(0, population.length);
				float threshold = rand.nextFloat();
				if (sum == 0 ||
					((population[indexParent1].getFitness() / (float)sum) > threshold)) {
						parent1 = population[indexParent1];
				}
			}
			
			// Select second parent
			while (parent2 == null) {
				indexParent2 = rand.nextInt(0, population.length);
				float threshold = rand.nextFloat();
				if (indexParent1 != indexParent2 &&
					(sum == 0 || population[indexParent1].getFitness() == sum ||
					((population[indexParent2].getFitness() / (float)sum) > threshold))) {
						parent2 = population[indexParent2];
				}
			}
			
			// Crossover
			newPopulation[i] = parent1.crossover(parent2);
			newPopulation[i + 1] = parent1.clone();
		}
		return newPopulation;
	}

	/**
	 * Applies a mutation to every genotype in the population
	 */
	private static Genotype[] mutatePopulation(float mutationRate, Genotype[] population) {
		Genotype[] newPopulation = new Genotype[population.length];
		// Mutation
		for (int i = 0; i < newPopulation.length; i++) {
			newPopulation[i] = population[i].mutate(mutationRate);
		}
		return newPopulation;
	}

	public Game getBestGame() {
		Game bestGame = games[0];
		for (int j = 1; j < games.length; j++) {
			// Check if it's alive
			if (games[j].getPlayer().getLife()) {
				// Compare the score to the previous best
				if (games[j].getPlayer().getScore() > bestGame.getPlayer().getScore()){
					bestGame = games[j];
				}
			}
		}
		return bestGame;
	}

	public ArrayList<Integer> getHighScoreList() {
		return highScoreList;
	}

	public ArrayList<Float> getAvgScoreList() {
		return avgScoreList;
	}

	public void setTimeMultiplier(int timeMultiplier) {
		this.timeMultiplier = timeMultiplier;
	}

	public int getHighScore() {
		int highScore = 0;
		for (int score : highScoreList) {
			if (score > highScore) {
				highScore = score;
			}
		}
		return highScore;
	}

	public int getGeneration() {
		return generation;
	}
}
