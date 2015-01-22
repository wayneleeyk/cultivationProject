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
	
	SpriteBatch batch;
	Texture background;
	Texture title;
	
	TextField usernameField;
	TextField passwordField;
	
	Animation anim;
	float frameCounter;
	
	public Login(final Cultivation pGame) {
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		game = pGame;
		batch = pGame.batch;
		anim = GifDecoder.loadGIFAnimation(1, Gdx.files.internal("potato_bounce.gif").read());
		frameCounter = 0;
		Skin skin = new Skin();
		
		BitmapFont pixFont = game.manager.get("pixFont.fnt");
		
		// Testing TiledDrawable
		Pixmap onePix = new Pixmap(1, 1, Format.RGBA8888);
		onePix.setColor(Color.WHITE);
		onePix.fill();
		TextureRegion white = new TextureRegion(new Texture(onePix));
		Drawable whitePix = new TiledDrawable(white);
		Drawable orangePix = skin.newDrawable(whitePix, Color.ORANGE);
		Drawable bluePix = skin.newDrawable(whitePix, 125/255f, 149/255f, 245/255f, 0.7f);
		
		table = new Table();
		table.debugAll();
		table.setPosition((Gdx.graphics.getWidth() - table.getWidth()) / 2.0f, 325);
		TextField.TextFieldStyle textfieldStyle = new TextField.TextFieldStyle(pixFont, Color.DARK_GRAY, null, bluePix, orangePix);
		
		// End testing
		
		usernameField = new TextField("", textfieldStyle); 
		usernameField.setMessageText("username");
		usernameField.setMaxLength(16);
		
		table.add(usernameField).left().pad(10, 0, 10, 0).width(300).colspan(2);
		table.row();
		
		passwordField = new TextField("", textfieldStyle);
		passwordField.setPasswordMode(true);
		passwordField.setPasswordCharacter('?');
		passwordField.setMessageText("password");
		passwordField.setMaxLength(16);
		
		table.add(passwordField).left().pad(10, 0, 10, 0).width(300).colspan(2);
		table.row();
		
		// Testing nine-patch
		
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("test/game.atlas"));
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
				Player player = pGame.client.login(usernameField.getText(), passwordField.getText());
				System.out.println("Login successful? "+ ((player.notNull())? "yup" : "no"));
			}
		});
		
		table.add(playButton).center().pad(10, 0, 10, 0);
		
		TextButton registerButton = new TextButton("Register", textbuttonStyle);
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
		batch.draw(title, (Gdx.graphics.getWidth() - title.getWidth()) / 2, 450);
		batch.draw(anim.getKeyFrame(frameCounter, true), 200, 10);
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
