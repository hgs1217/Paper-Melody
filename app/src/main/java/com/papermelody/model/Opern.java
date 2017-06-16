package com.papermelody.model;

import android.content.Context;
import android.support.annotation.RawRes;

import com.papermelody.util.StorageUtil;

import java.io.InputStream;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class Opern {
    /**
     * 乐谱类
     */

    private int width = 0;
    private int height = 0;
    private ArrayList<Integer> listX;
    private ArrayList<Integer> listY;
    private ArrayList<Integer> listWidth;
    private ArrayList<Integer> listHeight;
    private ArrayList<Integer> listTime;

    public Opern() {
        listX = new ArrayList<>();
        listY = new ArrayList<>();
        listWidth = new ArrayList<>();
        listHeight = new ArrayList<>();
        listTime = new ArrayList<>();
        listTime.add(0);
    }

    public Opern(Context context, @RawRes int rawResId) {
        this();
        InputStream inputStream = context.getResources().openRawResource(rawResId);
        String content = StorageUtil.getStringFromTxt(inputStream);
        String[] contents = content.split("[\t\n ]");
        ArrayList<String> rawString = new ArrayList<>();
        int cnt = 0;
        for (int i = 0; i < contents.length; ++i) {
            if (contents[i].length() > 0) {
                if (cnt == 0) {
                    width = Integer.parseInt(contents[i]);
                } else if (cnt == 1) {
                    height = Integer.parseInt(contents[i]);
                } else {
                    rawString.add(contents[i]);
                }
                cnt++;
            }
        }
        for (int i = 0; i < rawString.size() / 5; ++i) {
            listX.add(Integer.parseInt(rawString.get(5 * i)));
            listY.add(parseInt(rawString.get(5 * i + 1)));
            listWidth.add(parseInt(rawString.get(5 * i + 2)));
            listHeight.add(parseInt(rawString.get(5 * i + 3)));
            listTime.add(reformatTime(rawString.get(5 * i + 4)));
        }
    }

    private Integer reformatTime(String time) {
        String[] nums = time.split(":");
        Integer min = parseInt(nums[0]);
        Integer sec = parseInt(nums[1]);
        return min * 60 + sec;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ArrayList<Integer> getListX() {
        return listX;
    }

    public ArrayList<Integer> getListY() {
        return listY;
    }

    public ArrayList<Integer> getListWidth() {
        return listWidth;
    }

    public ArrayList<Integer> getListHeight() {
        return listHeight;
    }

    public ArrayList<Integer> getListTime() {
        return listTime;
    }
}
