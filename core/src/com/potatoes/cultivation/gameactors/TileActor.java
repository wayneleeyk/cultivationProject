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
	
	public TileActor(Tile tile, ActorAssets a) {
		myAssets = a;
		myTile = tile;
//		myLandtype = tile.getLandType();
//		myStructure = tile.getStructure();
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
	 * This method will update the information of the TileActor
	 * @param newLand - Can be null to not change it
	 * @param newStruct - Can be null to not change it
	 * @param newColor - Can be null to not change it
	 */
	public void updateTile(LandType newLand, StructureType newStruct, Color newColor) {
		if(newLand != null) myLandtype = newLand;
		if(newStruct != null) myStructure = newStruct;
		if(newColor != null) overlay = newColor;
	}
}