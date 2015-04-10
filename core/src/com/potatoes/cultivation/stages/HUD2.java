package com.potatoes.cultivation.stages;

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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.gameactors.PotatoActor;
import com.potatoes.cultivation.gameactors.TileActor;
import com.potatoes.cultivation.gameactors.VillageActor;
import com.potatoes.cultivation.helpers.ClickManager;
import com.potatoes.cultivation.logic.ActionType;
import com.potatoes.cultivation.logic.CultivationGame;
import com.potatoes.cultivation.logic.GameAction;
import com.potatoes.cultivation.logic.GameAction.MoveUnitAction;
import com.potatoes.cultivation.logic.GameMap;
import com.potatoes.cultivation.logic.GameMap.MapCoordinates;
import com.potatoes.cultivation.logic.GameMap.MapDirections;
import com.potatoes.cultivation.logic.StructureType;
import com.potatoes.cultivation.logic.Village.VillageStatus;
import com.potatoes.cultivation.logic.Player;
import com.potatoes.cultivation.logic.Tile;
import com.potatoes.cultivation.logic.UnitType;
import com.potatoes.cultivation.logic.Village;
import com.potatoes.cultivation.logic.VillageType;

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
		if(game.isMyTurn(currentPlayer) 
				&& targetVillageActor != null 
				&& targetVillageActor.getVillage().getOwner().equals(currentPlayer)
				&& targetVillageActor.getVillage().getStatus().equals(VillageStatus.VillageReady)) {
			if(!villageMenu.isVisible()) villageMenu.setVisible(true);
			villageMenu.setVillageActor(targetVillageActor);
			villageMenu.disableVillageButtonsAccordingly();
			System.out.println("Clicked Village type " + targetVillageActor.getVillage().getType());
		}
		else {
			villageMenu.setVisible(false);
		}
		
		// If the clicked potatoActor is yours, open the menu
		PotatoActor targetPotatoActor = cm.getPotatoActor();
		if(game.isMyTurn(currentPlayer) && targetPotatoActor != null 
				&& targetPotatoActor.getUnit().myVillage.getOwner().equals(currentPlayer)
				&& targetPotatoActor.getUnit().currentAction.equals(ActionType.ReadyForOrders)) {
			if(!potatoMenu.isVisible()) potatoMenu.setVisible(true);
			potatoMenu.setPotatoActor(targetPotatoActor);
			potatoMenu.disableUnitButtonsAccordingly();
		}
		else {
			potatoMenu.setVisible(false);
		}
		//Check if player wants to build watchtower
		if (cm.secondaryClickEnabled() && targetVillageActor !=null) {
			TileActor targetTile = cm.getTileActor();
			if (targetTile != null 
					&& targetTile.getTile().getVillage() != null 
					&& targetTile.getTile().getVillage().equals(targetVillageActor.getVillage())) {
				System.out.println("Adding tower to tile.." + targetTile.getX()+","+ targetTile.getY());
				GameAction.BuildWatchtowerAction gameAction = new GameAction.BuildWatchtowerAction(targetTile.getTile());
				gameApp.client.sendActions(gameAction);
				cm.reset();
			}
		}
		//Check if a cannoneer wants to shoot
		if (cm.secondaryClickEnabled() && cm.getPotatoActor()!=null) {
			PotatoActor target = cm.getSecondPotatoActor();
			System.out.println("Shooting target :"+target);
			if (target != null 
					&& target.getUnit() != null 
					&& !target.getUnit().getVillage().getOwner().equals(currentPlayer)){
				if(target.getUnit().myTile.inShootingDistance(game.getGameMap(),cm.getPotatoActor().getUnit().myTile)){
					System.out.println("Shoot tile "+target);
					target.getUnit().myVillage.removeWood(1);
					GameAction.FireCannonAction gameAction = new GameAction.FireCannonAction(target.getUnit().myTile);
					gameApp.client.sendActions(gameAction);
					cm.reset();
				}
				else System.out.println("Out of range!");
				cm.reset();
			}
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
			boolean tileHasVillage = tile.getVillage()!=null && tile.getVillage().getTile()== tile;
			String actionType = (tile.occupant!=null)?tile.occupant.getCurrentAction().name():"None";
			System.out.println("Clicked on "+tile + " owner:" + tile.owner+ " structure:"+tile.getStructure()+"\noccupant:"+tile.occupant+" action:"+actionType+"\nvillage:"+tile.getVillage()+" is on this tile "+tileHasVillage+"\nregion:"+tile.getRegion());
			previouslySelectedTile = cm.getTileActor();
		}
	}

	private void addGUIStuff() {
		// Top label
		Label turnName = new Label("", skin, "stats") {
			@Override
			public void act(float delta) {
				super.act(delta);
				String color = game.playerToPotatoColor(game.turnOf());
				if(color.equals("red")) {
					this.setColor(1, 0.5f, 0.5f, 1);
				}
				if(color.equals("yellow")) {
					this.setColor(1, 1, 0.5f, 1);
				}
				if(color.equals("purple")) {
					this.setColor(0.4f, 0.1f, 1, 1);
				}
				this.setText("  Round : " + game.getRoundsPlayed()/game.getPlayers().size() + "  --  Turn of : " + game.turnOf().getUsername());
			}
		};
		Label stats = new Label("", skin, "stats"){
			@Override
			public void act(float delta) {
				super.act(delta);
				if (currentVillage!=null && currentVillage.getType()!=null) {
					this.setText("  Village : " + currentVillage.getType() + ", " + 
								 "Gold : " + currentVillage.getGold() + ", " + 
								 "Logs : " + currentVillage.getWood());
				} else {
					this.setText("");
					
				}
			}
		};
		stats.setWidth(width);
		stats.setHeight(30);
		stats.setPosition(0, height - 60); //Hardcode FTW
		turnName.setHeight(30);
		turnName.setWidth(width);
		turnName.setPosition(0, height-30);
		this.addActor(stats);
		this.addActor(turnName);
		
		/********************* Buttons ******************************************/
		// End turn button
		endTurn = new TextButton("End Turn", skin, "red");
		endTurn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				GameAction.EndTurnAction endTurnAction = new GameAction.EndTurnAction();
				if(currentPlayer.equals(game.getPlayers().get(game.getPlayers().size() - 1))) {
					GameAction.GrowTreeAction growTreeAction = new GameAction.GrowTreeAction(game.getGameMap().getTreesToGrow());
					gameApp.client.sendActions(endTurnAction, growTreeAction);
				}
				else {
					gameApp.client.sendActions(endTurnAction);
				}
				System.out.println("current player: " + currentPlayer.getUsername());
				System.out.println("turn of: " + game.turnOf());
				cm.reset();
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
		TextButton upgradeVillage;
		TextButton hireVillager;
		TextButton buildWatchtower;
		
		/*
		 * Constructor for a villageMenuGroup, set to invisible when constructed
		 */
		public VillageMenuGroup() {
			upgradeVillage = new TextButton("Upgrade Village", skin, "default");
			hireVillager = new TextButton("Hire Villager", skin, "default");
			buildWatchtower = new TextButton("Build Watchtower", skin, "default");;
			this.addActor(upgradeVillage);
			this.addActor(hireVillager);
			this.addActor(buildWatchtower);
			
			float buttonHeight = upgradeVillage.getHeight();
			
			// Stack the buttons on top of each other
			buildWatchtower.setY(2*buttonHeight);
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
					Tile t = Cultivation.GAMEMANAGER.getGame().getVillagerSpawnPoint(myVillageActor.getVillage());
					System.out.println("hireVillager clicked. Spawn at: "+t);
					if(t!=null){
						GameAction.HireVillagerAction hireAction = new GameAction.HireVillagerAction(t);
//						hireAction.execute(game.GAMEMANAGER.getGame());
						gameApp.client.sendActions(hireAction);	
					}
				}
			});
			buildWatchtower.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					System.out.println("buildWatchtower clicked");
					cm.useSecondaryClick(true);
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
			System.out.println("Set village actor "+v + " "+ v.getStage()+ " parent:"+v.getParent());
			Vector2 villageScreen = v.getStage().stageToScreenCoordinates(villageStageCoord);
			Vector2 menuCoord = this.getStage().screenToStageCoordinates(villageScreen);
			this.setPosition(menuCoord.x - 100, menuCoord.y);	
		}		
		public void disableVillageButtonsAccordingly() {
			Village myVillage = myVillageActor.getVillage();
			VillageType myType = myVillageActor.getVillage().getType();
			if (myType==VillageType.Hovel || myVillage.getWood()<StructureType.Watchtower.getCost()) {
				buildWatchtower.setDisabled(true);
			} else {
				buildWatchtower.setDisabled(false);
			}
			if (myType==VillageType.Castle) {
				upgradeVillage.setDisabled(true);
			} else if (myType==VillageType.Hovel && myVillage.getWood()<VillageType.Town.getCost()) {
				upgradeVillage.setDisabled(true);
			} else if (myType==VillageType.Town && myVillage.getWood()<VillageType.Fort.getCost()) {
				upgradeVillage.setDisabled(true);
			} else if (myType==VillageType.Fort && myVillage.getWood()<VillageType.Castle.getCost()) {
				upgradeVillage.setDisabled(true);
			} else {
				upgradeVillage.setDisabled(false);
			}
		}
	}
	
	class PotatoMenuGroup extends Group {
		PotatoActor myPotato;
		Group topLevel;
		TextButton buildRoad;
		TextButton cultivateMeadow;
		TextButton upgradePotato;
		TextButton movePotato;
		TextButton fire;
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
			cultivateMeadow = new TextButton("Cultivate Meadow", skin, "default");
			fire = new TextButton("POW!", skin, "default");
			
			topLevel.addActor(upgradePotato);
			topLevel.addActor(buildRoad);
			topLevel.addActor(movePotato);
			topLevel.addActor(cultivateMeadow);
			topLevel.addActor(fire);
			
			float buttonHeight = upgradePotato.getHeight();
			
			// Stack the buttons on top of each other
			
			fire.setY(4*buttonHeight);
			cultivateMeadow.setY(3*buttonHeight);
			buildRoad.setY(2*buttonHeight);
			upgradePotato.setY(buttonHeight);
			
			// Add listeners to the buttons 
			upgradePotato.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					System.out.println("upgradePotato clicked");
					topLevel.setVisible(false);
					upgrades.setVisible(true);
				}
			});
			
			movePotato.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					topLevel.setVisible(false);
					directionMenu.setVisible(true);
				}
			});
			

			buildRoad.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					System.out.println("buildRoad clicked");
					GameAction.BuildRoadAction gameAction = new GameAction.BuildRoadAction(myPotato.getUnit().getTile());
					gameApp.client.sendActions(gameAction);
					cm.reset();
				}
			});

			cultivateMeadow.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					System.out.println("cultivate meadow clicked");
					GameAction.CultivateMeadowAction gameAction = new GameAction.CultivateMeadowAction(myPotato.getUnit().getTile());
					gameApp.client.sendActions(gameAction);
					cm.reset();
				}
			});
			
			fire.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					System.out.println("POW! clicked");
					cm.useSecondaryClick(true);
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
			
			cannoneer.addListener(new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					System.out.println("cannon clicked");
					GameAction.UpgradePotatoAction gameAction = new GameAction.UpgradePotatoAction(myPotato.getUnit(), UnitType.Cannon);
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
					button.setName("arrow-up");
					button.setY(verticalOffset + 20);
				}
				if(direction.equals(MapDirections.Down)) {
					button = new Button(skin, "arrow-down");
					button.setName("arrow-down");
					button.setY(-verticalOffset - 20);
				}
				if(direction.equals(MapDirections.LeftUp)) {
					button = new Button(skin, "arrow-leftUp");
					button.setName("arrow-leftUp");
					button.setY(verticalOffset);
					button.setX(-horizontalOffset);
				}
				if(direction.equals(MapDirections.LeftDown)) {
					button = new Button(skin, "arrow-leftDown");
					button.setName("arrow-leftDown");
					button.setY(-verticalOffset);
					button.setX(-horizontalOffset);
				}
				if(direction.equals(MapDirections.RightUp)) {
					button = new Button(skin, "arrow-rightUp");
					button.setName("arrow-rightUp");
					button.setY(verticalOffset);
					button.setX(horizontalOffset);
				}
				if(direction.equals(MapDirections.RightDown)) {
					button = new Button(skin, "arrow-rightDown");
					button.setName("arrow-rightDown");
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
			
			directionMenu.setX(15);
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
				buildRoad.setDisabled(false);
				cultivateMeadow.setDisabled(false);
				for(Actor a : directionMenu.getChildren()) {
					if(a instanceof Button) {
						Button aButton = (Button) a;
						aButton.setDisabled(false);
					}
				}
			}
		}

		/**
		 * Set the {@link PotatoActor} model to this menu. 
		 * Also sets position of the menu.
		 * @param p - The target PotatoActor
		 */
		public void setPotatoActor(PotatoActor p) {
			myPotato = p;
			Vector2 potatoStageCoord = p.localToStageCoordinates(new Vector2(p.getOriginX(), p.getOriginY()));
			Vector2 potatoScreen = p.getStage().stageToScreenCoordinates(potatoStageCoord);
			Vector2 menuCoord = this.getStage().screenToStageCoordinates(potatoScreen);
			this.setPosition(menuCoord.x, menuCoord.y);	
		}
		
		public void disableUnitButtonsAccordingly() {
			UnitType myType = myPotato.getUnit().myType;
			if(!myType.equals(UnitType.Peasant)) {
				buildRoad.setDisabled(true);
				cultivateMeadow.setDisabled(true);
			}
			
			VillageType myVillageType = myPotato.getUnit().getVillage().getType();
			//Check based on village type
			if (myVillageType==VillageType.Hovel) {
				soldier.setDisabled(true);
				knight.setDisabled(true);
				cannoneer.setDisabled(true);
			} else if (myVillageType==VillageType.Town) {
				knight.setDisabled(true);
				cannoneer.setDisabled(true);
			} else {
				knight.setDisabled(false);
				cannoneer.setDisabled(false);
			}
			//Check based on current unit type
			if(myType.equals(UnitType.Cannon)) {
				upgradePotato.setDisabled(true);
				fire.setVisible(true);
			}
			else if(myType.equals(UnitType.Knight)) {
				infantry.setDisabled(true);
				soldier.setDisabled(true);
				knight.setDisabled(true);
				fire.setVisible(false);
			}
			else if(myType.equals(UnitType.Soldier)) {
				infantry.setDisabled(true);
				soldier.setDisabled(true);
				fire.setVisible(false);
			}
			else if(myType.equals(UnitType.Infantry)) {
				infantry.setDisabled(true);
				fire.setVisible(false);
			}
			else if(myType.equals(UnitType.Peasant)){
				fire.setVisible(false);
			}
			
			// Disable directional buttons according to possibility of moving
			Tile p = myPotato.getUnit().getTile();
			MapCoordinates current = new MapCoordinates(p.x, p.y);
			Tile toUp = current.go(MapDirections.Up).getTile(game.getGameMap());
			Tile toDown = current.go(MapDirections.Down).getTile(game.getGameMap());
			Tile toLeftUp = current.go(MapDirections.LeftUp).getTile(game.getGameMap());
			Tile toRightUp = current.go(MapDirections.RightUp).getTile(game.getGameMap());
			Tile toLeftDown = current.go(MapDirections.LeftDown).getTile(game.getGameMap());
			Tile toRightDown = current.go(MapDirections.RightDown).getTile(game.getGameMap());
			
			if(!toUp.tryInvadeCheck(myPotato.getUnit())) {
				Button arrowUp = directionMenu.findActor("arrow-up");
				arrowUp.setDisabled(true);
			}
			if(!toDown.tryInvadeCheck(myPotato.getUnit())) {
				Button arrowDown = directionMenu.findActor("arrow-down");
				arrowDown.setDisabled(true);
			}
			if(!toLeftUp.tryInvadeCheck(myPotato.getUnit())) {
				Button arrowLeftUp = directionMenu.findActor("arrow-leftUp");
				arrowLeftUp.setDisabled(true);
			}
			if(!toRightUp.tryInvadeCheck(myPotato.getUnit())) {
				Button arrowRightUp = directionMenu.findActor("arrow-rightUp");
				arrowRightUp.setDisabled(true);
			}
			if(!toLeftDown.tryInvadeCheck(myPotato.getUnit())) {
				Button arrowLeftDown = directionMenu.findActor("arrow-leftDown");
				arrowLeftDown.setDisabled(true);
			}
			if(!toRightDown.tryInvadeCheck(myPotato.getUnit())) {
				Button arrowRightDown = directionMenu.findActor("arrow-rightDown");
				arrowRightDown.setDisabled(true);
			}


		}
	}
}