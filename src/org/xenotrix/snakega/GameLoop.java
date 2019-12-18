package org.xenotrix.snakega;

import javafx.animation.AnimationTimer;
import org.xenotrix.snakega.ui.Gui;

/**
 * Game loop. Not the most elegant but necessary for javafx.
 * @author XenotriX
 *
 */
public class GameLoop extends AnimationTimer {
	SnakeGA app;
	Gui gui;
	
	GameLoop(SnakeGA app, Gui gui){
		this.app = app;
		this.gui = gui;
	}

	long lastUpdate = System.currentTimeMillis();
		
	@Override
	public void handle(long current) {
		long currentTime = System.currentTimeMillis();
		long deltaTime = currentTime - lastUpdate;
		if (deltaTime >= 1000 / 60){
			lastUpdate = currentTime;
			app.update();
			gui.update(app);
		}
	}
}
