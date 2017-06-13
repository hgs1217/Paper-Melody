package com.papermelody.model;

/**
 * Created by 潘宇杰 on 2017-6-11 0011.
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
    private long createTime;
    private long size;

    public LocalMusic(String name, long createTime, long size) {
        this.filename = name;
        this.createTime = createTime;
        this.size = size;
    }

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

    public String getMusicAuthor() {
        return musicAuthor;
    }

    public void setMusicAuthor(String musicAuthor) {
        this.musicAuthor = musicAuthor;
    }

    public String getMusicInfo() {
        return musicInfo;
    }

    public void setMusicInfo(String musicInfo) {
        this.musicInfo = musicInfo;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getSize() {
        return size;
    }
}
