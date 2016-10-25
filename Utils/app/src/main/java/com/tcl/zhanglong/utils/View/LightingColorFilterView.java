package com.tcl.zhanglong.utils.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.tcl.zhanglong.utils.R;
import com.tcl.zhanglong.utils.Utils.MeasureUtil;

/**
 * Created by Steve on 16/10/11.
 */

public class LightingColorFilterView extends View {


    private Paint mPaint;//画笔
    private Context mContext;//上下文环境引用
    private Bitmap bitmap;//位图

    private int x,y;//位图绘制左上角的起点坐标
    private boolean isClick;//用来标识是否被点击过





    public LightingColorFilterView(Context context) {
        this(context,null);
    }


    public LightingColorFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        //初始化画笔
        initPaint();

        //初始化资源
        initRes(context);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClick){
                    //如果已经被点击时设置颜色这滤为空还原灰色
                    mPaint.setColorFilter(null);
                    isClick = false;
                }else{
                    //如果未被点击时设置颜色过滤后为黄色
                    mPaint.setColorFilter(new LightingColorFilter(0xFFFFFFFF,0x00FFFF00));
                    isClick = true;
                }

                invalidate();
            }
        });

    }


    public LightingColorFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化画笔
     */
    private void initPaint(){
        //实例化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    /**
     * 初始化资源
     */
    private void initRes(Context context){
        //获取位图
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bitmap1);

        /**
         * 计算位图绘制左上角的坐标使其位于屏幕中心
         * 屏幕坐标x轴向左偏移位图一半的的宽度
         * 屏幕坐标y轴向上偏移位图一伴的的高度
         */
        x = MeasureUtil.getScreenSize(context)[0]/2 - bitmap.getWidth()/2;
        y = MeasureUtil.getScreenSize(context)[1]/2 - bitmap.getHeight()/2;


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制位图
        canvas.drawBitmap(bitmap,x,y,mPaint);
    }
}
