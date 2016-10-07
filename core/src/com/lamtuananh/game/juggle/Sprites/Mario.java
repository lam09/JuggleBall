package com.lamtuananh.game.juggle.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.lamtuananh.game.juggle.JuggleGame;
import com.lamtuananh.game.juggle.Screens.PlayScreen;


/**
 * Created by brentaureli on 8/27/15.
 */
public class Mario extends Sprite {
    public enum State { FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD };
    public State currentState;
    public State previousState;

    public World world;
    public Body b2body;

    private TextureRegion marioStand;
    private Animation marioRun;
    private TextureRegion marioJump;
    private TextureRegion marioDead;
    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private Animation bigMarioRun;
    private Animation growMario;

    private float stateTimer;
    private boolean runningRight;
    private boolean marioIsBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;
    private boolean marioIsDead;
    private PlayScreen screen;

   // private Array<FireBall> fireballs;
    private Vector2 startPosition;
    private boolean kickingInProgress = false;
    private boolean kickingfinished = false;
    Vector2 touchPoint= new Vector2(-1,-1);

    public Mario(PlayScreen screen){
        //initialize default values
        this.screen = screen;
        this.world = screen.getWorld();
        startPosition = new Vector2(128 / JuggleGame.PPM,128/ JuggleGame.PPM);
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        //get run animation frames and add them to marioRun Animation
        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        marioRun = new Animation(0.1f, frames);

        frames.clear();

        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
        bigMarioRun = new Animation(0.1f, frames);

        frames.clear();

        //get set animation frames from growing mario
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        growMario = new Animation(0.2f, frames);


        //get jump animation frames and add them to marioJump Animation
        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0, 16, 16);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);

        //create texture region for mario standing
        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);

        //create dead mario texture region
        marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16);

        //define mario in Box2d
        defineMario(startPosition);

        //set initial values for marios location, width and height. And initial frame as marioStand.
        setBounds(0, 0, 16 / JuggleGame.PPM, 16 / JuggleGame.PPM);
        setRegion(marioStand);
      //  fireballs = new Array<FireBall>();

    }

    public void update(float dt){

        // time is up : too late mario dies T_T
        // the !isDead() method is used to prevent multiple invocation
        // of "die music" and jumping
        // there is probably better ways to do that but it works for now.
        if (screen.getHud().isTimeUp() && !isDead()) {
            die();
        }

        //update our sprite to correspond with the position of our Box2D body
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        //update sprite with the correct frame depending on marios current action
        if(Gdx.input.isTouched())
        {
            kickingInProgress = true;
            kickingfinished = false;
            touchPoint = new Vector2(Gdx.input.getX(), Gdx.input.getY());
          //  b2body.applyLinearImpulse(startPosition, touchPoint, true);
            System.out.println("touch point " + touchPoint.x + " " + touchPoint.y);
            //b2body.setLinearVelocity(touchPoint);
            b2body.setTransform(new Vector2(touchPoint.x/JuggleGame.PPM,1),0);
            b2body.setLinearVelocity(new Vector2(0,0));
//            b2body.setTransform(touchPoint,0);
            float x = touchPoint.x - b2body.getWorldCenter().x*JuggleGame.PPM;
            float y =JuggleGame.V_HEIGHT- touchPoint.y;// - b2body.getWorldCenter().y*MarioBros.PPM;
            //System.out.println("x y : " + x + " " + y);
            b2body.applyLinearImpulse(new Vector2(0,y*2.5f),b2body.getWorldCenter(),false);
        }
        if(b2body!=null) {
            System.out.println("b2body position " + b2body.getPosition().y);

            if (b2body.getPosition().y*100+getHeight() >= touchPoint.y && kickingInProgress) kickingfinished = true;
            if (kickingfinished) {
                b2body.setTransform(-1, -1, 0);
                kickingInProgress = false;
            }
        }
    }



    public void die() {

        if (!isDead()) {

            JuggleGame.manager.get("audio/music/mario_music.ogg", Music.class).stop();
            JuggleGame.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
            marioIsDead = true;
            Filter filter = new Filter();
            filter.maskBits = JuggleGame.NOTHING_BIT;

            for (Fixture fixture : b2body.getFixtureList()) {
                fixture.setFilterData(filter);
            }

            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
        }
    }

    public boolean isDead(){
        return marioIsDead;
    }

    public float getStateTimer(){
        return stateTimer;
    }

    public boolean isBig(){
        return marioIsBig;
    }

    public void jump(){
        if ( currentState != State.JUMPING ) {
            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            currentState = State.JUMPING;
        }
    }

/*    public void hit(Enemy enemy){
        if(enemy instanceof Turtle && ((Turtle) enemy).currentState == Turtle.State.STANDING_SHELL)
            ((Turtle) enemy).kick(enemy.b2body.getPosition().x > b2body.getPosition().x ? Turtle.KICK_RIGHT : Turtle.KICK_LEFT);
        else {
            if (marioIsBig) {
                marioIsBig = false;
                timeToRedefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                MarioBros.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
            } else {
                die();
            }
        }
    }
*/

    public void defineMario(Vector2 position){
        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
    //    bdef.position.set(MarioBros.V_WIDTH/2,MarioBros.V_HEIGHT/2);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(16 / JuggleGame.PPM);
        fdef.density = 30;
        fdef.restitution = 0.5f;
        fdef.filter.categoryBits = JuggleGame.MARIO_BIT;
        fdef.filter.maskBits = JuggleGame.GROUND_BIT |
                JuggleGame.BRICK_BIT ;


        fdef.shape = shape;
      //  fdef.friction = 0.1f;
       // fdef.restitution = 0.9f;
        b2body.createFixture(fdef).setUserData(this);
    }

    public void draw(Batch batch){
        super.draw(batch);
    }
}
