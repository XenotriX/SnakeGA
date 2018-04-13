package org.xenotrix.snakega;

import java.util.concurrent.ThreadLocalRandom;
/**
 * Represents the specimen and implements the genetic operations
 * @author XenotriX
 *
 */
public class Genotype {
	
	private final static int INPUT_COUNT = 6;
	private final static int OUTPUT_COUNT = 4;
	

	Genotype() {
		setChromosome(new float[OUTPUT_COUNT][]);
		ThreadLocalRandom rand = ThreadLocalRandom.current();
		for (int i = 0; i < OUTPUT_COUNT; i++) {
			getChromosome()[i] = new float[INPUT_COUNT];
			for (int j = 0; j < INPUT_COUNT; j++) {
				getChromosome()[i][j] = (float)rand.nextDouble(-1.0, 1.0);
			}
		}
	}
	
	/**
	 * Contains the weights of the NN.
	 * The first dimension represents the neuron of the
	 * output layer and the second dimension represents the weights.
	 */
	private float[][] chromosome;
	
	public float[][] getChromosome() {
		return chromosome;
	}
	
	public void setChromosome(float[][] chromosome) {
		this.chromosome = chromosome;
	}

	private int fitness = 0;
	
	/**
	 * Sets the fitness.
	 * @param fitness Fitness value
	 */
	public void setFitness(int fitness) {
		this.fitness = fitness;
	}
	
	/**
	 * Returns the fitness
	 * @return the fitness
	 */
	public int getFitness() {
		return fitness;
	}
	
	/**
	 * Calculates a child using a single point crossover.
	 * @param otherParent parent to crossover
	 * @return Child
	 */
	public Genotype crossover(Genotype otherParent) {
		float[][] newChromosome = new float[getChromosome().length][];
		
		for (int i = 0; i < getChromosome().length; i++) {
			newChromosome[i] = new float[getChromosome()[0].length];
			
			for (int j = 0; j < getChromosome()[0].length; j++) {
				if (i % 2 == 0)
					newChromosome[i][j] = getChromosome()[i][j];
				else
					newChromosome[i][j] = otherParent.getChromosome()[i][j];
			}
		}
		
		Genotype newGenotype = new Genotype();
		newGenotype.setChromosome(newChromosome);
		
		return newGenotype;
	}
	
	/**
	 * Applies mutation to the chromosome according to the mutation rate.
	 * @param mutationRate Mutation rate as % / 100
	 * @return mutated Genotype
	 */
	public Genotype mutate(float mutationRate){
		ThreadLocalRandom rand = ThreadLocalRandom.current();
		Genotype newGenotype = new Genotype();
		
		for (int i = 0; i < getChromosome().length; i++)
				for (int j = 0; j < getChromosome()[0].length; j++){
					if (rand.nextFloat() < mutationRate)
						newGenotype.getChromosome()[i][j] = this.getChromosome()[i][j] + (float)rand.nextDouble(-1.0, 1.0);
					else
						newGenotype.getChromosome()[i][j] = this.getChromosome()[i][j];
				}
		return newGenotype;
	}
	
	public Genotype clone() {
		Genotype newGenotype = new Genotype();
		float[][] newChromosome = new float[getChromosome().length][];
		for (int i = 0; i < getChromosome().length; i++) {
			newChromosome[i] = new float[getChromosome()[0].length];
			for (int j = 0; j < getChromosome()[0].length; j++){
				newChromosome[i][j] = this.getChromosome()[i][j];
			}
		}
		newGenotype.setChromosome(newChromosome);
		return newGenotype;
	}
}
