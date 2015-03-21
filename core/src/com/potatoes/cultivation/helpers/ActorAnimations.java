package com.potatoes.cultivation.helpers;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ActorAnimations {
	TextureAtlas mySprites;
	
	public Animation potato_yellow;
	public Animation potato_yellow_infantry; 
	public Animation potato_yellow_soldier;
	public Animation potato_yellow_knight;
	
	public Animation potato_red;
	public Animation potato_red_infantry; 
	public Animation potato_red_soldier;
	public Animation potato_red_knight;
	
	public Animation potato_purple;
	public Animation potato_purple_infantry; 
	public Animation potato_purple_soldier;
	public Animation potato_purple_knight;
	
	public ActorAnimations(TextureAtlas gameSprites) {
		mySprites = gameSprites;
		
		potato_yellow = newAnimation(mySprites.createSprite("potato_yellow"), 1, 2, 0.5f);
		potato_yellow_infantry = newAnimation(mySprites.createSprite("potato_yellow_infantry"), 1, 2, 0.5f);
		potato_yellow_soldier = newAnimation(mySprites.createSprite("potato_yellow_soldier"), 1, 2, 0.5f);
		potato_yellow_knight = newAnimation(mySprites.createSprite("potato_yellow_knight"), 1, 2, 0.5f);
		
		potato_red = newAnimation(mySprites.createSprite("potato_red"), 1, 2, 0.5f);
		potato_red_infantry = newAnimation(mySprites.createSprite("potato_red_infantry"), 1, 2, 0.5f);
		potato_red_soldier = newAnimation(mySprites.createSprite("potato_red_soldier"), 1, 2, 0.5f);
		potato_red_knight = newAnimation(mySprites.createSprite("potato_red_knight"), 1, 2, 0.5f);
		
		potato_purple = newAnimation(mySprites.createSprite("potato_purple"), 1, 2, 0.5f);
		potato_purple_infantry = newAnimation(mySprites.createSprite("potato_purple_infantry"), 1, 2, 0.5f);
		potato_purple_soldier = newAnimation(mySprites.createSprite("potato_purple_soldier"), 1, 2, 0.5f);
		potato_purple_knight = newAnimation(mySprites.createSprite("potato_purple_knight"), 1, 2, 0.5f);
	}
	
	public static Animation newAnimation(TextureRegion sprite, int row, int col, float frameDuration) {
		TextureRegion[][] tmp = sprite.split(sprite.getRegionWidth()/col, sprite.getRegionHeight()/row);
		TextureRegion[] frames = new TextureRegion[col*row];
		int index = 0;
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				frames[index++] = tmp[i][j];
			}
		}
		return new Animation(frameDuration, frames);
	}
}