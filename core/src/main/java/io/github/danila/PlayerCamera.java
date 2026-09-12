package io.github.danila;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class PlayerCamera {
    private final OrthographicCamera camera;
    private final float viewportWidth;
    private final float viewportHeight;

    public PlayerCamera(float viewportWidth, float viewportHeight) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        camera = new OrthographicCamera(viewportWidth, viewportHeight);
    }

    public void follow(float x, float y, float width, float height, Rectangle bounds) {
        float halfWidth = viewportWidth / 2f;
        float halfHeight = viewportHeight / 2f;
        float minX = bounds.x + halfWidth;
        float maxX = bounds.x + bounds.width - halfWidth;
        float minY = bounds.y + halfHeight;
        float maxY = bounds.y + bounds.height - halfHeight;

        float cameraX = minX > maxX ? bounds.x + bounds.width / 2f
            : MathUtils.clamp(x + width / 2f, minX, maxX);
        float cameraY = minY > maxY ? bounds.y + bounds.height / 2f
            : MathUtils.clamp(y + height / 2f, minY, maxY);

        camera.position.set(cameraX, cameraY, 0f);
        camera.update();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
