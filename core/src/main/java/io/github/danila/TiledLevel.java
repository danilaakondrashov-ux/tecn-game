package io.github.danila;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class TiledLevel {
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer renderer;
    private final TiledMapTileLayer floor;
    private final TiledMapTileLayer walls;
    private final MapLayer objects;
    private final Rectangle bounds;

    public TiledLevel(String path) {
        map = new TmxMapLoader().load(path);
        renderer = new OrthogonalTiledMapRenderer(map);

        floor = (TiledMapTileLayer) map.getLayers().get("floor");
        walls = (TiledMapTileLayer) map.getLayers().get("walls");
        objects = map.getLayers().get("objects");

        int mapWidth = map.getProperties().get("width", Integer.class);
        int mapHeight = map.getProperties().get("height", Integer.class);
        int tileWidth = map.getProperties().get("tilewidth", Integer.class);
        int tileHeight = map.getProperties().get("tileheight", Integer.class);

        bounds = new Rectangle(0, 0, mapWidth * tileWidth, mapHeight * tileHeight);
    }

    public void render(OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render();
    }

    public boolean collides(Rectangle player) {
        return collidesWithLayer(player, floor) || collidesWithLayer(player, walls);
    }

    private boolean collidesWithLayer(Rectangle player, TiledMapTileLayer layer) {
        int startX = (int) (player.x / layer.getTileWidth());
        int endX = (int) ((player.x + player.width - 1) / layer.getTileWidth());
        int startY = (int) (player.y / layer.getTileHeight());
        int endY = (int) ((player.y + player.height - 1) / layer.getTileHeight());

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                if (layer.getCell(x, y) != null) {
                    return true;
                }
            }
        }

        return false;
    }

    public Vector2 getLevelSpawn() {
        MapObject spawn = objects.getObjects().get("levelSpawn");

        if (spawn == null) {
            throw new RuntimeException("В карте нет объекта levelSpawn");
        }

        float x = spawn.getProperties().get("x", Float.class);
        float y = spawn.getProperties().get("y", Float.class);
        return new Vector2(x, y);
    }

    public Rectangle getLevelBounds() {
        return bounds;
    }

    public void dispose() {
        renderer.dispose();
        map.dispose();
    }
}
