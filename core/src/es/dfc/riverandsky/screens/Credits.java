package es.dfc.riverandsky.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import es.dfc.riverandsky.tween.ActorAccessor;
import es.dfc.riverandsky.tween.SpriteAccessor;

public class Credits implements Screen {

	private Stage stage; 
	private Skin skin;
	private Table table;
	
	private TweenManager tweenManager;
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta);		//loads stage, sets timing
		stage.draw();			//draw
		
		tweenManager.update(delta);
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		table.invalidateHierarchy();
	}

	@Override
	public void show() {
		
		//set stage, table
		stage = new Stage(); 
		table = new Table();
		
		Gdx.input.setInputProcessor(stage);
		
		skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), new TextureAtlas("ui/button.pack"));
		
		//LABELS\\

		Label projBy = new Label("A project of The Digital Forest Collective", skin, "defaultB");
		projBy.setFontScale(0.8f);
		projBy.setPosition(240, 975);

		Label projSite = new Label("inthetre.es", skin, "defaultB");
		projSite.setFontScale(0.6f);
		projSite.setPosition(260, 925);
		
		Label artBy = new Label("Art and Design", skin, "defaultB");
		artBy.setFontScale(0.8f);
		artBy.setPosition(240, 850);

		Label artByA = new Label("Holly Robin", skin, "defaultB");
		artByA.setFontScale(0.8f);
		artByA.setPosition(260, 800);

		Label artByB = new Label("therobinhead.com", skin, "defaultB");
		artByB.setFontScale(0.6f);
		artByB.setPosition(260, 750);

		Label codeBy = new Label("Code and Design", skin, "defaultB");
		codeBy.setFontScale(0.8f);
		codeBy.setPosition(240, 675);
		
		Label codeByA = new Label("Brendan Lehman", skin, "defaultB");
		codeByA.setFontScale(0.8f);
		codeByA.setPosition(260, 625);

		Label audioBy = new Label("Audio Production: Near North Mobile Media Lab", skin, "defaultB");
		audioBy.setFontScale(0.8f);
		audioBy.setPosition(240, 550);

		Label audioByA = new Label("Holly Cunningham", skin, "defaultB");
		audioByA.setFontScale(0.8f);
		audioByA.setPosition(260, 500);
		
		Label audioByB = new Label("Ben Leggett", skin, "defaultB");
		audioByB.setFontScale(0.8f);
		audioByB.setPosition(260, 450);

		Label audioByC = new Label("n2m2l.ca", skin, "defaultB");
		audioByC.setFontScale(0.6f);
		audioByC.setPosition(260, 400);
		
		Label thanks = new Label("Special Thanks", skin, "defaultB");
		thanks.setFontScale(0.8f);
		thanks.setPosition(240, 325);

		Label thanksA = new Label("Peter Zwarich ", skin, "defaultB");
		thanksA.setFontScale(0.6f);
		thanksA.setPosition(260, 275);

		Label thanksB = new Label("River & Sky Camping / Music Festival, Angele Sabourin, Clayton Drake, ", skin, "defaultB");
		thanksB.setFontScale(0.6f);
		thanksB.setPosition(260, 225);

		Label thanksC = new Label("Christian Pelletier, Andrew Knapp, Adam Turcotte, Nicolas Rouleau, Alex Gauvin", skin, "defaultB");
		thanksC.setFontScale(0.6f);
		thanksC.setPosition(260, 175);

		Label madeWith = new Label("Made with libGDX, Minim, Adobe CS6, and lots of love. Spring/Summer 2014, Sudbury, Ontario", skin, "defaultB");
		madeWith.setFontScale(0.4f);
		madeWith.setPosition(240, 100);

		Label heart = new Label("<3", skin, "defaultB");
		heart.setFontScale(0.4f);
		heart.setPosition(240, 50);

		Label back = new Label("<down> to gtfo", skin, "defaultB");
		back.setFontScale(0.3f);
		back.setPosition((Gdx.graphics.getWidth() * 0.88f), (Gdx.graphics.getHeight() * 0.05f));
		
		stage.addActor(projBy);
		stage.addActor(projSite);
		stage.addActor(artBy);
		stage.addActor(artByA);
		stage.addActor(artByB);
		stage.addActor(codeBy);
		stage.addActor(codeByA);
		stage.addActor(audioBy);
		stage.addActor(audioByA);
		stage.addActor(audioByB);
		stage.addActor(audioByC);
		stage.addActor(thanks);
		stage.addActor(thanksA);
		stage.addActor(thanksB);
		stage.addActor(thanksC);
		stage.addActor(madeWith);
		stage.addActor(heart);
		stage.addActor(back);
		
		//TWEEN\\
		
		tweenManager = new TweenManager();
		
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());
		Tween.registerAccessor(Actor.class, new ActorAccessor());
		
		//add labels
		Timeline.createSequence().beginSequence()
			.push(Tween.set(projBy, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(projSite, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(artBy, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(artByA, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(artByB, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(codeBy, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(codeByA, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(audioBy, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(audioByA, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(audioByB, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(audioByC, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(thanks, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(thanksA, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(thanksB, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(thanksC, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(madeWith, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(heart, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(back, ActorAccessor.ALPHA).target(0.2f))				//set button to transparent
			.push(Tween.to(projBy, ActorAccessor.ALPHA, 0.5f).target(1))		//play button from alpha 1 to 0
			.push(Tween.to(projSite, ActorAccessor.ALPHA, 0.5f).target(1))		//play button from alpha 1 to 0
			.push(Tween.to(artBy, ActorAccessor.ALPHA, 0.5f).target(1))		//play button from alpha 1 to 0
			.push(Tween.to(artByA, ActorAccessor.ALPHA, 0.5f).target(1))		//play button from alpha 1 to 0
			.push(Tween.to(artByB, ActorAccessor.ALPHA, 0.5f).target(1))		//play button from alpha 1 to 0
			.push(Tween.to(codeBy, ActorAccessor.ALPHA, 0.5f).target(1))		//exit button from alpha 1 to 0
			.push(Tween.to(codeByA, ActorAccessor.ALPHA, 0.5f).target(1))		//exit button from alpha 1 to 0
			.push(Tween.to(audioBy, ActorAccessor.ALPHA, 0.5f).target(1))		//exit button from alpha 1 to 0
			.push(Tween.to(audioByA, ActorAccessor.ALPHA, 0.5f).target(1))		//exit button from alpha 1 to 0
			.push(Tween.to(audioByB, ActorAccessor.ALPHA, 0.5f).target(1))		//exit button from alpha 1 to 0
			.push(Tween.to(audioByC, ActorAccessor.ALPHA, 0.5f).target(1))		//exit button from alpha 1 to 0
			.push(Tween.to(thanks, ActorAccessor.ALPHA, 0.5f).target(1))		//exit button from alpha 1 to 0
			.push(Tween.to(thanksA, ActorAccessor.ALPHA, 0.5f).target(1))		//exit button from alpha 1 to 0
			.push(Tween.to(thanksB, ActorAccessor.ALPHA, 0.5f).target(1))		//exit button from alpha 1 to 0
			.push(Tween.to(thanksC, ActorAccessor.ALPHA, 0.5f).target(1))		//exit button from alpha 1 to 0
			.push(Tween.to(madeWith, ActorAccessor.ALPHA, 0.5f).target(1))		//exit button from alpha 1 to 0
			.push(Tween.to(heart, ActorAccessor.ALPHA, 0.5f).target(1))		//exit button from alpha 1 to 0
			.end().start(tweenManager);
		
		tweenManager.update(Float.MIN_VALUE);
		
		//INPUT\\
		
		Gdx.input.setInputProcessor(new InputMultiplexer(new InputAdapter() {

			@Override
			public boolean keyDown(int keycode) {
				switch(keycode) {
				case Keys.DOWN:
					//TODO FADE OUT SCREEN
					((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
					break;
				case Keys.ESCAPE:
					Gdx.app.exit();
					break;
				}
				return false;
			}
		}));
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();
		
	}

}
