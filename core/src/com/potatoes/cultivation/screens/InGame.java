package com.potatoes.cultivation.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.potatoes.cultivation.Cultivation;

public class InGame extends ScreenAdapter{

	Cultivation game ;
	AssetManager assets = new AssetManager();
	Camera camera = new OrthographicCamera();
	Viewport viewport = new StretchViewport(100, 100, camera);
	
	
	public InGame(final Cultivation pGame ) {
		this.game = pGame;
		// load map
		
		// for each tile create an Image actor  
	}
	
	
	@Override
	public void dispose() {
		super.dispose();
	}


	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
}
