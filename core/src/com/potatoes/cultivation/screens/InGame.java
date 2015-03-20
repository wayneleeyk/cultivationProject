package com.potatoes.cultivation.screens;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.logic.CultivationGame;
import com.potatoes.cultivation.logic.GameAction;
import com.potatoes.cultivation.logic.GameMap;
import com.potatoes.cultivation.logic.LandType;
import com.potatoes.cultivation.logic.Region;
import com.potatoes.cultivation.logic.Tile;
import com.potatoes.cultivation.logic.Unit;
import com.potatoes.cultivation.logic.UnitType;
import com.potatoes.cultivation.logic.Village;
import com.potatoes.cultivation.logic.VillageType;
import com.potatoes.cultivation.networking.ActionBlockProtocol;
import com.potatoes.cultivation.networking.Protocol;
import com.potatoes.cultivation.networking.ProtocolHandler;

public class InGame extends ScreenAdapter {

	Cultivation game;
	SpriteBatch batch;
	TextureAtlas atlas;
	AtlasRegion hex_grass;
	AtlasRegion hex_sea;
	AtlasRegion hex_meadow;
	AtlasRegion hex_tree;
	AtlasRegion village_hovel;
	AtlasRegion village_town;
	AtlasRegion village_fort;
	float cameraWidth = 800, cameraHeight = 600;
	Camera camera = new OrthographicCamera(cameraWidth, cameraHeight);
	Stage gameStage = new Stage();
	HUD hud;
	GameMap gameMap;
	Set<Tile> tiles = new HashSet<>();
	List<Color> colorByIndex;
	TileGroup[][] tileGroups;
	List<PotatoImage> unitImages;

	ProtocolHandler<GameAction[]> updates;
	
