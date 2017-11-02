package com.clean.spaceplus.cleansdk.junk.engine;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.junk.engine.bean.MediaFile;
import com.clean.spaceplus.cleansdk.junk.engine.photo.PicRecycleNameConvert;
import com.clean.spaceplus.cleansdk.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/16 16:42
 * @copyright TCL-MIG
 */
public class MediaFileOperator {
    private static void deleteImagesFromMediaStore(String where, String[] args) {
        if (TextUtils.isEmpty(where) || (args == null) || (args.length == 0)) {
            return;
        }

        try {
            ContentResolver cr = SpaceApplication.getInstance().getContext().getContentResolver();
            cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, where, args);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public static void deleteImagesFromMediaStoreByList(List<MediaFile> list) {
        String where = MediaStore.Images.Media.DATA + " = ?";
        StringBuffer sb = new StringBuffer();
        List<String> pathList = new ArrayList<String>();
        for (MediaFile mediaFile : list) {
            if (sb.length() != 0) {
                sb.append(" or ");
            }
            sb.append(where);
            pathList.add(mediaFile.getPath());
        }

        String[] paths = new String[pathList.size()];
        pathList.toArray(paths);
        deleteImagesFromMediaStore(sb.toString(), paths);
    }

    private static void updateImagesFromMediaStore(ContentValues cv, String where, String[] args) {
        if (cv == null || TextUtils.isEmpty(where) || (args == null) || (args.length == 0)) {
            return;
        }
        try {
            ContentResolver cr = SpaceApplication.getInstance().getContext().getContentResolver();
            cr.update(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv, where, args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新MediaStore中MediaFile，批量处理，提高效率
     *
     */
    public static void updateImagesFromMediaStoreByMediaFiles(List<MediaFile> list) {
        updateImagesFromMediaStoreInternal(list, MediaStore.Images.Media._ID);
    }

    private static interface Proxy<T, R> {
        public R invoke(T t);
    }

    /**
     * 更新MediaStore中MediaFile，批量处理，提高效率
     *
     */
    private static void updateImagesFromMediaStoreInternal(List<MediaFile> list, String whereColumn) {
        if (list == null || list.isEmpty() || TextUtils.isEmpty(whereColumn)) {
            return;
        }
        String selection = null;
        Proxy<MediaFile, String> p = null;
        if (MediaStore.Images.Media._ID.equals(whereColumn)) {
            selection = MediaStore.Images.Media._ID + " = ?";
            p = new Proxy<MediaFile, String>() {
                public String invoke(MediaFile m) {
                    return String.valueOf(m.getId());
                }
            };
        } else if (MediaStore.Images.Media.DATA.equals(whereColumn)) {
            selection = MediaStore.Images.Media.DATA + " = ?";
            p = new Proxy<MediaFile, String>() {
                public String invoke(MediaFile m) {
                    return m.getPath();
                }
            };
        } else {
            return;
        }

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        for (MediaFile mediaFile : list) {
            ContentValues cv = new ContentValues(4);
            cv.put(MediaStore.Images.Media.SIZE, mediaFile.getSize());
            cv.put(MediaStore.Images.Media.MIME_TYPE, mediaFile.getMimeType());
            if (mediaFile.getDateTaken() > 0) {// 因为如果没有指定DATE_TAKEN，会拿DATE_MODIFIED的指定值设为DATE_TAKEN
                cv.put(MediaStore.Images.Media.DATE_MODIFIED, mediaFile.lastModified());
                cv.put(MediaStore.Images.Media.DATE_TAKEN, mediaFile.getDateTaken());
            }

            ops.add(ContentProviderOperation.newUpdate(MediaStore.Images.Media.EXTERNAL_CONTENT_URI).withSelection(selection, new String[]{p.invoke(mediaFile)}).withValues(cv).build());
        }

        try {
            ContentResolver cr = SpaceApplication.getInstance().getContext().getContentResolver();
            cr.applyBatch(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getAuthority(), ops);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void insertToMediaStoreByList(List<MediaFile> list) {
        ContentResolver cr = SpaceApplication.getInstance().getContext().getContentResolver();
        PicRecycleNameConvert mNameConvert = new PicRecycleNameConvert();

        for (MediaFile mediaFile : list) {
            File f = new File(mediaFile.getPath());
            String strTitile = mNameConvert.convert(f, PicRecycleNameConvert.CONVERT_RECOVERY_MEDIA_TITLE, 0);
            File parent = f.getParentFile();
            String path = StringUtils.toLowerCase(parent.toString());
            String name = StringUtils.toLowerCase(parent.getName());

            ContentValues newValues = new ContentValues();
            newValues.put(MediaStore.Images.Media.TITLE, strTitile);
            newValues.put(MediaStore.Images.Media.DISPLAY_NAME, strTitile);
            newValues.put(MediaStore.Images.Media.DATA, mediaFile.getPath());
            newValues.put(MediaStore.Images.Media.SIZE, mediaFile.getSize());
            newValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            newValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
            newValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            newValues.put(MediaStore.Images.Media.DATE_MODIFIED, mediaFile.lastModified());

            newValues.put(MediaStore.Images.ImageColumns.BUCKET_ID, path.hashCode());
            newValues.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, name);
            try {
                cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, newValues);
            } catch (Throwable e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
