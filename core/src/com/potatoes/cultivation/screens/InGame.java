package com.potatoes.cultivation.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.logic.GameMap;

public class InGame extends ScreenAdapter{

	Cultivation game ;
	SpriteBatch batch;
	TextureAtlas atlas;
	AtlasRegion hex;
	float cameraWidth=100, cameraHeight=100;
	Camera camera = new OrthographicCamera(cameraWidth,cameraHeight);
	Stage gameStage = new Stage();
	
	GameMap gameMap = new GameMap();
	
	
	public InGame(final Cultivation pGame ) {
		this.game = pGame;
		this.batch = game.batch;
		this.atlas = game.manager.get("ingame.atlas", TextureAtlas.class);
		hex = atlas.findRegion("grass");
		camera.lookAt(0, 0, 0);
		// load map

		// for each tile create an Image actor 
		Gdx.input.setInputProcessor(gameStage);
		gameStage.addListener(new DragListener(){
			float prevX , prevY;
			float ratioX = cameraWidth/Gdx.graphics.getWidth(), ratioY=cameraHeight/Gdx.graphics.getHeight();
			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				super.touchDragged(event, x, y, pointer);
				if(this.isDragging()){
					camera.position.add((prevX-x)*ratioX, (prevY-y)*ratioY,0);
					prevX = x; prevY = y;
				}
			}

			@Override
			public void dragStart(InputEvent event, float x, float y,
					int pointer) {
				super.dragStart(event, x, y, pointer);
				System.out.println("drag has started");
				prevX = x;
				prevY = y;
			}
			
		});
	}
	
	
	
	@Override
	public void dispose() {
		super.dispose();
	}
	@Override
	public void show() {
	}


	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// update camera
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		batch.begin();
		
		batch.draw(hex, 0, 0);
		batch.draw(hex, 700, 0);
		
		batch.end();
	}
}
