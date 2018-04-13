package org.xenotrix.snakega;

import javafx.animation.AnimationTimer;

/**
 * Game loop. Not the most elegant but necessary for javafx.
 * @author XenotriX
 *
 */
public class GameLoop extends AnimationTimer{
	SnakeGA app;
	
	GameLoop(SnakeGA app){
		this.app = app;
	}

	long lastUpdate = System.currentTimeMillis();
		
	@Override
	public void handle(long current) {
		long currentTime = System.currentTimeMillis();
		long deltaTime = currentTime - lastUpdate;
		if (deltaTime >= 1000 / 60){
			lastUpdate = currentTime;
			app.update();
		}
	}
}
