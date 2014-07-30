package es.dfp.riverandsky.screens;

import processing.core.PApplet;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import ddf.minim.AudioInput;
import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.spi.AudioRecordingStream;
import ddf.minim.ugens.FilePlayer;
import es.dfp.riverandsky.entities.Player;
import es.dfp.riverandsky.entities.ScrollingSprite;
import es.dfp.riverandsky.tween.ActorAccessor;
import es.dfp.riverandsky.tween.SpriteAccessor;

public class MainMenu extends PApplet implements Screen {

	//camera and tilemap
	private OrthographicCamera camera;
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;

	//player
	private TextureAtlas playerAtlas;
	private Player player;
	private boolean playOn = false, creditsOn = false, objOn = false, exitOn = false, extrasOn = false;
	
	//backgrounds
	private TextureAtlas beachAtlas;
	private Sprite bg1, bg2;
	private ScrollingSprite beach, items, title;
	private float bg1X, bg2X;
	
	private int[] platforms = new int[] {0}, pickups = new int[] {1};
	
	//tween
	private TweenManager tweenManager;
	
	//audio
	private Minim minim;
	private FilePlayer filePlayer;
	private AudioOutput out;
	
	private String fileName = "audio/Jas - River and Sky Recording_01.mp3";		//change at level selection

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.position.set(bg1.getX() + bg1.getWidth() / 2, bg1.getY() + bg1.getHeight() / 2, 0);
		camera.update();
		
		renderer.setView(camera);
		renderer.getSpriteBatch().setProjectionMatrix(camera.combined);
		
		renderer.getSpriteBatch().begin();													//START

			drawBackground();
		
		renderer.getSpriteBatch().end();													//END

		renderer.render(platforms);

		renderer.getSpriteBatch().begin();													//START
			
			drawMidground();
		
			player.draw(renderer.getSpriteBatch());
			
			
		renderer.getSpriteBatch().end();		
		
		//scroll speeds
		bg1X -= 0.5f;
		bg2X -= 0.4f;

		//reset separators
		if (bg1X <= -(Gdx.graphics.getWidth() / 2)) bg1X = Gdx.graphics.getWidth() / 2;
		if (bg2X <= -(Gdx.graphics.getWidth() / 2)) bg2X = Gdx.graphics.getWidth() / 2;

		
		//PLAYER\\
		
		//menu triggers\\
		
		//play
		if (player.getX() < 1025 && player.getX() > 650) {
			playOn = true;
			objOn = false;
			exitOn = false;
		}
		//credits
		if (player.getX() < 1750 && player.getX() > 1450) {
			creditsOn = true;
			exitOn = false;
		}
		//exit
		if (player.getX() < 1375 && player.getX() > 1050) {
			exitOn = true;
			playOn = false;
			creditsOn = false;
		}
		//goal
		if (player.getX() < 650 && player.getX() > 400) {
			objOn = true;
			playOn = false;
			extrasOn = false;
		}
		//extras
		if (player.getX() < 400 && player.getX() > 175) {
			extrasOn = true;
			objOn = false;
		}
		
