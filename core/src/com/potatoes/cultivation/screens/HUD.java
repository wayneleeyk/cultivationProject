package com.potatoes.cultivation.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.logic.CultivationGame;
import com.potatoes.cultivation.logic.GameAction;
import com.potatoes.cultivation.logic.GameAction.EndTurnAction;
import com.potatoes.cultivation.logic.GameMap;
import com.potatoes.cultivation.logic.Player;
import com.potatoes.cultivation.logic.Tile;
import com.potatoes.cultivation.logic.UnitType;
import com.potatoes.cultivation.logic.Village;
import com.potatoes.cultivation.logic.VillageType;
import com.potatoes.cultivation.screens.InGame.VillageImage;

public class HUD extends Stage{
	
	Batch batch;
	BitmapFont font;
	int width, height;
	GameMap map;
	Player currentPlayer;
	int logCount;
	int goldCount;
	String villageType;
	Cultivation gameApp;
	Skin skin;
	ArrayList<Actor> destroyableMenus = new ArrayList<Actor>();
	final TextButton endTurn;
	Label stats;
	CultivationGame game;
	
	public HUD(final Cultivation pGame, Batch batch, GameMap map, final Player currentPlayer) {
		
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		this.gameApp = pGame;
		this.game = pGame.GAMEMANAGER.getGame();
		this.batch = batch;
		this.width = Gdx.graphics.getWidth();
		this.height = Gdx.graphics.getHeight();
		this.currentPlayer = currentPlayer;
		this.logCount = 0;
		this.goldCount = 0;
		this.skin = pGame.skin;
		System.out.println(this.width/2 + " " + this.height/2);
		
		
		this.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("HUD clicked at " + x + "," + y);
				Actor hit = HUD.this.hit(x, y, true);
				
				if(hit != null) return true;
				for(Actor a : destroyableMenus) {
					a.remove();
				}
				return false;
			}
		});
		
		stats = new Label("HELLOW", skin, "white"){

			@Override
			public void act(float delta) {
				super.act(delta);
				if (villageType!=null) {
					this.setText("Turn :"+game.turnOf().getUsername() + "Village: " + villageType + ", Gold: " + goldCount + ", Logs " + logCount);
				} else {
					this.setText("Turn :"+game.turnOf().getUsername());
					
				}
			}
			
		};
		stats.setPosition(5, height-30); //Hardcode FTW
		this.addActor(stats);
		endTurn = new TextButton("End Turn", skin, "red");
		endTurn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				GameAction.EndTurnAction endTurnAction = new GameAction.EndTurnAction();
				gameApp.client.sendActions(endTurnAction);
				System.out.println("current player: " + currentPlayer.getUsername());
				System.out.println("turn of: " + game.turnOf());
//				endTurnAction.execute(game);
			}
		});
		this.addActor(endTurn);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		stats.act(delta);
		if (!game.isMyTurn(currentPlayer)) {
			endTurn.setVisible(false);
		} else {
			endTurn.setVisible(true);
		}
	}

	@Override
	public void draw() {
		super.draw();
//		this.getBatch().begin();
//		font.draw(this.getBatch(), "Turn :"+this.game.GAMEMANAGER.getGame().turnOf().getUsername() + "Village: " + villageType + ", Gold: " + goldCount + ", Logs " + logCount+ " \t Turn of : "+game.GAMEMANAGER.getGame().turnOf(), - this.width/2, this.height/2);
//		this.getBatch().end();
//		batch.begin();
//		if (villageType!=null) {
//			System.out.println("DRAWING when villageType != null");
//			font.draw(batch, "Turn :"+this.game.GAMEMANAGER.getGame().turnOf().getUsername() + "Village: " + villageType + ", Gold: " + goldCount + ", Logs " + logCount+ " \t Turn of : "+game.GAMEMANAGER.getGame().turnOf(), - this.width/2, this.height/2);
//		} else {
//			System.out.println("Drawing else");
//			font.draw(batch, "Click on a region to do an action", - this.width/2, this.height/2);
//		}
//		batch.end();
	}
	public void tileClicked(Tile t) {
		if (t.getPlayer()!=null && !t.getPlayer().equals(Player.nullPlayer) && t.getPlayer().getUsername().equals(currentPlayer.getUsername())) {
			Village v = t.getVillage();
			if(v == null) return;
			goldCount = v.getGold();
			logCount=v.getWood();
			villageType = v.getType().toString();
		} else {
			villageType = null;
		}
	}
	
	private int getNumberOfLogs(){
		int sum = 0;
		for (Village v : this.map.getVillages(currentPlayer)) {
			sum += v.getWood();
		}
		return sum;
	}

	public void villageClicked(final VillageImage villageImage, float x, float y) {
		System.out.println("CLICKED VILLAGE");
		final Group villageMenuGroup = new Group();
		this.addActor(villageMenuGroup);
		this.destroyableMenus.add(villageMenuGroup);
		
		System.out.println("clicked (" + x + ", " + y + ")");		
		final TextButton upgradeVillage = new TextButton("Upgrade Village", skin, "default");
		final TextButton hireVillager = new TextButton("Hire Villager", skin, "default");
		final TextButton hireCannon = new TextButton("Hire PowPow", skin, "default");
		
		final int yOffset = -50;
		
		upgradeVillage.setPosition(x, Gdx.graphics.getHeight() - y);
		upgradeVillage.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				System.out.println("upgradeVillage clicked");
				GameAction.UpgradeVillageAction gameAction = new GameAction.UpgradeVillageAction(villageImage.getVillage());
//				gameAction.execute(game.GAMEMANAGER.getGame());
				gameApp.client.sendActions(gameAction);
			}
		});
		
		hireVillager.setPosition(x + 48, Gdx.graphics.getHeight() - y - yOffset);
		hireVillager.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				System.out.println("hireVillager clicked");
				Tile t = gameApp.GAMEMANAGER.getGame().getVillageSpawnPoint(villageImage.getVillage());
				if(t!=null){
					GameAction.HireVillagerAction hireAction = new GameAction.HireVillagerAction(t);
//					hireAction.execute(game.GAMEMANAGER.getGame());
					gameApp.client.sendActions(hireAction);	
				}
			}
		});
		
		hireCannon.setPosition(x + 52, Gdx.graphics.getHeight() - y - 2 * yOffset);
		if(villageImage.getVillage().getGold() >= UnitType.Cannon.getCost() && villageImage.getVillage().higherThan(VillageType.Town)){
			hireCannon.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					System.out.println("Who let the powpow out");
					Tile t =  gameApp.GAMEMANAGER.getGame().getCannonSpawnPoint(villageImage.getVillage());
					if(t!=null){
						GameAction.HireCannonAction hireAction = new GameAction.HireCannonAction(t);
						gameApp.client.sendActions(hireAction);
					}
				}
			});
		}
		else{
			hireCannon.setColor(Color.GRAY);
		}
		
		villageMenuGroup.addActor(upgradeVillage);
		villageMenuGroup.addActor(hireVillager);
		villageMenuGroup.addActor(hireCannon);
		
		villageMenuGroup.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				System.out.println("menu group clicked");
				for(Actor a : destroyableMenus) {
					if(a.equals(villageMenuGroup)) continue;
					a.remove();
				}
			}
		});
	}	
}
