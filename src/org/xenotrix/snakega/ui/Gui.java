package org.xenotrix.snakega.ui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.xenotrix.snakega.Game;
import org.xenotrix.snakega.GameLoop;
import org.xenotrix.snakega.SnakeGA;
import org.xenotrix.snakega.ui.statistics.StatisticsView;

public class Gui extends Application {
    private Label lblGen;
    private Label lblGenotype;
    private Label lblScore;
    private GameView gameView;
    private NetworkView networkView;
    private int timeMultiplier;
    private StatisticsView statisticsView;

    @Override
    public void start(Stage stage){
        createWindow(stage);
    }

    /**
     * Creates a window with all the needed UI elements.
     * It's a bit messy :)
     * @return Window
     */
    private void createWindow(Stage stage){
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
        slidMultiplier.valueProperty().addListener((ov, old_val, new_val)
                -> timeMultiplier = new_val.intValue());
        menu.getChildren().add(slidMultiplier);

        // Graphs
        statisticsView = new StatisticsView();
        root.add(statisticsView, 1, 1);
        stage.setScene(new Scene(root, 1320, 900));
        stage.setTitle("SnakeGA");
        stage.show();
    }

    public void update(SnakeGA ga) {
        // Set options
        ga.setTimeMultiplier(timeMultiplier);
        // Update ui elements
        Game bestGame = ga.getBestGame();
        gameView.render(bestGame);
        statisticsView.update(ga.getHighScoreList(), ga.getAvgScoreList());
        networkView.render(bestGame);
        lblScore.setText(Integer.toString(ga.getHighScore()));
        lblGen.setText(Integer.toString(ga.getGeneration()));
        lblGenotype.setText(Integer.toString(bestGame.getId()));
    }
}