		tweenManager.update(delta);
		
	}

	private void drawBackground() {
		renderer.getSpriteBatch().draw(bg1, (bg1X - Gdx.graphics.getWidth()) + camera.position.x, 0);
		renderer.getSpriteBatch().draw(bg1, bg1X + camera.position.x, 0);

		renderer.getSpriteBatch().draw(bg2, (bg2X - Gdx.graphics.getWidth()) + camera.position.x, 0);
		renderer.getSpriteBatch().draw(bg2, bg2X + camera.position.x, 0);

	}

	private void drawMidground() {
		beach.drawAnimated(renderer.getSpriteBatch(), -800, -250, 0.5f);
		title.drawStatic(renderer.getSpriteBatch(), 1000, 450, 0.4f);
		items.drawStatic(renderer.getSpriteBatch(), -320, -270, 0.55f);

	}
	
	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width;
		camera.viewportHeight = height;
	}

	@Override
	public void show() {
		camera = new OrthographicCamera();
		
		//TILEMAP & RENDERER\\
		map = new TmxMapLoader().load("maps/menu.tmx");
		renderer = new OrthogonalTiledMapRenderer(map);

		//SCENE LAYERS\\
		//bg
		Texture bg1Texture = new Texture("img/scene/bg/trees1_1.png");
		Texture bg2Texture = new Texture("img/scene/bg/trees1_2.png");
		bg1 = new Sprite(bg1Texture);
		bg2 = new Sprite(bg2Texture);

		//mid
		beachAtlas = new TextureAtlas("img/scene/mid/beach.pack");
		Animation beachAnimation = new Animation(1/2f, beachAtlas.findRegions("beach"), Animation.PlayMode.LOOP);
		beach = new ScrollingSprite(beachAnimation);
		
		//sprites - menu items
		Texture titleTexture = new Texture("img/menus/title.png");
		title = new ScrollingSprite(titleTexture);
		title.setPosition(700, 300);
		title.setScale(0.5f);

		Texture itemsTexture = new Texture("img/menus/items.png");
		items = new ScrollingSprite(itemsTexture);
		items.setPosition(1500, 250);
		items.setScale(0.5f);
		
		//PLAYER\\
		
		playerAtlas = new TextureAtlas("img/sprite/holly.pack");
		
		Animation run, jumpUp, jumpDown, idle;
		Animation runL, jumpUpL, jumpDownL, idleL;
		
		run = new Animation(1/13f, playerAtlas.findRegions("run"), Animation.PlayMode.LOOP);
		jumpUp = new Animation(1/13f, playerAtlas.findRegions("jumpUp"), Animation.PlayMode.LOOP);
		jumpDown = new Animation(1/13f, playerAtlas.findRegions("jumpDown"), Animation.PlayMode.LOOP);
		idle = new Animation(1/13f, playerAtlas.findRegions("idle"), Animation.PlayMode.LOOP);
		runL = new Animation(1/13f, playerAtlas.findRegions("runL"), Animation.PlayMode.LOOP);
		idleL = new Animation(1/13f, playerAtlas.findRegions("idleL"), Animation.PlayMode.LOOP);
		
		//player = new Player(run, runL, jumpUp, jumpDown, idle, idleL, (TiledMapTileLayer) map.getLayers().get("collision"));
		player.setMenuMode(true);
		
		player.setPosition(875, 270);
		player.setSize(90,90);
		
		Gdx.input.setInputProcessor(new InputMultiplexer(new InputAdapter() {

			@Override
			public boolean keyDown(int keycode) {
				switch(keycode) {
				case Keys.UP:
					if (playOn) {
						Tween.set(bg1, SpriteAccessor.ALPHA).target(1).start(tweenManager);	
						Tween.set(bg2, SpriteAccessor.ALPHA).target(1).start(tweenManager);	
						Tween.set(beach, SpriteAccessor.ALPHA).target(1).start(tweenManager);	
						Tween.set(items, SpriteAccessor.ALPHA).target(1).start(tweenManager);
						Tween.set(player, SpriteAccessor.ALPHA).target(1).start(tweenManager);
						Tween.set(title, SpriteAccessor.ALPHA).target(1).start(tweenManager);
						Tween.to(bg1, SpriteAccessor.ALPHA, 2).target(0.4f).start(tweenManager);
						Tween.to(bg2, SpriteAccessor.ALPHA, 2).target(0.4f).start(tweenManager);
						Tween.to(beach, SpriteAccessor.ALPHA, 2).target(0.4f).start(tweenManager);
						Tween.to(items, SpriteAccessor.ALPHA, 2).target(0.4f).start(tweenManager);
						Tween.to(player, SpriteAccessor.ALPHA, 2).target(0.4f).start(tweenManager);
						Tween.to(title, SpriteAccessor.ALPHA, 2).target(0.4f).setCallback(new TweenCallback()
							{					
								@Override
								public void onEvent(int type, BaseTween<?> source) {
									((Game) Gdx.app.getApplicationListener()).setScreen(new es.dfp.riverandsky.screens.Game());								}
							}).start(tweenManager);
					}
					else if (creditsOn) {
						Tween.set(bg1, SpriteAccessor.ALPHA).target(1).start(tweenManager);	
						Tween.set(bg2, SpriteAccessor.ALPHA).target(1).start(tweenManager);	
						Tween.set(beach, SpriteAccessor.ALPHA).target(1).start(tweenManager);	
						Tween.set(items, SpriteAccessor.ALPHA).target(1).start(tweenManager);
						Tween.set(player, SpriteAccessor.ALPHA).target(1).start(tweenManager);
						Tween.set(title, SpriteAccessor.ALPHA).target(1).start(tweenManager);
						Tween.to(bg1, SpriteAccessor.ALPHA, 2).target(0.4f).start(tweenManager);
						Tween.to(bg2, SpriteAccessor.ALPHA, 2).target(0.4f).start(tweenManager);
						Tween.to(beach, SpriteAccessor.ALPHA, 2).target(0.4f).start(tweenManager);
						Tween.to(items, SpriteAccessor.ALPHA, 2).target(0.4f).start(tweenManager);
						Tween.to(player, SpriteAccessor.ALPHA, 2).target(0.4f).start(tweenManager);
						Tween.to(title, SpriteAccessor.ALPHA, 2).target(0.4f).setCallback(new TweenCallback()
							{					
								@Override
								public void onEvent(int type, BaseTween<?> source) {
									((Game) Gdx.app.getApplicationListener()).setScreen(new Credits());								}
							}).start(tweenManager);
					}
					else if (objOn) {
						Tween.set(bg1, SpriteAccessor.ALPHA).target(1).start(tweenManager);	
						Tween.set(bg2, SpriteAccessor.ALPHA).target(1).start(tweenManager);	
						Tween.set(beach, SpriteAccessor.ALPHA).target(1).start(tweenManager);	
						Tween.set(items, SpriteAccessor.ALPHA).target(1).start(tweenManager);
						Tween.set(player, SpriteAccessor.ALPHA).target(1).start(tweenManager);
						Tween.set(title, SpriteAccessor.ALPHA).target(1).start(tweenManager);
						Tween.to(bg1, SpriteAccessor.ALPHA, 2).target(0.4f).start(tweenManager);
						Tween.to(bg2, SpriteAccessor.ALPHA, 2).target(0.4f).start(tweenManager);
						Tween.to(beach, SpriteAccessor.ALPHA, 2).target(0.4f).start(tweenManager);
						Tween.to(items, SpriteAccessor.ALPHA, 2).target(0.4f).start(tweenManager);
						Tween.to(player, SpriteAccessor.ALPHA, 2).target(0.4f).start(tweenManager);
						Tween.to(title, SpriteAccessor.ALPHA, 2).target(0.4f).setCallback(new TweenCallback()
							{					
								@Override
								public void onEvent(int type, BaseTween<?> source) {
									((Game) Gdx.app.getApplicationListener()).setScreen(new Objectives());								}
							}).start(tweenManager);
					}
					else if (exitOn) Gdx.app.exit();
					break;
				}
				return false;
			}
		}, player));
	
		//TWEEN\\
		
		tweenManager = new TweenManager();
		
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());
		Tween.registerAccessor(Actor.class, new ActorAccessor());
		
		//fade in from white
		Tween.set(bg1, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.set(bg2, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.set(beach, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.set(items, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.set(player, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.set(title, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.to(bg1, SpriteAccessor.ALPHA, 3).target(1).start(tweenManager);
		Tween.to(bg2, SpriteAccessor.ALPHA, 4).target(1).start(tweenManager);
		Tween.to(beach, SpriteAccessor.ALPHA, 1).target(1).start(tweenManager);
		Tween.to(items, SpriteAccessor.ALPHA, 1).target(1).start(tweenManager);
		Tween.to(player, SpriteAccessor.ALPHA, 1.4f).target(1).start(tweenManager);
		Tween.to(title, SpriteAccessor.ALPHA, 1.4f).target(1).start(tweenManager);

		tweenManager.update(Float.MIN_VALUE);
		
		//AUDIO

		minim = new Minim(this);
		out = minim.getLineOut(Minim.STEREO, 1024);
		
		//play song
		AudioRecordingStream file = minim.loadFileStream(fileName, 1024, true);
		filePlayer = new FilePlayer(file);
		filePlayer.loop();
		
		filePlayer.patch(out);
		out.mute();
		
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
		map.dispose();
		renderer.dispose();
		playerAtlas.dispose();
		beachAtlas.dispose();
		minim.dispose();
		bg1.getTexture().dispose();
		bg2.getTexture().dispose();
		beach.getTexture().dispose();
		title.getTexture().dispose();
		items.getTexture().dispose();
		player.getTexture().dispose();
	}

}
