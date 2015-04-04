package com.potatoes.cultivation.gameactors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.potatoes.cultivation.helpers.ClickManager;
import com.potatoes.cultivation.logic.Village;
import com.potatoes.cultivation.logic.VillageType;

public class VillageActor extends Actor {
	ActorAssets myAssets;
	Village myVillage;
	VillageType myType;
	
	public VillageActor(Village v, ActorAssets a, final ClickManager cm) {
		myVillage = v;
		myType = v.getType();
		myAssets = a;
		AtlasRegion region = myAssets.stringToAtlasRegion.get("village_" + myType.toString().toLowerCase());
		setWidth(region.getRegionWidth());
		setHeight(region.getRegionHeight());
		
		// Listener for clicking
		this.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Village clicked at " + x + " " + y);
				cm.addClickedActor(VillageActor.this);
				event.stop();
			}
		});
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
