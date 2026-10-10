package io.github.danila;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.files.FileHandle;

public class AudioManegemante {
    private static final String musicPath ="sounds/BGM Pack 1 OGG/hope.ogg";
    private Music bgm ;
    private final Preferences settings = Gdx.app.getPreferences("pigkingame-audio");
    private float musicVolume = settings.getFloat("music", 0.5f);
    private float soundVolume = settings.getFloat("sound", 1f);
    public AudioManegemante(){
        FileHandle musicFile = Gdx.files.internal(musicPath);
        if (!musicFile.exists()){
            Gdx.app.error("AudioManager",
                "Music file not found: " + musicPath);
            return;
        }
        bgm=Gdx.audio.newMusic(musicFile);
        bgm.setLooping(true);
        bgm.setVolume(musicVolume);
    }
    public void playMusic (){
        if (bgm!=null && !bgm.isPlaying()){
            bgm.play();
        }
    }
      public void setVolume(float volume){
        musicVolume = MathUtils.clamp(volume, 0f, 1f);
        if (bgm!=null ){
            bgm.setVolume(musicVolume);

        }

      }
    public float getMusicVolume() { return musicVolume; }
    public float getSoundVolume() { return soundVolume; }
    public void setSoundVolume(float volume) {
        soundVolume = MathUtils.clamp(volume, 0f, 1f);
    }
    public long playSound(Sound sound) {
        return sound.play(soundVolume);
    }
    public void saveSettings() {
        settings.putFloat("music", musicVolume);
        settings.putFloat("sound", soundVolume);
        settings.flush();
    }
    public void dispose(){
        if (bgm != null) {
            bgm.dispose();
            bgm= null;
        }


    }
}
