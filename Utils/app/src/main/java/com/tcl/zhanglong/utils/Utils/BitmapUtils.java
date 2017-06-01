package com.tcl.zhanglong.utils.Utils;

import android.graphics.Bitmap;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.HONEYCOMB_MR1;
import static android.os.Build.VERSION_CODES.KITKAT;

/**
 * Created by Steve on 17/5/15.
 */

public class BitmapUtils {

    /**
     * 通用，获取图片的Bytes大小
     * @param bitmap
     * @return
     */
    public static int getBitmapBytes(Bitmap bitmap) {
        int result;
        if (SDK_INT >= KITKAT) {
            result = bitmap.getAllocationByteCount();
        } else if (SDK_INT >= HONEYCOMB_MR1) {
            result = getByteCount(bitmap);
        } else {
            result = bitmap.getRowBytes() * bitmap.getHeight();
        }
        if (result < 0) {
            throw new IllegalStateException("Negative size: " + bitmap);
        }
        return result;
    }

    public static int getByteCount(Bitmap bitmap) {
        return bitmap.getByteCount();
    }
}
