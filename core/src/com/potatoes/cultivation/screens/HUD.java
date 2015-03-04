package com.potatoes.cultivation.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.potatoes.cultivation.logic.GameMap;
import com.potatoes.cultivation.logic.Player;
import com.potatoes.cultivation.logic.Village;

public class HUD extends Stage{
	
	SpriteBatch batch;
	BitmapFont font;
	int width, height;
	GameMap map;
	Player currentPlayer;
	
	public HUD(SpriteBatch batch, GameMap map, Player currentPlayer) {
		
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		this.batch = batch;
		this.width = Gdx.graphics.getWidth();
		this.height = Gdx.graphics.getHeight();
		this.currentPlayer = currentPlayer;
		System.out.println(this.width/2 + " " + this.height/2);
	}

	@Override
	public void draw() {
		super.draw();
		
		batch.begin();
		font.draw(batch, "Logs "+this.getNumberOfLogs(), - this.width/2, this.height/2);
		batch.end();
	}
	
	private int getNumberOfLogs(){
		int sum = 0;
		for (Village v : this.map.getVillages(currentPlayer)) {
			sum += v.getWood();
		}
		return sum;
	}
	
}
