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

public class InGame2 extends ScreenAdapter {
	Cultivation aGame;
	CultivationGame aRound;
	GameWorld world;
	HUD hud;
	ProtocolHandler<GameAction[]> updates;
	
	public InGame2(final Cultivation pGame, CultivationGame pGameRound) {
		aGame = pGame;
		aRound = pGameRound;
		ActorAssets assets = new ActorAssets(pGame.manager.get("ingame.atlas", TextureAtlas.class));
		
		ClickManager cm = new ClickManager(world);
		
		hud = new HUD(pGame, null, pGameRound.getGameMap(), pGame.player, cm);
		world = new GameWorld(aRound, assets, cm);
		addDragControls();
		addHandlers();
	}

	public GameWorld getGameWorld(){
		return this.world;
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

	private void addDragControls() {
		world.addListener(new DragListener() {
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
				world.getCamera().translate(prevX - x, prevY - y, 0);
				super.drag(event, x, y, pointer);
			}

			@Override
			public void dragStop(InputEvent event, final float x, final float y, int pointer) {
				world.addAction(new TemporalAction(1, Interpolation.pow2Out) {
					@Override
					protected void update(float percent) {
						float dirX = (prevX - x) * 0.5f;
						float dirY = (prevY - y) * 0.5f;
						float inversePercent = 1 - percent;
						world.getCamera().translate(inversePercent * dirX, inversePercent * dirY, 0);
					}
					
				});
				super.dragStop(event, x, y, pointer);
			}
		});
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