package org.xenotrix.snakega.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import org.xenotrix.snakega.Game;
import org.xenotrix.snakega.Player;

public class NetworkView extends Canvas {
    public NetworkView(double width, double height) {
        super(width, height);
    }

    /**
     * Renders the network of the given game
     */
    public void render(Game game) {
        Player player = game.getPlayer();
        float[] inputs = player.inputs;
        float[] outputs = player.outputs;
        float[][] weights = player.getGenotype().getChromosome();

        float circleSize = ((float)getHeight() / inputs.length) * 0.75f;
        int gap = 10;

        GraphicsContext ctx = getGraphicsContext2D();

        //// Clear Background
        ctx.clearRect(0, 0, getWidth(), getHeight());

        // Measurements
        float height = (float)getHeight();
        int numberOfInputs = inputs.length;
        float offsetTopInputs = (height - (numberOfInputs * circleSize + (numberOfInputs - 1) * gap)) / 2;
        int numberOfOuputs = outputs.length;
        float offsetTopOutputs = (height - (numberOfOuputs * circleSize + (numberOfOuputs - 1) * gap)) / 2;

        //// Draw lines
        for(int o = 0; o < weights.length; o++){
            for(int i = 0; i < weights[0].length; i++){
                double greyScale = 1 / (1 + Math.exp(-weights[o][i]));
                ctx.setStroke(new Color(greyScale, greyScale, greyScale, 1));
                ctx.beginPath();
                ctx.moveTo(20 + circleSize / 2, i * (circleSize + gap) + offsetTopInputs + circleSize / 2);
                ctx.lineTo(getWidth() - 70 + circleSize / 2, o * (circleSize + gap) + offsetTopOutputs + circleSize / 2);
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
            ctx.fillArc(getWidth() - 70, i * (circleSize + gap) + offsetTopOutputs, circleSize, circleSize, 0, 360, ArcType.OPEN);
            ctx.strokeArc(getWidth() - 70, i * (circleSize + gap) + offsetTopOutputs, circleSize, circleSize, 0, 360, ArcType.OPEN);
        }
    }

}
