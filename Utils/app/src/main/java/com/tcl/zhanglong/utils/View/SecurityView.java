package com.tcl.zhanglong.utils.View;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.steve.commonlib.DebugLog;
import com.tcl.zhanglong.utils.R;
import com.tcl.zhanglong.utils.View.helper.Point;
import com.tcl.zhanglong.utils.View.helper.PointEvaluator;

/**
 * ━━━━━━神兽出没━━━━━━
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　>      <　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　⌒　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃  护码神兽
 * 　　　　┃　　　┃
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * <p>
 * Created by Steve on 17/8/14.
 * <p>
 * ━━━━━━感觉萌萌哒━━━━━━
 */

public class SecurityView extends View{

    private Paint paint;

    private Paint runPaint;

    private Paint otherPaint;

    private Paint pathPaint;

    private Resources resources;

    private PorterDuffXfermode porterDuffXfermode;

    private float cx,cy,cx1,cy1,cx2,cy2;

    private float [] currentPosition = new float[2];

    private Point point1,point2,point12,point22;

    private Path path;

    private RectF rectF;

    private PathMeasure pathMeasure;

    private float px1,py1,px2,py2;
    private float fx1,fy1,fx2,fy2,fx3,fy3;


    public SecurityView(Context context) {
        super(context);
        initView(context);
    }

    public SecurityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SecurityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        path = new Path();

        resources = context.getResources();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);

        runPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        runPaint.setColor(resources.getColor(R.color._0066e3));
        runPaint.setStyle(Paint.Style.FILL);

    otherPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        otherPaint.setColor(resources.getColor(R.color._0085d2));
        otherPaint.setStyle(Paint.Style.FILL);

        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        pathPaint.setColor(Color.TRANSPARENT);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeWidth(2);


        porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (cx==0){
            cx = getWidth()/2;
            cy = getWidth()/2;
            cx1 = cx - 100;
            cy1 = cy + 100;
            cx2 = cx;
            cy2 = cy;
            currentPosition[0] = cx;
            currentPosition[1] = cy;
            point1 = new Point(cx1,cy1);
            point2 = new Point(cx2,cy2);
            point22 = new Point(cx2 + 250,cy2 + 100);
            px1 = cx + (float)(350*Math.sin(150*Math.PI/180));
            py1 = cy - (float)(350*Math.cos(150*Math.PI/180));
            px2 = cx + (float)(350*Math.sin(60*Math.PI/180));
            py2 = cy - (float)(350*Math.cos(60*Math.PI/180));
            fx1 = (cx + px1) / 2;
            fy1 = (cy + py1) / 2;
            fx2 = (px1 + px2) / 2;
            fy2 = (py2 + py2) / 2;
            fx3 = (px2 + cx) / 2;
            fy3 = (py2 + cy) / 2;
            rectF = new RectF(cx - 350,cy - 350,cx + 350,cy + 350);
            path.moveTo(cx,cy);
            //path.arcTo(rectF,30,-40,false);
            DebugLog.d("px1 %f py1 %f px2 %f py2 %f",px1,py1,px2,py2);
            path.cubicTo(cx + 100,cy + 300,cx + 600,cy + 300,px2,py2);
            path.quadTo(fx3-100,fy3 + 100,cx,cy);
            pathMeasure = new PathMeasure(path,true);
        }

        canvas.drawCircle(cx,cy,300,paint);
        canvas.drawPath(path,pathPaint);

        //画底圈

        sc1 = canvas.saveLayer(0,0,getWidth(),getHeight(),null,Canvas.ALL_SAVE_FLAG);

        canvas.drawCircle(cx,cy,300,otherPaint);

        otherPaint.setXfermode(porterDuffXfermode);

        canvas.drawCircle(cx1,cy1,300,otherPaint);

        otherPaint.setXfermode(null);

        canvas.restoreToCount(sc1);

        //画上圈

        sc = canvas.saveLayer(0,0,getWidth(),getHeight(),null,Canvas.ALL_SAVE_FLAG);

        canvas.drawCircle(cx,cy,300,runPaint);

        runPaint.setXfermode(porterDuffXfermode);

        canvas.drawCircle(currentPosition[0],currentPosition[1],400,runPaint);

        runPaint.setXfermode(null);

        canvas.restoreToCount(sc);


    }

    int sc;
    int sc1;

    public void startAnim(){
        ValueAnimator anim = ValueAnimator.ofObject(new PointEvaluator(),point2,point22);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Point currentPoint = (Point) animation.getAnimatedValue();
                cx2 = currentPoint.getX();
                cy2 = currentPoint.getY();
                postInvalidate();
            }
        });

        anim.setDuration(5000);
        anim.setInterpolator(new LinearInterpolator());

        anim.start();
    }


    public void startPathAnim(){
        final ValueAnimator anim = ValueAnimator.ofFloat(0,pathMeasure.getLength());
        anim.setDuration(13000);
        anim.setInterpolator(new LinearInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                pathMeasure.getPosTan(value,currentPosition,null);
                postInvalidate();
            }
        });
        anim.start();
    }


}
