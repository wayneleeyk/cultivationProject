package com.potatoes.cultivation;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.potatoes.cultivation.networking.Client;
import com.potatoes.cultivation.screens.InGame;
import com.potatoes.cultivation.screens.Splash;

public class Cultivation extends Game {
	public SpriteBatch batch;
	public AssetManager manager;
	public Skin skin;
	
	// Connection related 
	public Client client = new Client("localhost", 7470);
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		manager = new AssetManager();
		loadFiles();
		manager.finishLoading();
		skin = manager.get("gui.json", Skin.class);
		this.setScreen(new InGame(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	private void loadFiles() {
		// Load the skin (and related resources)
		SkinParameter param = new SkinParameter("gui.atlas");
		manager.load("gui.json", Skin.class, param);
		
		// Load in-game needed graphics
		manager.load("ingame.atlas", TextureAtlas.class);
	}
}
