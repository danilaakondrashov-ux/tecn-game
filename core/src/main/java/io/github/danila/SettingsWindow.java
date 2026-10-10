package io.github.danila;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import java.util.function.Consumer;

public class SettingsWindow extends Window {
    private final AudioManegemante audio;

    public SettingsWindow(Skin skin, AudioManegemante audio, Runnable returnToMenu) {
        super("Настройки", skin);
        this.audio = audio;
        setModal(true);
        setMovable(false);
        getTitleLabel().setText("");
        pad(26);
        Label title = new Label("Настройки", skin);
        title.setAlignment(Align.center);
        add(title).colspan(3).expandX().fillX().padBottom(24).row();
        addVolume("Звук", audio.getSoundVolume(), audio::setSoundVolume, skin);
        addVolume("Музыка", audio.getMusicVolume(), audio::setVolume, skin);
        add(button("Продолжить", skin, this::close)).colspan(3).width(340).height(50).padTop(22).row();
        add(button("Главное меню", skin, () -> {
            close();
            returnToMenu.run();
        })).colspan(3).width(340).height(50).padTop(12).row();
        pack();
    }

    private void addVolume(String title, float value, Consumer<Float> update, Skin skin) {
        Slider slider = new Slider(0f, 1f, 0.01f, false, skin);
        slider.setValue(value);
        Label percent = new Label(Math.round(value * 100) + "%", skin);
        percent.setAlignment(Align.right);
        slider.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                update.accept(slider.getValue());
                percent.setText(Math.round(slider.getValue() * 100) + "%");
            }
        });
        add(new Label(title, skin)).left().width(100);
        add(slider).width(220).height(34).pad(8, 14, 8, 14);
        add(percent).width(60).right().row();
    }

    private TextButton button(String text, Skin skin, Runnable action) {
        TextButton button = new TextButton(text, skin);
        button.getLabel().setAlignment(Align.center);
        button.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) { action.run(); }
        });
        return button;
    }

    public void show(Stage stage) {
        if (isOpen()) return;
        stage.addActor(this);
        centerOnStage();
        stage.setKeyboardFocus(this);
    }

    public void centerOnStage() {
        if (getStage() != null) {
            setPosition((getStage().getWidth() - getWidth()) / 2f,
                (getStage().getHeight() - getHeight()) / 2f);
        }
    }

    public boolean isOpen() { return getStage() != null; }

    public void close() {
        audio.saveSettings();
        if (getStage() != null) getStage().setKeyboardFocus(null);
        remove();
    }
}
