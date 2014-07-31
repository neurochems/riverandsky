package es.dfp.riverandsky.screens;

import java.util.Iterator;

import processing.core.PApplet;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.spi.AudioRecordingStream;
import ddf.minim.ugens.FilePlayer;
import es.dfp.riverandsky.entities.ScrollingSprite;
import es.dfp.riverandsky.physics.CollisionListener;
import es.dfp.riverandsky.physics.MapBodyManager;
import es.dfp.riverandsky.tween.ActorAccessor;
import es.dfp.riverandsky.tween.SpriteAccessor;

public class Game extends PApplet implements Screen {

	//level and scene management
	private static int level = 1;
	private static boolean end = false;
	
	//tween
	private TweenManager tweenManager;

	//box2d
	private World world;
	private Box2DDebugRenderer debug;
	
	private final float TIMESTEP = 1 / 60f;
	private final int VELOCITYITERATIONS = 8, POSITIONITERATIONS = 3;
	private float accumulator;
	
	private MapBodyManager mapBodyManager;
	
	//camera, map, and renderer
	private OrthographicCamera camera;
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	
	//player
	private TextureAtlas playerAtlas;
	//private Player player;
	private Body player;
	private float speed = 1;
	private float jumpHeight = 20;
	private Vector2 movement = new Vector2();

	enum moveState {MS_STOP, MS_RIGHT, MS_LEFT};
	private moveState move = moveState.MS_STOP;
	
	public static int numFootContacts = 0;
	private int jumpTimeout = 0;
	
	//background and tile map layers
	private TextureAtlas cosmicAtlas, beachAtlas, isleAtlas;
	private Sprite bg1, bg2, grass;
	private ScrollingSprite cheese, land, sign, cosmic, coffee, cake, beach;		//just passing through
	private ScrollingSprite straw, djembe, isle, boler, cloud;
	private float bg1X, bg2X, cheeseX, landX, signX, cosmicX, coffeeX, cakeX, beachX, strawX, djembeX, isleX, bolerX, grassX;

	private float endX;
	private boolean holdScreen = true;
	private static boolean fadeOut = false;
	private boolean tweenOut = false;
	private float lastCameraX;
	
	private int[] platforms = new int[] {0}, pickups = new int[] {1};
	
	//pickups 
	private TextureAtlas itemAtlas;
	private ScrollingSprite orb, endItem;
	private static int itemCollected = 0;
	
	private boolean itemTease = true;
	
	//minim
	private Minim minim;
	private FilePlayer filePlayer;
	private AudioOutput out;
	//private AudioInput in;

	private String fileName = "audio/Daniel Romano - River And Sky.mp3";		//change at level selection
	//private String fileName = "audio/Jas - River and Sky Recording_01.mp3";		//change at level selection

	
	//private Delay spacey;
	//private MoogFilter moog;
	//private Flanger flange;
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//if (!end) camera.position.set(((player.getX() - 500) + bg1.getWidth() / 2), bg1.getY() + bg1.getHeight() / 2, 0);
		//camera.position.y = player.getBody().getPosition().y > camera.position.y ? player.getBody().getPosition().y : camera.position.y;
		camera.update();
		
		//renderer.setView(camera);
		//renderer.getSpriteBatch().setProjectionMatrix(camera.combined);

		//acculmulator makes constant physics update time, to coincide with graphics delta
		accumulator += delta;
		while (accumulator > TIMESTEP) {
			world.step(TIMESTEP, VELOCITYITERATIONS, POSITIONITERATIONS);
			accumulator -= TIMESTEP;
		}

		movement.x = player.getLinearVelocity().x;
		float desiredVel = 0;
		switch (move) {
		case MS_LEFT: desiredVel = Math.max(movement.x - 0.6f, -15.0f); break; 
		case MS_STOP: desiredVel = movement.x * 0.93f; break; 
		case MS_RIGHT: desiredVel = Math.min(movement.x + 0.6f, 15.0f); break; 
		}
		float deltaV = desiredVel - movement.x;
		float impulse = player.getMass() * deltaV;	//f = mv / t		
		player.applyLinearImpulse(new Vector2 (impulse, movement.y), player.getWorldCenter(), true);
		
		if (jumpTimeout < -35) movement.y = 0;				//set long press jump limit 
		jumpTimeout--;
		
		System.out.println("jumpTimeout: " + jumpTimeout);

