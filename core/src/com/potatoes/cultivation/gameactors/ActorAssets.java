package com.potatoes.cultivation.gameactors;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ActorAssets  {
	TextureAtlas mySprites;
	
	public HashMap<String, Animation> stringToAnimation;
	public HashMap<String, AtlasRegion> stringToAtlasRegion;
	
	public ActorAssets(TextureAtlas gameSprites) {
		mySprites = gameSprites;
		stringToAnimation = new HashMap<String, Animation>();
		stringToAtlasRegion = new HashMap<String, AtlasRegion>();
		
		Animation potato_yellow = newAnimation(mySprites.createSprite("potato_yellow"), 1, 2, 0.5f);
		Animation potato_yellow_infantry = newAnimation(mySprites.createSprite("potato_yellow_infantry"), 1, 2, 0.5f);
		Animation potato_yellow_soldier = newAnimation(mySprites.createSprite("potato_yellow_soldier"), 1, 2, 0.5f);
		Animation potato_yellow_knight = newAnimation(mySprites.createSprite("potato_yellow_knight"), 1, 2, 0.5f);
		Animation potato_yellow_cannon = newAnimation(mySprites.createSprite("potato_yellow_cannon"), 1, 2, 0.2f);
		
		Animation potato_red = newAnimation(mySprites.createSprite("potato_red"), 1, 2, 0.5f);
		Animation potato_red_infantry = newAnimation(mySprites.createSprite("potato_red_infantry"), 1, 2, 0.5f);
		Animation potato_red_soldier = newAnimation(mySprites.createSprite("potato_red_soldier"), 1, 2, 0.5f);
		Animation potato_red_knight = newAnimation(mySprites.createSprite("potato_red_knight"), 1, 2, 0.5f);
		Animation potato_red_cannon = newAnimation(mySprites.createSprite("potato_red_cannon"), 1, 2, 0.2f);
		
		Animation potato_purple = newAnimation(mySprites.createSprite("potato_purple"), 1, 2, 0.5f);
		Animation potato_purple_infantry = newAnimation(mySprites.createSprite("potato_purple_infantry"), 1, 2, 0.5f);
		Animation potato_purple_soldier = newAnimation(mySprites.createSprite("potato_purple_soldier"), 1, 2, 0.5f);
		Animation potato_purple_knight = newAnimation(mySprites.createSprite("potato_purple_knight"), 1, 2, 0.5f);
		Animation potato_purple_cannon = newAnimation(mySprites.createSprite("potato_purple_cannon"), 1, 2, 0.2f);
		
		stringToAnimation.put("potato_yellow_peasant", potato_yellow);
		stringToAnimation.put("potato_yellow_infantry", potato_yellow_infantry);
		stringToAnimation.put("potato_yellow_soldier", potato_yellow_soldier);
		stringToAnimation.put("potato_yellow_knight", potato_yellow_knight);
		stringToAnimation.put("potato_yellow_cannon", potato_yellow_cannon);
		
		stringToAnimation.put("potato_red_peasant", potato_red);
		stringToAnimation.put("potato_red_infantry", potato_red_infantry);
		stringToAnimation.put("potato_red_soldier", potato_red_soldier);
		stringToAnimation.put("potato_red_knight", potato_red_knight);
		stringToAnimation.put("potato_red_cannon", potato_red_cannon);
		
		stringToAnimation.put("potato_purple_peasant", potato_purple);
		stringToAnimation.put("potato_purple_infantry", potato_purple_infantry);
		stringToAnimation.put("potato_purple_soldier", potato_purple_soldier);
		stringToAnimation.put("potato_purple_knight", potato_purple_knight);
		stringToAnimation.put("potato_purple_cannon", potato_purple_cannon);
		
		AtlasRegion hex_grass = mySprites.findRegion("grass");
		AtlasRegion hex_sea = mySprites.findRegion("tile_sea");
		AtlasRegion hex_meadow = mySprites.findRegion("tile_meadow");
		AtlasRegion hex_tree = mySprites.findRegion("tile_tree");
		
		stringToAtlasRegion.put("hex_grass", hex_grass);
		stringToAtlasRegion.put("hex_sea", hex_sea);
		stringToAtlasRegion.put("hex_meadow", hex_meadow);
		stringToAtlasRegion.put("hex_tree", hex_tree);
		
		AtlasRegion hex_grass_yellow = mySprites.findRegion("grass_yellow");
		AtlasRegion hex_meadow_yellow = mySprites.findRegion("tile_meadow_yellow");
		AtlasRegion hex_tree_yellow = mySprites.findRegion("tile_tree_yellow");
		
		stringToAtlasRegion.put("hex_grass_yellow", hex_grass_yellow);
		stringToAtlasRegion.put("hex_meadow_yellow", hex_meadow_yellow);
		stringToAtlasRegion.put("hex_tree_yellow", hex_tree_yellow);
		
		AtlasRegion hex_grass_red = mySprites.findRegion("grass_red");
		AtlasRegion hex_meadow_red = mySprites.findRegion("tile_meadow_red");
		AtlasRegion hex_tree_red = mySprites.findRegion("tile_tree_red");
		
		stringToAtlasRegion.put("hex_grass_red", hex_grass_red);
		stringToAtlasRegion.put("hex_meadow_red", hex_meadow_red);
		stringToAtlasRegion.put("hex_tree_red", hex_tree_red);
		
		AtlasRegion hex_grass_purple = mySprites.findRegion("grass_purple");
		AtlasRegion hex_meadow_purple = mySprites.findRegion("tile_meadow_purple");
		AtlasRegion hex_tree_purple = mySprites.findRegion("tile_tree_purple");
		
		stringToAtlasRegion.put("hex_grass_purple", hex_grass_purple);
		stringToAtlasRegion.put("hex_meadow_purple", hex_meadow_purple);
		stringToAtlasRegion.put("hex_tree_purple", hex_tree_purple);
		
		AtlasRegion village_hovel = mySprites.findRegion("village-hovel");
		AtlasRegion village_town = mySprites.findRegion("village-town");
		AtlasRegion village_fort = mySprites.findRegion("village-fort");
		AtlasRegion village_castle = mySprites.findRegion("village-castle");
		
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