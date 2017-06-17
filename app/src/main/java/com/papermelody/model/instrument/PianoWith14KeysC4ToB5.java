package com.papermelody.model.instrument;

import com.papermelody.R;

/**
 * Created by HgS_1217_ on 2017/6/17.
 */

public class PianoWith14KeysC4ToB5 extends PianoWith14KeysCToB {
    /**
     * 14键钢琴，音阶从C4到B5
     */

    private static int[] voiceResId = new int[]{R.raw.c4, R.raw.d4, R.raw.e4, R.raw.f4, R.raw.g4, R.raw.a4, R.raw.b4,
            R.raw.c5, R.raw.d5, R.raw.e5, R.raw.f5, R.raw.g5, R.raw.a5, R.raw.b5, R.raw.c4m, R.raw.d4m, R.raw.f4m,
            R.raw.g4m, R.raw.a4m, R.raw.c5m, R.raw.d5m, R.raw.f5m, R.raw.g5m, R.raw.a5m};

    public static int getVoiceResId(int keyNum) {
        return voiceResId[keyNum];
    }
}
