package com.potatoes.cultivation.gameactors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.potatoes.cultivation.helpers.ClickManager;
import com.potatoes.cultivation.logic.CultivationGame;
import com.potatoes.cultivation.logic.LandType;
import com.potatoes.cultivation.logic.StructureType;
import com.potatoes.cultivation.logic.Tile;

public class TileActor extends Group {
	Tile myTile;
	Color overlay;
	
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
			overlay = theRound.playerToColor(myTile.getPlayer());
		}
		else {
			overlay = Color.WHITE;
		}
		
		// Add listener to Tiles
		this.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Tile clicked at " + x + " " + y);
				cm.addClickedActor(TileActor.this);
				event.stop();
			}
		});
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
		if(myTile.getPlayer() != null && myTile.getPlayer().notNull()) {
			overlay = theRound.playerToColor(myTile.getPlayer());
		}
		else {
			overlay = Color.WHITE;
		}
	}
	
	public Tile getTile(){
		return this.myTile;
	}
}