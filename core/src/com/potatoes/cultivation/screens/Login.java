package com.potatoes.cultivation.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class Login extends ScreenAdapter {
	Stage stage;
	Game game;
	Table table;
	
	public Login(final Game pGame) {
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		game = pGame;
		
		
		Label.LabelStyle titleStyle = new Label.LabelStyle(new BitmapFont(), Color.BLACK);
		Label title = new Label("Cultivation", titleStyle);
		title.setPosition((Gdx.graphics.getWidth() - title.getWidth()) / 2.0f, 500);
		
		table = new Table();
		table.debug();
		table.setPosition((Gdx.graphics.getWidth() - table.getWidth()) / 2.0f, 300);
		table.add(new Label("Username:", titleStyle));
		TextField.TextFieldStyle textfieldStyle = new TextField.TextFieldStyle(new BitmapFont(), Color.BLUE, null, null, null);
		table.add(new TextField("Username", textfieldStyle));
		table.row();
		table.add(new Label("Password:", titleStyle));
		final TextField passwordField = new TextField("", textfieldStyle);
		passwordField.setPasswordMode(true);
		passwordField.setPasswordCharacter('*');
		table.add(passwordField);
		table.row();
		TextButton.TextButtonStyle textbuttonStyle = new TextButton.TextButtonStyle(null, null, null, new BitmapFont());
		textbuttonStyle.fontColor = Color.GREEN;
		TextButton playButton = new TextButton("Play!", textbuttonStyle);
		playButton.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				System.out.println("Clicked");
				System.out.println(passwordField.getText());
			}
		});
		table.add(playButton).colspan(2).center();
		
		
		stage.addActor(title);
		stage.addActor(table);
				
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();
	}
}