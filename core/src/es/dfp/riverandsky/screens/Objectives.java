package es.dfp.riverandsky.screens;

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

import es.dfp.riverandsky.tween.ActorAccessor;
import es.dfp.riverandsky.tween.SpriteAccessor;

public class Objectives implements Screen {

	private Stage stage; 
	private Skin skin;
	
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
	}

	@Override
	public void show() {
		
		//set stage, table
		stage = new Stage(); 
		
		Gdx.input.setInputProcessor(stage);
		
		skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), new TextureAtlas("ui/button.pack"));
		
		//LABELS\\

		Label poemA = new Label("We all require nature to survive.", skin, "defaultB");
		poemA.setFontScale(0.8f);
		poemA.setPosition(240, 900);

		Label poemB = new Label("It provides us energy,", skin, "defaultB");
		poemB.setFontScale(0.8f);
		poemB.setPosition(240, 825);
		
		Label poemC = new Label("it provides us food,", skin, "defaultB");
		poemC.setFontScale(0.8f);
		poemC.setPosition(240, 750);

		Label poemD = new Label("it provides us beauty.", skin, "defaultB");
		poemD.setFontScale(0.8f);
		poemD.setPosition(240, 675);

		Label poemE = new Label("We mine from it to create its opposite:", skin, "defaultB");
		poemE.setFontScale(0.8f);
		poemE.setPosition(240, 600);
		
		Label poemF = new Label("Machines.", skin, "defaultB");
		poemF.setFontScale(0.8f);
		poemF.setPosition(240, 525);
		
		Label poemG = new Label("But from these machines we can make art.", skin, "defaultB");
		poemG.setFontScale(0.8f);
		poemG.setPosition(240, 450);

		Label poemH = new Label("Together with music,", skin, "defaultB");
		poemH.setFontScale(0.8f);
		poemH.setPosition(240, 375);

		Label poemI = new Label("we're here to reunite,", skin, "defaultB");
		poemI.setFontScale(0.8f);
		poemI.setPosition(240, 300);

		Label poemJ = new Label("nature and machine.", skin, "defaultB");
		poemJ.setFontScale(0.8f);
		poemJ.setPosition(240, 225);

		Label poemK = new Label("Join us.", skin, "defaultB");
		poemK.setFontScale(0.8f);
		poemK.setPosition(240, 150);

		Label back = new Label("<down> to gtfo", skin, "defaultB");
		back.setFontScale(0.3f);
		back.setPosition((Gdx.graphics.getWidth() * 0.88f), (Gdx.graphics.getHeight() * 0.05f));
		
		stage.addActor(poemA);
		stage.addActor(poemB);
		stage.addActor(poemC);
		stage.addActor(poemD);
		stage.addActor(poemE);
		stage.addActor(poemF);
		stage.addActor(poemG);
		stage.addActor(poemH);
		stage.addActor(poemI);
		stage.addActor(poemJ);
		stage.addActor(poemK);
		stage.addActor(back);
		
		//TWEEN\\
		
		tweenManager = new TweenManager();
		
		Tween.registerAccessor(Actor.class, new ActorAccessor());

		//add labels
		Timeline.createSequence().beginSequence()
			.push(Tween.set(poemA, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(poemB, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(poemC, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(poemD, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(poemE, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(poemF, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(poemG, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(poemH, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(poemI, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(poemJ, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(poemK, ActorAccessor.ALPHA).target(0))				//set button to transparent
			.push(Tween.set(back, ActorAccessor.ALPHA).target(0.2f))				//set button to transparent
			.push(Tween.to(poemA, ActorAccessor.ALPHA, 2).target(1))		//play button from alpha 1 to 0
			.push(Tween.to(poemB, ActorAccessor.ALPHA, 1).target(1))		//play button from alpha 1 to 0
			.push(Tween.to(poemC, ActorAccessor.ALPHA, 1).target(1))		//play button from alpha 1 to 0
			.push(Tween.to(poemD, ActorAccessor.ALPHA, 1).target(1))		//exit button from alpha 1 to 0
			.push(Tween.to(poemE, ActorAccessor.ALPHA, 2).target(1))		//exit button from alpha 1 to 0
			.push(Tween.to(poemF, ActorAccessor.ALPHA, 2).target(1))		//exit button from alpha 1 to 0
			.push(Tween.to(poemG, ActorAccessor.ALPHA, 2).target(1))		//exit button from alpha 1 to 0
			.push(Tween.to(poemH, ActorAccessor.ALPHA, 1).target(1))		//exit button from alpha 1 to 0
			.push(Tween.to(poemI, ActorAccessor.ALPHA, 1).target(1))		//exit button from alpha 1 to 0
			.push(Tween.to(poemJ, ActorAccessor.ALPHA, 1).target(1))		//exit button from alpha 1 to 0
			.push(Tween.to(poemK, ActorAccessor.ALPHA, 2).target(1))		//exit button from alpha 1 to 0
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
