package com.potatoes.cultivation.screens;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.logic.CultivationGame;
import com.potatoes.cultivation.logic.Player;
import com.potatoes.cultivation.networking.GameDataProtocol;
import com.potatoes.cultivation.networking.GetARoomProtocol;
import com.potatoes.cultivation.networking.LoadProtocol;
import com.potatoes.cultivation.networking.Protocol;
import com.potatoes.cultivation.networking.ProtocolHandler;

public class GameRoom extends ScreenAdapter {
	int roomNumber;
	Cultivation game;
	public Set<Player> playersInRoom;
	Skin skin;
	
	Stage stage;
	Label listOfPlayers;
	private float timer = 0;
	
	ProtocolHandler<Set<Player>> getRoomHandler;
	ProtocolHandler<CultivationGame> receivingGame;
	ProtocolHandler<List<CultivationGame>> listOfGamesToLoadHandler;
	
	public GameRoom(final Cultivation pGame, int room) {
		roomNumber = room;
		game = pGame;
		skin = game.skin;
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
		getRoomHandler = new ProtocolHandler<Set<Player>>() {
			@Override
			public void handle(Protocol p) {
				if(p instanceof GetARoomProtocol) {
					result = ((GetARoomProtocol) p).getList();
				}
			}
		};
		
		game.client.insertHandler(getRoomHandler);

		
		
		game.client.getPlayersForRoom(room);
		playersInRoom = getRoomHandler.getResult();
		
		// Setting up the room		
		final Table table = new Table();
		table.setFillParent(true);
		
		listOfPlayers = new Label(getPlayerNames(), skin, "white");
		table.add(listOfPlayers).expand().left().top();
		
		TextButton start = new TextButton("Start Game!", skin, "default");
		start.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				System.out.println("Pressed start");
				System.out.println("Made game");
				List<Player> players = new LinkedList<>();
				players.addAll(playersInRoom);
				System.out.println("added players");
				game.GAMEMANAGER.newGame(players);
				System.out.println("Generated new game");
				game.client.clearAllHandlers();
				game.client.startGame(game.GAMEMANAGER.getGame());
				System.out.println("sent game");
				game.setScreen(new InGame2(pGame, game.GAMEMANAGER.getGame()));
			}
		});
		table.add(start).width(200).expand().center();
		
		final TextButton load = new TextButton("Load Game!", skin, "default");
		load.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				System.out.println("Pressed Load");
				System.out.println("Loaded game");
				List<Player> players = new LinkedList<>();
				players.addAll(playersInRoom);
				game.client.load(players);
				System.out.println("Players in room :"+players);
				System.out.println("sent request");
			}
		});
		table.add(load).width(200).expand().center();
		
//		table.setDebug(true);
		
		stage.addActor(table);
		receivingGame = new ProtocolHandler<CultivationGame>() {
			@Override
			public void handle(Protocol p) {
				if(p instanceof GameDataProtocol){
					System.out.println("Handling a game protocol");
					result = ((GameDataProtocol) p).getGame();
					game.GAMEMANAGER.setGame(result);
					System.out.println(game.GAMEMANAGER.getGame().getGameMap());
					game.client.clearAllHandlers();
//					game.setScreen(new InGame(game, game.GAMEMANAGER.getGame()));
				}
			}
		};
		
		game.client.insertHandler(receivingGame);
		
		listOfGamesToLoadHandler = new ProtocolHandler<List<CultivationGame>>() {
			@Override
			public void handle(Protocol p) {
				if(p instanceof LoadProtocol){
					System.out.println("Received list of saved games");
					load.setVisible(false);
					result = new LinkedList<>();
					for (GameDataProtocol data : ((LoadProtocol) p).gameData()) {
						result.add(data.getGame());
					}
					Table savedGamesGroup = new Table();
					ScrollPane scrollPane = new ScrollPane(savedGamesGroup);
					table.getCells().removeIndex(table.getCells().size -1);
					table.add(scrollPane).left().top().expandX().expandY();
					savedGamesGroup.left().top();
					for (CultivationGame cgame : result) {
						final CultivationGame toLoad = cgame;
						System.out.println("Adding load option "+"Rounds:"+cgame.getRoundsPlayed()+"  Turn:"+cgame.turnOf());
						TextButton button = new TextButton("Rounds:"+cgame.getRoundsPlayed()+"\nTurn:"+cgame.turnOf(),skin, "default-mini");
						savedGamesGroup.add(button).left().row();
						button.addListener(new ChangeListener(){
							@Override
							public void changed(ChangeEvent event, Actor actor) {
								game.client.clearAllHandlers();
								game.client.startGame(toLoad);
								game.GAMEMANAGER.setGame(toLoad);
								game.setScreen(new InGame2(pGame, toLoad));
							}
						});
					}
//					savedGamesGroup.setDebug(true);
				}
			}
		};
		game.client.insertHandler(listOfGamesToLoadHandler);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		updateRoomInfo();
		receivedGame();
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void dispose() {
		super.dispose();
	}
	
	private String getPlayerNames() {
		String list = "List of players:\n";
		if(playersInRoom != null) {
			for(Player p : playersInRoom) {
				list += p.toString();
				list += "\n";
			}
		}
		return list;
	}
	
	private void updateRoomInfo() {
		if(getRoomHandler.isAvailable()) {
			playersInRoom = getRoomHandler.getResult();
			listOfPlayers.setText(getPlayerNames());
		}
	}
	
	private void receivedGame() {
		if(receivingGame.isAvailable()) {
			game.setScreen(new InGame2(game, game.GAMEMANAGER.getGame()));
		}
	}
	
//	public void updateRoomInfo(Collection<Player> players){
//		playersInRoom.clear();
//		playersInRoom.addAll(players);
//		listOfPlayers.setText(getPlayerNames());
//	}
}