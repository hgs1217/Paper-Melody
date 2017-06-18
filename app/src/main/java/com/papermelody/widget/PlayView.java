package com.papermelody.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.papermelody.util.CanvasUtil;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangtonghui on 17/6/16.
 */

public class PlayView extends View {

    Paint paint;
    private List<Bean> list = null;
    private int MaxAlpha = 255;//
    private boolean START = true;//
    private Path path; //
    public PlayView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public PlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        list = new ArrayList<Bean>();
        path = new Path();
    }

    /**
     * Myview��С
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        path.reset();
        for (int i = 0; i < list.size(); i++) {
            Bean bean = list.get(i);

            path.moveTo(bean.getX(), bean.getY() - 5 * bean.getRadius());

            for (double j = 0; j <= 2 * Math.PI; j += 0.001) {
                float x = (float) (16 * Math.sin(j) * Math.sin(j) * Math.sin(j));
                float y = (float) (13 * Math.cos(j) - 5 * Math.cos(2 * j) - 2
                        * Math.cos(3 * j) - Math.cos(4 * j));
                x *= bean.getRadius();
                y *= bean.getRadius();
                x = bean.getX() - x;
                y = bean.getY() - y;
                path.lineTo(x, y);
            }
            canvas.drawPath(path, paint);
        }
    }


    private Handler beanHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Refresh();
                    invalidate();
                    if (list != null && list.size() > 0) {
                        beanHandler.sendEmptyMessageDelayed(0, 50);
                    }
                    break;

                default:
                    break;
            }
        }

    };

    private Paint initPaint(int alpha) {
        paint = new Paint();
        paint.setAntiAlias(true);// �����
        //paint.setStyle(Paint.Style.FILL);// ���
        paint.setAlpha(alpha);// ͸����
        //paint.setColor(Color.RED);// ��ɫ
        Shader mShader = new LinearGradient(0, 0, 200, 200,
                new int[]{Color.GREEN, Color.WHITE},
                null, Shader.TileMode.REPEAT);
        paint.setShader(mShader);
        return paint;
    }

    private void Refresh() {
        for (int i = 0; i < list.size(); i++) {
            Bean bean = list.get(i);
            if (START == false && bean.getAlpha() == 0) {
                list.remove(i);
                bean.setPaint(null);
                bean = null;
                continue;
            } else if (START == true) {
                START = false;
            }
            bean.setRadius(bean.getRadius()+(float) 0.3);
            bean.setAlpha(bean.getAlpha()-20);

            if (bean.getRadius() > 5 ) {
                bean.setRadius(0);
                bean.setAlpha(0);
            }
            if (bean.getAlpha() < 0) {

                bean.setAlpha(0);
                bean.setRadius(0);
            }

            bean.getPaint().setAlpha(bean.getAlpha());


        }
    }


    public void addBean(List<Point> tapping,boolean []judge) {
        if (tapping != null) {
            for (int i = 0; i < tapping.size(); i++) {
                if (judge[i]) {

                    Bean bean = new Bean();

                    bean.setRadius(0); //

                    bean.setAlpha(MaxAlpha);

                    bean.setX((int) CanvasUtil.transformX(tapping.get(i).x));
                    Log.d("xxxxxxx", bean.getX() + " ");


                    bean.setY((int) CanvasUtil.transformY(tapping.get(i).y));
                    Log.d("xxxxxxx", bean.getY() + " ");


                    bean.setPaint(initPaint(bean.getAlpha()));

                    if (list.size() == 0) {

                        START = true;
                    }
                    list.add(bean);


                    invalidate();

                    if (START) {
                        beanHandler.sendEmptyMessage(0);
                    }
                }
            }


        }
    }
}
