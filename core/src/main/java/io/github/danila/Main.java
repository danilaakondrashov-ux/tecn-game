package io.github.danila;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {
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
    private Stage stage;
    private Skin skin;
    private Preferences records;
    private LevelTimer timer;
    private MainMenu mainMenu;
    private SettingsWindow settingsWindow;
    private boolean playing;
    private boolean paused;
    private String currentLevelPath;
    private String suggestedLevelPath = "tiled/lvl1.tmx";
    private static final String[] LEVELS = {"tiled/lvl1.tmx", "tiled/lvl2.tmx"};

    @Override
    public void create() {
        batch = new SpriteBatch();
        idleTexture = new Texture("assets/hero/Idle.png");
        runTexture = new Texture("assets/hero/Run.png");
        jumpTexture = new Texture("assets/hero/Jump.png");
        idleAnimation = createAnimation(idleTexture, 12);
        runAnimation = createAnimation(runTexture, 6);
        jumpFrame = new TextureRegion(jumpTexture, 0, 0, WIDTH, HEIGHT);


        playerCamera = new PlayerCamera(400f, 225f);


        playerRectangle = new Rectangle(x, y, WIDTH, HEIGHT);
        records = Gdx.app.getPreferences("pigkingame-records");
        stage = new Stage(new ScreenViewport());
        skin = new GameSkin();
        timer = new LevelTimer(skin);
        audio = new AudioManegemante();
        settingsWindow = new SettingsWindow(skin, audio, () -> showMenu(null));
        mainMenu = new MainMenu(stage, skin, records, LEVELS, this::startLevel, () -> showMenu(null));
        Gdx.input.setInputProcessor(stage);
        showMenu(null);
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
        float frameDelta = Math.max(0f, Gdx.graphics.getDeltaTime());
        float delta = MathUtils.clamp(frameDelta, 0f, 1f / 30f);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (playing) {
                if (settingsWindow.isOpen()) settingsWindow.close();
                else settingsWindow.show(stage);
            } else showMenu(null);
        }
        if (playing && !paused && !settingsWindow.isOpen()) {
            timer.update(frameDelta);
            updatePlayer(delta);
            String nextLevelPath = level.getNextLevel(playerRectangle);

            if (nextLevelPath != null && !nextLevelPath.isEmpty()) {
                boolean best = !records.contains(currentLevelPath)
                    || timer.getElapsed() < records.getFloat(currentLevelPath);
                if (best) {
                    records.putFloat(currentLevelPath, (float) timer.getElapsed());
                    records.flush();
                }
                suggestedLevelPath = nextLevelPath;
                showMenu(MainMenu.levelName(currentLevelPath) + " — " + LevelTimer.formatTime(timer.getElapsed())
                    + (best ? "   Новый рекорд!" : ""));
            }
        }
        ScreenUtils.clear(0.08f, 0.12f, 0.18f, 1f);
        stage.getViewport().apply();
        if (playing) {
            playerCamera.follow(x, y, WIDTH, HEIGHT, level.getLevelBounds());

            ScreenUtils.clear(0.23f, 0.21f, 0.3f, 1f);
            level.render(playerCamera.getCamera());
            renderPlayer();
        }
        stage.act(Math.min(frameDelta, 0.1f));
        stage.draw();
    }

    private void showMenu(String completion) {
        if (settingsWindow.isOpen()) settingsWindow.close();
        playing = false;
        mainMenu.show(completion, suggestedLevelPath, currentLevelPath);
    }

    private void startLevel(String path) {
        loadLevel(path);
        currentLevelPath = path;
        timer.reset();
        playing = true;
        mainMenu.showHud(timer, () -> settingsWindow.show(stage));
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        playerCamera.resize(width, height);
        settingsWindow.centerOnStage();
    }

    @Override public void pause() { paused = true; }
    @Override public void resume() { paused = false; }

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
        playerRectangle.set(x, y, WIDTH, HEIGHT);
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

    @Override
    public void dispose() {
        audio.saveSettings();
        batch.dispose();
        if (level != null) level.dispose();
        stage.dispose();
        skin.dispose();
        idleTexture.dispose();
        runTexture.dispose();
        jumpTexture.dispose();
        audio.dispose();
    }
    private void loadLevel(String path) {
        TiledLevel nextLevel = new TiledLevel(path);
        Vector2 spawn = nextLevel.getLevelSpawn();

        if (level != null) {
            level.dispose();
        }

        level = nextLevel;

        x = spawn.x;
        y = spawn.y;

        verticalSpeed = 0f;
        animationTime = 0f;
        onGround = false;
        moving = false;
        facingLeft = true;

        updatePlayerRectangle();
    }

}