	public InGame(final Cultivation pGame, CultivationGame aGameRound) {
		this.game = pGame;
		this.batch = game.batch;
		this.hud = new HUD(this.game, this.batch, this.gameMap,
				this.game.player);
		this.atlas = game.manager.get("ingame.atlas", TextureAtlas.class);
		this.gameMap = aGameRound.getGameMap();
		// Assigns a unique color to each player to colour the tiles that they
		// own
		colorByIndex = new ArrayList<Color>();
		colorByIndex.add(new Color(1, 0, 0, 0.99f));
		colorByIndex.add(new Color(0, 1, 1, 0.7f));
		colorByIndex.add(new Color(1, 1, 1, 0.99f));

		hex_grass = atlas.findRegion("grass");
		hex_sea = atlas.findRegion("tile_sea");
		hex_meadow = atlas.findRegion("tile_meadow");
		hex_tree = atlas.findRegion("tile_tree");
		village_hovel = atlas.findRegion("village-hovel");
		village_town = atlas.findRegion("village-town");
		village_fort = atlas.findRegion("village-fort");
		camera.lookAt(0, 0, 0);
		
		int width = gameMap.getMap().length;
		int height = gameMap.getMap()[0].length;
		
		tileGroups = new TileGroup[width][height];
		unitImages = new ArrayList<PotatoImage>();

		// load map
		Tile[][] map = gameMap.getMap();
		game.GAMEMANAGER.getGameMap().setTGMap(tileGroups);

		int originX = hex_grass.getRegionWidth() / 2, originY = hex_grass
				.getRegionHeight() / 2;
		Stack<Actor> stackToDraw = new Stack<Actor>();

		// for each tile create an Image actor

		final Clicked click = new Clicked();
		final Region someRegion = gameMap.getRegions(game.player).iterator().next();

		Tile occupying = someRegion.getTiles().iterator().next();
//		Unit u = new Unit(occupying);
//		occupying.occupant = u;
//		occupying.updateOwner(game.player);
//		u.myVillage = someRegion.getVillage();
//		u.myType = UnitType.Peasant;
		gameMap.PrintPlayersStuff(game.player);
		for (int y = 0; y < width; y++) {
			for (int x = 0; x < height; x++) {
				AtlasRegion hexToDraw;
				if (map[x][y].getLandType() == LandType.Sea) {
					hexToDraw = hex_sea;
				} else if (map[x][y].getLandType() == LandType.Meadow) {
					hexToDraw = hex_meadow;
				} else if (map[x][y].getLandType() == LandType.Tree) {
					hexToDraw = hex_tree;
				} else {
					hexToDraw = hex_grass;
				}
				final TileGroup tileGroup = new TileGroup(map[x][y]);
				tileGroups[x][y] =tileGroup;
				tileGroup.setOrigin(originX, originY);
				tileGroup.setPosition(x * 308 * 0.75f + 150, x * 88 / 2.0f
						+ (y * 88));
				final Image tile = new Image(hexToDraw);
				tileGroup.addActor(tile);
				tileGroup.setTileImage(tile);
				tileGroup.addClick(click);
				if (map[x][y].containsVillage()) {
					// Draw village on top of tile
					final VillageImage village;
					Village v = map[x][y].getVillage();
					if (v.getType() == VillageType.Hovel) {
						village = new VillageImage(village_hovel, v);
					} else if (v.getType() == VillageType.Town) {
						village = new VillageImage(village_town, v);
					} else {
						village = new VillageImage(village_fort, v);
					}
					village.setPosition(200, 44, 0);
					village.setOrigin(originX, originY);
					village.addListener(new ClickListener() {
						@Override
						public void clicked(InputEvent event, float x, float y) {
							Vector2 screenCoord = village
									.getStage()
									.stageToScreenCoordinates(
											village.localToStageCoordinates(new Vector2(
													x, y)));
							hud.villageClicked(village, screenCoord.x, screenCoord.y);
						}
					});
					tileGroup.addActor(village);
					tileGroup.setVillageImage(village);
				}
				// System.out.println("Number of players:"+aGameRound.getPlayers().size());

				int playerNum = aGameRound.getPlayers().indexOf(
						map[x][y].getPlayer());
				if (playerNum != -1) {
					// If tile is owned by a player, tint it the corresponding
					// colour
					tile.setColor(colorByIndex.get(playerNum));
				}
				// tile.setPosition(x*tile.getWidth()*0.75f,
				// x*tile.getHeight()/2.0f + (y*tile.getHeight()));
				tileGroup
						.setPosition(x * 308 * 0.75f, x * 88 / 2.0f + (y * 88));
				// tile.setPosition(x*308*0.75f, x*88/2.0f + (y*88));
				// tile.setOrigin(originX, originY);
				final Tile clickedTile = map[x][y];
				final int finalX = x, finalY = y;
				tile.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						System.out.println("clicked on tile " + finalX + " " + finalY);
						hud.tileClicked(clickedTile);
						if (click.isClicked()) {
							boolean good = true;
							if (!gameMap.getNeighbouringTiles(
									click.potato.potato.myTile).contains(
									clickedTile)) {
								good = false;
							} else if (clickedTile.getUnit() != null
									&& clickedTile
											.getUnit()
											.getType()
											.compareTo(
													click.potato.potato
															.getType()) > 1) {
								good = false;
							} else if (clickedTile.getLandType().equals(
									LandType.Sea)) {
								good = false;
							}
							if (good
									&& clickedTile.getLandType().equals(
											LandType.Tree)) {
								clickedTile.updateLandType(LandType.Grass);
								click.potato.potato.getVillage().addWood(1);
								tile.setDrawable(new TextureRegionDrawable(
										hex_grass));
							}
							if (good && clickedTile.getPlayer() == null) {
								clickedTile.updateOwner(click.potato.potato.getVillage().getOwner());
								tile.setColor(colorByIndex.get(0));
								someRegion.addTile(clickedTile);
								clickedTile.updateOwner(someRegion.getOwner());
							}
							if (good) {
								tileGroups[finalX][finalY].setUnit(click.potato);
								click.potato.setPosition(50, 40);
								Tile start = click.potato.potato.getTile();
								click.potato.potato
										.updateTileLocation(clickedTile);
								tile.getParent().addActor(click.potato);
								game.client.sendActions(new GameAction.MoveUnitAction(start, clickedTile));
							}
							click.reset();
						}

					}
				});
				Unit potatosan = map[x][y].getUnit();
				if (potatosan != null && playerNum != -1) {
					String colour = PotatoColours.values()[playerNum].colourName;
					UnitType unitType = potatosan.getType();
					String name = "";
					if (unitType.equals(UnitType.Peasant)) {
						name = "potato_" + colour.toLowerCase();
					} else {
						name = "potato_" + unitType.name().toLowerCase()
								+ colour.toLowerCase();
					}
					System.out.println("Potato name " + name);

					PotatoImage potatoImage = new PotatoImage(atlas.findRegion(name),
							potatosan, click, colour.toLowerCase());
					tileGroup.addActor(potatoImage);
					tileGroups[x][y].setUnit(potatoImage);
					unitImages.add(potatoImage);
					potatoImage.setPosition(50, 40);
				}
				stackToDraw.push(tileGroup);
			}
		}
		while (stackToDraw.size() > 0) {
			gameStage.addActor(stackToDraw.pop());
		}
		// Stage
		InputMultiplexer inputs = new InputMultiplexer(hud, gameStage);
		Gdx.input.setInputProcessor(inputs);
		gameStage.addListener(new DragListener() {
			float prevX, prevY;

			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				super.touchDragged(event, x, y, pointer);
				if (this.isDragging()) {
					gameStage.getCamera().position.add(prevX - x, prevY - y, 0);
				}
			}

			@Override
			public void dragStart(InputEvent event, float x, float y,
					int pointer) {
				super.dragStart(event, x, y, pointer);
				prevX = x;
				prevY = y;
			}
		});
		gameStage.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.LEFT)
					gameStage.getCamera().position.add(-50, 0, 0);
				if (keycode == Keys.DOWN)
					gameStage.getCamera().position.add(0, -50, 0);
				if (keycode == Keys.RIGHT)
					gameStage.getCamera().position.add(50, 0, 0);
				if (keycode == Keys.UP)
					gameStage.getCamera().position.add(0, 50, 0);
				return true;
			}

		});
		
		
		////////////////////////////////////////////
		/// Handlers
		updates = new ProtocolHandler<GameAction[]>(){

			@Override
			public void handle(Protocol p) {
				if(p instanceof ActionBlockProtocol) {
					result = ((ActionBlockProtocol) p).getActions();
				}
			}
			
		};
		game.client.insertHandler(updates);
		
		
		/////////////////////////////////////////////
	}
	
	public enum PotatoColours {
		PURPLE("purple"), RED("red"), YELLOW("yellow");
		final public String colourName;

		private PotatoColours(String colourName) {
			this.colourName = colourName;
		}
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		for (int i=0; i<tileGroups.length; i++) {
			for (int j=0; j<tileGroups[0].length; j++) {
				tileGroups[i][j].updateTileGroup();
			}
		}
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// check for updates
		checkUpdates();
		
		// update camera
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		gameStage.draw();
		batch.end();

		hud.draw();
		


//		for (PotatoImage i : unitImages) {
//			i.UpdateImage();
//		}
	}
	
	private void checkUpdates() {
		if(updates.isAvailable()) {
			System.out.println("new updates!");
			for(GameAction gameAction : updates.getResult()) {
				gameAction.execute(game.GAMEMANAGER.getGame());
			}
			updates.reset();
		}
	}
	
	public class TileGroup extends Group {
		Tile tile;
		PotatoImage potatoImage;
		Image tileImage;
		VillageImage villageImage;
		LandType currentLandType;
		VillageType currentVillageType;
		Clicked click;
		
		public TileGroup(Tile tile) {
			super();
			this.tile = tile;
			this.currentLandType = tile.getLandType();
		}
		public void addClick(Clicked click) {
			this.click = click;
		}
		public void setVillageImage(VillageImage image) {
			this.villageImage = image;
			this.currentVillageType = image.getVillage().getType();
		}
		public void setTileImage(Image image) {
			this.tileImage = image;
		}
		public Image getTileImage() {
			return this.tileImage;
		}
		public void setUnit(PotatoImage image) {
			this.potatoImage = image;
			this.addActor(image);
			image.toFront();
		}
		public PotatoImage getUnit() {
			return this.potatoImage;
		}
		public void updateTileGroup() {
			LandType latestLandType = tile.getLandType();
			if (latestLandType != currentLandType) {
				//Update tile image's texture to latest land type
				currentLandType = latestLandType;
				TextureRegionDrawable drawable; 
				if (latestLandType==LandType.Grass) {
					drawable = new TextureRegionDrawable(hex_grass);
				} else if (latestLandType==LandType.Meadow) {
					drawable = new TextureRegionDrawable(hex_meadow);
				} else {// (latestLandType==LandType.Tree) 
					drawable = new TextureRegionDrawable(hex_tree);
				}
				tileImage.setDrawable(drawable);
			}
			if (villageImage!=null) {
				VillageType latestVillageType = villageImage.getVillage().getType();
				if (latestVillageType!= currentVillageType) {
					//Update village image's texture to latest village type
					currentVillageType = latestVillageType;
					TextureRegionDrawable drawable; 
					if (latestVillageType == VillageType.Hovel) {
						drawable = new TextureRegionDrawable(village_hovel);
					} else if (latestVillageType == VillageType.Town) {
						drawable = new TextureRegionDrawable(village_town);
					} else { //(latestVillageType == VillageType.Fort)
						drawable = new TextureRegionDrawable(village_fort);
					}
					villageImage.setDrawable(drawable);
				}
			}
			if (tile.getUnit()!=null && potatoImage!=null) {
				potatoImage.UpdateImage();
//				potatoImage.setPosition(this.getX()-5, this.getY()-5);
//				potatoImage.setPosition(0,0);
				potatoImage.toFront();
			} else if (tile.getUnit()!=null && potatoImage==null) {
				int playerNum = game.GAMEMANAGER.getGame().getPlayers().indexOf(tile.getPlayer());
				String colour = PotatoColours.values()[playerNum].colourName;
				PotatoImage poImg = new PotatoImage(atlas.findRegion("potato_"+colour), tile.getUnit(), click, colour);
				this.addActor(poImg);
				System.out.println("tile group position " + this.getX() + ","+this.getY());
				Vector2 coord = this.getStage().stageToScreenCoordinates(new Vector2(this.getX(), this.getY()));
				poImg.setPosition(this.getX(), this.getY());
				System.out.println("GetX GetY:" + this.getX()+", " + this.getY());
				System.out.println("Stage to screen coord: " + coord.x + "," + coord.y);
				poImg.toFront();
				setUnit(poImg);
				poImg.setPosition(50, 40);
			} else if (tile.getUnit()==null) {
				//remove reference to potato image, it has moved to another tile or got mashed potato
				potatoImage = null;
			}
		}
	}
	class VillageImage extends Image {
		private Village village;

		public VillageImage(AtlasRegion region, Village village) {
			super(region);
			this.village = village;
		}

		public Village getVillage() {
			return village;
		}
		//Below method is not used anymore
		public void updateVillageSprite() {
			TextureRegionDrawable drawable;
			if (village.getType() == VillageType.Hovel) {
				drawable = new TextureRegionDrawable(village_hovel);
			} else if (village.getType() == VillageType.Town) {
				drawable = new TextureRegionDrawable(village_town);
			} else {
				drawable = new TextureRegionDrawable(village_fort);
			}
			this.setDrawable(drawable);
		}
	}

	class PotatoImage extends Image {
		Unit potato;
		Clicked click;
		PotatoImage reference;
		UnitType currentUnitType;
		String colour;

		public PotatoImage(AtlasRegion region, final Unit potato,
				final Clicked click, String colour) {
			super(region);
			this.potato = potato;
			this.click = click;
			this.reference = this;
			this.currentUnitType = potato.getType();
			this.colour = colour;
			this.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					System.out.println("Tickled on potato " + potato.getTile());
					click.clickedOn(reference);
					
				}
			});
		}
		public void UpdateImage() {
			UnitType latestUnitType = potato.getType();
			if (latestUnitType != currentUnitType) {
				currentUnitType = latestUnitType;
				TextureRegionDrawable drawable;
				String name;
				if (latestUnitType == UnitType.Peasant) {
					name = "potato_"+colour;
				} else if (latestUnitType == UnitType.Infantry) {
					name = "potato_"+colour+"_infantry";
				} else if (latestUnitType == UnitType.Soldier) {
					name = "potato_"+colour+"_soldier";
				} else {//(latestUnitType == UnitType.Knight) {
					name = "potato_"+colour+"_knight";
				}
				drawable = new TextureRegionDrawable(atlas.findRegion(name));
				reference.setDrawable(drawable);
			}
		}
	}

	class Clicked {
		PotatoImage potato = null;
		boolean isClicked = false;

		public void clickedOn(PotatoImage potato) {
			this.potato = potato;
			this.isClicked = true;
		}

		public boolean isClicked() {
			return this.isClicked;
		}

		public Unit potato() {
			return potato.potato;
		}

		public PotatoImage actor() {
			return potato;
		}

		public void reset() {
			this.potato = null;
			this.isClicked = false;
		}
	}
}
