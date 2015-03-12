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
import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.logic.CultivationGame;
import com.potatoes.cultivation.logic.GameMap;
import com.potatoes.cultivation.logic.LandType;
import com.potatoes.cultivation.logic.Region;
import com.potatoes.cultivation.logic.Tile;
import com.potatoes.cultivation.logic.Unit;
import com.potatoes.cultivation.logic.UnitType;
import com.potatoes.cultivation.logic.Village;
import com.potatoes.cultivation.logic.VillageType;

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

		Tile[][] map = gameMap.getMap();

		// load map
		int width = gameMap.getMap().length;
		int height = gameMap.getMap()[0].length;

		int originX = hex_grass.getRegionWidth() / 2, originY = hex_grass
				.getRegionHeight() / 2;
		Stack<Actor> stackToDraw = new Stack<Actor>();

		// for each tile create an Image actor
		Image[][] tiles = new Image[width][height];

		final Clicked click = new Clicked();
		final Region someRegion = gameMap.getRegions(game.player).iterator().next();

		Tile occupying = someRegion.getTiles().iterator().next();
		Unit u = new Unit(occupying);
		occupying.occupant = u;
		occupying.owner = game.player;
		u.myVillage = someRegion.getVillage();
		u.myType = UnitType.Peasant;

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
				final Group tileGroup = new Group();
				tileGroup.setOrigin(originX, originY);
				tileGroup.setPosition(x * 308 * 0.75f + 150, x * 88 / 2.0f
						+ (y * 88));
				final Image tile = new Image(hexToDraw);
				tileGroup.addActor(tile);
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
							hud.villageClicked(village, screenCoord.x,
									screenCoord.y);
						}
					});
					tileGroup.addActor(village);
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
				final int X = x, Y = y;
				tile.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						System.out.println("clicked on tile " + X + " " + Y);
						
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
							if (good && clickedTile.owner == null) {
								clickedTile.owner = click.potato.potato
										.getVillage().getOwner();
								someRegion.addTile(clickedTile);
								tile.setColor(colorByIndex.get(0));
							}
							if (good) {
								click.potato.potato
										.updateTileLocation(clickedTile);
								tile.getParent().addActor(click.potato);
							}
							click.reset();
						}
						hud.update(clickedTile);
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

					Actor potatoImage = new PotatoImage(atlas.findRegion(name),
							potatosan, click);
					tileGroup.addActor(potatoImage);
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
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// update camera
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		gameStage.draw();
		batch.end();

		hud.draw();
	}

	class VillageImage extends Image {
		public Village village;

		public VillageImage(AtlasRegion region, Village village) {
			super(region);
			this.village = village;
		}

		public Village getVillage() {
			return village;
		}

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

		public PotatoImage(AtlasRegion region, final Unit potato,
				final Clicked click) {
			super(region);
			this.potato = potato;
			this.click = click;
			this.reference = this;
			this.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					System.out.println("Tickled on potato " + potato.getTile());
					click.clickedOn(reference);
				}
			});
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
