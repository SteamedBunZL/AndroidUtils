package com.tcl.zhanglong.utils.View;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import com.tcl.zhanglong.utils.Utils.MeasureUtil;
import com.tcl.zhanglong.utils.View.helper.PorterDuffBo;

/**
 * Created by Steve on 16/10/25.
 */

public class PorterDuffView extends View{

    /**
     * PorterDuff模式常量
     * 可以在此更改不同的模式测试
     */
    private static final PorterDuff.Mode MODE = PorterDuff.Mode.ADD;

    private static final int RECT_SIZE_SMALL = 400;//左右上方示例渐变正方形的尺寸大小

    private static final int RECT_SIZE_BIG = 800;//中间测试渐变正方形的尺寸大小

    private Paint mPaint;//画笔

    private PorterDuffBo porterDuffBo;//PoterDuff类的业务对象

    private PorterDuffXfermode porterDuffXfermode;//图形混合模式

    private int screenW,screenH;//屏幕尺寸

    private int s_l,s_t;//左上正方形的原点坐标

    private int d_l,d_t;//右上正方形的原点坐标

    private int rectX,recY;//中间正形的原点坐标



    public PorterDuffView(Context context) {
        super(context);

        //实例化画笔并设置抗锯齿
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        //实例化业务对象
        porterDuffBo = new PorterDuffBo();

        //实例业混合模式
        porterDuffXfermode = new PorterDuffXfermode(MODE);

        //计算坐标

    }

    private void calu(Context context){
        //获取包含屏幕尺寸的数组
        int[] screenSize = MeasureUtil.getScreenSize((Activity)context);

        //获取屏幕尺寸
        screenW = screenSize[0];
        screenH = screenSize[1];

        //计算左上方正方形原点坐标
        d_l = screenW - RECT_SIZE_SMALL;
        d_t = 0;

        //计算中间正方形原点坐标
        rectX = screenW/2 - RECT_SIZE_BIG /2;
        recY = RECT_SIZE_SMALL + (screenH - RECT_SIZE_SMALL)/2 - RECT_SIZE_BIG/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置画布颜色为黑色以便我们更好的观察
        canvas.drawColor(Color.BLACK);

        //设置业务对象尺寸计算生成左右上方的渐变方形
//        porterDuffBo
    }

    public PorterDuffView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
