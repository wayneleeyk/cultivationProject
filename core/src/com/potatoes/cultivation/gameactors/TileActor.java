package com.potatoes.cultivation.gameactors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.potatoes.cultivation.helpers.ClickManager;
import com.potatoes.cultivation.logic.CultivationGame;
import com.potatoes.cultivation.logic.LandType;
import com.potatoes.cultivation.logic.StructureType;
import com.potatoes.cultivation.logic.Tile;

public class TileActor extends Group {
	Tile myTile;
	String color;
	
	LandType myLandtype;
	StructureType myStructure;
	
	ActorAssets myAssets;
	
	CultivationGame theRound;
	
	/**
	 * 
	 * @param t - The actual game tile
	 * @param a - The ActorAssets
	 * @param c - The overlay color of the tile (if null, then default to white)
	 * @param cm - ClickManager
	 */
	public TileActor(Tile t, ActorAssets a, CultivationGame pRound, final ClickManager cm) {
		myAssets = a;
		myTile = t;
		myLandtype = t.getLandType();
		myStructure = t.getStructure();
		theRound = pRound;
		if(myTile.getPlayer() != null && myTile.getPlayer().notNull()) {
			color = theRound.playerToPotatoColor(myTile.getPlayer());
		}
		else {
			color = null;
		}
		
		// Add listener to Tiles
		this.addListener(cm.new ClickToCMListener());
	}
	
	/**
	 * Only for debuggin, dont use this constructor
	 * @param a - ActorAssets
	 */
	public TileActor(ActorAssets a) {
		myAssets = a;
		color = null;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		refreshTile();
		// Draw the structure
		
		// Draw the base
		batch.draw(myAssets.stringToAtlasRegion.get("hex_" + myLandtype.toString().toLowerCase() + (color != null? ("_" + color) : "")),
				getX(), getY());
		super.draw(batch, parentAlpha);
	}
	/*
	 * Math is used here to not detect the diagonal parts of the hexagon...
	 * And this is hardcoded... so need to change if we're changing the dimensions
	 * of the hexagon
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.scenes.scene2d.Group#hit(float, float, boolean)
	 */
	@Override
	public Actor hit(float x, float y, boolean touchable) {
		Actor child = super.hit(x, y, touchable);
		if(child != null) return child;
		if (touchable && getTouchable() != Touchable.enabled) return null;
		if(x >= 0 && x < 308 && y >= 0 && y < 88) {
			if((y < 0.567f * x + 44 && y > 0.567f * x - 131) &&
			(y > -0.567f * x + 44 && y < -0.567f * x + 219))
			{
				return this;
			}
		}
		return null;
	}

	/**
	 * This method will explicitly change the information of the TileActor.
	 * This may be useful for debugging. 
	 * @param newLand - Can be null to not change it
	 * @param newStruct - Can be null to not change it
	 * @param newColor - Can be null to not change it, if not null, then it's either "yellow", "red" or "purple"
	 */
	public void updateTile(LandType newLand, StructureType newStruct, String newColor) {
		if(newLand != null) myLandtype = newLand;
		if(newStruct != null) myStructure = newStruct;
		if(newColor != null) color = newColor;
	}
	/*
	 * This method refreshes the tile's information
	 */
	public void refreshTile() {
		myLandtype = myTile.getLandType();
		myStructure = myTile.getStructure();
		if(myTile.getPlayer() != null && myTile.getPlayer().notNull()) {
			color = theRound.playerToPotatoColor(myTile.getPlayer());
		}
		else {
			color = null;
		}
	}
	
	public Tile getTile(){
		return this.myTile;
	}
}