		//RENDER\\
/*		
		renderer.getSpriteBatch().begin();													//START

			drawBackground();
			drawMidground();
			
			if (itemTease) orb.drawAnimated(renderer.getSpriteBatch(), (landX + 2070) + camera.position.x, 400, 0.2f);

			if (!end) drawPlatformLayer1();
			
		renderer.getSpriteBatch().end();													//END

		renderer.render(pickups);
		renderer.render(platforms);

		renderer.getSpriteBatch().begin();													//START
			
			endItem.drawAnimated(renderer.getSpriteBatch(), 46010, 190, 0.25f);
			cloud.drawStatic(renderer.getSpriteBatch(), 44850, 60, 0.7f);

			player.draw(renderer.getSpriteBatch());

			if (!end) drawPlatformLayer2();

			drawForeground();
		
		renderer.getSpriteBatch().end();													//END
*/
		debug.render(world, camera.combined);

		//scroll speed
		//setScrollSpeeds();

		//reset separator
		//resetSeparators();

		/*if (!end) lastCameraX = camera.position.x;
		if (end) { 
			camera.position.set(lastCameraX, bg1.getY() + bg1.getHeight() / 2, 0);
			player.setX(player.getX() + 4);
		}
		
		if (end) {
			if (holdScreen) {
				endX = player.getX();
				camera.position.x = endX;
				holdScreen = false;
			}
		}
		if (fadeOut) fadeOut();
		 
		
		tweenManager.update(delta);
*/
	}


	private void drawBackground() {
		renderer.getSpriteBatch().draw(bg1, (bg1X - Gdx.graphics.getWidth()) + camera.position.x, 0);
		renderer.getSpriteBatch().draw(bg1, bg1X + camera.position.x, 0);
		renderer.getSpriteBatch().draw(bg1, bg1X + camera.position.x - Gdx.graphics.getWidth() * 2, 0);
		
		renderer.getSpriteBatch().draw(bg2, (bg2X - Gdx.graphics.getWidth()) + camera.position.x, 0);
		renderer.getSpriteBatch().draw(bg2, bg2X + camera.position.x, 0);
		renderer.getSpriteBatch().draw(bg2, bg2X + camera.position.x - Gdx.graphics.getWidth() * 2, 0);
	}

	private void drawMidground() {
		cheese.drawStatic(renderer.getSpriteBatch(), (cheeseX - 1000) + camera.position.x, -400, 0.3f);
		land.drawStatic(renderer.getSpriteBatch(), (landX + 1500) + camera.position.x, -700, 0.3f);
		sign.drawStatic(renderer.getSpriteBatch(), (signX + 2500) + camera.position.x, -500, 0.1f);
		cosmic.drawAnimated(renderer.getSpriteBatch(), (cosmicX + 7000) + camera.position.x, 300, 1);
		coffee.drawStatic(renderer.getSpriteBatch(), (coffeeX + 3000) + camera.position.x, -400, 0.4f);
		cake.drawStatic(renderer.getSpriteBatch(), (cakeX + 250) + camera.position.x, -350, 0.35f);
		beach.drawAnimated(renderer.getSpriteBatch(), (beachX + 9600) + camera.position.x, -400, 0.5f);
	}

	private void drawPlatformLayer1() {
		boler.drawStatic(renderer.getSpriteBatch(), (bolerX + 5200) + camera.position.x, -340, 0.3f);
		djembe.drawStatic(renderer.getSpriteBatch(), (djembeX + 2700) + camera.position.x, -485, 0.2325f);

	}

	private void drawPlatformLayer2() {
		straw.drawStatic(renderer.getSpriteBatch(), (strawX + 1350) + camera.position.x, -284, 0.3f);
		isle.drawAnimated(renderer.getSpriteBatch(), (isleX + 4000) + camera.position.x, 340, 0.5f);
		
	}

	private void drawForeground() {
		renderer.getSpriteBatch().draw(grass, (grassX - Gdx.graphics.getWidth()) + camera.position.x, 0);
		renderer.getSpriteBatch().draw(grass, grassX + camera.position.x, 0);
		renderer.getSpriteBatch().draw(grass, grassX + camera.position.x - Gdx.graphics.getWidth() * 2, 0);		
	}
	
	private void setScrollSpeeds() {
		//background
		bg1X -= 0.5f;
		bg2X -= 0.7f;
		
		//midground
		cheeseX -= 1.5f;
		landX -= 1.5f;
		signX -= 1.2f;
		cosmicX -= 1.5f;
		coffeeX -= 1.5f;
		cakeX -= 1.5f;
		beachX -= 1.5f;
		
		//platform
		strawX -= 4;
		bolerX -= 4;
		djembeX -= 4;
		isleX -= 4;
		
		//foreground
		if (!end) grassX -= 7;		
		
		/*if (end) {
			//background
			bg1X = 0;
			bg2X = 0;
			
			//midground
			cheeseX = 0;
			landX = 0;
			signX = 0;
			cosmicX = 0;
			coffeeX = 0;
			cakeX = 0;
			beachX = 0;
			
			//platform
			strawX = 0;
			bolerX = 0;
			djembeX = 0;
			isleX = 0;
			
			//foreground
			grassX = 0;		
		}*/
	}
	
	private void resetSeparators() {
		//backgrounds
		if (bg1X <= -(Gdx.graphics.getWidth() / 2)) bg1X = Gdx.graphics.getWidth() / 2;
		if (bg2X <= -(Gdx.graphics.getWidth() / 2)) bg2X = Gdx.graphics.getWidth() / 2;
		
		//midground
		if (cheeseX <= -(Gdx.graphics.getWidth() * 2)) cheeseX = Gdx.graphics.getWidth() * 1.3f;
		if (landX <= -(Gdx.graphics.getWidth() * 2)) { 
			landX = Gdx.graphics.getWidth() * 1.3f;
			itemTease = false;
		}
		if (signX <= -(Gdx.graphics.getWidth() * 4)) signX = Gdx.graphics.getWidth() * 1.3f;
		if (cosmicX <= -(Gdx.graphics.getWidth() * 5)) cosmicX = Gdx.graphics.getWidth() * 1.3f;
		if (coffeeX <= -(Gdx.graphics.getWidth() * 4)) coffeeX = Gdx.graphics.getWidth() * 1.3f;
		if (cakeX <= -(Gdx.graphics.getWidth() * 3)) cakeX = Gdx.graphics.getWidth() * 1.3f;
		if (beachX <= -(Gdx.graphics.getWidth() * 7)) beachX = Gdx.graphics.getWidth() * 1.3f;
		
		//platform
		if (strawX <= -(Gdx.graphics.getWidth()) * 4) strawX = Gdx.graphics.getWidth() / 2;
		if (bolerX <= -(Gdx.graphics.getWidth()) * 4) bolerX = Gdx.graphics.getWidth() / 2;
		if (djembeX <= -(Gdx.graphics.getWidth()) * 2.4f) djembeX = Gdx.graphics.getWidth() / 2;
		if (isleX <= -(Gdx.graphics.getWidth()) * 3) isleX = Gdx.graphics.getWidth() / 2;
		
		//foreground
		if (grassX <= -(Gdx.graphics.getWidth() / 2)) grassX = Gdx.graphics.getWidth() / 2;		
	}

	private void fadeOut() {
		tweenOut = true;
		if (tweenOut) {
			Tween.set(bg1, SpriteAccessor.ALPHA).target(1).start(tweenManager);
			Tween.set(bg2, SpriteAccessor.ALPHA).target(1).start(tweenManager);
			Tween.set(cheese, SpriteAccessor.ALPHA).target(1).start(tweenManager);
			Tween.set(land, SpriteAccessor.ALPHA).target(1).start(tweenManager);
			Tween.set(sign, SpriteAccessor.ALPHA).target(1).start(tweenManager);
			Tween.set(cosmic, SpriteAccessor.ALPHA).target(1).start(tweenManager);
			Tween.set(cake, SpriteAccessor.ALPHA).target(1).start(tweenManager);
			Tween.set(beach, SpriteAccessor.ALPHA).target(1).start(tweenManager);
			Tween.set(straw, SpriteAccessor.ALPHA).target(1).start(tweenManager);
			Tween.set(boler, SpriteAccessor.ALPHA).target(1).start(tweenManager);
			Tween.set(djembe, SpriteAccessor.ALPHA).target(1).start(tweenManager);
			Tween.set(isle, SpriteAccessor.ALPHA).target(1).start(tweenManager);
			Tween.set(grass, SpriteAccessor.ALPHA).target(1).start(tweenManager);
			Tween.set(cloud, SpriteAccessor.ALPHA).target(1).start(tweenManager);
			Tween.set(orb, SpriteAccessor.ALPHA).target(1).start(tweenManager);
			Tween.set(endItem, SpriteAccessor.ALPHA).target(1).start(tweenManager);
			//Tween.set(map, ActorAccessor.ALPHA).target(1).start(tweenManager);
			//Tween.set(player, SpriteAccessor.ALPHA).target(1).start(tweenManager);
			Tween.to(bg1, SpriteAccessor.ALPHA, 3).target(0).start(tweenManager);
			Tween.to(bg2, SpriteAccessor.ALPHA, 4).target(0).start(tweenManager);
			Tween.to(cheese, SpriteAccessor.ALPHA, 1).target(0).start(tweenManager);
			Tween.to(land, SpriteAccessor.ALPHA, 1).target(0).start(tweenManager);
			Tween.to(sign, SpriteAccessor.ALPHA, 1).target(0).start(tweenManager);
			Tween.to(cosmic, SpriteAccessor.ALPHA, 1).target(0).start(tweenManager);
			Tween.to(cake, SpriteAccessor.ALPHA, 1).target(0).start(tweenManager);
			Tween.to(beach, SpriteAccessor.ALPHA, 1).target(0).start(tweenManager);
			Tween.to(straw, SpriteAccessor.ALPHA, 1).target(0).start(tweenManager);
			Tween.to(boler, SpriteAccessor.ALPHA, 1).target(0).start(tweenManager);
			Tween.to(djembe, SpriteAccessor.ALPHA, 1).target(0).start(tweenManager);
			Tween.to(isle, SpriteAccessor.ALPHA, 1).target(0).start(tweenManager);
			Tween.to(grass, SpriteAccessor.ALPHA, 1).target(0).start(tweenManager);
			Tween.to(cloud, SpriteAccessor.ALPHA, 1).target(0).start(tweenManager);
			Tween.to(orb, SpriteAccessor.ALPHA, 1).target(0).start(tweenManager);
			//Tween.to(player, SpriteAccessor.ALPHA, 1.4f).target(0).start(tweenManager);
			//Tween.to(map, ActorAccessor.ALPHA, 1.4f).target(0).start(tweenManager);
			Tween.to(endItem, SpriteAccessor.ALPHA, 1.4f).target(0).setCallback(new TweenCallback()
				{					
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new es.dfp.riverandsky.screens.MainMenu());}   //score screen eventually, unless you want it to be rendered in the black ending??
				}).start(tweenManager);
			
			tweenOut = false;
		}
	}
	
	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width / 10;
		camera.viewportHeight = height / 10;
	}

	@Override
	public void show() {
		
		//CAMERA\\
		camera = new OrthographicCamera(Gdx.graphics.getWidth() / 10, Gdx.graphics.getHeight() / 10);
		
		//WORLD\\
		world = new World(new Vector2(0, -9.81f), true);
		debug = new Box2DDebugRenderer();
		
		//TILEMAP & RENDERER\\
		//mapBodyManager = new MapBodyManager(world, 1.0f, Gdx.files.internal("maps/materials.json"), 1);
		map = new TmxMapLoader().load("maps/grass1.tmx");
		renderer = new OrthogonalTiledMapRenderer(map);
		//mapBodyManager.createPhysics(map, "physics");
		
		//BOX2D TEST SHAPES
		
		//player\\
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(-50, -48);
		
		PolygonShape playerShape = new PolygonShape();
		playerShape.setAsBox(0.75f, 1.5f);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = playerShape;
		fixtureDef.density = 0.6f;
		fixtureDef.friction = 0.5f;
		fixtureDef.restitution = 0;
		
		player = world.createBody(bodyDef);
		player.createFixture(fixtureDef);
		
		player.setFixedRotation(true);
		
		//foot sensor
		playerShape.setAsBox(0.1f, 0.1f, new Vector2(0, -1.5f), 0);
		fixtureDef.isSensor = true;
		Fixture footSensorFixture = player.createFixture(fixtureDef);
		footSensorFixture.setUserData(Integer.valueOf(3));
		System.out.println("footSensorFixture.getUserData(): " + footSensorFixture.getUserData());
		
		playerShape.dispose();

		//ball
		bodyDef.position.set(-10, -25);
		
		CircleShape ballShape = new CircleShape();
		ballShape.setRadius(0.5f);
		
		fixtureDef.isSensor = false;
		fixtureDef.shape = ballShape;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 0.1f;
		fixtureDef.restitution = 0.75f;
		
		world.createBody(bodyDef).createFixture(fixtureDef);
		
		ballShape.dispose();
		
		//ground
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(0, 0);
		
		ChainShape groundShape = new ChainShape();
		groundShape.createChain(new Vector2[] { new Vector2(-100, -50), new Vector2(15, -50), new Vector2(15, -25), new Vector2(100, -25)});
		
		fixtureDef.shape = groundShape;
		fixtureDef.friction = 0.5f;
		fixtureDef.restitution = 0;
		
		world.createBody(bodyDef).createFixture(fixtureDef);
		
		groundShape.dispose();
		
		//test platform 1
		bodyDef.position.set(0, 0);
		
		ChainShape p1Shape = new ChainShape();
		p1Shape.createChain(new Vector2[] { new Vector2(25, 0), new Vector2(15, 0)});
		
		fixtureDef.shape = p1Shape;
		fixtureDef.friction = 0.5f;
		fixtureDef.restitution = 0;
		
		world.createBody(bodyDef).createFixture(fixtureDef);
		
		p1Shape.dispose();

		//test platform 2
		bodyDef.position.set(0, 0);
		
		ChainShape p2Shape = new ChainShape();
		p2Shape.createChain(new Vector2[] { new Vector2(-25, 20), new Vector2(-35, 20)});
		
		fixtureDef.shape = p2Shape;
		fixtureDef.friction = 0.5f;
		fixtureDef.restitution = 0;
		
		world.createBody(bodyDef).createFixture(fixtureDef);
		
		p2Shape.dispose();

		//test platform 3
		bodyDef.position.set(0, 0);
		
		ChainShape p3Shape = new ChainShape();
		p2Shape.createChain(new Vector2[] { new Vector2(25, 30), new Vector2(15, 30)});
		
		fixtureDef.shape = p3Shape;
		fixtureDef.friction = 0.5f;
		fixtureDef.restitution = 0;
		
		world.createBody(bodyDef).createFixture(fixtureDef);
		
		p3Shape.dispose();

		//test platform 4
		bodyDef.position.set(0, 0);
		
		ChainShape p4Shape = new ChainShape();
		p2Shape.createChain(new Vector2[] { new Vector2(75, 30), new Vector2(65, 30)});
		
		fixtureDef.shape = p4Shape;
		fixtureDef.friction = 0.5f;
		fixtureDef.restitution = 0;
		
		world.createBody(bodyDef).createFixture(fixtureDef);
		
		p4Shape.dispose();
		
		//set contact listener
		world.setContactListener(new CollisionListener());
		
		Gdx.input.setInputProcessor(new InputAdapter() {

			@Override
			public boolean keyDown(int keycode) {
				switch(keycode) {
				case Keys.ESCAPE:
					Gdx.app.exit();
					break;
				case Keys.UP:
					if (numFootContacts < 1) break;
					if (jumpTimeout > 0) break;
					player.applyLinearImpulse(0, jumpHeight, player.getLocalCenter().x, player.getLocalCenter().y, true);
					jumpTimeout = 15;
					movement.y = speed;
					break;
				case Keys.DOWN:
					movement.y = -speed;
					break;
				case Keys.LEFT:
					move = moveState.MS_LEFT;
					//movement.x = -speed;
					break;
				case Keys.RIGHT:
					move = moveState.MS_RIGHT;
					//movement.x = speed;
				}
				return true;
			}
			
			@Override
			public boolean keyUp(int keycode) {
				switch(keycode) {
				case Keys.UP:
				case Keys.DOWN:
					movement.y = 0;
					break;
				case Keys.LEFT:
				case Keys.RIGHT:
					move = moveState.MS_STOP;
					//movement.x = 0;
				}
				return true;
			}
			
			@Override
			public boolean scrolled(int amount) {
				camera.zoom += amount / 25f;
				return true;
			}

		});
		
		//SCENE LAYERS\\
		//bg
/*		Texture bg1Texture = new Texture("img/scene/bg/trees1_1.png");
		Texture bg2Texture = new Texture("img/scene/bg/trees1_2.png");
		bg1 = new Sprite(bg1Texture);
		bg2 = new Sprite(bg2Texture);
		bg1X = Gdx.graphics.getWidth() + camera.position.x;
		bg2X = Gdx.graphics.getWidth() + camera.position.x;
		
		//mid
		Texture cheeseTexture = new Texture("img/scene/mid/cheese.png");
		Texture landTexture = new Texture("img/scene/mid/land.png");
		Texture signTexture = new Texture("img/scene/mid/sign.png");
		cosmicAtlas = new TextureAtlas("img/scene/mid/cosmic.pack");
		Animation cosmicAnimation = new Animation(1/5f, cosmicAtlas.findRegions("cosmic"), Animation.PlayMode.LOOP);
		Texture coffeeTexture = new Texture("img/scene/mid/coffee.png");
		Texture cakeTexture = new Texture("img/scene/mid/cupcake.png");
		beachAtlas = new TextureAtlas("img/scene/mid/beach.pack");
		Animation beachAnimation = new Animation(1/2f, beachAtlas.findRegions("beach"), Animation.PlayMode.LOOP);
		cheese = new ScrollingSprite(cheeseTexture);
		land = new ScrollingSprite(landTexture);
		sign = new ScrollingSprite(signTexture);
		cosmic = new ScrollingSprite(cosmicAnimation);
		coffee = new ScrollingSprite(coffeeTexture);
		cake = new ScrollingSprite(cakeTexture);
		beach= new ScrollingSprite(beachAnimation);
		cheeseX = Gdx.graphics.getWidth() + camera.position.x;
		landX = Gdx.graphics.getWidth() + camera.position.x;
		signX = Gdx.graphics.getWidth() + camera.position.x;
		cosmicX = Gdx.graphics.getWidth() + camera.position.x;
		coffeeX = Gdx.graphics.getWidth() + camera.position.x;
		cakeX = Gdx.graphics.getWidth() + camera.position.x;
		beachX = Gdx.graphics.getWidth() + camera.position.x;

		//plat
		Texture strawTexture = new Texture("img/scene/plat/straw.png");
		Texture bolerTexture = new Texture("img/scene/plat/boler.png");
		Texture djembeTexture = new Texture("img/scene/plat/djembe.png");
		isleAtlas = new TextureAtlas("img/scene/plat/isle.pack");
		Animation isleAnimation = new Animation(1/2f, isleAtlas.findRegions("isle"), Animation.PlayMode.LOOP);
		Texture cloudTexture = new Texture("img/scene/plat/end.png");
		straw = new ScrollingSprite(strawTexture);
		boler = new ScrollingSprite(bolerTexture);
		djembe = new ScrollingSprite(djembeTexture);
		isle = new ScrollingSprite(isleAnimation);
		cloud = new ScrollingSprite(cloudTexture);
		strawX = Gdx.graphics.getWidth() + camera.position.x;
		bolerX = Gdx.graphics.getWidth() + camera.position.x;
		djembeX = Gdx.graphics.getWidth() + camera.position.x;
		isleX = Gdx.graphics.getWidth() + camera.position.x;
		

		//fore
		Texture grassTexture = new Texture("img/scene/fore/grass1.png");
		grass = new Sprite(grassTexture);
		grassX = Gdx.graphics.getWidth() + camera.position.x;
		
		//PLAYER & ITEMS\\
		playerAtlas = new TextureAtlas("img/sprite/holly.pack");
		itemAtlas = new TextureAtlas("img/items/items.pack");
		
		Animation run = new Animation(1/13f, playerAtlas.findRegions("run"), Animation.PlayMode.LOOP);
		Animation jumpDown = new Animation(1/13f, playerAtlas.findRegions("jumpDown"), Animation.PlayMode.NORMAL);
		Animation jumpUp = new Animation(1/13f, playerAtlas.findRegions("jumpUp"), Animation.PlayMode.NORMAL);
		Animation idle = new Animation(1/13f, playerAtlas.findRegions("idle"), Animation.PlayMode.LOOP);
		
		Animation orbAni = new Animation(1/6f, itemAtlas.findRegions("orb"), Animation.PlayMode.LOOP);
		Animation endItemAni = new Animation(1/6f, itemAtlas.findRegions("melon"), Animation.PlayMode.LOOP);

		//player = new Player(world, 2, 1, 0.5f, run, jumpUp, jumpDown, idle, (TiledMapTileLayer) map.getLayers().get("pickups"));
		orb = new ScrollingSprite(orbAni);
		endItem = new ScrollingSprite(endItemAni);
		
		//world.setContactFilter(player);
		//world.setContactListener(player);
		
		//player.setPosition(500, 180);
		//player.setSize(90,90);
		
		orb.setPosition(950, 500);
		orb.setAlpha(0.9f);
		endItem.setPosition(950, 500);
		
		//check controllers
		for (Controller controller: Controllers.getControllers()) {
			Gdx.app.log("Connected controller: ", controller.getName());
		}

		//ANIMATED TILES -- PICKUPS\\
		
		// frames
		Array<StaticTiledMapTile> melonTiles = new Array<StaticTiledMapTile>(6);
		Array<StaticTiledMapTile> grilledTiles = new Array<StaticTiledMapTile>(6);
		Array<StaticTiledMapTile> cheesusTiles = new Array<StaticTiledMapTile>(6);
		Array<StaticTiledMapTile> appleTiles = new Array<StaticTiledMapTile>(6);
		Array<StaticTiledMapTile> kiwiTiles = new Array<StaticTiledMapTile>(6);
		Array<StaticTiledMapTile> lemonTiles = new Array<StaticTiledMapTile>(6);
		Array<StaticTiledMapTile> orangeTiles = new Array<StaticTiledMapTile>(6);
		Array<StaticTiledMapTile> bluitTiles = new Array<StaticTiledMapTile>(6);
		Array<StaticTiledMapTile> raspTiles = new Array<StaticTiledMapTile>(6);

		// get the frame tiles
		Iterator<TiledMapTile> tiles = map.getTileSets().getTileSet("grass").iterator();
		while(tiles.hasNext()) {
			TiledMapTile tile = tiles.next();
			if(tile.getProperties().containsKey("animation") && tile.getProperties().get("animation", String.class).equals("melon"))
				melonTiles.add((StaticTiledMapTile) tile);
			if(tile.getProperties().containsKey("animation") && tile.getProperties().get("animation", String.class).equals("grilled"))
				grilledTiles.add((StaticTiledMapTile) tile);
			if(tile.getProperties().containsKey("animation") && tile.getProperties().get("animation", String.class).equals("cheesus"))
				cheesusTiles.add((StaticTiledMapTile) tile);
			if(tile.getProperties().containsKey("animation") && tile.getProperties().get("animation", String.class).equals("apple"))
				appleTiles.add((StaticTiledMapTile) tile);
			if(tile.getProperties().containsKey("animation") && tile.getProperties().get("animation", String.class).equals("kiwi"))
				kiwiTiles.add((StaticTiledMapTile) tile);
			if(tile.getProperties().containsKey("animation") && tile.getProperties().get("animation", String.class).equals("lemon"))
				lemonTiles.add((StaticTiledMapTile) tile);
			if(tile.getProperties().containsKey("animation") && tile.getProperties().get("animation", String.class).equals("orange"))
				orangeTiles.add((StaticTiledMapTile) tile);
			if(tile.getProperties().containsKey("animation") && tile.getProperties().get("animation", String.class).equals("bluit"))
				bluitTiles.add((StaticTiledMapTile) tile);
			if(tile.getProperties().containsKey("animation") && tile.getProperties().get("animation", String.class).equals("rasp"))
				raspTiles.add((StaticTiledMapTile) tile);
		}

		// create the animated tile
		AnimatedTiledMapTile animatedMelonTile = new AnimatedTiledMapTile(1 / 5f, melonTiles);
		AnimatedTiledMapTile animatedGrilledTile = new AnimatedTiledMapTile(1 / 5f, grilledTiles);
		AnimatedTiledMapTile animatedCheesusTile = new AnimatedTiledMapTile(1 / 5f, cheesusTiles);
		AnimatedTiledMapTile animatedAppleTile = new AnimatedTiledMapTile(1 / 5f, appleTiles);
		AnimatedTiledMapTile animatedKiwiTile = new AnimatedTiledMapTile(1 / 5f, kiwiTiles);
		AnimatedTiledMapTile animatedLemonTile = new AnimatedTiledMapTile(1 / 5f, lemonTiles);
		AnimatedTiledMapTile animatedOrangeTile = new AnimatedTiledMapTile(1 / 5f, orangeTiles);
		AnimatedTiledMapTile animatedBluitTile = new AnimatedTiledMapTile(1 / 5f, bluitTiles);
		AnimatedTiledMapTile animatedRaspTile = new AnimatedTiledMapTile(1 / 5f, raspTiles);

		// animated tilemap layer
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("pickups");

		// replace static with animated tile
		for(int x = 0; x < layer.getWidth(); x++)
			for(int y = 0; y < layer.getHeight(); y++) {
				Cell cell = layer.getCell(x, y);
/*				if(cell.getTile().getProperties().containsKey("animation") && cell.getTile().getProperties().get("animation", String.class).equals("melon"))
					cell.setTile(animatedMelonTile);
				if(cell.getTile().getProperties().containsKey("animation") && cell.getTile().getProperties().get("animation", String.class).equals("grilled"))
					cell.setTile(animatedGrilledTile);
				if(cell.getTile().getProperties().containsKey("animation") && cell.getTile().getProperties().get("animation", String.class).equals("cheesus"))
					cell.setTile(animatedCheesusTile);
				if(cell.getTile().getProperties().containsKey("animation") && cell.getTile().getProperties().get("animation", String.class).equals("apple"))
					cell.setTile(animatedAppleTile);
				if(cell.getTile().getProperties().containsKey("animation") && cell.getTile().getProperties().get("animation", String.class).equals("kiwi"))
					cell.setTile(animatedKiwiTile);
				if(cell.getTile().getProperties().containsKey("animation") && cell.getTile().getProperties().get("animation", String.class).equals("lemon"))
					cell.setTile(animatedLemonTile);
				if(cell.getTile().getProperties().containsKey("animation") && cell.getTile().getProperties().get("animation", String.class).equals("orange"))
					cell.setTile(animatedOrangeTile);
				if(cell.getTile().getProperties().containsKey("animation") && cell.getTile().getProperties().get("animation", String.class).equals("bluit"))
					cell.setTile(animatedBluitTile);
				if(cell.getTile().getProperties().containsKey("animation") && cell.getTile().getProperties().get("animation", String.class).equals("rasp"))
					cell.setTile(animatedRaspTile);
			}
		
		//TWEEN\\
		
		tweenManager = new TweenManager();
		
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());
		Tween.registerAccessor(Actor.class, new ActorAccessor());
		
		//fade in from white
		Tween.set(cheese, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.set(land, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.set(sign, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.set(cosmic, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.set(cake, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.set(beach, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.set(straw, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.set(boler, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.set(djembe, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.set(isle, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.set(grass, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.set(cloud, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.set(orb, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.set(endItem, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		//Tween.set(map, ActorAccessor.ALPHA).target(0).start(tweenManager);
		//Tween.set(player, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.set(bg1, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.set(bg2, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.to(bg1, SpriteAccessor.ALPHA, 3).target(1).start(tweenManager);
		Tween.to(bg2, SpriteAccessor.ALPHA, 4).target(1).start(tweenManager);
		Tween.to(cheese, SpriteAccessor.ALPHA, 1).target(1).start(tweenManager);
		Tween.to(land, SpriteAccessor.ALPHA, 1).target(1).start(tweenManager);
		Tween.to(sign, SpriteAccessor.ALPHA, 1).target(1).start(tweenManager);
		Tween.to(cosmic, SpriteAccessor.ALPHA, 1).target(1).start(tweenManager);
		Tween.to(cake, SpriteAccessor.ALPHA, 1).target(1).start(tweenManager);
		Tween.to(beach, SpriteAccessor.ALPHA, 1).target(1).start(tweenManager);
		Tween.to(straw, SpriteAccessor.ALPHA, 1).target(1).start(tweenManager);
		Tween.to(boler, SpriteAccessor.ALPHA, 1).target(1).start(tweenManager);
		Tween.to(djembe, SpriteAccessor.ALPHA, 1).target(1).start(tweenManager);
		Tween.to(isle, SpriteAccessor.ALPHA, 1).target(1).start(tweenManager);
		Tween.to(grass, SpriteAccessor.ALPHA, 1).target(1).start(tweenManager);
		Tween.to(cloud, SpriteAccessor.ALPHA, 1).target(1).start(tweenManager);
		Tween.to(orb, SpriteAccessor.ALPHA, 1).target(1).start(tweenManager);
		Tween.to(endItem, SpriteAccessor.ALPHA, 1.4f).target(1).start(tweenManager);
		//Tween.to(map, ActorAccessor.ALPHA, 1.4f).target(1).start(tweenManager);
		//Tween.to(player, SpriteAccessor.ALPHA, 1.4f).target(1).start(tweenManager);

		tweenManager.update(Float.MIN_VALUE);
		
		//create audio
		
		minim = new Minim(this);
		out = minim.getLineOut(Minim.STEREO, 1024);
		
		//play song
		AudioRecordingStream file = minim.loadFileStream(fileName, 1024, true);
		filePlayer = new FilePlayer(file);
		filePlayer.play();
		
		filePlayer.patch(out);
		out.mute();
		
		//Controllers.addListener(player);
		//Gdx.input.setInputProcessor(player);
*/
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
		world.dispose();
		debug.dispose();
/*		map.dispose();
		renderer.dispose();
		minim.dispose();
		playerAtlas.dispose();
		cosmicAtlas.dispose();
		beachAtlas.dispose();
		isleAtlas.dispose();
		bg1.getTexture().dispose();
		bg2.getTexture().dispose();
		cheese.getTexture().dispose();
		land.getTexture().dispose();
		sign.getTexture().dispose();
		cosmic.getTexture().dispose();
		cake.getTexture().dispose();
		beach.getTexture().dispose();
		straw.getTexture().dispose();
		djembe.getTexture().dispose();
		isle.getTexture().dispose();
		grass.getTexture().dispose();
		cloud.getTexture().dispose();
		orb.getTexture().dispose();
		endItem.getTexture().dispose();
		//player.getTexture().dispose();
		
		//mapBodyManager.destroyPhysics();
		
		Gdx.input.setInputProcessor(null); */
	}

	//GETTERS&&SETTERS
	
	public boolean isEnd() {
		return end;
	}

	public static void setEnd(boolean triggeredend) {
		end = triggeredend;
	}

	public static void setFadeOut(boolean fadeout) {
		fadeOut = fadeout;
	}

}
