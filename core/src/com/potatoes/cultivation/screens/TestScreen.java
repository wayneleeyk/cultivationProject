package com.potatoes.cultivation.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
	
	public TestScreen(Cultivation pGame) {
		game = pGame;
		batch = pGame.batch;
		a = game.manager.get("ingame.atlas", TextureAtlas.class);
		potato = a.findRegion("potato_red_knight");
		
		actorAssets = new ActorAssets(a);
		gameStage = new Stage();
		
		Unit one = new Unit();
		one.myType = UnitType.Peasant;
		Unit two = new Unit();
		two.myType = UnitType.Knight;
		
		final PotatoActor p = new PotatoActor(one, actorAssets, "yellow");
		
		final TileActor start = new TileActor(null, actorAssets, null);
		final TileActor destination = new TileActor(null, actorAssets, null);
		final TileActor dest2 = new TileActor(null, actorAssets, null);
		
		start.updateTile(LandType.Meadow, null, Color.RED);
		destination.updateTile(LandType.Tree, null, null);
		dest2.updateTile(LandType.Meadow, null, Color.TEAL);
		
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
					p.testMove2(destination);
					return true;
				}
				if(keycode == Input.Keys.SPACE && p.getActions().size == 0) {
					p.testMove2(dest2);
					return true;
				}
				if(keycode == Input.Keys.CONTROL_RIGHT && p.getActions().size == 0) {
					p.testMove2(start);
					return true;
				}
				return false;
			}
		});
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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