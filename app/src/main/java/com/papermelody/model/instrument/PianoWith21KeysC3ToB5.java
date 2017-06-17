package com.papermelody.model.instrument;

import com.papermelody.R;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class PianoWith21KeysC3ToB5 extends PianoWith21KeysCToB {
    /**
     * 21键钢琴，音阶从C3到B5
     */

    private static int[] voiceResId = new int[]{R.raw.c3, R.raw.d3, R.raw.e3, R.raw.f3, R.raw.g3, R.raw.a3, R.raw.b3,
            R.raw.c4, R.raw.d4, R.raw.e4, R.raw.f4, R.raw.g4, R.raw.a4, R.raw.b4, R.raw.c5, R.raw.d5, R.raw.e5,
            R.raw.f5, R.raw.g5, R.raw.a5, R.raw.b5, R.raw.c3m, R.raw.d3m, R.raw.f3m, R.raw.g3m, R.raw.a3m,
            R.raw.c4m, R.raw.d4m, R.raw.f4m, R.raw.g4m, R.raw.a4m, R.raw.c5m, R.raw.d5m, R.raw.f5m, R.raw.g5m,
            R.raw.a5m};

    public static int getVoiceResId(int keyNum) {
        return voiceResId[keyNum];
    }
}
