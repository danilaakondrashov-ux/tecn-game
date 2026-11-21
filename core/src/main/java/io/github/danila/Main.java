package io.github.danila;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Main extends ApplicationAdapter {

    //DEBUG

    private ShapeRenderer hitboxRenderer ;
    private boolean showHitBox;
    private static final int WIDTH = 38;
    private static final int HEIGHT = 28;
    private AudioManegemante audio ;
    private SpriteBatch batch;
    private Texture idleTexture;
    private Texture runTexture;
    private Texture jumpTexture;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> runAnimation;
    private TextureRegion jumpFrame;
    private TiledLevel level;
    private PlayerCamera playerCamera;
    private Rectangle playerRectangle;


    private float x;
    private float y;
    private float animationTime;
    private float verticalSpeed;
    private final float speed = 200f;
    private final float gravity = 700f;
    private boolean onGround;
    private boolean moving;
    private boolean facingLeft = true;
    private Stage stage ;
    private Skin skin ;
    private LevelTimer timer ;
    private Preferences records ;
    private String currentLevelPath ;

    @Override
    public void create() {
        batch = new SpriteBatch();
        hitboxRenderer = new ShapeRenderer();
        idleTexture = new Texture("assets/hero/Idle.png");
        runTexture = new Texture("assets/hero/Run.png");
        jumpTexture = new Texture("assets/hero/Jump.png");
        idleAnimation = createAnimation(idleTexture, 12);
        runAnimation = createAnimation(runTexture, 6);
        jumpFrame = new TextureRegion(jumpTexture, 0, 0, WIDTH, HEIGHT);
        stage = new Stage(new ScreenViewport());
        skin = new GameSkin();
     timer=new LevelTimer(skin);
     records=Gdx.app.getPreferences("my_game_records");

        Table hud=new Table();
        hud.setFillParent(true);
        hud.top().left().pad(20);
        hud.add(timer);
        stage.addActor(hud);

        playerCamera = new PlayerCamera(400f, 225f);


        playerRectangle = new Rectangle(x, y, WIDTH, HEIGHT);
        loadLevel("tiled/lvl1.tmx");
        audio=new AudioManegemante();
        audio.setVolume(0.5f);
        audio.playMusic();

    }

    private Animation<TextureRegion> createAnimation(Texture texture, int frameCount) {
        TextureRegion[] frames = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = new TextureRegion(texture, i * WIDTH, 0, WIDTH, HEIGHT);
        }
        return new Animation<>(0.1f, frames);
    }

    @Override
    public void render() {

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)){
            showHitBox = !showHitBox;
        }
        float delta = MathUtils.clamp(Gdx.graphics.getDeltaTime(), 0f, 1f / 30f);
        timer.update(Gdx.graphics.getDeltaTime());
        updatePlayer(delta);
        if (level.touchedSpikes(playerRectangle)){
            resetAttempt();
        }
        else {
            String nextLevelPath = level.getNextLevel(playerRectangle);

            if (nextLevelPath != null && !nextLevelPath.isEmpty()) {
                loadLevel(nextLevelPath);
            }
        }

        playerCamera.follow(x, y, WIDTH, HEIGHT, level.getLevelBounds());

        ScreenUtils.clear(0.4f, 0.7f, 0.9f, 1f);
        level.render(playerCamera.getCamera());
        renderPlayer();

        if (showHitBox){
            hitboxRenderer.setProjectionMatrix(playerCamera.getCamera().combined);
            hitboxRenderer.begin(ShapeRenderer.ShapeType.Line);
            hitboxRenderer.setColor(1f,0f,0f,1f);
            hitboxRenderer.rect(
                playerRectangle.x,
                playerRectangle.y,
                playerRectangle.width,
                playerRectangle.height
                );
            hitboxRenderer.end();
        }
        stage.act(delta);
        stage.draw();
    }
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    private void updatePlayer(float delta) {
        moving = false;
        float oldX = x;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= speed * delta;
            moving = true;
            facingLeft = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += speed * delta;
            moving = true;
            facingLeft = false;
        }

        updatePlayerRectangle();
        if (level.collides(playerRectangle)) {
            x = oldX;
            updatePlayerRectangle();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && onGround) {
            verticalSpeed = 350f;
            onGround = false;
        }

        float oldY = y;
        verticalSpeed -= gravity * delta;
        y += verticalSpeed * delta;
        updatePlayerRectangle();

        if (level.collides(playerRectangle)) {
            y = oldY;
            if (verticalSpeed < 0f) onGround = true;
            verticalSpeed = 0f;
            updatePlayerRectangle();
        } else {
            onGround = false;
        }

        animationTime += delta;
    }

    private void updatePlayerRectangle() {
        playerRectangle.set(x+11f, y, WIDTH-22f, HEIGHT-7f);
    }

    private void renderPlayer() {
        TextureRegion frame;

        if (!onGround) {
            frame = jumpFrame;
        } else if (moving) {
            frame = runAnimation.getKeyFrame(animationTime, true);
        } else {
            frame = idleAnimation.getKeyFrame(animationTime, true);
        }

        batch.setProjectionMatrix(playerCamera.getCamera().combined);
        batch.begin();
        if (facingLeft) {
            batch.draw(frame, x, y, WIDTH, HEIGHT);
        } else {
            batch.draw(frame, x + WIDTH, y, -WIDTH, HEIGHT);
        }
        batch.end();
    }


    private void resetAttempt(){
        Vector2 spawn=level.getLevelSpawn();
        x=spawn.x;
        y=spawn.y;
        verticalSpeed=0f;
        animationTime=0f;
        onGround=false;
        moving=false;
        facingLeft=false;
        updatePlayerRectangle();



    }
    @Override
    public void dispose() {
        batch.dispose();
        level.dispose();
        idleTexture.dispose();
        runTexture.dispose();
        jumpTexture.dispose();
        audio.dispose();
        stage.dispose();
        skin.dispose();
        hitboxRenderer.dispose();
    }
    private void loadLevel(String path) {
        TiledLevel nextLevel = new TiledLevel(path);
        Vector2 spawn = nextLevel.getLevelSpawn();

        if (level != null) {
            level.dispose();
        }

        level = nextLevel;

        resetAttempt();
        currentLevelPath=path;
        timer.reset();

    }

    private void saveRecord(){
        float time =(float) timer.getElapsed();
        float bestTime=records.getFloat(currentLevelPath,Float.MAX_VALUE);
        if (time<bestTime){
            records.putFloat(currentLevelPath,time);
            records.flush();
        }

    }

}
