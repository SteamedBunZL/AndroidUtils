package com.tcl.zhanglong.utils.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Steve on 16/12/20.
 */

public class MySurfaceView extends GLSurfaceView{

    private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例

    private SceneRender myRender;//场景渲染器

    private float myPreviousY;//上次屏幕上的触控位置的Y坐标

    private float myPreviousX;//上次屏幕上的触控位置的X坐标

    public MySurfaceView(Context context) {
        super(context);

        myRender = new SceneRender();//创建场景渲染器
        this.setRenderer(myRender);//设置渲染器
        this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//渲染模式为主动渲染
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();//获取当前触点Y坐标
        float x = event.getX();//获取当前触点X坐标

        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                float dy = y - myPreviousY;//滑动距离在Y轴方向的垂直距离
                float dx = x - myPreviousX;
                myRender.tr.yAngle+=dx*TOUCH_SCALE_FACTOR;
                myRender.tr.zAngle+=dy*TOUCH_SCALE_FACTOR;
                requestRender();
        }

        myPreviousY = y;
        myPreviousX = x;


        return true;
    }

    private class SceneRender implements GLSurfaceView.Renderer{

        Triangle tr = new Triangle();

        public SceneRender() {
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            gl.glDisable(GL10.GL_DITHER);
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,GL10.GL_FASTEST);
            gl.glClearColor(0,0,0,0);
            gl.glEnable(GL10.GL_DEPTH_TEST);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glViewport(0,0,width,height);
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            float ratio = (float)width/height;
            gl.glFrustumf(-ratio,ratio,-1,1,1,10);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            gl.glEnable(GL10.GL_CULL_FACE);
            gl.glShadeModel(GL10.GL_SMOOTH);
            gl.glFrontFace(GL10.GL_CCW);
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT| GL10.GL_DEPTH_BUFFER_BIT);
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glTranslatef(0,0,-2.0f);
            tr.drawSelf(gl);
        }
    }

}
