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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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
import com.potatoes.cultivation.networking.JoinRoomProtocol;
import com.potatoes.cultivation.networking.LoginProtocol;
import com.potatoes.cultivation.networking.Protocol;
import com.potatoes.cultivation.networking.ProtocolHandler;
import com.potatoes.cultivation.networking.RegisterProtocol;

public class Login extends ScreenAdapter {
	Stage stage;
	Cultivation game;
	Skin skin;
	
	SpriteBatch batch;
	TextureRegion background;
	TextureRegion title;

	private TextField usernameField;
	private TextField passwordField;
	
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
		
		////////////////////////////////////////////////
		// HANDLERS
		game.client.insertHandler(new ProtocolHandler() {
			@Override
			public void handle(Protocol p) {
				if(p instanceof LoginProtocol) {
					game.setPlayer(((LoginProtocol) p).player());
				}
			}
		});
		
		game.client.insertHandler(new ProtocolHandler() {
			@Override
			public void handle(Protocol p) {
				if(p instanceof RegisterProtocol) {
					game.setPlayer(((RegisterProtocol) p).player());
				}
			}
		});
		////////////////////////////////////////////////
		
		final Table table = new Table();
		
		// Setting a dialog window -- This is gonna be changed later to
		// something less hacky
		class MessageDialog extends Table {
			private Label msg = new Label("", skin, "default");
			private TextButton cancel;
			private float timer = 0;
			
			public MessageDialog(float posX, float posY, float width, float height ) {
				setWidth(width);
				setHeight(height);
				setBackground(skin.getDrawable("table-brown"));
				setPosition(posX, posY);
				add(msg).pad(10, 0, 10, 0);
				row();
				cancel = new TextButton("Cancel", skin, "default");
				cancel.setVisible(false);
				cancel.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						MessageDialog.this.setVisible(false);
						table.setTouchable(Touchable.enabled);
					}
				});
				add(cancel).right();
				setVisible(false);
			}
			
			public Label getMsg() {
				return msg;
			}
			
			@Override
			public void setVisible(boolean visible) {
				super.setVisible(visible);
				timer = 0;
				cancel.setVisible(false);
			}
			
			@Override
			public void act(float delta) {
				super.act(delta);
				timer += delta;
				if(timer > 5 && game.player == null) {
					msg.setText("Net Error!");
					cancel.setVisible(true);
				}
				else if(game.player != null) {
					if(!game.player.notNull()) {
						game.setPlayer(null);
						msg.setText("Invalid player");
						cancel.setVisible(true);
					}
					else {
						// Enter the game room 0 (for testing)
						class JoinHandler implements ProtocolHandler {
							public boolean joinResult;
							@Override
							public void handle(Protocol p) {
								if(p instanceof JoinRoomProtocol) {
									joinResult = ((JoinRoomProtocol) p).getResult();
								}
							}
						}
						JoinHandler jh = new JoinHandler();
						game.client.insertHandler(jh);
						System.out.println("Before enter");
						game.client.joinRoom(0, game.player);
						System.out.println("Entering");
						if(jh.joinResult) game.setScreen(new GameRoom(game, 0));
						else {
							game.setPlayer(null);
							msg.setText("Net Error!");
							cancel.setVisible(true);
						}
					}
				}
			}
		}
		final MessageDialog dialog = new MessageDialog(Gdx.graphics.getWidth()/2 - 125, Gdx.graphics.getHeight()/2 - 60, 250, 120);
	
		table.setWidth(360);
		table.setHeight(240);
		table.setBackground(skin.getDrawable("table-brown"));
		table.setPosition(30, 85);
		
		usernameField = new TextField("", skin, "default");
		usernameField.setMessageText("username");
		usernameField.setMaxLength(16);
		
		Container<TextField> usernameContainer = new Container<TextField>(usernameField);
		usernameContainer.setBackground(skin.getDrawable("onepix-light-brown"));
		usernameContainer.left().prefWidth(300).pad(3, 7, 3, 0);
		
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

		table.add(passwordContainer).pad(10, 0, 10, 0).width(300).colspan(2);
		table.row();
		
		TextButton playButton = new TextButton("Play!", skin, "default");
		playButton.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				pGame.client.login(usernameField.getText(), passwordField.getText());
				dialog.setVisible(true);
				dialog.getMsg().setText("Logging in...");
				table.setTouchable(Touchable.disabled);;
			}
		});
		
		table.add(playButton).center().pad(10, 0, 10, 0);
		
		TextButton registerButton = new TextButton("Register", skin, "default");
		registerButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.client.register(usernameField.getText(), passwordField.getText());
				dialog.setVisible(true);
				dialog.getMsg().setText("Registering...");
				table.setTouchable(Touchable.disabled);
			}
		});		

		table.add(registerButton).center().pad(10, 0, 10, 0);
		stage.addActor(table);
		stage.addActor(dialog);
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
