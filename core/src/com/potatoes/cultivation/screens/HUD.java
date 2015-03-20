package com.potatoes.cultivation.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.logic.GameAction;
import com.potatoes.cultivation.logic.GameMap;
import com.potatoes.cultivation.logic.Player;
import com.potatoes.cultivation.logic.Tile;
import com.potatoes.cultivation.logic.UnitType;
import com.potatoes.cultivation.logic.Village;
import com.potatoes.cultivation.logic.VillageType;
import com.potatoes.cultivation.screens.InGame.VillageImage;

public class HUD extends Stage{
	
	SpriteBatch batch;
	BitmapFont font;
	int width, height;
	GameMap map;
	Player currentPlayer;
	int logCount;
	int goldCount;
	String villageType;
	Cultivation game;
	Skin skin;
	ArrayList<Actor> destroyableMenus = new ArrayList<Actor>();
	
	public HUD(Cultivation game, SpriteBatch batch, GameMap map, Player currentPlayer) {
		
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		this.game = game;
		this.batch = batch;
		this.width = Gdx.graphics.getWidth();
		this.height = Gdx.graphics.getHeight();
		this.currentPlayer = currentPlayer;
		this.logCount = 0;
		this.goldCount = 0;
		this.skin = game.skin;
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
		
	}

	@Override
	public void draw() {
		super.draw();
		
		batch.begin();
		if (villageType!=null) {
			font.draw(batch, "Village: " + villageType + ", Gold: " + goldCount + ", Logs " + logCount, - this.width/2, this.height/2);
		} else {
			font.draw(batch, "Click on a region to do an action", - this.width/2, this.height/2);
		}
		batch.end();
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
				game.client.sendActions(gameAction);
			}
		});
		
		hireVillager.setPosition(x + 48, Gdx.graphics.getHeight() - y - yOffset);
		hireVillager.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				System.out.println("hireVillager clicked");
				Tile t = game.GAMEMANAGER.getGame().getVillageSpawnPoint(villageImage.getVillage());
				if(t!=null){
					GameAction.HireVillagerAction hireAction = new GameAction.HireVillagerAction(t);
//					hireAction.execute(game.GAMEMANAGER.getGame());
					game.client.sendActions(hireAction);	
				}
			}
		});
		
		hireCannon.setPosition(x + 52, Gdx.graphics.getHeight() - y - 2 * yOffset);
		if(villageImage.getVillage().getGold() >= UnitType.Cannon.getCost() && villageImage.getVillage().higherThan(VillageType.Town)){
			hireCannon.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					System.out.println("Who let the powpow out");
					Tile t =  game.GAMEMANAGER.getGame().getCannonSpawnPoint(villageImage.getVillage());
					if(t!=null){
						GameAction.HireCannonAction hireAction = new GameAction.HireCannonAction(t);
						game.client.sendActions(hireAction);
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
