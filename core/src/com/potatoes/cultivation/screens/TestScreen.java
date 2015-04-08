package com.potatoes.cultivation.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.gameactors.ActorAssets;
import com.potatoes.cultivation.gameactors.PotatoActor;
import com.potatoes.cultivation.gameactors.TileActor;
import com.potatoes.cultivation.logic.LandType;
import com.potatoes.cultivation.logic.Tile;
import com.potatoes.cultivation.logic.Unit;
import com.potatoes.cultivation.logic.UnitType;

public class TestScreen extends ScreenAdapter {
	Cultivation game;
	SpriteBatch batch;
	TextureAtlas a;
	Stage gameStage;
	TextureRegion potato;
	ActorAssets actorAssets;
	
	OrthographicCamera cam;
	Viewport viewport;
	
	public TestScreen(Cultivation pGame) {
		game = pGame;
		batch = pGame.batch;
		a = game.manager.get("ingame.atlas", TextureAtlas.class);
		actorAssets = new ActorAssets(a);
		gameStage = new Stage();
		
		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		viewport = new StretchViewport(1000, 1000, cam);
//		gameStage.setViewport(viewport);
		
		Unit one = new Unit();
		one.myType = UnitType.Peasant;
		Unit two = new Unit();
		two.myType = UnitType.Knight;
		
		final PotatoActor p = new PotatoActor(one, actorAssets, "yellow", null);
		
		final TileActor start = new TileActor(actorAssets);
		final TileActor destination = new TileActor(actorAssets);
		final TileActor dest2 = new TileActor(actorAssets);
		
		start.updateTile(LandType.Meadow, null, "red");
		destination.updateTile(LandType.Tree, null, null);
		dest2.updateTile(LandType.Meadow, null, "purple");
		
		start.setPosition(100, 100);
		start.addActor(p);
		p.setPosition(80, 30);

		destination.setPosition(333, 144);
		dest2.setPosition(100, 188);
		
		gameStage.addActor(dest2);
		gameStage.addActor(destination);
		gameStage.addActor(start);
		
		gameStage.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if(keycode == Input.Keys.ENTER && p.getActions().size == 0) {
//					p.testMove2(destination);
//					p.testUpgrade();
					return true;
				}
				if(keycode == Input.Keys.SPACE && p.getActions().size == 0) {
//					p.testMove2(dest2);
					return true;
				}
				if(keycode == Input.Keys.CONTROL_RIGHT && p.getActions().size == 0) {
//					p.testMove2(start);
					return true;
				}
				return false;
			}
		});
		
		gameStage.addListener(new DragListener() {
			float prevX, prevY;

			@Override
			public void dragStart(InputEvent event, float x, float y,
					int pointer) {
				prevX = x;
				prevY = y;
				super.dragStart(event, x, y, pointer);
			}

			@Override
			public void drag(InputEvent event, float x, float y, int pointer) {
				gameStage.getCamera().translate(prevX - x, prevY - y, 0);
				super.drag(event, x, y, pointer);
			}

			@Override
			public void dragStop(InputEvent event, final float x, final float y, int pointer) {
				gameStage.addAction(new TemporalAction(1, Interpolation.pow2Out) {
					@Override
					protected void update(float percent) {
						float dirX = (prevX - x)/2;
						float dirY = (prevY - y)/2;
						float inversePercent = 1 - percent;
						gameStage.getCamera().translate(inversePercent * dirX, inversePercent * dirY, 0);
					}
					
				});
				super.dragStop(event, x, y, pointer);
			}
		});
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		cam.update();
		gameStage.act();
		gameStage.draw();
	}
	
	@Override
	public void show() {
		Gdx.input.setInputProcessor(gameStage);
	}
	
	@Override
	public void dispose() {
		
	}
	
	@Override
	public void hide() {
		dispose();
	}
}