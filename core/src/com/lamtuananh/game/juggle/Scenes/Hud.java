package com.lamtuananh.game.juggle.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.lamtuananh.game.juggle.JuggleGame;
import com.lamtuananh.game.juggle.Screens.PlayScreen;
import com.lamtuananh.game.juggle.Sprites.Ball;

/**
 * Created by brentaureli on 8/17/15.
 */
public class Hud implements Disposable {

    //Scene2D.ui Stage and its own Viewport for HUD
    public Stage stage;
    private Viewport viewport;

    //Mario score/time Tracking Variables
    public  static Integer worldTimer;
    private boolean timeUp; // true when the world timer reaches 0
    public static float timeCount;
    private static Integer score;

    //Scene2D widgets
    private Label countdownLabel;
    private static Label scoreLabel;
    private Label timeLabel;
    private Label levelLabel;
    private Label worldLabel;
    private Label marioLabel;
    private BitmapFont font;

    public Hud(SpriteBatch sb){
        //define our tracking variables
        worldTimer = 0;
        timeCount = 0;
        score = 0;


        //setup the HUD viewport using a new camera seperate from our gamecam
        //define our stage using that viewport and our games spritebatch
        viewport = new FitViewport(JuggleGame.V_WIDTH, JuggleGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        //define a table used to organize our hud's labels
        Table table = new Table();
        //Top-Align table
        table.top();
        //make the table fill the entire stage
        table.setFillParent(true);
        font = JuggleGame.manager.get("Font/win_table.fnt",BitmapFont.class);
        //define our labels using the String, and a Label style consisting of a font and color
        countdownLabel = new Label(worldTimer.toString(), new Label.LabelStyle(font, Color.WHITE));
        scoreLabel =new Label(Ball.ballNumber.toString(), new Label.LabelStyle(font, Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("0", new Label.LabelStyle(font, Color.WHITE));
        worldLabel = new Label("HIGHTSCORE", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        marioLabel = new Label("BALL", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        //add our labels to our table, padding the top, and giving them all equal width with expandX
        table.add(marioLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        //add a second row to our table
        table.row();
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countdownLabel).expandX();

        //add our table to the stage
        stage.addActor(table);

    }

    public void update(float dt){
        timeCount += dt;
        if(timeCount >= 1){
            if (worldTimer >= 0) {
                worldTimer++;
            } else {
                timeUp = true;
            }
            countdownLabel.setText(worldTimer.toString());
            timeCount = 0;
        }
        scoreLabel.setText(Ball.ballNumber.toString());
        levelLabel.setText(PlayScreen.hightscore.toString());
    }

    public static void addScore(int value){
        score += value;
        scoreLabel.setText(Ball.ballNumber.toString());
    }

    @Override
    public void dispose() { stage.dispose(); }

    public boolean isTimeUp() { return timeUp; }
}
