package com.potatoes.cultivation.gameactors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.potatoes.cultivation.logic.LandType;
import com.potatoes.cultivation.logic.StructureType;
import com.potatoes.cultivation.logic.Tile;

public class TileActor extends Group {
	Tile myTile;
	Color overlay;
	
	LandType myLandtype;
	StructureType myStructure;
	
	ActorAssets myAssets;
	
	/**
	 * 
	 * @param t - The actual game tile
	 * @param a - The ActorAssets
	 * @param c - The overlay color of the tile (if null, then default to white)
	 */
	public TileActor(Tile t, ActorAssets a, Color c) {
		myAssets = a;
		myTile = t;
		myLandtype = t.getLandType();
		myStructure = t.getStructure();
		overlay = (c != null)? c : Color.WHITE;
	}
	
	/**
	 * Only for debuggin, dont use this constructor
	 * @param a - ActorAssets
	 */
	public TileActor(ActorAssets a) {
		myAssets = a;
		overlay = Color.WHITE;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.setColor(overlay);
		// Draw the structure
		
		// Draw the base
		batch.draw(myAssets.stringToAtlasRegion.get("hex_" + myLandtype.toString().toLowerCase()),
				getX(), getY());
		batch.setColor(Color.WHITE);
		super.draw(batch, parentAlpha);
	}
	
	/**
	 * This method will explicitly change the information of the TileActor.
	 * This may be useful for setting the color and debugging. 
	 * @param newLand - Can be null to not change it
	 * @param newStruct - Can be null to not change it
	 * @param newColor - Can be null to not change it
	 */
	public void updateTile(LandType newLand, StructureType newStruct, Color newColor) {
		if(newLand != null) myLandtype = newLand;
		if(newStruct != null) myStructure = newStruct;
		if(newColor != null) overlay = newColor;
	}
	/*
	 * This method refreshes the tile's information
	 */
	public void refreshTile() {
		myLandtype = myTile.getLandType();
		myStructure = myTile.getStructure();
	}
	
	public Tile getTile(){
		return this.myTile;
	}
}