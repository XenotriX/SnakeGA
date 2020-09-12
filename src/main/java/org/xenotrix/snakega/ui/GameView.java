package org.xenotrix.snakega.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.xenotrix.snakega.Game;

public class GameView extends Canvas {
    public GameView(double width, double height) {
        super(width, height);
    }
    /**
     * TODO: This MUST be set in the main class
     */
    private static final int GRID_SIZE = 20;
    public void render(Game game) {
        double size = this.getWidth();
        float ratio = (float)size / GRID_SIZE;
        GraphicsContext ctx = this.getGraphicsContext2D();
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
        ctx.fillRect(game.getApplePosition()[0] * ratio, game.getApplePosition()[1] * ratio, ratio, ratio);
        // Draw player
        ctx.setFill(Color.WHITE);
        for (int[] pos: game.getPlayer().getBody()){
            ctx.fillRect(pos[0] * ratio, pos[1] * ratio, ratio, ratio);
        }
    }
}
