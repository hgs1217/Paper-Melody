package com.papermelody.model.instrument;

import com.papermelody.R;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class PianoWith21KeysC4ToB6 extends PianoWith21KeysCToB {
    /**
     * 21键钢琴，音阶从C4到B6
     */

    private static int[] voiceResId = new int[]{R.raw.c4, R.raw.d4, R.raw.e4, R.raw.f4, R.raw.g4, R.raw.a4, R.raw.b4, 
            R.raw.c5, R.raw.d5, R.raw.e5, R.raw.f5, R.raw.g5, R.raw.a5, R.raw.b5, R.raw.c6, R.raw.d6, R.raw.e6, 
            R.raw.f6, R.raw.g6, R.raw.a6, R.raw.b6, R.raw.c4m, R.raw.d4m, R.raw.f4m, R.raw.g4m, R.raw.a4m, 
            R.raw.c5m, R.raw.d5m, R.raw.f5m, R.raw.g5m, R.raw.a5m, R.raw.c6m, R.raw.d6m, R.raw.f6m, R.raw.g6m, R.raw.a6m};
    
    public static int getVoiceResId(int keyNum) {
        return voiceResId[keyNum];
    }
}
