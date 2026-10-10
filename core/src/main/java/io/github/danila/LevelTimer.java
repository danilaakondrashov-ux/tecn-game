package io.github.danila;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import java.util.Locale;

public class LevelTimer extends Label {
    private double elapsed;

    public LevelTimer(Skin skin) {
        super("", skin);
        reset();
    }

    public void reset() {
        elapsed = 0;
        refresh();
    }

    public void update(float delta) {
        elapsed += Math.max(0f, delta);
        refresh();
    }

    public double getElapsed() {
        return elapsed;
    }

    private void refresh() {
        setText("Время: " + formatTime(elapsed));
    }

    public static String formatTime(double seconds) {
        long millis = (long) (seconds * 1000);
        return String.format(Locale.ROOT, "%02d:%02d.%03d", millis / 60000,
            millis / 1000 % 60, millis % 1000);
    }
}
