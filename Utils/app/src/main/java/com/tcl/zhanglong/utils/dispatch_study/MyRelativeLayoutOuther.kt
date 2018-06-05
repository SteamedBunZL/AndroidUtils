package com.tcl.zhanglong.utils.dispatch_study

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout
import com.steve.commonlib.DebugLog

/**
 * Created by Steve on 2018/5/21.
 */
class MyRelativeLayoutOuther : RelativeLayout {

    companion object {
        val TAG = "MyRelativeLayoutOuther"
    }

    constructor(context: Context):super(context)

    constructor(context: Context, attributeSet: AttributeSet):super(context,attributeSet)

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when(ev?.action){
            MotionEvent.ACTION_DOWN -> {
                DebugLog.t(TAG).e("ACTION_DOWN")
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                DebugLog.t(TAG).e("ACTION_MOVE")
            }
            MotionEvent.ACTION_UP -> {
                DebugLog.t(TAG).e("ACTION_UP")
            }

            MotionEvent.ACTION_CANCEL -> {
                DebugLog.t(TAG).e("ACTION_CANCEL")
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when(ev?.action){
            MotionEvent.ACTION_DOWN -> {
                DebugLog.t(TAG).e("ACTION_DOWN")
            }
            MotionEvent.ACTION_MOVE -> {
                DebugLog.t(TAG).e("ACTION_MOVE")
            }
            MotionEvent.ACTION_UP -> {
                DebugLog.t(TAG).e("ACTION_UP")
            }

            MotionEvent.ACTION_CANCEL -> {
                DebugLog.t(TAG).e("ACTION_CANCEL")
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                DebugLog.t(TAG).e("ACTION_DOWN")
            }
            MotionEvent.ACTION_MOVE -> {
                DebugLog.t(TAG).e("ACTION_MOVE")
            }
            MotionEvent.ACTION_UP -> {
                DebugLog.t(TAG).e("ACTION_UP")
            }

            MotionEvent.ACTION_CANCEL -> {
                DebugLog.t(TAG).e("ACTION_CANCEL")
            }
        }
        return super.onTouchEvent(event)
    }
}