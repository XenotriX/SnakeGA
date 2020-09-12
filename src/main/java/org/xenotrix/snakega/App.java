package org.xenotrix.snakega;

import javafx.application.Application;
import javafx.stage.Stage;
import org.xenotrix.snakega.ui.Gui;

public class App extends Application {
    Gui gui;
    SnakeGA algorithm;
    private GameLoop loop;

    public App() {
        gui = new Gui();
        algorithm = new SnakeGA();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        algorithm = new SnakeGA();
        loop = new GameLoop(algorithm, gui);
        loop.start();
        gui.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
