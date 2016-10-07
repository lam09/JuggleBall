package com.lamtuananh.game.juggle.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lamtuananh.game.juggle.JuggleGame;
import com.lamtuananh.game.juggle.Scenes.Hud;
import com.lamtuananh.game.juggle.Sprites.Ball;
import com.lamtuananh.game.juggle.Tools.B2WorldCreator;
import com.lamtuananh.game.juggle.Tools.WorldContactListener;


import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by brentaureli on 8/14/15.
 */
public class PlayScreen implements Screen {
    //Reference to our Game, used to set Screens
    public static Integer hightscore = 0;
    private JuggleGame game;
    private TextureAtlas atlas;
    public static boolean alreadyDestroyed = false;

    //basic playscreen variables
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;
    public Sound sound;
    //Tiled map variables
    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    //sprites
   // private Ball player;
    private Ball ball;
    private Array<Ball> balls;
    private Music music;


    public int states = State.MENU; // 0 - menu 1 - playing 2 gameover
    private Texture bgtexture;
    private BitmapFont info_font;
    public PlayScreen(JuggleGame game){
        atlas = new TextureAtlas("Mario_and_Enemies.pack");
        bgtexture = JuggleGame.manager.get("bg.jpg", Texture.class);
        sound = JuggleGame.manager.get("audio/sounds/ballTouch.mp3",Sound.class);
        info_font = JuggleGame.manager.get("Font/info_white.fnt",BitmapFont.class);
        this.game = game;
        //create cam used to follow mario through cam world
        gamecam = new OrthographicCamera();

        //create a FitViewport to maintain virtual aspect ratio despite screen size
        gamePort = new FitViewport(JuggleGame.V_WIDTH / JuggleGame.PPM, JuggleGame.V_HEIGHT / JuggleGame.PPM, gamecam);

        //create our game HUD for scores/timers/level info
        hud = new Hud(game.batch);

        //Load our map and setup our map renderer
        maploader = new TmxMapLoader();
        map = maploader.load("level3.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1  / JuggleGame.PPM);

        //initially set our gamcam to be centered correctly at the start of of map
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        //create our Box2D world, setting no gravity in X, -10 gravity in Y, and allow bodies to sleep
        world = new World(new Vector2(0, -15), true);
        //allows for debug lines of our box2d world.
        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator(this);

        //create mario in our game world
        //player = new Ball(this);
    //player.setPosition(MarioBros.V_WIDTH/2,MarioBros.V_HEIGHT);
       // ball = new Ball(this);
        world.setContactListener(new WorldContactListener());

        music = JuggleGame.manager.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        music.setVolume(0.3f);
        //music.play();

       // items = new Array<Item>();
        balls = new Array<Ball>();
        //balls.add(ball);
       // itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
    }

   // public void spawnItem(ItemDef idef){
    //    itemsToSpawn.add(idef);
  //  }


 /*   public void handleSpawningItems(){
        if(!itemsToSpawn.isEmpty()){
            ItemDef idef = itemsToSpawn.poll();
            if(idef.type == Mushroom.class){
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }

*/
    public TextureAtlas getAtlas(){
        return atlas;
    }

    @Override
    public void show() {


    }

    public void handleInput(float dt){
        if(states == State.MENU && Gdx.input.justTouched()) states = State.PLAYING;
        if(states==State.GAMEOVER) {
            System.out.print("GAME OVER");
            if(Hud.worldTimer > hightscore) hightscore = Hud.worldTimer;
            for(Ball ba:balls)
            {
                ba.remove();
            }
            balls.clear();
            Ball.ballNumber = 0;
            Hud.worldTimer = 0;
            timeToCreateNewBall=0;
            states= State.MENU;
           // createNewBall();
        }
    }
    Integer timeToCreateNewBall = 0;
    private void createNewBall(){
        if(Hud.worldTimer>=timeToCreateNewBall && Ball.ballNumber<=2)
        {
            timeToCreateNewBall+=5;
            Ball ba = new Ball(this);
            balls.add(ba);
        }
    }
    public void update(float dt){
        //handle user input first
        handleInput(dt);
   //     handleSpawningItems();

        if(states == State.PLAYING) {
            world.step(1 / 60f, 6, 2);
            createNewBall();
            //player.update(dt);
            if(balls!= null)
            for (Ball ba : balls)
                ba.update(dt);
            hud.update(dt);

        }
//        ball.update(dt);
 /*       for(Enemy enemy : creator.getEnemies()) {
            enemy.update(dt);
            if(enemy.getX() < player.getX() + 224 / MarioBros.PPM) {
                enemy.b2body.setActive(true);
            }
        }
*/


        //attach our gamecam to our players.x coordinate
     /*   if(player.currentState != Mario.State.DEAD) {
            gamecam.position.x = player.b2body.getPosition().x;
        }
*/
        //update our gamecam with correct coordinates after changes
        gamecam.update();
        //tell our renderer to draw only what our camera can see in our game world.
        renderer.setView(gamecam);

    }


    @Override
    public void render(float delta) {
        //separate our update logic from render
        update(delta);

        //Clear the game screen with Black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.batch.draw(bgtexture,0,0,JuggleGame.V_WIDTH,JuggleGame.V_HEIGHT);
        if(states==State.MENU) {
            info_font.draw(game.batch, "> TOUCH TO PLAY <", 30, 500);
        }
        game.batch.end();
        //render our game map
        renderer.render();

        //renderer our Box2DDebugLines
        if(states==State.PLAYING)
        b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
       // player.draw(game.batch);
      //  ball.draw(game.batch);
        if(states==State.PLAYING) {
            for (int i = 0 ; i <balls.size;i++)
                balls.get(i).draw(game.batch);
        }
        game.batch.end();

        //Set our batch to now draw what the Hud camera sees.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if(gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }

    }

    public boolean gameOver(){
    /*    if(player.currentState == Mario.State.DEAD && player.getStateTimer() > 3){
            return true;
        }*/
        return false;
    }

    @Override
    public void resize(int width, int height) {
        //updated our game viewport
        gamePort.update(width,height);

    }

    public TiledMap getMap(){
        return map;
    }
    public World getWorld(){
        return world;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        //dispose of all our opened resources
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }

    public Hud getHud(){ return hud; }
}
