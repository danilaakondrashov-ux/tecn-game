package io.github.danila;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;

public class AudioManegemante {
    private static final String musicPath ="assets/sounds/BGM Pack 1 OGG/hope.ogg";
    private Music bgm ;
    public AudioManegemante(){
        FileHandle musicFile = Gdx.files.internal(musicPath);
        if (!musicFile.exists()){
            Gdx.app.error("AudioManager",
                "Music file not found: assets/" + musicPath);
            return;
        }
        bgm=Gdx.audio.newMusic(musicFile);
        bgm.setLooping(true);
        bgm.setVolume(0.5f);
    }
    public void playMusic (){
        if (bgm!=null && !bgm.isPlaying()){
            bgm.play();
        }
    }
      public void setVolume(float volume){
        if (bgm!=null ){
            bgm.setVolume(volume);

        }

      }
    public void dispose(){
        if (bgm != null) {
            bgm.dispose();
            bgm= null;
        }


    }
}
