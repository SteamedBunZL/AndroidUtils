package com.tcl.zhanglong.utils.activity.customview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.tcl.zhanglong.utils.R;


/**
 * 光照颜色过滤
 *
 * LightingColorFilter(0xFFFFFFFF, 0x00000000)的时候原图是不会有任何改变的，如果我们想增加红色的值，那么LightingColorFilter(0xFFFFFFFF, 0x00XX0000)就好，
 * 其中XX取值为00至FF。那么这个方法有什么存在的意义呢？存在必定合理，这个方法存在一定是有它可用之处的，
 * 前些天有个盆友在群里问点击一个图片如何直接改变它的颜色而不是为他多准备另一张点击效果的图片，这种情况下该方法就派上用场了！如下图一个灰色的星星，我们点击后让它变成黄色
 *
 * 当我们不想要颜色过滤的效果时，setColorFilter(null)并重绘视图即可
 * Created by Steve on 16/10/11.
 */

public class LightingColorFilterActivity extends AppCompatActivity{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lightingcolorfilter);
    }
}
