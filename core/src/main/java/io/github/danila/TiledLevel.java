package io.github.danila;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
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
    private final MapLayer spikes;
    private final Rectangle bounds;

    public TiledLevel(String path) {
        map = new TmxMapLoader().load(path);
        renderer = new OrthogonalTiledMapRenderer(map);

        floor = (TiledMapTileLayer) map.getLayers().get("floor");
        walls = (TiledMapTileLayer) map.getLayers().get("walls");
        objects = map.getLayers().get("objects");
        spikes = map.getLayers().get("spikes");
        int mapWidth = map.getProperties().get("width", Integer.class);
        int mapHeight = map.getProperties().get("height", Integer.class);
        int tileWidth = map.getProperties().get("tilewidth", Integer.class);
        int tileHeight = map.getProperties().get("tileheight", Integer.class);

        bounds = new Rectangle(0, 0, mapWidth * tileWidth, mapHeight * tileHeight);
    }
    public boolean touchedSpikes(Rectangle player){
        if (spikes==null){
            return false;
        }
        for (MapObject object : spikes.getObjects()){
            if (object instanceof RectangleMapObject spike){
                if (player.overlaps(spike.getRectangle())){
                    return true;
                }
            }
        }
        return false;
    }
    public void render(OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render();
    }

    public void drawHitboxes(ShapeRenderer shapes) {
        shapes.setColor(Color.GREEN);
        drawTileHitboxes(shapes, floor);
        drawTileHitboxes(shapes, walls);
        if (objects != null && objects.getObjects().get("exit") instanceof RectangleMapObject exit) {
            Rectangle box = exit.getRectangle();
            shapes.rect(box.x, box.y, box.width, box.height);
        }

        if (spikes == null) return;
        shapes.setColor(Color.YELLOW);
        for (MapObject object : spikes.getObjects()) {
            if (object instanceof RectangleMapObject spike) {
                Rectangle box = spike.getRectangle();
                if (box.width == 0f && box.height == 0f) {
                    // Показываем точку, если у объекта в Tiled нет размеров.
                    shapes.line(box.x - 2f, box.y, box.x + 2f, box.y);
                    shapes.line(box.x, box.y - 2f, box.x, box.y + 2f);
                } else {
                    shapes.rect(box.x, box.y, box.width, box.height);
                }
            }
        }
    }

    private void drawTileHitboxes(ShapeRenderer shapes, TiledMapTileLayer layer) {
        float tileWidth = layer.getTileWidth();
        float tileHeight = layer.getTileHeight();
        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                if (layer.getCell(x, y) != null) {
                    shapes.rect(x * tileWidth, y * tileHeight, tileWidth, tileHeight);
                }
            }
        }
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

    public String getNextLevel (Rectangle player) {
        if (objects==null){
            return null;
        }
        MapObject exit = objects.getObjects().get("exit");
        if (!(exit instanceof RectangleMapObject)) {
            return null;
        }
        Rectangle exitBound =((RectangleMapObject) exit).getRectangle();
        if (player.overlaps(exitBound)){
            String nextLevel = exit.getProperties().get("nextLevel", String.class);
            if (nextLevel == null || nextLevel.trim().isEmpty()) {
                return null;
            }
            String path = nextLevel.trim();
            if (!path.startsWith("tiled/")) {
                path = "tiled/" + path;
            }
            if (!path.endsWith(".tmx")) {
                path += ".tmx";
            }
            return path;

        }
        return null;
    }
    public void dispose() {
        renderer.dispose();
        map.dispose();
    }
}
