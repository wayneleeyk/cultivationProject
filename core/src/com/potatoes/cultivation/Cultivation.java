package com.potatoes.cultivation;

import com.badlogic.gdx.Game;
import com.potatoes.cultivation.screens.Login;

public class Cultivation extends Game {
	
	
	@Override
	public void create () {
		this.setScreen(new Login(this));
	}

	@Override
	public void render () {
		super.render();
	}
}
