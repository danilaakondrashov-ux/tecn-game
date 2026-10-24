package io.github.danila;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import java.util.function.Consumer;

public class MainMenu {
    private final Stage stage;
    private final Skin skin;
    private final Preferences records;
    private final String [] levels;
    private final Consumer <String> startLevel;
    private final Runnable returnToMenu;

    public MainMenu(Stage stage, Skin skin, Preferences records, String[] levels, Consumer<String> startLevel, Runnable returnToMenu) {
        this.stage = stage;
        this.skin = skin;
        this.records = records;
        this.levels = levels.clone();
        this.startLevel = startLevel;
        this.returnToMenu = returnToMenu;
    }
    private TextButton button (String text,Runnable action){
        TextButton button = new TextButton(text,skin);
        button.getLabel().setAlignment(Align.center);
        button.addListener(event -> {
            if (event instanceof ChangeListener.ChangeEvent){
                action.run();
            }
            return false;
        });
        return button;

    }
    private void addButton(Table table,String text,Runnable action){
       table.add(button(text,action)).width(300).height(50).padBottom(10).row();

    }
    public static String levelName(String path) {
        return path.replace("tiled/lvl", "Уровень ").replace(".tmx", "");
    }
    public void showRecords(){
        Table table= newPage("Best time");
        for (String levelPath:levels){
            String time="Not completed";
            if (records.contains(levelPath)){
                float bestTime=records.getFloat(levelPath);
                time=LevelTimer.formatTime(bestTime);
            }
            String text=levelName(levelPath)+" - "+ time;
            Label recordLabel=new Label(text,skin);
            table.add(recordLabel).padBottom(18).row();
        }
        addButton(table,"Exit",returnToMenu);
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
    public void show(String completion, String nextLevelPath, String completedLevelPath) {
        Table table = newPage("Главное меню");
        if (completion == null) {
            addButton(table, "Начать играть", () -> startLevel.accept(levels[0]));
        } else {
            table.add(new Label(completion, skin)).padBottom(20).row();
            addButton(table, "Повторить: " + levelName(completedLevelPath),
                () -> startLevel.accept(completedLevelPath));
            addButton(table, "Следующий: " + levelName(nextLevelPath),
                () -> startLevel.accept(nextLevelPath));
        }
        addButton(table, "Рекорды", this::showRecords);
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
}
