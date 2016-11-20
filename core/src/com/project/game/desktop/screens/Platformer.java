package com.project.game.desktop.screens;

import com.badlogic.gdx.Game;

public class Platformer extends Game{

	@Override
	public void create() {
		// TODO Auto-generated method stub
		setScreen(new MainMenu(this));
	}

}
