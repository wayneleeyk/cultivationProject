package com.potatoes.cultivation;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.potatoes.cultivation.networking.Client;
import com.potatoes.cultivation.screens.Splash;

public class Cultivation extends Game {
	public SpriteBatch batch;
	public AssetManager manager;
	
	// Connection related 
	public Client client = new Client("localhost", 7470);
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		manager = new AssetManager();
		loadFiles();
		manager.finishLoading();
		this.setScreen(new Splash(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	private void loadFiles() {
		manager.load("companyLogo.png", Texture.class);
		manager.load("pixFont.fnt", BitmapFont.class);
		manager.load("gameTitle.png", Texture.class);
		manager.load("landscape.png", Texture.class);
	}
}
