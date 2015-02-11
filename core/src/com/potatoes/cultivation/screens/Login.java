package com.potatoes.cultivation.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.logic.Player;

public class Login extends ScreenAdapter {
	Stage stage;
	Cultivation game;
	Table table;
	Skin skin;
	
	SpriteBatch batch;
	TextureRegion background;
	TextureRegion title;

	TextField usernameField;
	TextField passwordField;
	
	Animation anim;
	float frameCounter;
	
	public Login(final Cultivation pGame) {
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		game = pGame;
		batch = pGame.batch;
		skin = pGame.skin;
		anim = GifDecoder.loadGIFAnimation(1, Gdx.files.internal("potato_bounce.gif").read());
		frameCounter = 0;
		
		table = new Table();
		table.setWidth(360);
		table.setHeight(240);
		table.setBackground(skin.getDrawable("table-brown"));
//		table.debugAll();
//		table.setPosition((Gdx.graphics.getWidth() - table.getWidth()) / 2.0f, (Gdx.graphics.getHeight() - table.getHeight()) / 2.0f);
		table.setPosition(30, 85);
		
		usernameField = new TextField("", skin, "default");
		usernameField.setMessageText("username");
		usernameField.setMaxLength(16);
		
		Container<TextField> usernameContainer = new Container<TextField>(usernameField);
		usernameContainer.setBackground(skin.getDrawable("onepix-light-brown"));
		usernameContainer.left().prefWidth(300).pad(3, 7, 3, 0);
		
//		table.add(usernameField).left().pad(10, 0, 10, 0).width(300).colspan(2);
		table.add(usernameContainer).pad(10, 0, 10, 0).width(300).colspan(2);
		table.row();
		
		passwordField = new TextField("", skin, "default");
		passwordField.setPasswordMode(true);
		passwordField.setPasswordCharacter('?');
		passwordField.setMessageText("password");
		passwordField.setMaxLength(16);
		
		Container<TextField> passwordContainer = new Container<TextField>(passwordField);
		passwordContainer.setBackground(skin.getDrawable("onepix-light-brown"));
		passwordContainer.left().prefWidth(300).pad(3, 7, 3, 0);

//		table.add(passwordField).left().pad(10, 0, 10, 0).width(300).colspan(2);
		table.add(passwordContainer).pad(10, 0, 10, 0).width(300).colspan(2);
		table.row();
		
		TextButton playButton = new TextButton("Play!", skin, "default");
		playButton.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				System.out.println("Username: " + usernameField.getText());
				System.out.println("Password: " + passwordField.getText());
				Player player = pGame.client.login(usernameField.getText(), passwordField.getText());
				System.out.println("Login successful? "+ ((player.notNull())? "yup" : "no"));
				if (player.notNull()) game.setScreen(new InGame(game));
			}
		});
		
		table.add(playButton).center().pad(10, 0, 10, 0);
		
		TextButton registerButton = new TextButton("Register", skin, "default");
		registerButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Player player = pGame.client.createAccount(usernameField.getText(), passwordField.getText());
				System.out.println("Login successful? "+ ((player!=null)? "yup" : "no"));
			}
		});		

		table.add(registerButton).center().pad(10, 0, 10, 0);
		stage.addActor(table);
				
	}
	
	@Override
	public void render(float delta) {
		frameCounter += delta;
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Draw background and title
		batch.begin();
		batch.enableBlending();
		batch.draw(background, 0, 0);
		batch.draw(title, (Gdx.graphics.getWidth() - title.getRegionWidth()) / 2, 450);
		batch.draw(anim.getKeyFrame(frameCounter, true), 410, 50);
		batch.end();
		
		// Draw stage
		stage.act(delta);
		stage.draw();
	}
	
	@Override
	public void show() {
		title = skin.getRegion("gameTitle");
		background = skin.getRegion("landscape");
	}
	
	
	@Override
	public void dispose() {
		super.dispose();
		this.stage.dispose();
		// need to dispose textures
		// we can use assetmanager to do this
		
		// I've changed the textures to textureRegion, so disposing
		// one will the rest of the texture. But we can put the Login/Splash
		// related images in one atlas and dispose that instead
	}
}
