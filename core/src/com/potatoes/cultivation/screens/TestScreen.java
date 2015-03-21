package com.potatoes.cultivation.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.gui.PotatoActor;
import com.potatoes.cultivation.helpers.ActorAnimations;

public class TestScreen extends ScreenAdapter {
	Cultivation game;
	SpriteBatch batch;
	TextureAtlas a;
	Stage gameStage;
	TextureRegion potato;
	ActorAnimations animations;
	
	public TestScreen(Cultivation pGame) {
		game = pGame;
		batch = pGame.batch;
		a = game.manager.get("ingame.atlas", TextureAtlas.class);
		potato = a.findRegion("potato_red_knight");
		
		animations = new ActorAnimations(a);
		
		gameStage = new Stage();
		PotatoActor p = new PotatoActor(null, animations.potato_yellow);
		PotatoActor po = new PotatoActor(null, animations.potato_yellow_infantry);
		PotatoActor pot = new PotatoActor(null, animations.potato_purple_knight);
		po.setPosition(p.getWidth(), 0);
		pot.setPosition(p.getWidth() * 2, 0);
		gameStage.addActor(p);
		gameStage.addActor(po);
		gameStage.addActor(pot);
		
		pot.testMove();
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

	}
	
	@Override
	public void dispose() {
		
	}
	
	@Override
	public void hide() {
		dispose();
	}
}