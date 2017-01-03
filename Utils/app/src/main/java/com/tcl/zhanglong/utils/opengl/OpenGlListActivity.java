package com.tcl.zhanglong.utils.opengl;

import com.tcl.zhanglong.utils.activity.BaseListActivity;
import com.tcl.zhanglong.utils.opengl_new.GL_Demo;

import java.util.Arrays;

/**
 * Created by Steve on 16/12/20.
 */

public class OpenGlListActivity extends BaseListActivity{


    @Override
    protected String[] getFuncStrArray() {
        return new String[]{
                FirstOpenGlActivity.class.getName(),
                GL_Demo.class.getName()
        };
    }
}
