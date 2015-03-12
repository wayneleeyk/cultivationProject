package com.potatoes.cultivation.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.potatoes.cultivation.logic.GameMap;
import com.potatoes.cultivation.logic.Player;
import com.potatoes.cultivation.logic.Region;
import com.potatoes.cultivation.logic.Tile;
import com.potatoes.cultivation.logic.Village;

public class HUD extends Stage{
	
	SpriteBatch batch;
	BitmapFont font;
	int width, height;
	GameMap map;
	Player currentPlayer;
	int logCount;
	int goldCount;
	String villageType;
	
	public HUD(SpriteBatch batch, GameMap map, Player currentPlayer) {
		
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		this.batch = batch;
		this.width = Gdx.graphics.getWidth();
		this.height = Gdx.graphics.getHeight();
		this.currentPlayer = currentPlayer;
		this.logCount = 0;
		this.goldCount = 0;
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
		if (t.getPlayer()!=null && t.getPlayer().getUsername().equals(currentPlayer.getUsername()) && t.getVillage() != null) {
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
	
}
