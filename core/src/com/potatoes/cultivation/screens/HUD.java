package com.potatoes.cultivation.screens;


import sun.awt.X11.Screen;

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
import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.logic.GameMap;
import com.potatoes.cultivation.logic.Player;
import com.potatoes.cultivation.logic.Region;
import com.potatoes.cultivation.logic.Tile;
import com.potatoes.cultivation.logic.Village;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

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
	public void tileClicked(Tile t) {
		if (t.getPlayer()!=null && t.getPlayer().getUsername().equals(currentPlayer.getUsername())) {
			Village v = t.getVillage();
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

	public void villageClicked(float x, float y) {
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
				return true;
				
			}
		});
		
		villageMenuGroup.addActor(upgradeVillage);
	
		villageMenuGroup.getStage().addListener(new ClickListener() {
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
	
}
