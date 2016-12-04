package com.project.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;
		config.vSyncEnabled = true;
		config.foregroundFPS = 60;
		config.resizable = false;
		config.title = "Java Game Platformer Demo";
		new LwjglApplication(new Platformer(), config);
	}
}
