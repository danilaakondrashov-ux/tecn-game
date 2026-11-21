package io.github.danila;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class GameSkin extends Skin  {

    public GameSkin (){
        super(new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas")));
        add("default-font", new BitmapFont(Gdx.files.internal("ui/menu-font.fnt")));
        configureLabels();
        configureButtons();
        configureSlider();
        configureMenu();
    }
    private void configureButtons(){
        TextButton.TextButtonStyle style=new TextButton.TextButtonStyle();
        style.font=getFont("default-font");
        style.fontColor= Color.BLACK;
        style.up=getDrawable("button-normal");
        style.down=getDrawable("button-normal-pressed");
        add("default",style);
    }
    private void configureSlider (){
        Slider.SliderStyle style = new Slider.SliderStyle();
        style.background=getDrawable("slider");
        Pixmap pixel =new Pixmap(16,16,Pixmap.Format.RGBA8888);
        pixel.setColor(Color.SALMON);
        pixel.fillCircle(8,8,7);
        add("slider-circle", new Texture(pixel));
        pixel.dispose();

        style.knob=getDrawable("slider-circle");
        add("default-horizontal",style);
    }
    private void configureMenu (){
        add("menu-background",
            newDrawable("white", new Color(0.08f, 0.12f, 0.18f, 1f)), Drawable.class);
        Window.WindowStyle style =new Window.WindowStyle();
        style.titleFont=getFont("default-font");
        style.titleFontColor=Color.CYAN;
        style.background=getDrawable("window");
        add("default",style);

    }
    private void configureLabels() {
        add("default", new Label.LabelStyle(getFont("default-font"), Color.WHITE));
    }
}
