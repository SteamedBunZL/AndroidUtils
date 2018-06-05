package com.tcl.zhanglong.utils.dispatch_study

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import com.steve.commonlib.DebugLog
import com.tcl.zhanglong.utils.R

/**
 * Created by Steve on 2018/5/21.
 */
class StudyDispatchActivity:AppCompatActivity(),View.OnClickListener{

    companion object {
        val TAG = "StudyDispatchActivity"
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.my_textview -> DebugLog.t(TAG).e("MyTextView onClick")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_dispatch)
        var my_textview = findViewById<MyTextView>(R.id.my_textview)
        my_textview.setOnClickListener(this)
        my_textview.setOnTouchListener { v, event ->
            when(event?.action){
                MotionEvent.ACTION_DOWN -> {
                    DebugLog.t(TAG).e("ACTION_DOWN")
//                        return true
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
            return@setOnTouchListener false
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
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
        return super.dispatchTouchEvent(ev)
//        return false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
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
        return super.onTouchEvent(event)
    }
}