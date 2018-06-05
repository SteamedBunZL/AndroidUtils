package com.tcl.zhanglong.utils.slide_confilct

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import android.widget.Scroller

/**
 * Created by Steve on 2018/5/23.
 */
class HorizontalScrollViewEx:ViewGroup {

    var mChildrenSize = 0
    var mChildWidth = 0
    var mChildIndex = 0

    var mLastX = 0
    var mLastY = 0

    var mLastXIntercept = 0
    var mLastYIntercept = 0

    lateinit var mScroller:Scroller
    lateinit var mVelocityTracker:VelocityTracker

    constructor(context: Context):super(context){
        init()
    }

    constructor(context: Context,attributeSet: AttributeSet):super(context,attributeSet){
        init()
    }

    constructor(context: Context,attributeSet: AttributeSet,defStyle:Int):super(context,attributeSet,defStyle){
        init()
    }


    private fun init(){
        if (mScroller == null){
            mScroller = Scroller(context)
            mVelocityTracker = VelocityTracker.obtain()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var measureWidth = 0
        var measureHeight = 0
        measureChildren(widthMeasureSpec,heightMeasureSpec)

        var widthSpaceSize = MeasureSpec.getSize(widthMeasureSpec)
        var widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)

        var heightSpaceSize = MeasureSpec.getSize(heightMeasureSpec)
        var heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)

        if (childCount == 0){
            setMeasuredDimension(0,0)
        }else if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST){
            val childView = getChildAt(0)
            measureWidth = childView.measuredWidth*childCount
            measureHeight = childView.measuredHeight
            setMeasuredDimension(measuredWidth,measuredHeight)
        }else if(heightMeasureSpec == MeasureSpec.AT_MOST){
            val childView = getChildAt(0)
            measureHeight = childView.measuredHeight
            setMeasuredDimension(widthSpaceSize,childView.measuredHeight)
        }else if(widthMeasureSpec == MeasureSpec.AT_MOST){
            val childView = getChildAt(0)
            measureWidth = childView.measuredWidth*childCount
            setMeasuredDimension(measuredWidth,heightSpaceSize)
        }

    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var childLeft = 0
        mChildrenSize = childCount

        for (i in 0..childCount){
            val childView = getChildAt(i)
            if (childView.visibility != View.GONE){
                val childWidth = childView.measuredWidth
                mChildWidth = childWidth
                childView.layout(childLeft,0,childLeft + childWidth,childView.measuredHeight)
                childLeft += childWidth
            }
        }
    }

    private fun smoothScrollBy(dx:Int,dy:Int){
        mScroller.startScroll(scrollX,0,dx,0,500)
        invalidate()
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()){
            scrollTo(mScroller.currX,mScroller.currY)
            postInvalidate()
        }
    }

    override fun onDetachedFromWindow() {
        mVelocityTracker.recycle()
        super.onDetachedFromWindow()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        var intercepted = false
        var x = ev!!.getX()
        var y = ev!!.getY()

        when(ev.action){
            MotionEvent.ACTION_DOWN -> {
                intercepted = false
                if (!mScroller.isFinished){
                    mScroller.abortAnimation()
                    intercepted = true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                var deltaX = x - mLastXIntercept
                var deltaY = y - mLastYIntercept
                if (Math.abs(deltaX) > Math.abs(deltaY)){
                    intercepted = true
                }else{
                    intercepted = false
                }
            }

            MotionEvent.ACTION_UP -> {
                intercepted = false
            }
        }

        mLastX = x.toInt()
        mLastY = y.toInt()
        mLastXIntercept = x.toInt()
        mLastYIntercept = y.toInt()

        return intercepted
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }
}