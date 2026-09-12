package io.github.danila;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture idleTexture;
    private Texture runTexture;
    private Texture jumpTexture;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> runAnimation;
    private TextureRegion jumpFrame;
    private float animationTime = 0 ;

    //переменные по физике и положению
    private float x = 100;
    private float y = 50;
    private float speed =200;
    private float verticalSpeed =0;
    private float gravity =700 ;

// переключатели состояния
    private boolean onGround = true;
    private boolean move = false;
    private boolean facingLeft = false;
    int height =28;

    int width = 38;

    @Override
    public void create() {

        batch = new SpriteBatch();
        idleTexture = new Texture("assets/hero/Idle.png");
        runTexture = new Texture("assets/hero/Run.png");
        jumpTexture = new Texture("assets/hero/Jump.png");
        idleAnimation = rotater(idleTexture,12);
        runAnimation = rotater(runTexture,6);
        jumpFrame = new TextureRegion(jumpTexture);
    }

    private Animation<TextureRegion> rotater(Texture texture,int count){
        TextureRegion[] frames= new TextureRegion[count];

            int frameY=0;
        for (int i = 0; i < count; i++) {
            int frameX = i * width;

            frames[i] = new TextureRegion(
                texture,
                frameX,
                frameY,
                width,
                height
            );
        }

        float frameDuration = 0.1f;

        return new Animation<TextureRegion>(
            frameDuration,
            frames
        );
    }

    @Override
    public void render() {
        float rawDelta= Gdx.graphics.getDeltaTime();
        float delta= MathUtils.clamp(rawDelta,0f,1f/60f);
        move=false;


        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            x=x-speed*delta;
            move = true;
            facingLeft=true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            x=x+speed*delta;
                move=true;
                facingLeft=false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) &&onGround){
            verticalSpeed=350;
            onGround=false;


        }

        if (!onGround){
            y=y+verticalSpeed*delta;
            verticalSpeed=verticalSpeed-gravity*delta;
        }
        if (y<=50){
            y=50;
            verticalSpeed=0;
            onGround=true;

        }


        TextureRegion currentFrame;
        animationTime=animationTime+delta;

        if (!onGround){
            currentFrame=jumpFrame;

        }else if (move){
            currentFrame=runAnimation.getKeyFrame(animationTime,true);
        }
        else {
            currentFrame=idleAnimation.getKeyFrame(animationTime,true);
        }
        ScreenUtils.clear(
            0.4f,
            0.7f,
            0.9f,
            1
        );

        batch.begin();

        if (facingLeft) {
            batch.draw(
                currentFrame,
                x,
                y,
                width,
                height
            );
        } else {
            batch.draw(
                currentFrame,
                x + width,
                y,
                -width,
               height
            );
        }

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        idleTexture.dispose();
        runTexture.dispose();
        jumpTexture.dispose();

    }
}
