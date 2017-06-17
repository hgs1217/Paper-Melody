package com.papermelody.model.instrument;

import com.papermelody.R;

/**
 * Created by HgS_1217_ on 2017/6/17.
 */

public class PianoWith14KeysC3ToB4 extends PianoWith14KeysCToB {
    /**
     * 14键钢琴，音阶从C3到B4
     */

    private static int[] voiceResId = new int[]{R.raw.c3, R.raw.d3, R.raw.e3, R.raw.f3, R.raw.g3, R.raw.a3, R.raw.b3,
            R.raw.c4, R.raw.d4, R.raw.e4, R.raw.f4, R.raw.g4, R.raw.a4, R.raw.b4, R.raw.c3m, R.raw.d3m, R.raw.f3m,
            R.raw.g3m, R.raw.a3m, R.raw.c4m, R.raw.d4m, R.raw.f4m, R.raw.g4m, R.raw.a4m};

    public static int getVoiceResId(int keyNum) {
        return voiceResId[keyNum];
    }
}
