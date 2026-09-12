package io.github.danila;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

    @Override
    public void create() {
        batch = new SpriteBatch();
        idleTexture = new Texture("assets/hero/Idle.png");
        runTexture = new Texture("assets/hero/Run.png");
        jumpTexture = new Texture("assets/hero/Jump.png");
        idleAnimation = createAnimation(idleTexture, 12);
        runAnimation = createAnimation(runTexture, 6);
        jumpFrame = new TextureRegion(jumpTexture, 0, 0, WIDTH, HEIGHT);

        level = new TiledLevel("tiled/lvl1.tmx");
        playerCamera = new PlayerCamera(400f, 225f);
        Vector2 spawn = level.getLevelSpawn();
        x = spawn.x;
        y = spawn.y;
        playerRectangle = new Rectangle(x, y, WIDTH, HEIGHT);
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
        float delta = MathUtils.clamp(Gdx.graphics.getDeltaTime(), 0f, 1f / 30f);
        updatePlayer(delta);
        playerCamera.follow(x, y, WIDTH, HEIGHT, level.getLevelBounds());

        ScreenUtils.clear(0.4f, 0.7f, 0.9f, 1f);
        level.render(playerCamera.getCamera());
        renderPlayer();
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
        batch.dispose();
        level.dispose();
        idleTexture.dispose();
        runTexture.dispose();
        jumpTexture.dispose();
    }
}
