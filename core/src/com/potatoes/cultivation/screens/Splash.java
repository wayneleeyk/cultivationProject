package com.potatoes.cultivation.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.potatoes.cultivation.Cultivation;

public class Splash extends ScreenAdapter {
	Cultivation game;
	SpriteBatch batch;
	TextureRegion logo;
	float splashTime = 2.0f;
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
		
		// Fade-out
		else if(splashTime-fadeTime < elapsedTime && elapsedTime < splashTime) {
			batch.setColor(1, 1, 1, (-1/fadeTime * elapsedTime) + splashTime/fadeTime);
		}

		batch.draw(logo, (Gdx.graphics.getWidth() - logo.getRegionWidth())/2, 
						 (Gdx.graphics.getHeight() - logo.getRegionHeight() )/2);
		
		batch.end();
		if(elapsedTime > splashTime) {
			batch.setColor(1, 1, 1, 1);
			game.setScreen(new Login(game));
		}
	}
	
	@Override
	public void show() {
		logo = game.skin.getRegion("companyLogo");
		elapsedTime = 0;
	}
	
	@Override
	public void dispose() {
		// Can't dispose a textureRegion since it'll dispose the rest of the atlas
	}
	
	@Override
	public void hide() {
		dispose();
	}
}