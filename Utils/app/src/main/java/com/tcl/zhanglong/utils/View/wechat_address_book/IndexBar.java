package com.tcl.zhanglong.utils.View.wechat_address_book;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.steve.commonlib.DebugLog;
import com.tcl.zhanglong.utils.R;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static android.R.attr.baseline;
import static android.R.attr.theme;

/**
 * Created by Steve on 2017/12/4.
 */

public class IndexBar extends View {
    public IndexBar(Context context) {
        this(context,null);

    }

    public IndexBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public IndexBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    public static String[] INDEX_STRING = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};

    private List<String> mIndexDatas;

    private int mWidth,mHeight;

    private int mGapHeight;

    private Paint mPaint;

    private int mPressedBackground;

    private TextView mPressedShowTextView;

    private LinearLayoutManager mLayoutManager;

    private List<MusicBean> mSourcesDatas;



    private void init(Context context,AttributeSet attrs,int defStyleAttr){
        int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,16,getResources().getDisplayMetrics());
        mPressedBackground = Color.BLACK;
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.IndexBar,defStyleAttr,0);
        int n = typedArray.getIndexCount();
        for(int i = 0;i<n;i++){
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.IndexBar_indexBarTextSize)
                textSize = typedArray.getDimensionPixelSize(attr,textSize);
            else if (attr == R.styleable.IndexBar_indexBarPressBackground)
                mPressedBackground = typedArray.getColor(attr,mPressedBackground);
        }
        typedArray.recycle();

        initIndexDatas();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(textSize);
        mPaint.setColor(Color.BLACK);

        setOnIndexPressedListener(new onIndexPressedListener() {
            @Override
            public void onIndexPressed(int index, String text) {
                if (mPressedShowTextView!=null){
                    mPressedShowTextView.setVisibility(View.VISIBLE);
                    mPressedShowTextView.setText(text);
                }


                if (mLayoutManager!=null){
                    int position = getPosByTag(text);
                    DebugLog.w("=== text = %s,position = %d",text,position);
                    if (position!=-1){
                        mLayoutManager.scrollToPositionWithOffset(position,0);
                    }
                }
            }

            @Override
            public void onMontionEventEnd() {
                if (mPressedShowTextView!=null)
                    mPressedShowTextView.setVisibility(View.GONE);
            }
        });

    }

    private void initIndexDatas(){
        mIndexDatas = Arrays.asList(INDEX_STRING);
    }

    public interface onIndexPressedListener{
        void onIndexPressed(int index,String text);

        void onMontionEventEnd();
    }

    private onIndexPressedListener mOnIndexPressedListener;

    public void setOnIndexPressedListener(onIndexPressedListener listener){
        this.mOnIndexPressedListener = listener;
    }


    private int getPosByTag(String tag){
        if (null == mSourcesDatas || mSourcesDatas.isEmpty()){
            return -1;
        }

        if (TextUtils.isEmpty(tag)){
            return -1;
        }

        for(int i = 0;i<mSourcesDatas.size();i++){
            if (tag.equals(mSourcesDatas.get(i).getTag()))
                return i;
        }

        return -1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);

        int measureWidth = 0,measureHeight = 0;

        Rect indexBounds = new Rect();
        String index;
        for(int i = 0;i<mIndexDatas.size();i++){
            index = mIndexDatas.get(i);
            mPaint.getTextBounds(index,0,index.length(),indexBounds);
            measureWidth = Math.max(indexBounds.width(),measureWidth);
            measureHeight = Math.max(indexBounds.height(),measureHeight);
        }

        measureHeight*= mIndexDatas.size();
        switch (wMode){
            case MeasureSpec.EXACTLY:
                measureWidth = wSize;
                break;
            case MeasureSpec.AT_MOST:
                measureWidth = Math.min(measureWidth,wSize);
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }

        switch (hMode){
            case MeasureSpec.EXACTLY:
                measureHeight = hSize;
                break;
            case MeasureSpec.AT_MOST:
                measureHeight = Math.min(measureHeight,hSize);
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }

        setMeasuredDimension(measureWidth,measureHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int t = getPaddingTop();
        String index;
        for(int i = 0;i<mIndexDatas.size();i++){
            index = mIndexDatas.get(i);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            int baseline = (int) ((mGapHeight - fontMetrics.bottom - fontMetrics.top) / 2);
            canvas.drawText(index, mWidth / 2 - mPaint.measureText(index) / 2, t + mGapHeight * i + baseline, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                setBackgroundColor(mPressedBackground);
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                int pressI = (int) ((y - getPaddingTop()) / mGapHeight);
                if (pressI <0)
                    pressI = 0;
                else if (pressI >= mIndexDatas.size())
                    pressI = mIndexDatas.size()-1;

                if (mOnIndexPressedListener!=null && pressI>-1&&pressI<mIndexDatas.size())
                    mOnIndexPressedListener.onIndexPressed(pressI,mIndexDatas.get(pressI));
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setBackgroundResource(android.R.color.transparent);

                if (mOnIndexPressedListener!=null)
                    mOnIndexPressedListener.onMontionEventEnd();
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        if (mIndexDatas==null||mIndexDatas.isEmpty())
            return;
        computeGapHeight();
    }

    private void computeGapHeight() {
        mGapHeight = (mHeight - getPaddingTop() - getPaddingBottom()) / mIndexDatas.size();
    }

    public IndexBar setSourceDatas(List<MusicBean> sourceDatas){
        this.mSourcesDatas = sourceDatas;
        initSourceDatas();
        return this;
    }

    private void initSourceDatas(){
        if (mSourcesDatas==null||mSourcesDatas.isEmpty())
            return;

        fillIndexTag(mSourcesDatas);
    }

    private void fillIndexTag(List<MusicBean> datas){
        if (datas == null|| datas.isEmpty())
            return;
        int size = datas.size();
        MusicBean bean = null;
        for(int i = 0;i<size;i++){
            bean = datas.get(i);
            String tagStr = bean.getMusic().substring(0,1).toUpperCase();
            if (tagStr.matches("[A-Z]"))
                bean.setTag(tagStr);
            else
                bean.setTag("#");

        }
    }

    public IndexBar setLayoutManager(LinearLayoutManager mLayoutManager) {
        this.mLayoutManager = mLayoutManager;
        return this;
    }

    public IndexBar setPressedShowTextView(TextView mPressedShowTextView) {
        this.mPressedShowTextView = mPressedShowTextView;
        return this;
    }


}


