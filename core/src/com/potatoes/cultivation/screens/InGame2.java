package com.potatoes.cultivation.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.gameactors.ActorAssets;
import com.potatoes.cultivation.helpers.ClickManager;
import com.potatoes.cultivation.logic.CultivationGame;
import com.potatoes.cultivation.logic.GameAction;
import com.potatoes.cultivation.networking.ActionBlockProtocol;
import com.potatoes.cultivation.networking.Protocol;
import com.potatoes.cultivation.networking.ProtocolHandler;
import com.potatoes.cultivation.stages.GameWorld;
import com.potatoes.cultivation.stages.HUD;
import com.potatoes.cultivation.stages.HUD2;

public class InGame2 extends ScreenAdapter {
	Cultivation aGame;
	CultivationGame aRound;
	GameWorld world;
	HUD2 hud;
	ProtocolHandler<GameAction[]> updates;
	
	public InGame2(final Cultivation pGame, CultivationGame pGameRound) {
		aGame = pGame;
		aRound = pGameRound;
		ActorAssets assets = new ActorAssets(pGame.manager.get("ingame.atlas", TextureAtlas.class));
		
		ClickManager cm = new ClickManager();		
//		hud = new HUD(pGame, null, pGameRound.getGameMap(), pGame.player, cm);\
		hud = new HUD2(pGame, cm);
		world = new GameWorld(aRound, assets, cm);
		cm.setWorld(world);
		
		addHandlers();
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		gameUpdate();
		
		world.act(delta);
		world.draw();
		hud.act(delta);
		hud.draw();
	}
	
	@Override
	public void show() {
		InputMultiplexer inputs = new InputMultiplexer(hud, world);
		Gdx.input.setInputProcessor(inputs);
	}
	
	private void addHandlers() {
		updates = new ProtocolHandler<GameAction[]>(){

			@Override
			public void handle(Protocol p) {
				if(p instanceof ActionBlockProtocol) {
					result = ((ActionBlockProtocol) p).getActions();
				}
			}
			
		};
		aGame.client.insertHandler(updates);
	}
	
	private void gameUpdate() {
		if(updates.isAvailable()) {
			System.out.println("new updates!");
			for(GameAction gameAction : updates.getResult()) {
				gameAction.execute(aRound);
			}
			updates.reset();
		}
	}
}