package com.potatoes.cultivation.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
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
	
	
	public Login(final Cultivation pGame) {
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		game = pGame;
		batch = pGame.batch;
		
		BitmapFont pixFont = game.manager.get("pixFont.fnt");
				
		table = new Table();
		table.setPosition((Gdx.graphics.getWidth() - table.getWidth()) / 2.0f, 250);
		TextField.TextFieldStyle textfieldStyle = new TextField.TextFieldStyle(pixFont, Color.BLUE, null, null, null);
		
		usernameField = new TextField("", textfieldStyle); 
		usernameField.setMessageText("username");
		usernameField.setMaxLength(16);
		
		table.add(usernameField).left().pad(10, 0, 10, 0).width(300);
		table.row();
		
		passwordField = new TextField("", textfieldStyle);
		passwordField.setPasswordMode(true);
		passwordField.setPasswordCharacter('?');
		passwordField.setMessageText("password");
		passwordField.setMaxLength(16);
		
		table.add(passwordField).left().pad(10, 0, 10, 0).width(300);
		table.row();
		
		// Testing nine-patch
		
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("test/game.atlas"));
		Skin skin = new Skin();
		NinePatch button = atlas.createPatch("box-up");
		Drawable buttonUp = new NinePatchDrawable(button);
		Drawable buttonDown = skin.newDrawable(buttonUp, Color.GREEN);
		Drawable buttonOver = skin.newDrawable(buttonUp, Color.OLIVE);
		
		final TextButton.TextButtonStyle textbuttonStyle = new TextButton.TextButtonStyle(buttonUp, buttonDown, null, pixFont);
		textbuttonStyle.over = buttonOver;
		textbuttonStyle.fontColor = Color.GREEN;
		textbuttonStyle.overFontColor = Color.CYAN;
		textbuttonStyle.downFontColor = Color.RED;
		// Testing ends
		
		TextButton playButton = new TextButton("Play!", textbuttonStyle);
		playButton.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				System.out.println("Username: " + usernameField.getText());
				System.out.println("Password: " + passwordField.getText());
				boolean successful = pGame.client.login(usernameField.getText(), passwordField.getText());
				System.out.println("Login successful "+ successful);
			}
		});
		
//		playButton.addListener(new ClickListener(){
//			@Override
//			public void enter(InputEvent event, float x, float y, int pointer,
//					Actor fromActor) {
//				super.enter(event, x, y, pointer, fromActor);
//				event.getTarget().setColor(Color.RED);
//			}
//			@Override
//			public void exit(InputEvent event, float x, float y, int pointer,
//					Actor toActor) {
//				super.exit(event, x, y, pointer, toActor);
//				event.getTarget().setColor(Color.GREEN);
//			}
//			@Override
//			public void clicked(InputEvent event, float x, float y) {
//				super.clicked(event, x, y);
//				// connect to the server
//				boolean succesful = pGame.client.login(usernameField.getText(), passwordField.getText());
//				System.out.println("Login succesful "+succesful);
//			}
//		});
		table.add(playButton).center().pad(10, 0, 10, 0);
		
		TextButton registerButton = new TextButton("Register", textbuttonStyle);
		registerButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				boolean successful = pGame.client.createAccount(usernameField.getText(), passwordField.getText());
				System.out.println("Register successful "+ successful);
			}
		});
		
//		registerButton.addListener(new ClickListener(){
//			@Override
//			public void clicked(InputEvent event, float x, float y) {
//				super.clicked(event, x, y);
//				// Connect to server
//				boolean successful = pGame.client.createAccount(usernameField.getText(), passwordField.getText());
//				System.out.println("Register successful " + successful);
//			}
//			@Override
//			public void enter(InputEvent event, float x, float y, int pointer,
//					Actor fromActor) {
//				super.enter(event, x, y, pointer, fromActor);
//				event.getTarget().setColor(Color.BLUE);
//			}
//			@Override
//			public void exit(InputEvent event, float x, float y, int pointer,
//					Actor toActor) {
//				super.exit(event, x, y, pointer, toActor);
//				event.getTarget().setColor(Color.GREEN);
//			}
//		});
		table.row();
		table.add(registerButton).center().pad(10, 0, 10, 0);
		
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