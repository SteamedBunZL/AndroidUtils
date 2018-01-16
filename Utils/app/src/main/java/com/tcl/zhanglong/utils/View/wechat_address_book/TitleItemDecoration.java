package com.tcl.zhanglong.utils.View.wechat_address_book;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Steve on 2017/12/4.
 */

public class TitleItemDecoration extends RecyclerView.ItemDecoration{

    private ArrayList<MusicBean> mDatas = new ArrayList<>();
    private int mTitleHeight;
    private static int mTitleFontSize;
    private Paint mPaint;
    private Rect mBounds;

    private static int COLOR_TITLE_BG = Color.parseColor("#FFDFDFDF");
    private static int COLOR_TITLE_FONT = Color.parseColor("#FF000000");


    public TitleItemDecoration(Context context, List<MusicBean> datas) {
        super();
        mDatas.clear();
        mDatas.addAll(datas);
        mBounds = new Rect();
        mPaint = new Paint();
        mTitleHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics());
        mTitleFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, context.getResources().getDisplayMetrics());
        mPaint.setTextSize(mTitleFontSize);
        mPaint.setAntiAlias(true);

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position =  ((RecyclerView.LayoutParams)view.getLayoutParams()).getViewLayoutPosition();
        if (position > -1){
            if (position ==0){
                outRect.set(0,mTitleHeight,0,0);
            }else{
                if (null !=  mDatas.get(position).getTag() && !mDatas.get(position).getTag().equals(mDatas.get(position - 1).getTag())){
                    outRect.set(0,mTitleHeight,0,0);
                }else{
                    outRect.set(0,0,0,0);
                }
            }
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft();//RecyclerView可能设置了paddingleft
        final int right = parent.getWidth() - parent.getPaddingRight();//RecyclerView可能设置了paddingRight
        final int childCount = parent.getChildCount();
        for (int i = 0;i<childCount;i++){
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int position = params.getViewLayoutPosition();
            if (position > -1){
                if (position ==0){
                    drawTitleArea(c,left,right,child,params,position);
                }else{
                    if (null!=mDatas.get(position).getTag()&&!mDatas.get(position).getTag().equals(mDatas.get(position-1).getTag())){
                        drawTitleArea(c,left,right,child,params,position);
                    }
                }
            }
        }
    }

    private void drawTitleArea(Canvas c,int left,int right,View child,RecyclerView.LayoutParams params,int position){
        mPaint.setColor(COLOR_TITLE_BG);//这里的top 减掉titileHeight是对的，因为y坐标是向下的 child.getTop()获取的是ReycyclerView里的每一项
        c.drawRect(left,child.getTop() - params.topMargin - mTitleHeight,right,child.getTop() - params.topMargin,mPaint);
        mPaint.setColor(COLOR_TITLE_FONT);

        mPaint.getTextBounds(mDatas.get(position).getTag(),0,mDatas.get(position).getTag().length(),mBounds);
        c.drawText(mDatas.get(position).getTag(),child.getPaddingLeft(),child.getTop() - params.topMargin - (mTitleHeight/2 - mBounds.height()/2),mPaint);
    }


    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int pos = ((LinearLayoutManager) parent.getLayoutManager()).findFirstVisibleItemPosition();

        if (pos < 0)
            return;

        boolean flag  = false;

        String tag = mDatas.get(pos).getTag();
        View child = parent.findViewHolderForLayoutPosition(pos).itemView;//获取到position中的child

        if (tag!=null&&!tag.equals(mDatas.get(pos + 1).getTag())){
            if (child.getHeight() + child.getTop()<mTitleHeight){
                c.save();
                flag = true;
                c.translate(0,child.getHeight() + child.getTop() - mTitleHeight);
            }
        }
        mPaint.setColor(COLOR_TITLE_BG);//这里的top 减掉titileHeight是对的，因为y坐标是向下的 child.getTop()获取的是ReycyclerView里的每一项
        c.drawRect(parent.getPaddingLeft(),parent.getPaddingTop(),parent.getRight() - parent.getPaddingRight(),parent.getPaddingTop() + mTitleHeight,mPaint);
        mPaint.setColor(COLOR_TITLE_FONT);

        mPaint.getTextBounds(tag,0,tag.length(),mBounds);
        c.drawText(tag,child.getPaddingLeft(),parent.getPaddingTop() + mTitleHeight - (mTitleHeight/2 - mBounds.height()/2),mPaint);

        if (flag)
            c.restore();

    }


}
