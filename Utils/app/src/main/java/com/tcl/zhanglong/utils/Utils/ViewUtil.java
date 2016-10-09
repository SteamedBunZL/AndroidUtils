package com.tcl.zhanglong.utils.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 主要是Paint 和 Canvas的一些属性 有详细注释
 *
 * Created by Steve on 16/10/9.
 */

public class ViewUtil extends View{

    private Paint mPaint;

    private Paint mPaint2;

    private Context mContext;


    public ViewUtil(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public ViewUtil(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }


    public ViewUtil(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }


    private void initView(){

        mPaint = new Paint();

        //当前画笔设置一个画笔，说白了就是把另一个画笔的属性设置Copy给我们的画笔
        mPaint2.set(mPaint);

        mPaint.setARGB(20,30,40,50);

        //透明度设置
        mPaint.setAlpha(50);

        //打开抗锯齿，不过我要说明一点，抗锯齿是依赖于算法的，算法决定抗锯齿的效率，在我们绘制棱角分明的图像时，比如一个矩形、一张位图，我们不需要打开抗锯齿。
        mPaint.setAntiAlias(true);

        //设置颜色
        mPaint.setColor(Color.RED);

        ColorMatrix colorMatrix = new ColorMatrix(new float[]{
                0.5F, 0, 0, 0, 0,
                0, 0.5F, 0, 0, 0,
                0, 0, 0.5F, 0, 0,
                0, 0, 0, 1, 0,
        });

        //设置颜色过滤
        mPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));


    }


}
