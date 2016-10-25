package com.tcl.zhanglong.utils.View;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.view.View;

import com.tcl.zhanglong.utils.R;
import com.tcl.zhanglong.utils.Utils.MeasureUtil;

/**
 * Created by Steve on 16/10/12.
 */

public class PorterDuffColorFilterView extends View{

    private Paint mPaint;// 画笔
    private Context mContext;// 上下文环境引用
    private Bitmap bitmap;// 位图


    private int x, y;// 位图绘制时左上角的起点坐标


    public PorterDuffColorFilterView(Context context) {
        this(context,null);


    }


    public PorterDuffColorFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        // 初始化画笔
        initPaint();

        // 初始化资源
        initRes(context);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        // 实例化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // 设置颜色过滤
        mPaint.setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.DARKEN));
    }

    /**
     * 初始化资源
     */
    private void initRes(Context context) {
        // 获取位图
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bitmap2);

        /*
         * 计算位图绘制时左上角的坐标使其位于屏幕中心
         * 屏幕坐标x轴向左偏移位图一半的宽度
         * 屏幕坐标y轴向上偏移位图一半的高度
         */
        x = MeasureUtil.getScreenSize((Activity) mContext)[0] / 2 - bitmap.getWidth() / 2;
        y = MeasureUtil.getScreenSize((Activity) mContext)[1] / 2 - bitmap.getHeight() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制位图
        canvas.drawBitmap(bitmap, x, y, mPaint);
    }
}
