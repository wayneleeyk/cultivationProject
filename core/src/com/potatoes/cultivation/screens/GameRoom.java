package com.potatoes.cultivation.screens;

import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.logic.Player;

public class GameRoom extends ScreenAdapter {
	int roomNumber;
	Cultivation game;
	public Set<Player> playersInRoom;
	Skin skin;
	
	Stage stage;
	Label listOfPlayers;
	private float timer = 0;
	
	public GameRoom(Cultivation pGame, int room) {
		roomNumber = room;
		game = pGame;
		skin = game.skin;
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
		playersInRoom = game.client.getPlayersForRoom(room);
		
		// Setting up the room
		
		Table table = new Table();
		table.setFillParent(true);
		
		listOfPlayers = new Label(getPlayerNames(), skin, "white");
		table.add(listOfPlayers).expand().left().top();
		
		TextButton start = new TextButton("Start Game!", skin, "default");
		table.add(start).width(200).expand().center();
		
		table.setDebug(true);
		
		stage.addActor(table);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		updateRoomInfo(delta);
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
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
	
	private void updateRoomInfo(float delta) {
		timer += delta;
		// Update every 5 seconds
		if(timer > 5) {
			game.client.updateRoomInfo(roomNumber);
			timer = 0;
			listOfPlayers.setText(getPlayerNames());
		}
	}
}