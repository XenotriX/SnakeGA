package org.xenotrix.snakega.ui.statistics;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

public class Graph extends Canvas {

    public Graph(double width, double height) {
        super(width, height);
    }

    /**
     * Draws a graph of the provided data on the canvas
     * @param list List of values
     */
    public void render(ArrayList<Float> list) {
        GraphicsContext ctx = getGraphicsContext2D();
        int length = list.size();
        if (length == 0) return;

        ctx.clearRect(0, 0, getWidth(), getHeight());

        double[] x = new double[length + 2];
        double[] y = new double[length + 2];


        // Find highest
        float highest = 0;
        for (float val: list){
            if (val > highest) highest = val;
        }

        // set points
        float ratio = (float)getWidth() / (length - 1);
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
            y[i] = getHeight() - (sum / count) * (getHeight() / highest);
        }

        // Set bottom points
        x[x.length - 2] = getWidth();
        y[y.length - 2] = getHeight();
        x[x.length - 1] = 0;
        y[y.length - 1] = getHeight();

        ctx.fillPolygon(x, y, length + 2);
    }

}
