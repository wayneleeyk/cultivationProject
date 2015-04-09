package com.potatoes.cultivation.stages;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.potatoes.cultivation.gameactors.ActorAssets;
import com.potatoes.cultivation.gameactors.PotatoActor;
import com.potatoes.cultivation.gameactors.TileActor;
import com.potatoes.cultivation.gameactors.VillageActor;
import com.potatoes.cultivation.helpers.ClickManager;
import com.potatoes.cultivation.logic.CultivationGame;
import com.potatoes.cultivation.logic.GameMap;
import com.potatoes.cultivation.logic.GameMap.MapCoordinates;
import com.potatoes.cultivation.logic.GameMap.MapDirections;
import com.potatoes.cultivation.logic.Tile;
import com.potatoes.cultivation.logic.UnitType;

public class GameWorld extends Stage{
	public TileActor[][] tiles;
	CultivationGame gameRound;
	ClickManager cm;
	ActorAssets assets;
	
	public GameWorld(CultivationGame aRound, ActorAssets actorAssets, ClickManager aCM) {
		gameRound = aRound;
		cm = aCM;
		assets = actorAssets;
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
				final TileActor newTile = new TileActor(t, assets, aRound, cm);
				newTile.setPosition(i * tileWidth * 0.75f + 150, i * tileHeight/2 + (j * tileHeight));
				
				if(t.containsVillage()) {
					final VillageActor village = new VillageActor(t.getVillage(), assets, cm);
					newTile.addActor(village);
					village.setPosition(150, 30);
				}
				
				if(t.occupant != null) {
					final PotatoActor potato = new PotatoActor(t.occupant, assets, aRound.playerToPotatoColor(t.getPlayer()), cm);
					newTile.addActor(potato);
					potato.setPosition(80, 30);
				}
				
				tiles[i][j] = newTile;
			}
		}
		
		// Add in reverse order to GameWorld (to draw back to front)
		for(int j = mapHeight - 1; j >= 0; j--) {
			for(int i = mapWidth - 1; i >= 0; i--) {
				this.addActor(tiles[i][j]);
			}
		}
		
		// Add dragcontrols
		addDragControls();
	}
	
	public void setCM(ClickManager cm) {
		this.cm = cm;
	}
	
	public void setAssetManager(ActorAssets a) {
		this.assets = a;
	}
	
	private void addDragControls() {
		this.addListener(new DragListener() {
			float prevX, prevY;

			@Override
			public void dragStart(InputEvent event, float x, float y,
					int pointer) {
				prevX = x;
				prevY = y;
				super.dragStart(event, x, y, pointer);
			}

			@Override
			public void drag(InputEvent event, float x, float y, int pointer) {
				GameWorld.this.getCamera().translate(prevX - x, prevY - y, 0);
				super.drag(event, x, y, pointer);
			}

			@Override
			public void dragStop(InputEvent event, final float x, final float y, int pointer) {
				GameWorld.this.addAction(new TemporalAction(1, Interpolation.pow2Out) {
					@Override
					protected void update(float percent) {
						float dirX = (prevX - x) * 0.5f;
						float dirY = (prevY - y) * 0.5f;
						float inversePercent = 1 - percent;
						GameWorld.this.getCamera().translate(inversePercent * dirX, inversePercent * dirY, 0);
					}
					
				});
				super.dragStop(event, x, y, pointer);
			}
		});
	}
	
	public void createVillageAt(int x, int y){
		TileActor tile = tiles[x][y];
		Tile t = tile.getTile();
		VillageActor villageActor = new VillageActor(t.getVillage(), assets, cm);
		tile.addActor(villageActor);
		villageActor.setPosition(150, 30);
	}

	public void createPotatoAt(int x, int y) {
		TileActor tile = tiles[x][y];
		Tile t = tile.getTile();
		PotatoActor p = new PotatoActor(t.getUnit(), assets, gameRound.playerToPotatoColor(t.getPlayer()), cm);
		tile.addActor(p);
		p.setPosition(80, 30);
	}

	public void upgradePotatoAt(int x, int y, UnitType uType) {
		TileActor tile = tiles[x][y];
		PotatoActor p = tile.findActor("PotatoActor");
		p.upgradePotato(uType);
	}
	
	public void removePotatoAt(int x, int y){
		TileActor tile = tiles[x][y];
		PotatoActor p = tile.findActor("PotatoActor");
		if(p != null) p.remove();
	}
	
	public void movePotato(int fromX, int fromY, int toX, int toY){
		TileActor tileActor = tiles[fromX][fromY];
		PotatoActor potato = tileActor.findActor("PotatoActor");
		MapCoordinates mapCoordFrom = new MapCoordinates(fromX, fromY);
		MapCoordinates mapCoordTo = new MapCoordinates(toX, toY);
		if(mapCoordFrom.go(MapDirections.Up).equals(mapCoordTo) || mapCoordFrom.go(MapDirections.Down).equals(mapCoordTo)) {
			potato.moveToTile(tiles[toX][toY], 1);
		}
		else {
			potato.moveToTile(tiles[toX][toY], 2);
		}
	}
	
	public void movePotatoV2(int fromX, int fromY, int toX, int toY, Action move) {
		TileActor tileActor = tiles[fromX][fromY];
		PotatoActor potato = tileActor.findActor("PotatoActor");
		MapCoordinates mapCoordFrom = new MapCoordinates(fromX, fromY);
		MapCoordinates mapCoordTo = new MapCoordinates(toX, toY);
		if(mapCoordFrom.go(MapDirections.Up).equals(mapCoordTo) || mapCoordFrom.go(MapDirections.Down).equals(mapCoordTo)) {
			potato.moveToTileV2(tiles[toX][toY], 1, move);
		}
		else {
			potato.moveToTileV2(tiles[toX][toY], 2, move);
		}
	}

	public void removeVillageAt(int x, int y) {
		TileActor tileActor = tiles[x][y];
		VillageActor village = tileActor.findActor("VillageActor");
		if(village!=null) {
			village.remove();
			System.out.println("Removing village actor at "+x+" "+y+" done:"+tileActor.findActor("VillageActor")==null);
		}
	}
}
