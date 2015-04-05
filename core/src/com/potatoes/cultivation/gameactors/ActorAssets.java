package com.potatoes.cultivation.gameactors;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ActorAssets {
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
	
	public AtlasRegion hex_grass;
	public AtlasRegion hex_sea;
	public AtlasRegion hex_meadow;
	public AtlasRegion hex_tree;
	
	public AtlasRegion village_hovel;
	public AtlasRegion village_town;
	public AtlasRegion village_fort;
	public AtlasRegion village_castle;
	
	public HashMap<String, Animation> stringToAnimation;
	public HashMap<String, AtlasRegion> stringToAtlasRegion;
	
	public ActorAssets(TextureAtlas gameSprites) {
		mySprites = gameSprites;
		stringToAnimation = new HashMap<String, Animation>();
		stringToAtlasRegion = new HashMap<String, AtlasRegion>();
		
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
		
		stringToAnimation.put("potato_yellow_peasant", potato_yellow);
		stringToAnimation.put("potato_yellow_infantry", potato_yellow_infantry);
		stringToAnimation.put("potato_yellow_solider", potato_yellow_soldier);
		stringToAnimation.put("potato_yellow_knight", potato_yellow_knight);
		
		stringToAnimation.put("potato_red_peasant", potato_red);
		stringToAnimation.put("potato_red_infantry", potato_red_infantry);
		stringToAnimation.put("potato_red_solider", potato_red_soldier);
		stringToAnimation.put("potato_red_knight", potato_red_knight);
		
		stringToAnimation.put("potato_purple_peasant", potato_purple);
		stringToAnimation.put("potato_purple_infantry", potato_purple_infantry);
		stringToAnimation.put("potato_purple_solider", potato_purple_soldier);
		stringToAnimation.put("potato_purple_knight", potato_purple_knight);
		
		hex_grass = mySprites.findRegion("grass");
		hex_sea = mySprites.findRegion("tile_sea");
		hex_meadow = mySprites.findRegion("tile_meadow");
		hex_tree = mySprites.findRegion("tile_tree");
		
		village_hovel = mySprites.findRegion("village-hovel");
		village_town = mySprites.findRegion("village-town");
		village_fort = mySprites.findRegion("village-fort");
		village_castle = mySprites.findRegion("village-castle");
		
		stringToAtlasRegion.put("hex_grass", hex_grass);
		stringToAtlasRegion.put("hex_sea", hex_sea);
		stringToAtlasRegion.put("hex_meadow", hex_meadow);
		stringToAtlasRegion.put("hex_tree", hex_tree);
		
		stringToAtlasRegion.put("village_hovel", village_hovel);
		stringToAtlasRegion.put("village_town", village_town);
		stringToAtlasRegion.put("village_fort", village_fort);
		stringToAtlasRegion.put("village_castle", village_castle);
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