package es.dfc.riverandsky.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.dfc.riverandsky.tween.ActorAccessor;
import es.dfc.riverandsky.tween.SpriteAccessor;

public class Splash implements Screen{

	private Stage stage; 
	private Skin skin;
	
	private SpriteBatch batch;
	private Sprite bg, RS, sub;			//background, R&S logo, subtitle
	
	private TweenManager tweenManager;
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
			//bg.draw(batch);
			//RS.draw(batch);
			//sub.draw(batch);
		batch.end();
		
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
		batch = new SpriteBatch();
		
		//sprites - bg, title parts
		Texture bgTexture = new Texture("img/menus/naturMM.png");
		bg = new Sprite(bgTexture);
		bg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		Texture RSTexture = new Texture("img/menus/R&S.png");
		RS = new Sprite(RSTexture);
		RS.setSize(1000, 420);
		RS.setPosition((Gdx.graphics.getWidth() / 2) - 500, 550);
		
		Texture subTexture = new Texture("img/menus/subtitle.png");
		sub = new Sprite(subTexture);
		sub.rotate(20f);
		sub.setScale(0.8f);
		sub.setPosition((Gdx.graphics.getWidth() / 2) + 100, 550);
		
		//set stage
		stage = new Stage();
		
		//Gdx.input.setInputProcessor(stage);  FOR SKIPPING
		
		skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), new TextureAtlas("ui/button.pack"));
		
		//credit opener
		Label holly = new Label("Holly Robin", skin, "defaultB");
		holly.setPosition(300, 700);

		Label nnmml = new Label("Near North Mobile Media Lab", skin, "defaultB");
		nnmml.setPosition(300, 500);

		Label brendan = new Label("Brendan Lehman", skin, "defaultB");
		brendan.setPosition(300, 600);
		
		Label river = new Label("and River & Sky Music / Camping Festival", skin, "defaultB");
		river.setPosition(300, 500);
		
		Label present = new Label("present...", skin, "defaultB");
		present.setPosition(300, 400);

		stage.addActor(holly);
		stage.addActor(nnmml);
		stage.addActor(brendan);
		stage.addActor(river);
		stage.addActor(present);

		//TWEEN
		tweenManager = new TweenManager();
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());
		Tween.registerAccessor(Label.class, new ActorAccessor());
		
		//start splash screen at no opacity
		Timeline.createSequence().beginSequence()
			//.push(Tween.set(bg, SpriteAccessor.ALPHA).target(0))
			//.push(Tween.set(RS, SpriteAccessor.ALPHA).target(0))
			//.push(Tween.set(sub, SpriteAccessor.ALPHA).target(0))
			.push(Tween.set(holly, ActorAccessor.ALPHA).target(0))
			.push(Tween.set(nnmml, ActorAccessor.ALPHA).target(0))
			.push(Tween.set(brendan, ActorAccessor.ALPHA).target(0))
			.push(Tween.set(river, ActorAccessor.ALPHA).target(0))
			.push(Tween.set(present, ActorAccessor.ALPHA).target(0))
			//play text sequence, load up title
			.push(Tween.to(holly, ActorAccessor.ALPHA, 1).target(1).repeatYoyo(1, 0.5f))
			.push(Tween.to(nnmml, ActorAccessor.ALPHA, 1).target(1).repeatYoyo(1, 0.5f))
			.push(Tween.to(brendan, ActorAccessor.ALPHA, 1).target(1).repeatYoyo(1, 0.5f))
			.push(Tween.to(river, ActorAccessor.ALPHA, 1).target(1).repeatYoyo(1, 0.5f))
			.push(Tween.to(present, ActorAccessor.ALPHA, 1).target(1).repeatYoyo(1, 0.5f))
			//.push(Tween.to(bg, SpriteAccessor.ALPHA, 1).target(1))
			//.push(Tween.to(RS, SpriteAccessor.ALPHA, 1).target(1))
			//.push(Tween.to(sub, SpriteAccessor.ALPHA, 1).target(1))
			//switch to main menu
			.end().setCallback(new TweenCallback()
			{					
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					((Game) Gdx.app.getApplicationListener()).setScreen(new es.dfc.riverandsky.screens.MainMenu());
				}
			}).start(tweenManager);
		
		tweenManager.update(Float.MIN_VALUE);
		
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
		batch.dispose();
		bg.getTexture().dispose();
		RS.getTexture().dispose();
		sub.getTexture().dispose();
	}

}
