package com.tcl.zhanglong.utils.activity.customview;

import com.tcl.zhanglong.utils.activity.BaseActivity;

/**
 * 这个API因为不支持硬件加速在API 16已经过时了
 * AvoidXfermode只有一个含参的构造方法AvoidXfermode(int opColor, int tolerance, AvoidXfermode.Mode mode)
 *
 * AvoidXfermode有三个参数，第一个opColor表示一个16进制的可以带透明通道的颜色值例如0x12345678，第二个参数tolerance表示容差值
 * 最后一个参数表示AvoidXfermode的具体模式，其可选值只有两个：AvoidXfermode.Mode.AVOID或者AvoidXfermode.Mode.TARGET
 *
 * AvoidXfermode.Mode.TARGET
 * 在该模式下Android会判断画布上的颜色是否会有跟opColor不一样的颜色，比如我opColor是红色，那么在TARGET模式下就会去判断我们的画
 * 布上是否有存在红色的地方，如果有，则把该区域“染”上一层我们画笔定义的颜色，否则不“染”色，而tolerance容差值则表示画布上的像素和我
 * 们定义的红色之间的差别该是多少的时候才去“染”的，比如当前画布有一个像素的色值是(200, 20, 13)，而我们的红色值为(255, 0, 0)，
 * 当tolerance容差值为255时，即便(200, 20, 13)并不等于红色值也会被“染”色，容差值越大“染”色范围越广反之则反
 * Created by Steve on 16/10/12.
 */

public class AvoidXfermodeActivity extends BaseActivity{
    @Override
    protected int getContentView() {
        return 0;
    }
}
