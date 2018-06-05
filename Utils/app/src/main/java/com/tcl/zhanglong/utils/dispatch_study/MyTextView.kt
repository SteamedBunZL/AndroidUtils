package com.tcl.zhanglong.utils.dispatch_study

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Switch
import android.widget.TextView
import com.steve.commonlib.DebugLog
import java.util.jar.Attributes

/**
 * Created by Steve on 2018/5/21.
 */
class MyTextView:TextView {

    companion object {
        val TAG = "MyTextView"
    }

    constructor(context: Context) : super(context)

    constructor(context: Context,attributes: AttributeSet):super(
            context,
            attributes
    )

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
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
        return super.dispatchTouchEvent(event)
//        return false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                DebugLog.t(TAG).e("ACTION_DOWN")
                return false
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
//        return false
        return super.onTouchEvent(event)
    }
}