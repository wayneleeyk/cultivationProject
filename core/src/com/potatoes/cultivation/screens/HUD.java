package com.potatoes.cultivation.screens;


import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.logic.GameAction;
import com.potatoes.cultivation.logic.GameMap;
import com.potatoes.cultivation.logic.Player;
import com.potatoes.cultivation.logic.Tile;
import com.potatoes.cultivation.logic.Village;
import com.potatoes.cultivation.screens.InGame.VillageImage;

public class HUD extends Stage implements Serializable{
	private static final long serialVersionUID = 6359788424056569636L;
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
	public void update(Tile t) {
		System.out.println("Owner is "+t.owner + " current player is " + currentPlayer.getUsername() + " tile's village is "+t.getVillage());
		if (t.getPlayer()!=null && t.getPlayer().getUsername().equals(currentPlayer.getUsername()) && t.getVillage() != null) {
			Village v = t.getVillage();
			goldCount = v.getGold();
			logCount=v.getWood();
			villageType = v.getType().toString();
		} else {
			villageType = null;
		}
	}
	
	public void update(Village v){
		goldCount = v.getGold();
		logCount = v.getWood();
		villageType = v.getType().toString();
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
		System.out.println("clicked (" + x + ", " + y + ")");		
		final TextButton upgradeVillage = new TextButton("Upgrade Village", skin, "default");
		upgradeVillage.setPosition(x, Gdx.graphics.getHeight() -y);
		upgradeVillage.setVisible(true);
		upgradeVillage.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("Button pressed");
				GameAction.UpgradeVillageAction gameAction = new GameAction.UpgradeVillageAction(villageImage.getVillage());
				gameAction.execute(game.GAMEMANAGER.getGame());
				villageImage.updateVillageSprite();
				
				return true;
			}
		});
		
		
		villageMenuGroup.addActor(upgradeVillage);
	
		villageMenuGroup.getStage().addListener(new ClickListener() {
			//Listener that destroys village menu buttons when clicked anywhere except buttons
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("HUD clicked at " + x + "," + y);
//				Actor hit = HUD.this.hit(x, y, true);
//				System.out.println("Actor hit:" + hit);
				villageMenuGroup.removeActor(upgradeVillage);
				villageMenuGroup.getStage().removeListener(this);
				return false;
			}
		});
	}

	public int getLogCount() {
		return logCount;
	}

	public void setLogCount(int logCount) {
		this.logCount = logCount;
	}

	public int getGoldCount() {
		return goldCount;
	}

	public void setGoldCount(int goldCount) {
		this.goldCount = goldCount;
	}

	public String getVillageType() {
		return villageType;
	}

	public void setVillageType(String villageType) {
		this.villageType = villageType;
	}
	
}
