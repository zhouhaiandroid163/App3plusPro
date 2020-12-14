package com.zjw.apps3pluspro.module.device.entity;


/**
 * Created by baina on 18-1-3.
 */

public class MusicInfo {

    int VolumeLevel;//音量等级(0-15)
    int PlayState;//，0 =正在播放,1 =已暂停，-1未获取
    int Playprogress;//播放进度(未开发)
    String musicName;

    public MusicInfo() {
        super();
    }

    public MusicInfo(int volume_level, int play_state, int paly_progress, String music_name) {
        super();

        setVolumeLevel(volume_level);
        setPlayState(play_state);
        setPlayprogress(paly_progress);
        setMusicName(music_name);
    }


    public int getVolumeLevel() {
        return VolumeLevel;
    }

    public void setVolumeLevel(int volumeLevel) {
        VolumeLevel = volumeLevel;
    }

    public int getPlayState() {
        return PlayState;
    }

    public void setPlayState(int playState) {
        PlayState = playState;
    }

    public int getPlayprogress() {
        return Playprogress;
    }

    public void setPlayprogress(int playprogress) {
        Playprogress = playprogress;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    @Override
    public String toString() {
        return "MusicInfo{" +
                "VolumeLevel=" + VolumeLevel +
                ", PlayState=" + PlayState +
                ", Playprogress=" + Playprogress +
                ", musicName='" + musicName + '\'' +
                '}';
    }
}
