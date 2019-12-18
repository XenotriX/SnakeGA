package org.xenotrix.snakega.ui.statistics;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.ArrayList;

public class StatisticsView extends TabPane {
    private final Graph graphBest;
    private final Graph graphAvg;

    public StatisticsView() {
        setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        Tab bestPerGen = new Tab("Best per generation");
        Tab avgPerGen = new Tab("Average per generation");
        getTabs().add(bestPerGen);
        getTabs().add(avgPerGen);
        graphBest = new Graph(700, 300);
        graphAvg = new Graph(700, 300);
        bestPerGen.setContent(graphBest);
        avgPerGen.setContent(graphAvg);
    }

    public void update(ArrayList<Integer> highScoreList, ArrayList<Float> avgScoreList) {
        graphBest.renderInts(highScoreList);
        graphAvg.renderFloats(avgScoreList);
    }
}
