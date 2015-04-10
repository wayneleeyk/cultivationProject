package com.potatoes.cultivation.gameactors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.potatoes.cultivation.helpers.ClickManager;
import com.potatoes.cultivation.logic.Village;
import com.potatoes.cultivation.logic.Village.VillageStatus;
import com.potatoes.cultivation.logic.VillageType;

public class VillageActor extends Actor {
	ActorAssets myAssets;
	Village myVillage;
	VillageType myType;
	Label villageStatus;
	
	public VillageActor(Village v, ActorAssets a, final ClickManager cm) {
		myVillage = v;
		myType = v.getType();
		myAssets = a;
		AtlasRegion region = myAssets.stringToAtlasRegion.get("village_" + myType.toString().toLowerCase());
		setWidth(region.getRegionWidth());
		setHeight(region.getRegionHeight());
		
		// Listener for clicking
		this.addListener(cm.new ClickToCMListener());
		
		// Naming the actor for finding
		this.setName("VillageActor");
		
		// Making the label for village status
		villageStatus = new Label("" ,a.mySkin, "default-mini");
		villageStatus.setFontScale(0.55f);
		villageStatus.setPosition(100, 10);
	}
	
	public void refreshVillage() {
		if(this.myVillage.getRegion() == null) this.remove();
		myType = myVillage.getType();
		villageStatus.setText(myVillage.getStatus().toString() +" (HP:"+ myVillage.HP()+"/"+myType.maxHP()+")");
		AtlasRegion region = myAssets.stringToAtlasRegion.get("village_" + myType.toString().toLowerCase());
		setWidth(region.getRegionWidth());
		setHeight(region.getRegionHeight());
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		refreshVillage();
		villageStatus.draw(batch, parentAlpha);
		if(!myVillage.getStatus().equals(VillageStatus.VillageReady)) {
			batch.setColor(0.4f, 0.4f, 0.4f, 1);
		}
		batch.draw(myAssets.stringToAtlasRegion.get("village_" + myType.toString().toLowerCase()), 
				getX(), getY());
		batch.setColor(Color.WHITE);
		super.draw(batch, parentAlpha);
	}
	
	public Village getVillage(){
		return this.myVillage;
	}
	
}
