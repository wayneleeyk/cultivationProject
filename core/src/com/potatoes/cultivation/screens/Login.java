package com.potatoes.cultivation.screens;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.potatoes.cultivation.Cultivation;

public class Login extends ScreenAdapter {
	Stage stage;
	Cultivation game;
	Table table;
	
	SpriteBatch batch;
	Texture background;
	Texture title;
	
	TextField usernameField;
	TextField passwordField;
	
	public Login(Cultivation pGame) {
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		game = pGame;
		batch = pGame.batch;
		
		BitmapFont pixFont = game.manager.get("pixFont.fnt");
				
		table = new Table();
		table.setPosition((Gdx.graphics.getWidth() - table.getWidth()) / 2.0f, 300);
		TextField.TextFieldStyle textfieldStyle = new TextField.TextFieldStyle(pixFont, Color.BLUE, null, null, null);
		
		usernameField = new TextField("", textfieldStyle); 
		usernameField.setMessageText("username");
		usernameField.setMaxLength(16);
		
		table.add(usernameField).left();
		table.row();
		
		passwordField = new TextField("", textfieldStyle);
		passwordField.setPasswordMode(true);
		passwordField.setPasswordCharacter('*');
		passwordField.setMessageText("password");
		passwordField.setMaxLength(16);
		
		table.add(passwordField).left();
		table.row();
		
		TextButton.TextButtonStyle textbuttonStyle = new TextButton.TextButtonStyle(null, null, null, pixFont);
		textbuttonStyle.fontColor = Color.GREEN;
		TextButton playButton = new TextButton("Play!", textbuttonStyle);
		playButton.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				System.out.println("Username: " + usernameField.getText());
				System.out.println("Password: " + passwordField.getText());
			}
		});
		table.add(playButton).center();
		
		
		stage.addActor(table);
				
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Draw background and title
		batch.begin();
		batch.enableBlending();
		batch.draw(background, 0, 0);
		batch.draw(title, (Gdx.graphics.getWidth() - title.getWidth()) / 2, 400);
		batch.end();
		
		// Draw stage
		stage.act(delta);
		stage.draw();
	}
	
	@Override
	public void show() {
		title = game.manager.get("gameTitle.png");
		background = game.manager.get("landscape.png");
	}
}