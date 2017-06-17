package com.papermelody.model;

import android.content.Context;
import android.support.annotation.RawRes;

import com.papermelody.R;
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

    public static final int OPERN_PUGONGYINGDEYUEDING = 0;
    public static final int OPERN_SHUOHAODEXINGFUNE = 1;
    public static final int OPERN_TEST = 2;

    private static int[] OPERN_RAWS = new int[] {R.raw.opern_pugongyingdeyueding, R.raw.opern_shuohaodexingfune,
                                    R.raw.opern_p};
    private static int[] OPERN_DRAWABLES = new int[] {R.drawable.opern_pugongyingdeyueding, R.drawable.opern_shuohaodexingfune,
                                    R.drawable.opern_pugongyingdeyueding};

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

    public static int getOpernRaw(int opern) {
        return OPERN_RAWS[opern];
    }

    public static int getOpernDrawable(int opern) {
        return OPERN_DRAWABLES[opern];
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
