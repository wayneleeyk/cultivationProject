package com.potatoes.cultivation.stages;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.potatoes.cultivation.gameactors.ActorAssets;
import com.potatoes.cultivation.gameactors.TileActor;
import com.potatoes.cultivation.gameactors.VillageActor;
import com.potatoes.cultivation.helpers.ClickManager;
import com.potatoes.cultivation.logic.CultivationGame;
import com.potatoes.cultivation.logic.GameMap;
import com.potatoes.cultivation.logic.Tile;

public class GameWorld extends Stage {
	public TileActor[][] tiles;
	CultivationGame gameRound;
	ClickManager cm;
	
	public GameWorld(CultivationGame aRound, ActorAssets assets, ClickManager aCM) {
		gameRound = aRound;
		cm = aCM;
		GameMap map = aRound.getGameMap();
		Tile[][] gameMap = map.getMap();
		int mapWidth = gameMap.length;
		int mapHeight = gameMap[0].length;
		tiles = new TileActor[mapWidth][mapHeight];

		int tileWidth = 308;
		int tileHeight = 88;
		
		for(int i = 0; i < mapWidth; i++) {
			for(int j = 0; j < mapHeight; j++) {
				Tile t = gameMap[i][j];
				final TileActor newTile = new TileActor(t, assets, gameRound.playerToColor(t.getPlayer()));
				newTile.setPosition(i * tileWidth * 0.75f + 150, i * tileHeight/2 + (j * tileHeight));
				
				// TODO Add village actor and listeners
				if(gameMap[i][j].containsVillage()) {
					final VillageActor village = new VillageActor(t.getVillage(), assets);
					newTile.addActor(village);
					village.setPosition(150, 30);
					village.addListener(new ChangeListener(){
						@Override
						public void changed(ChangeEvent event, Actor actor) {
							System.out.println("Village is being bullied");
							cm.addClickedActor(newTile);							
						}
						
					});
				}
				
				// Add listener to Tiles
				newTile.addListener(new ChangeListener() {

					@Override
					public void changed(ChangeEvent event, Actor actor) {
						cm.addClickedActor(newTile);
					}
					
				});
				tiles[i][j] = newTile;
			}
		}
		
		// Add in reverse order to GameWorld (to draw back to front)
		for(int j = mapHeight - 1; j >= 0; j--) {
			for(int i = mapWidth - 1; i >= 0; i--) {
				this.addActor(tiles[i][j]);
			}
		}
	}
}
