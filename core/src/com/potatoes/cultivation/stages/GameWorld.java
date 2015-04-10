package com.potatoes.cultivation.stages;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.potatoes.cultivation.gameactors.ActorAssets;
import com.potatoes.cultivation.gameactors.PotatoActor;
import com.potatoes.cultivation.gameactors.TileActor;
import com.potatoes.cultivation.gameactors.VillageActor;
import com.potatoes.cultivation.helpers.ClickManager;
import com.potatoes.cultivation.logic.CultivationGame;
import com.potatoes.cultivation.logic.GameMap;
import com.potatoes.cultivation.logic.GameMap.MapCoordinates;
import com.potatoes.cultivation.logic.GameMap.MapDirections;
import com.potatoes.cultivation.logic.Region;
import com.potatoes.cultivation.logic.Tile;
import com.potatoes.cultivation.logic.UnitType;
import com.potatoes.cultivation.networking.ProtocolHandler;

public class GameWorld extends Stage{
	public TileActor[][] tiles;
	CultivationGame gameRound;
	ClickManager cm;
	ActorAssets assets;
	OrthographicCamera myCam;
	private float maxX;
	private float maxY;

	ProtocolHandler<MapCoordinates> villageLocator;
	public Queue<MapCoordinates> villageConstructionSites = new ConcurrentLinkedQueue<>();
	
	public GameWorld(CultivationGame aRound, ActorAssets actorAssets, ClickManager aCM) {
		myCam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.getViewport().setCamera(myCam);
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
		
		// Set maxX and maxY
		maxX = tiles[mapWidth - 1][mapHeight - 1].getX();
		maxY = tiles[mapWidth - 1][mapHeight - 1].getY();
		
		System.out.println("maxX is " + maxX);
		System.out.println("maxY is " + maxY);
		
		// Add dragcontrols
		addZoomControls();
	}
	
	@Override
	public void act(float delta) {
		myCam.position.x = MathUtils.clamp(myCam.position.x, 0, maxX);
		myCam.position.y = MathUtils.clamp(myCam.position.y, 0, maxY);
		super.act(delta);
	}

	public void setCM(ClickManager cm) {
		this.cm = cm;
	}
	
	public void setAssetManager(ActorAssets a) {
		this.assets = a;
	}
	
	public void setVillageLocator(ProtocolHandler<MapCoordinates> handler){
		this.villageLocator = handler;
	}
	
	public ProtocolHandler<MapCoordinates> getVillageLocator(){
		return this.villageLocator;
	}
	
	private void addZoomControls() {
		this.addListener(new InputListener() {
			@Override
			public boolean scrolled(InputEvent event, float x, float y,
					int amount) {
				myCam.zoom = MathUtils.clamp(myCam.zoom + ((float) amount )/10, 0.3f, 3f);
				return true;
			}
		});
	}
	
	public void createVillageAt(int x, int y){
		System.out.println("Setting up village image at "+x+" "+y);
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

	public void createVillageAt(MapCoordinates result) {
		this.villageConstructionSites.add(result);
	}
	
	public MapCoordinates getNextVillageConstructionSite(Region region){
		for (MapCoordinates coordinate : this.villageConstructionSites) {
			Tile t = this.gameRound.getGameMap().getMap()[coordinate.i()][coordinate.j()];
			if(region.getTiles().contains(t)) return coordinate;
		}
		return null;
	}
}
