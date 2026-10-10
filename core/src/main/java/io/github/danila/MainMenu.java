package io.github.danila;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import java.util.function.Consumer;
import com.badlogic.gdx.utils.Align;

public class MainMenu {
    private final Stage stage;
    private final Skin skin;
    private final Preferences records;
    private final String[] levels;
    private final Consumer<String> startLevel;
    private final Runnable returnToMenu;

    public MainMenu(Stage stage, Skin skin, Preferences records, String[] levels,
                    Consumer<String> startLevel, Runnable returnToMenu) {
        this.stage = stage;
        this.skin = skin;
        this.records = records;
        this.levels = levels.clone();
        this.startLevel = startLevel;
        this.returnToMenu = returnToMenu;
    }

    public void show(String completion, String nextLevelPath, String completedLevelPath) {
        Table table = newPage("Главное меню");
        if (completion != null) table.add(new Label(completion, skin)).padBottom(20).row();
        if (completion != null && completedLevelPath != null) {
            addButton(table, "Повторить: " + levelName(completedLevelPath),
                () -> startLevel.accept(completedLevelPath));
        } else {
            addButton(table, "Начать играть", () -> startLevel.accept(levels[0]));
        }
        if (completion != null && nextLevelPath != null) {
            addButton(table, "Следующий: " + levelName(nextLevelPath),
                () -> startLevel.accept(nextLevelPath));
        }
        addButton(table, "Рекорды", this::showRecords);
    }

    public void showRecords() {
        Table table = newPage("Рекорды — лучшее время");
        for (String path : levels) {
            String time = records.contains(path)
                ? LevelTimer.formatTime(records.getFloat(path)) : "не пройден";
            table.add(new Label(levelName(path) + " — " + time, skin)).padBottom(18).row();
        }
        addButton(table, "Главное меню", returnToMenu);
    }

    public void showHud(LevelTimer timer, Runnable openSettings) {
        stage.clear();
        Table hud = new Table();
        hud.setFillParent(true);
        hud.top().left().pad(16);
        Table timerPanel = new Table();
        timerPanel.add(timer).pad(10, 14, 10, 14);
        hud.add(timerPanel).expandX().left();
        hud.add(button("Настройки", openSettings)).width(170).height(48);
        stage.addActor(hud);
    }

    private Table newPage(String title) {
        stage.clear();
        Table table = new Table();
        table.setFillParent(true);
        table.setBackground(skin.getDrawable("menu-background"));
        stage.addActor(table);
        table.add(new Label(title, skin)).padBottom(24).row();
        return table;
    }

    private TextButton button(String text, Runnable action) {
        TextButton button = new TextButton(text, skin);
        button.getLabel().setAlignment(Align.center);
        button.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                action.run();
            }
        });
        return button;
    }

    private void addButton(Table table, String text, Runnable action) {
        table.add(button(text, action)).width(340).height(50).padBottom(12).row();
    }

    public static String levelName(String path) {
        String name = path.substring(path.lastIndexOf('/') + 1).replace(".tmx", "");
        return "Уровень " + name.replaceFirst("^lvl", "");
    }
}
