
package org.xenotrix.snakega;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.xenotrix.snakega.ui.GameView;
import org.xenotrix.snakega.ui.NetworkView;
// TODO Split this into multiple classes to improve readability.
/**
 * Main class
 * @author XenotriX
 *
 */
public class SnakeGA extends Application {
	private static final float MUTATION_RATE = 0.2f;
	private static final int POPULATION_SIZE = 1000;
	
	// Statistics
	ArrayList<Float> highScoreList = new ArrayList<Float>();
	ArrayList<Float> avgScoreList = new ArrayList<Float>();
	int generation = 1;
	
	static int timeMultiplier = 1;
	GameLoop loop;
	
	Game[] games = initializeGames(POPULATION_SIZE);
	Genotype[] population = initializePopulation(POPULATION_SIZE);
	
	Stage stage;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage){
		this.stage = stage;
		createWindow(this.stage);
		assignGenotypes(games, population);
		loop = new GameLoop(this);
		loop.start();
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
			 * and find the best one.
			 */
			boolean isRunning = false;
			Game bestGame = games[0];
			for (int j = 0; j < games.length; j++) {
				if (games[j].getPlayer().getLife()) {
					if (games[j].getPlayer().getScore() > bestGame.getPlayer().getScore()){
						bestGame = games[j];
						lblGenotype.setText(Integer.toString(j));
					}
					games[j].next();
					isRunning = true;
				}
			}
			render(bestGame); // Render the best game on the canvas
			lblScore.setText(Integer.toString(bestGame.getPlayer().getScore()));
			
			// Gets executed when every player is dead
			if (!isRunning) {
				// Calculate and display statistics
				lblGen.setText(Integer.toString(generation + 1));

				int highestFitness = getHighestFitness(population);
				highScoreList.add((float)highestFitness);

				avgScoreList.add(calcAverage(population));

				// Render Graphs
				renderGraph(graphBestCanvas, highScoreList);
				renderGraph(graphAvgCanvas, avgScoreList);
				
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
	 * Draws a graph of the provided data on the canvas
	 * @param canvas Canvas to draw on
	 * @param list List of values
	 */
	public void renderGraph(Canvas canvas, ArrayList<Float> list){
		GraphicsContext ctx = canvas.getGraphicsContext2D();
		int length = list.size();
		if (length == 0) return;
		
		ctx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		double[] x = new double[length + 2];
		double[] y = new double[length + 2];
		
		
		// Find highest
		float highest = 0;
		for (float val: list){
			if (val > highest) highest = val;
		}
		
		// set points
		float ratio = (float)canvas.getWidth() / (length - 1);
		for (int i = 0; i < length; i++){
			// Smooth graph
			float sum = 0;
			int count = 0;
			for (int j = (i > 5)? i - 5 : i; j < ((i < length - 5)? i + 5 : length) ; j++){
				sum += list.get(j);
				count++;
			}
			// Add values
			x[i] = ratio * i;
			y[i] = canvas.getHeight() - (sum / count) * (canvas.getHeight() / highest);
		}
		
		// Set bottom points
		x[x.length - 2] = canvas.getWidth();
		y[y.length - 2] = canvas.getHeight();
		x[x.length - 1] = 0;
		y[y.length - 1] = canvas.getHeight();
		
		ctx.fillPolygon(x, y, length + 2);
	}
	
	public void render(Game game){
		gameView.render(game);
		networkView.render(game);
	}

	/**
	 * Creates the initial population
	 * @param Population size
	 * @return Population
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
	 * @param window
	 * @param Population size
	 * @return Games
	 */
	private static Game[] initializeGames(int populationSize) {
		// Initiate game
		Game[] games = new Game[populationSize];
		
		for (int i = 0; i < games.length; i++) {
			Game game = new Game();
			Genotype genotype = new Genotype();
			game.getPlayer().setGenotype(genotype);
			games[i] = game;
		}
		
		return games;
	}

	// UI elements that need to be accessible.
	static GameView gameView;
	static NetworkView networkView;
	static Canvas graphBestCanvas;
	static Canvas graphAvgCanvas;
	static Label lblGenotype;
	static Label lblScore;
	static Label lblGen;
	
	/**
	 * Creates a window with all the needed UI elements.
	 * It's a bit messy :)
	 * @return Window
	*/
	private static void createWindow(Stage stage){
		// Grid
		GridPane root = new GridPane();
		root.setHgap(10);
		root.setVgap(10);
		root.setPadding(new Insets(0, 10, 0, 10));
		//// Columns
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(38);
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(62);
		root.getColumnConstraints().addAll(column1, column2);
	
		// Game
		VBox game = new VBox();
		root.add(game, 0, 0);
		//// Title
		HBox stats = new HBox();
		game.getChildren().add(stats);
		////// Generation
		Label lbl3 = new Label("Gen: ");
		lbl3.setFont(new Font(30));
		stats.getChildren().add(lbl3);
		lblGen = new Label("1");
		lblGen.setFont(new Font(30));
		stats.getChildren().add(lblGen);
		////// Genotype
		Label lbl1 = new Label(" Genotype: ");
		lbl1.setFont(new Font(30));
		stats.getChildren().add(lbl1);
		lblGenotype = new Label();
		lblGenotype.setFont(new Font(30));
		stats.getChildren().add(lblGenotype);
		////// Score
		Label lbl2 = new Label(" Score: ");
		lbl2.setFont(new Font(30));
		stats.getChildren().add(lbl2);
		lblScore = new Label();
		lblScore.setFont(new Font(30));
		stats.getChildren().add(lblScore);
		////// Game
		gameView = new GameView(500, 500);
		game.getChildren().add(gameView);

		// Neural Network
		networkView = new NetworkView(500, 500);
		root.add(networkView, 1, 0);
		
		// Menu
		VBox menu = new VBox();
		root.add(menu, 0, 1);
		//// Options
		menu.getChildren().add(new Label("Options"));
		menu.getChildren().add(new Separator());
		Label lblMult = new Label("Time Scale: ");
		menu.getChildren().add(lblMult);
		Slider slidMultiplier = new Slider(1, 100, 1);
		slidMultiplier.valueProperty().addListener(new ChangeListener<Number>(){
			public void changed(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val){
						timeMultiplier = new_val.intValue();
			}
		});
		menu.getChildren().add(slidMultiplier);
		
		// Graphs
		TabPane graphs = new TabPane();
		graphs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		root.add(graphs, 1, 1);
		//// Best per generation
		Tab bestPerGen = new Tab("Best per generation");
		graphs.getTabs().add(bestPerGen);
		graphBestCanvas = new Canvas(700, 300);
		bestPerGen.setContent(graphBestCanvas);
		//// Avg per generation
		Tab avgPerGen = new Tab("Average per generation");
		graphs.getTabs().add(avgPerGen);
		graphAvgCanvas = new Canvas(700, 300);
		avgPerGen.setContent(graphAvgCanvas);
		stage.setScene(new Scene(root, 1320, 900));
		stage.setTitle("SnakeGA");
		stage.show();
	}

	/**
	 * Calculates the average fitness of the given population
	 * @param Population
	 * @return Average fitness
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
	 * @param Population
	 * @return Highest fitness
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
	 * @param Games
	 * @param Population
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
	 * @param Population
	 * @return New population
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
			int indexParent2 = 0;
			
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
			newPopulation[i + 1] = parent1.clone(); // FIXME Replace with clone()
		}
		return newPopulation;
	}

	/**
	 * Applies a mutation to every genotype in the population
	 * @param Mutation rate
	 * @param Population
	 * @return Mutated population
	 */
	private static Genotype[] mutatePopulation(final float MUTATION_RATE, Genotype[] population) {
		Genotype[] newPopulation = new Genotype[population.length];
		// Mutation
		for (int i = 0; i < newPopulation.length; i++) {
			newPopulation[i] = population[i].mutate(MUTATION_RATE);
		}
		return newPopulation;
	}
}
