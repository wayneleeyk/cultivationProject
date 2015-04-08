package com.potatoes.cultivation.stages;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.gameactors.PotatoActor;
import com.potatoes.cultivation.gameactors.TileActor;
import com.potatoes.cultivation.gameactors.VillageActor;
import com.potatoes.cultivation.helpers.ClickManager;
import com.potatoes.cultivation.logic.CultivationGame;
import com.potatoes.cultivation.logic.GameAction;
import com.potatoes.cultivation.logic.GameAction.MoveUnitAction;
import com.potatoes.cultivation.logic.GameMap;
import com.potatoes.cultivation.logic.GameMap.MapCoordinates;
import com.potatoes.cultivation.logic.GameMap.MapDirections;
import com.potatoes.cultivation.logic.Player;
import com.potatoes.cultivation.logic.Tile;
import com.potatoes.cultivation.logic.UnitType;
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
	PotatoMenuGroup potatoMenu;
	
	TileActor previouslySelectedTile;
	
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
		if(game.isMyTurn(currentPlayer) && targetVillageActor != null && targetVillageActor.getVillage().getOwner().equals(currentPlayer)) {
			if(!villageMenu.isVisible()) villageMenu.setVisible(true);
			villageMenu.setVillageActor(targetVillageActor);
		}
		else {
			villageMenu.setVisible(false);
		}
		
		// If the clicked potatoActor is yours, open the menu
		PotatoActor targetPotatoActor = cm.getPotatoActor();
		if(game.isMyTurn(currentPlayer) && targetPotatoActor != null && targetPotatoActor.getUnit().myVillage.getOwner().equals(currentPlayer)) {
			if(!potatoMenu.isVisible()) potatoMenu.setVisible(true);
			potatoMenu.setPotatoActor(targetPotatoActor);
			potatoMenu.disableButtonsAccordingToTypeAndResources();
		}
		else {
			potatoMenu.setVisible(false);
		}
		
		//If it's your turn, display the "End Turn" button
		if (!game.isMyTurn(currentPlayer)) {
			endTurn.setVisible(false);
		} else {
			endTurn.setVisible(true);
		}
		
		/**********************************************************************/
		
		if(previouslySelectedTile != cm.getTileActor() && cm.getTileActor()!=null) {
			Tile tile = cm.getTileActor().getTile();
			System.out.println("Clicked on "+tile + " owner:" + tile.owner+" occupant:"+tile.occupant);
			previouslySelectedTile = cm.getTileActor();
		}
	}

	private void addGUIStuff() {
		// Top label
		Label stats = new Label("", skin, "stats"){
			@Override
			public void act(float delta) {
				super.act(delta);
				if (currentVillage!=null && currentVillage.getType()!=null) {
					this.setText("  Turn :"+ game.turnOf().getUsername() + "  " + 
								 "Village: " + currentVillage.getType() + ", " + 
								 "Gold: " + currentVillage.getGold() + ", " + 
								 "Logs " + currentVillage.getWood());
				} else {
					this.setText("  Turn :"+ game.turnOf().getUsername());
					
				}
			}
		};
		stats.setWidth(width);
		stats.setHeight(55);
		stats.setPosition(0, height - 55); //Hardcode FTW
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
		potatoMenu = new PotatoMenuGroup();
		this.addActor(villageMenu);
		this.addActor(potatoMenu);
		villageMenu.toBack();
		potatoMenu.toBack();
	}
	
	class VillageMenuGroup extends Group {
		VillageActor myVillageActor;
		
		/*
		 * Constructor for a villageMenuGroup, set to invisible when constructed
		 */
		public VillageMenuGroup() {
			TextButton upgradeVillage = new TextButton("Upgrade Village", skin, "default");
			TextButton hireVillager = new TextButton("Hire Villager", skin, "default");
			
			this.addActor(upgradeVillage);
			this.addActor(hireVillager);
			
			float buttonHeight = upgradeVillage.getHeight();
			
			// Stack the buttons on top of each other
			upgradeVillage.setY(buttonHeight);
			
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
					Tile t = Cultivation.GAMEMANAGER.getGame().getVillagerSpawnPoint(myVillageActor.getVillage());
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
			myVillageActor = v;
			Vector2 villageStageCoord = v.localToStageCoordinates(new Vector2(v.getX(), v.getY()));
			Vector2 villageScreen = v.getStage().stageToScreenCoordinates(villageStageCoord);
			Vector2 menuCoord = this.getStage().screenToStageCoordinates(villageScreen);
			this.setPosition(menuCoord.x - 100, menuCoord.y);	
		}		
	}
	
	class PotatoMenuGroup extends Group {
		PotatoActor myPotato;
		Group topLevel;
		TextButton buildRoad;
		TextButton upgradePotato;
		TextButton movePotato;
		Group upgrades;
		TextButton infantry;
		TextButton soldier;
		TextButton knight;
		TextButton cannoneer;
		Group directionMenu;
		
		public PotatoMenuGroup() {
			/******* First level group **********/
			topLevel = new Group();
			
			upgradePotato = new TextButton("Upgrade Potato", skin, "default");
			buildRoad = new TextButton("Build Road", skin, "default");
			movePotato = new TextButton("Move", skin, "default");
			
			topLevel.addActor(upgradePotato);
			topLevel.addActor(buildRoad);
			topLevel.addActor(movePotato);
			
			float buttonHeight = upgradePotato.getHeight();
			
			// Stack the buttons on top of each other
			upgradePotato.setY(2*buttonHeight);
			buildRoad.setY(buttonHeight);
			
			// Add listeners to the buttons 
			upgradePotato.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					System.out.println("upgradePotato clicked");
					topLevel.setVisible(false);
					upgrades.setVisible(true);
				}
			});
			
			buildRoad.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					System.out.println("buildRoad clicked");
					cm.reset();
				}
			});
			
			movePotato.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					topLevel.setVisible(false);
					directionMenu.setVisible(true);
				}
			});
			
			/******* Second level group - Upgrade Group **********/
			upgrades = new Group();
			infantry = new TextButton("Infantry", skin, "default");
			soldier = new TextButton("Solider", skin, "default");
			knight = new TextButton("Knight", skin, "default");
			cannoneer = new TextButton("Cannoneer", skin, "default");
			
			upgrades.addActor(infantry);
			upgrades.addActor(soldier);
			upgrades.addActor(knight);
			upgrades.addActor(cannoneer);
			
			infantry.setY(3*buttonHeight);
			soldier.setY(2*buttonHeight);
			knight.setY(buttonHeight);
			
			infantry.addListener(new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					System.out.println("infantry clicked");
					GameAction.UpgradePotatoAction gameAction = new GameAction.UpgradePotatoAction(myPotato.getUnit(), UnitType.Infantry);
					gameApp.client.sendActions(gameAction);
					cm.reset();
				}
			});
			
			infantry.addListener(new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					System.out.println("infantry clicked");
					GameAction.UpgradePotatoAction gameAction = new GameAction.UpgradePotatoAction(myPotato.getUnit(), UnitType.Infantry);
					gameApp.client.sendActions(gameAction);
					cm.reset();
				}
			});
			
			soldier.addListener(new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					System.out.println("soldier clicked");
					GameAction.UpgradePotatoAction gameAction = new GameAction.UpgradePotatoAction(myPotato.getUnit(), UnitType.Soldier);
					gameApp.client.sendActions(gameAction);
					cm.reset();
				}
			});
			
			knight.addListener(new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					System.out.println("knight clicked");
					GameAction.UpgradePotatoAction gameAction = new GameAction.UpgradePotatoAction(myPotato.getUnit(), UnitType.Knight);
					gameApp.client.sendActions(gameAction);
					cm.reset();
				}
			});
			
			upgrades.setVisible(false);
			
			/*********** Second level Group - Direction Menu**********/
			int horizontalOffset = 120;
			int verticalOffset = 40;
			
			directionMenu = new Group();
			
			for (MapDirections direction : MapDirections.values()) {
				final MapDirections thatWay = direction;
				Button button = null;
				if(direction.equals(MapDirections.Up)) {
					button = new Button(skin, "arrow-up");
					button.setY(verticalOffset + 20);
				}
				if(direction.equals(MapDirections.Down)) {
					button = new Button(skin, "arrow-down");
					button.setY(-verticalOffset - 20);
				}
				if(direction.equals(MapDirections.LeftUp)) {
					button = new Button(skin, "arrow-leftUp");
					button.setY(verticalOffset);
					button.setX(-horizontalOffset);
				}
				if(direction.equals(MapDirections.LeftDown)) {
					button = new Button(skin, "arrow-leftDown");
					button.setY(-verticalOffset);
					button.setX(-horizontalOffset);
				}
				if(direction.equals(MapDirections.RightUp)) {
					button = new Button(skin, "arrow-rightUp");
					button.setY(verticalOffset);
					button.setX(horizontalOffset);
				}
				if(direction.equals(MapDirections.RightDown)) {
					button = new Button(skin, "arrow-rightDown");
					button.setY(-verticalOffset);
					button.setX(horizontalOffset);
				}
				
				button.addListener(new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y) {
						super.clicked(event, x, y);
						System.out.println(thatWay.name() + " clicked");
						Tile tile = myPotato.getUnit().getTile();
						GameMap.MapCoordinates destination = new MapCoordinates(tile.x, tile.y).go(thatWay);
						System.out.println("Moving from "+tile.x +" "+tile.y+" to "+ destination.getTile(game.getGameMap()).x+" "+destination.getTile(game.getGameMap()).y);
						System.out.println("From a "+tile+" to "+destination.getTile(game.getGameMap()));
						GameAction.MoveUnitAction moveAction = new MoveUnitAction(tile, destination.getTile(game.getGameMap()));
						gameApp.client.sendActions(moveAction);
						cm.reset();
					}
				});
				directionMenu.addActor(button);
			}
			
			directionMenu.setX(70);
			directionMenu.setY(-30);
			directionMenu.setVisible(false);
			
			/*** Adding the create stuff to this ***/
			
			this.addActor(topLevel);
			this.addActor(upgrades);
			this.addActor(directionMenu);
			this.setVisible(false);
		}
		
		@Override
		public void setVisible(boolean visible) {
			super.setVisible(visible);
			if(visible) {
				topLevel.setVisible(true);
				upgrades.setVisible(false);
				directionMenu.setVisible(false);
				infantry.setDisabled(false);
				soldier.setDisabled(false);
				knight.setDisabled(false);
				upgradePotato.setDisabled(false);
			}
		}

		/**
		 * Set the {@link PotatoActor} model to this menu. 
		 * Also sets position of the menu.
		 * @param p - The target PotatoActor
		 */
		public void setPotatoActor(PotatoActor p) {
			myPotato = p;
			Vector2 potatoStageCoord = p.localToStageCoordinates(new Vector2(p.getX(), p.getY()));
			Vector2 potatoScreen = p.getStage().stageToScreenCoordinates(potatoStageCoord);
			Vector2 menuCoord = this.getStage().screenToStageCoordinates(potatoScreen);
			this.setPosition(menuCoord.x - 100, menuCoord.y);	
		}
		
		public void disableButtonsAccordingToTypeAndResources() {
			UnitType myType = myPotato.getUnit().myType;
			if(myType.equals(UnitType.Cannon)) {
				upgradePotato.setDisabled(true);
			}
			if(myType.equals(UnitType.Knight)) {
				infantry.setDisabled(true);
				soldier.setDisabled(true);
				knight.setDisabled(true);
			}
			if(myType.equals(UnitType.Soldier)) {
				infantry.setDisabled(true);
				soldier.setDisabled(true);
			}
			if(myType.equals(UnitType.Infantry)) {
				infantry.setDisabled(true);
			}
			
			// According to resources...
			// Something like village.canHire?
		}
	}
}