package com.papermelody.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HgS_1217_ on 2017/5/2.
 */

public class GsonDateTypeAdapter extends TypeAdapter<Date> {
    /**
     * gson日期转换器
     */

    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.getTime());
        }
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        if (in.peek() == null) {
            return null;
        }
        String str = in. nextString();
        Date d = null;
        try {
            d = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(str);
        } catch (Exception e) {
        }
        return d;
    }
}
