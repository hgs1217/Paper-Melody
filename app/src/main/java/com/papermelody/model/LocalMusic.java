package com.papermelody.model;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class LocalMusic extends Music {
    /**
     * 本地音乐类
     */

    private String filename;
    private String path;
    private String musicName;
    private String musicAuthor;
    private String musicInfo;

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getMusicName() {
        return musicName;
    }

    @Override
    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    @Override
    public String getMusicAuthor() {
        return musicAuthor;
    }

    @Override
    public void setMusicAuthor(String musicAuthor) {
        this.musicAuthor = musicAuthor;
    }

    @Override
    public String getMusicInfo() {
        return musicInfo;
    }

    @Override
    public void setMusicInfo(String musicInfo) {
        this.musicInfo = musicInfo;
    }
}
