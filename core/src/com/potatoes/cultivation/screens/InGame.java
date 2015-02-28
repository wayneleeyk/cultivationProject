package com.potatoes.cultivation.screens;

import java.util.HashSet;
import java.util.Set;

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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.logic.GameMap;
import com.potatoes.cultivation.logic.LandType;
import com.potatoes.cultivation.logic.Tile;

public class InGame extends ScreenAdapter{

	Cultivation game ;
	SpriteBatch batch;
	TextureAtlas atlas;
	AtlasRegion hex_grass;
	AtlasRegion hex_sea;
	float cameraWidth=800, cameraHeight=600;
	Camera camera = new OrthographicCamera(cameraWidth,cameraHeight);
	Stage gameStage = new Stage();
	
	int width = 5;
	int height = 5;
	GameMap gameMap = new GameMap(width,height);
	
	Set<Tile> tiles = new HashSet<>();
	
	public InGame(final Cultivation pGame ) {
		this.game = pGame;
		this.batch = game.batch;
		this.atlas = game.manager.get("ingame.atlas", TextureAtlas.class);
		hex_grass = atlas.findRegion("grass");
		hex_sea = atlas.findRegion("sea");
		camera.lookAt(0, 0, 0);
		// load map

		int originX = hex_grass.getRegionWidth()/2, originY = hex_grass.getRegionHeight()/2;
			
		// for each tile create an Image actor 
		Image[][] tiles = new Image[width][height];
		for (int x=0; x<width; x++) {
			for (int y=0;y<height;y++) {
//				AtlasRegion hexToDraw = null;
//				if (gameMap.getTile(x, y).getLandType() == LandType.Grass) {
//					hexToDraw = hex_grass;
//				} else if (gameMap.getTile(x, y).getLandType() == LandType.Sea) {
//					hexToDraw = hex_sea;
//				}
				final Image tile = new Image(hex_grass);
				tile.setPosition(x*tile.getWidth()*0.75f, x*tile.getHeight()/2.0f + (y*tile.getHeight()));
				tile.setOrigin(originX, originY);
				tile.addListener(new EventListener() {
					@Override
					public boolean handle(Event event) {
//						event.getListenerActor().rotateBy(10);
						return false;
					}
				});
				gameStage.addActor(tile);
			}
		}
		
		
		// Stage
		Gdx.input.setInputProcessor(gameStage);
		gameStage.addListener(new DragListener(){
			float prevX , prevY;
			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				super.touchDragged(event, x, y, pointer);
				if(this.isDragging()){
					gameStage.getCamera().position.add(prevX-x, prevY-y,0);
				}
			}

			@Override
			public void dragStart(InputEvent event, float x, float y,
					int pointer) {
				super.dragStart(event, x, y, pointer);
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
		gameStage.draw();
		batch.end();
	}
}
