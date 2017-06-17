package com.papermelody.model.instrument;

import android.util.Log;

import com.papermelody.R;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class FluteWith7Holes extends Flute {
    /**
     * 7孔笛子，16个音，C调笛，其中第一孔不按住表示急吹，反之为缓吹
     */

    // 0 代表闭， 1 代表开
    public static final int LOW_G = 0;  // 0 000000
    public static final int LOW_A = 1;  // 0 000001
    public static final int LOW_B = 2;  // 0 000011
    public static final int MID_C = 3;  // 0 000111
    public static final int MID_D = 4;  // 0 001111
    public static final int MID_E = 5;  // 0 011111
    public static final int MID_F = 6;  // 0 100111 或 0 100011
    public static final int MID_G = 7;  // 1 000000 或 0 100000
    public static final int MID_A = 8;  // 1 000001
    public static final int MID_B = 9;  // 1 000011
    public static final int HIGH_C = 10;    // 1 000111
    public static final int HIGH_D = 11;    // 1 001111
    public static final int HIGH_E = 12;    // 1 011111
    public static final int HIGH_F = 13;    // 1 100001 或 1 101111 或 1 010011
    public static final int HIGH_G = 14;    // 1 100000 或 1 100111
    public static final int HIGH_A = 15;    // 1 001001

    public static final int KEY_NUM = 16;

    private static int[] voiceResId = new int[]{R.raw.f_g3, R.raw.f_a3, R.raw.f_b3, R.raw.f_c4, R.raw.f_d4,
            R.raw.f_e4, R.raw.f_f4, R.raw.f_g4, R.raw.f_a4, R.raw.f_b4, R.raw.f_c5, R.raw.f_d5, R.raw.f_e5,
            R.raw.f_f5, R.raw.f_g5, R.raw.f_a5};

    /**
     * 根据六个孔的开闭状况返回对应的voiceResId
     * @return
     */
    public static int holesToVoice(boolean holes[]) {
        if (holes.length != 7) {
            return -1;
        }
        String status = "";
        for (int i = 0; i < 7; ++i) {
            if (holes[i]) {
                status += "1";
            } else {
                status += "0";
            }
        }
        switch (status) {
            case "0000000":
                return LOW_G;
            case "0000001":
                return LOW_A;
            case "0000011":
                return LOW_B;
            case "0000111":
                return MID_C;
            case "0001111":
                return MID_D;
            case "0011111":
                return MID_E;
            case "0100111":
            case "0100011":
                return MID_F;
            case "1000000":
            case "0100000":
                return MID_G;
            case "1000001":
                return MID_A;
            case "1000011":
                return MID_B;
            case "1000111":
                return HIGH_C;
            case "1001111":
                return HIGH_D;
            case "1011111":
                return HIGH_E;
            case "1100001":
            case "1101111":
            case "1010011":
                return HIGH_F;
            case "1100000":
            case "1100111":
                return HIGH_G;
            case "1001001":
                return HIGH_A;
        }
        return -1;
    }

    public static int getVoiceResId (int keyNum) {
        Log.d("TESTT", voiceResId[keyNum]+"");
        return voiceResId[keyNum];
    }
}
