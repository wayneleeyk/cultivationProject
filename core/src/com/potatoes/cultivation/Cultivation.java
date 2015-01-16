package com.potatoes.cultivation;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
