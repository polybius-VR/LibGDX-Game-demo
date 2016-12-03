package com.project.game.desktop;

import com.badlogic.gdx.Game;
import com.project.game.desktop.screens.SplashScreen;

public class Platformer extends Game{

	@Override
	public void create() {
		// TODO Auto-generated method stub
		setScreen(new SplashScreen(this));
	}

}
