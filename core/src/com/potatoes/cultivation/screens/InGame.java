package com.potatoes.cultivation.screens;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.logic.CultivationGame;
import com.potatoes.cultivation.logic.GameMap;
import com.potatoes.cultivation.logic.LandType;
import com.potatoes.cultivation.logic.Player;
import com.potatoes.cultivation.logic.Tile;
import com.potatoes.cultivation.logic.Unit;
import com.potatoes.cultivation.logic.UnitType;

public class InGame extends ScreenAdapter{

	Cultivation game;
	SpriteBatch batch;
	TextureAtlas atlas;
	AtlasRegion hex_grass;
	AtlasRegion hex_sea;
	AtlasRegion hex_meadow;
	AtlasRegion hex_tree;
	AtlasRegion village_hovel;
	float cameraWidth=800, cameraHeight=600;
	Camera camera = new OrthographicCamera(cameraWidth,cameraHeight);
	Stage gameStage = new Stage();
	HUD hud;	
	GameMap gameMap;
	Set<Tile> tiles = new HashSet<>();
	List<Color> colorByIndex;
	
	public InGame(final Cultivation pGame, CultivationGame aGameRound) {
		this.game = pGame;
		this.batch = game.batch;
		this.hud = new HUD(this.batch, this.gameMap, this.game.player);
		this.atlas = game.manager.get("ingame.atlas", TextureAtlas.class);
		this.gameMap = aGameRound.getGameMap();
		
		//Assigns a unique color to each player to colour the tiles that they own
		colorByIndex = new ArrayList<Color>();
		colorByIndex.add(new Color(1,0,0,0.99f));
		colorByIndex.add(new Color(0,1,1,0.7f));
		colorByIndex.add(new Color(1,1,1,0.99f));
		
		
		
		hex_grass = atlas.findRegion("grass");
		hex_sea = atlas.findRegion("tile_sea");
		hex_meadow = atlas.findRegion("tile_meadow");
		hex_tree = atlas.findRegion("tile_tree");
		village_hovel = atlas.findRegion("village-hovel");
		camera.lookAt(0, 0, 0);

		Tile[][] map = gameMap.getMap();
		
		// load map
		int width = gameMap.getMap().length;
		int height = gameMap.getMap()[0].length;
		
		int originX = hex_grass.getRegionWidth()/2, originY = hex_grass.getRegionHeight()/2;
		Stack<Actor> stackToDraw = new Stack<Actor>();	
		
		// for each tile create an Image actor 
		Image[][] tiles = new Image[width][height];
		
		Unit u = new Unit(1, 1);
		Unit u2 = new Unit(3, 3);
		map[1][1].occupant = u;
		map[3][3].occupant = u2;
		map[1][1].owner = game.player;
		map[3][3].owner = game.player;
		gameMap.PrintPlayersStuff(game.player);
		for (int y=0; y<width; y++) {
			for (int x=0;x<height;x++) {
//				//Draw village on top of tile 
//				if (map[x][y].containsVillage()) {
//					System.out.println("Found village");
//					final Image village = new Image(village_hovel);
//					village.setPosition(x*308*0.75f+150, x*88/2.0f + (y*88));
//					village.setOrigin(originX, originY);
//					stackToDraw.push(village);
//				}
	
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
				final Image tile = new Image(hexToDraw);
				tileGroup.addActor(tile);
				if(map[x][y].containsVillage()) {
					Image village = new Image(village_hovel);
					tileGroup.addActor(village);
					village.toFront();
					village.setPosition(150, 44);
				}
//				System.out.println("Number of players:"+aGameRound.getPlayers().size());

				int playerNum = aGameRound.getPlayers().indexOf(map[x][y].getPlayer());			
				if (playerNum != -1) {
					//If tile is owned by a player, tint it the corresponding colour
					tile.setColor(colorByIndex.get(playerNum));
				}
//				tile.setPosition(x*tile.getWidth()*0.75f, x*tile.getHeight()/2.0f + (y*tile.getHeight()));
				tileGroup.setPosition(x*308*0.75f, x*88/2.0f + (y*88));
//				tile.setPosition(x*308*0.75f, x*88/2.0f + (y*88));
//				tile.setOrigin(originX, originY);
				final Tile clickedTile = map[x][y];
				tile.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						hud.tileClicked(clickedTile);
					}
				});
				
				Unit potatosan = map[x][y].getUnit();
//				Image potatoImage = null;
				if(potatosan!=null && playerNum != -1){
					String colour = PotatoColours.values()[playerNum].colourName;
					UnitType unitType = potatosan.getType();
					String name = "";
					if(unitType.equals(UnitType.Peasant)){
						name = "potato_"+colour.toLowerCase();
					}
					else{
						name = "potato_"+unitType.name().toLowerCase()+colour.toLowerCase();
					}
					System.out.println("Potato name " +name);
					
					Actor potatoImage = new PotatoImage(atlas.findRegion(name));
					tileGroup.addActor(potatoImage );
					potatoImage.setPosition(50,40);
//					potatoImage.setPosition(x*tile.getWidth()*0.75f, x*tile.getHeight()/2.0f + (y*tile.getHeight()));
//					potatoImage.setPosition(x*308*0.75f, x*88/2.0f + (y*88));
					
//					potatoImage.setOrigin(originX, originY);
				}
//				if(potatoImage != null) tileGroup.addActor(potatoImage);
//				tile.addListener(new EventListener() {
//					@Override
//					public boolean handle(Event event) {
//						event.getListenerActor().setColor(1, 0, 0, 0.5f);
//						return false;
//					}
//				});
//				stackToDraw.push(tile);
				stackToDraw.push(tileGroup);
			}
		}
		while (stackToDraw.size()>0) {
			gameStage.addActor(stackToDraw.pop());
		}
		// Stage
		Gdx.input.setInputProcessor(gameStage);
		gameStage.addListener(new DragListener(){
			float prevX , prevY;
			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				super.touchDragged(event, x, y, pointer);
				if(this.isDragging()){
					gameStage.getCamera().position.add(prevX-x, prevY-y,0);
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
	}
	
	public enum PotatoColours{
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
	
	class PotatoImage extends Image{
		public PotatoImage(AtlasRegion region) {
			super(region);
			this.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y) {
					System.out.println("potatoman is tickled");
				}
			});
		}
	}
}
