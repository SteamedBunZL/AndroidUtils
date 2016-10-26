package com.tcl.zhanglong.utils.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * 心电图view
 * Created by Steve on 16/10/26.
 */

public class ECGView extends View {

    private Paint mPaint;
    private Path mPath;


    private int screenW,screenH;
    private float x,y;//路径初始坐标
    private float initScreenW;//屏幕初始宽度
    private float initX;//初始X轴坐标
    private float transX,moveX;//画布移动的距离

    private boolean isCanvasMove;//画布是否需要平移



    public ECGView(Context context) {
        this(context,null);
    }


    public ECGView(Context context, AttributeSet attrs) {
        super(context,attrs);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(5);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setShadowLayer(7,0,0,Color.GREEN);

        mPath = new Path();
        transX = 0;
        isCanvasMove = false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        screenW = w;
        screenH = h;

        x = 0;
        y = (screenH/2) + screenH/4 + screenH/10;

        initScreenW = screenW;

        initX = screenW/2 + screenW/4;

        moveX = screenW/24;

        mPath.moveTo(x,y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        mPath.lineTo(x,y);

        canvas.translate(-transX,0);

        calCoors();

        canvas.drawPath(mPath,mPaint);

        invalidate();
    }

    private void calCoors(){
        if (isCanvasMove == true)
            transX +=4;

        if (x<initX){
            x += 8;
        }else{
            if (x<initX + moveX){
                x+=2;
                y-=8;
            }else{
                if (x<initX + moveX*2){
                    x+=2;
                    y+=14;
                }else{
                    if (x<initX + moveX*3){
                        x+=2;
                        y-=12;
                    }else{
                        if (x<initX+moveX*4){
                            x+=2;
                            y+=6;
                        }else {
                            if (x<initScreenW){
                                x+=8;
                            }else {
                                isCanvasMove = true;
                                initX = initX + initScreenW;
                            }
                        }
                    }
                }
            }
        }


    }
}
