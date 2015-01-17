package com.potatoes.cultivation.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import com.potatoes.cultivation.Cultivation;

public class Splash extends ScreenAdapter {
	Cultivation game;
	SpriteBatch batch;
	Texture logo;
	float splashTime = 2.5f;
	float fadeTime = 0.5f;
	float elapsedTime;
	
	public Splash(Cultivation pGame) {
		game = pGame;
		batch = pGame.batch;
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		elapsedTime += delta;
		batch.begin();
		
		// Fade-in
		if(elapsedTime < fadeTime) {
			batch.setColor(1, 1, 1, elapsedTime/fadeTime);
		}
		
		// Weird Zapping fade-out
//		else if(splashTime-fadeTime < elapsedTime && elapsedTime < splashTime) {
//			batch.setColor(1, 1, 1, 1/(elapsedTime-1.5f));
//		}
		
		// Fade-out
		else if(splashTime-fadeTime < elapsedTime && elapsedTime < splashTime) {
			batch.setColor(1, 1, 1, (-1/fadeTime * elapsedTime) + splashTime/fadeTime);
		}

		batch.draw(logo, (Gdx.graphics.getWidth() - logo.getWidth() )/2, 
						 (Gdx.graphics.getHeight() - logo.getHeight() )/2);
		
		batch.end();
		if(elapsedTime > splashTime) {
			batch.setColor(1, 1, 1, 1);
			game.setScreen(new Login(game));
		}
	}
	
	@Override
	public void show() {
		logo = game.manager.get("companyLogo.png", Texture.class);
		elapsedTime = 0;
	}
	
	@Override
	public void dispose() {
		logo.dispose();
	}
	
	@Override
	public void hide() {
		dispose();
	}
}