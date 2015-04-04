package com.potatoes.cultivation.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.gameactors.VillageActor;
import com.potatoes.cultivation.helpers.ClickManager;
import com.potatoes.cultivation.logic.CultivationGame;
import com.potatoes.cultivation.logic.GameAction;
import com.potatoes.cultivation.logic.Player;
import com.potatoes.cultivation.logic.Tile;
import com.potatoes.cultivation.logic.Village;

public class HUD2 extends Stage {
	Cultivation gameApp;
	CultivationGame game;
	ClickManager cm;
	Player currentPlayer;
	Skin skin;
	
	Village currentVillage;
	
	private int width;
	private int height;
	
	TextButton endTurn;
	VillageMenuGroup villageMenu;
	
	public HUD2(Cultivation app, ClickManager cm) {
		this.gameApp = app;
		this.game = gameApp.GAMEMANAGER.getGame();
		this.cm = cm;
		this.currentPlayer = gameApp.player;
		this.skin = gameApp.skin;
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		
		addGUIStuff();
	}
	
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		//If the latest clicked village belongs to you, update reference to current village
		Village lastClickedVillage = cm.lastClickedVillage();
		if (lastClickedVillage != null && lastClickedVillage.getOwner().equals(currentPlayer)) {
			currentVillage = lastClickedVillage;
		}
		else {
			currentVillage = null;
		}
		
		// If the clicked villageActor is yours, open the menu
		VillageActor targetVillageActor = cm.getVillageActor();
		if(targetVillageActor != null && targetVillageActor.getVillage().getOwner().equals(currentPlayer)) {
			villageMenu.setVisible(true);
			villageMenu.setVillageActor(targetVillageActor);
		}
		else {
			villageMenu.setVisible(false);
		}
		
		//If it's your turn, display the "End Turn" button
		if (!game.isMyTurn(currentPlayer)) {
			endTurn.setVisible(false);
		} else {
			endTurn.setVisible(true);
		}
	}

	private void addGUIStuff() {
		// Top label
		Label stats = new Label("", skin, "white"){
			@Override
			public void act(float delta) {
				super.act(delta);
				if (currentVillage!=null && currentVillage.getType()!=null) {
					this.setText("Turn :"+ game.turnOf().getUsername() + 
								 "Village: " + currentVillage.getType() + ", " + 
								 "Gold: " + currentVillage.getGold() + ", " + 
								 "Logs " + currentVillage.getWood());
				} else {
					this.setText("Turn :"+ game.turnOf().getUsername());
					
				}
			}
		};
		stats.setPosition(5, height-30); //Hardcode FTW
		this.addActor(stats);
		
		/********************* Buttons ******************************************/
		// End turn button
		endTurn = new TextButton("End Turn", skin, "red");
		endTurn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				GameAction.EndTurnAction endTurnAction = new GameAction.EndTurnAction();
				gameApp.client.sendActions(endTurnAction);
				System.out.println("current player: " + currentPlayer.getUsername());
				System.out.println("turn of: " + game.turnOf());
			}
		});
		this.addActor(endTurn);
		
		// Save button
		TextButton save = new TextButton("Save Game", skin, "red");
		save.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameApp.client.save(game);
				System.out.println("saving game");
			}
		});
		save.setPosition(this.width - save.getWidth(), this.height - save.getHeight());
		this.addActor(save);
		
		/******************** MenuGroups ************************/
		villageMenu = new VillageMenuGroup();
		this.addActor(villageMenu);
	}
	
	class VillageMenuGroup extends Group {
		VillageActor myVillageActor;
		
		/*
		 * Constructor for a villageMenuGroup, set to invisible when constructed
		 */
		public VillageMenuGroup() {
			TextButton upgradeVillage = new TextButton("Upgrade Village", skin, "default");
			TextButton hireVillager = new TextButton("Hire Villager", skin, "default");
			TextButton hireCannon = new TextButton("Hire PowPow", skin, "default");
			
			this.addActor(upgradeVillage);
			this.addActor(hireVillager);
			this.addActor(hireCannon);
			
			float buttonHeight = upgradeVillage.getHeight();
			
			// Stack the buttons on top of each other
			upgradeVillage.setY(2*buttonHeight);
			hireVillager.setY(buttonHeight);
			
			// Add listeners to the buttons 
			upgradeVillage.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					System.out.println("upgradeVillage clicked");
					GameAction.UpgradeVillageAction gameAction = new GameAction.UpgradeVillageAction(myVillageActor.getVillage());
//					gameAction.execute(game.GAMEMANAGER.getGame());
					gameApp.client.sendActions(gameAction);
				}
			});
			
			hireVillager.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					System.out.println("hireVillager clicked");
					Tile t = Cultivation.GAMEMANAGER.getGame().getVillageSpawnPoint(myVillageActor.getVillage());
					if(t!=null){
						GameAction.HireVillagerAction hireAction = new GameAction.HireVillagerAction(t);
//						hireAction.execute(game.GAMEMANAGER.getGame());
						gameApp.client.sendActions(hireAction);	
					}
				}
			});
			
			this.setVisible(false);
		}
		
		/**
		 * Set the {@link VillageActor} model to this menu. 
		 * Also sets position of the menu.
		 * @param v - The target VillageActor
		 */
		public void setVillageActor(VillageActor v) {
			if(myVillageActor == null) {
				myVillageActor = v;
				Vector2 villageStageCoord = v.localToStageCoordinates(new Vector2(v.getX(), v.getY()));
				Vector2 villageScreen = v.getStage().stageToScreenCoordinates(villageStageCoord);
				Vector2 menuCoord = this.getStage().screenToStageCoordinates(villageScreen);
				this.setPosition(menuCoord.x - 100, menuCoord.y);	
			}
		}

		@Override
		public void setVisible(boolean visible) {
			super.setVisible(visible);
			if(!visible) {
				myVillageActor = null;
			}
		}
		
		
	}
}