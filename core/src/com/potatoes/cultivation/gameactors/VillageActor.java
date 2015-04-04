package com.potatoes.cultivation.gameactors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.potatoes.cultivation.logic.Village;
import com.potatoes.cultivation.logic.VillageType;

public class VillageActor extends Actor {
	ActorAssets myAssets;
	Village myVillage;
	VillageType myType;
	
	public VillageActor(Village v, ActorAssets a) {
		myVillage = v;
		myType = v.getType();
		myAssets = a;
		AtlasRegion region = myAssets.stringToAtlasRegion.get("village_" + myType.toString().toLowerCase());
		setWidth(region.getRegionWidth());
		setHeight(region.getRegionHeight());
	}
	
	public void refreshVillage() {
		myType = myVillage.getType();
		AtlasRegion region = myAssets.stringToAtlasRegion.get("village_" + myType.toString().toLowerCase());
		setWidth(region.getRegionWidth());
		setHeight(region.getRegionHeight());
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(myAssets.stringToAtlasRegion.get("village_" + myType.toString().toLowerCase()), 
				getX(), getY());
		super.draw(batch, parentAlpha);
	}
	
	public Village getVillage(){
		return this.myVillage;
	}
	
}
