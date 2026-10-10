package io.github.danila;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class GameSkin extends Skin {
    public GameSkin() {
        BitmapFont font = new BitmapFont(Gdx.files.internal("ui/menu-font.fnt"));
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        add("default-font", font);
        Pixmap pixel = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixel.setColor(Color.WHITE);
        pixel.fill();
        add("white", new Texture(pixel));
        pixel.dispose();
        configureLabels();
        configureButtons();
        configureSliders();
        configureMenu();
    }

    private void configureLabels() {
        add("default", new Label.LabelStyle(getFont("default-font"), Color.WHITE));
    }

    private void configureButtons() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = getFont("default-font");
        style.fontColor = Color.WHITE;
        style.up = newDrawable("white", Color.valueOf("343B48"));
        style.over = newDrawable("white", Color.valueOf("465267"));
        style.down = newDrawable("white", Color.valueOf("252C38"));
        add("default", style);
    }

    private void configureSliders() {
        Slider.SliderStyle style = new Slider.SliderStyle();
        style.background = newDrawable("white", Color.valueOf("4B5260"));
        style.background.setMinHeight(4);
        style.knobBefore = newDrawable("white", Color.valueOf("A3B8D8"));
        style.knobBefore.setMinHeight(4);
        Pixmap circle = new Pixmap(22, 22, Pixmap.Format.RGBA8888);
        circle.setColor(Color.WHITE);
        circle.fillCircle(11, 11, 10);
        add("slider-circle", new Texture(circle));
        circle.dispose();
        style.knob = newDrawable("slider-circle", Color.valueOf("DCE6F5"));
        style.knobOver = newDrawable("slider-circle", Color.WHITE);
        style.knobDown = style.knobOver;
        add("default-horizontal", style);
    }

    private void configureMenu() {
        add("menu-background", newDrawable("white", Color.valueOf("191D26")), Drawable.class);
        add("hud-background", newDrawable("white", new Color(0.1f, 0.12f, 0.16f, 0.95f)), Drawable.class);

        Window.WindowStyle style = new Window.WindowStyle();
        style.titleFont = getFont("default-font");
        style.titleFontColor = Color.WHITE;
        style.background = newDrawable("white", Color.valueOf("252B36"));
        style.stageBackground = newDrawable("white", new Color(0f, 0f, 0f, 0.55f));
        add("default", style);
    }
}
