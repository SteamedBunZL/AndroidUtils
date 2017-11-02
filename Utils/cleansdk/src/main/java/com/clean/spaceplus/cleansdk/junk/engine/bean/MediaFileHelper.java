package com.clean.spaceplus.cleansdk.junk.engine.bean;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.junk.engine.util.NameFilter;
import com.clean.spaceplus.cleansdk.junk.engine.util.PathOperFunc;

import java.io.File;
import java.io.IOException;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/7 14:31
 * @copyright TCL-MIG
 */
public class MediaFileHelper {
    private final File file;
    private final ContentResolver contentResolver;
    private final Uri filesUri;
    private final Uri imagesUri;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public MediaFileHelper(ContentResolver contentResolver, File file) {
        this.file = file;
        this.contentResolver = contentResolver;
        filesUri = MediaStore.Files.getContentUri("external");
        imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    /**
     * Deletes the file. Returns true if the file has been successfully deleted or otherwise does not exist. This operation is not
     * recursive.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public boolean delete()
            throws IOException {
        if (!file.exists()) {
            return true;
        }

        boolean directory = file.isDirectory();
        if (directory) {
            // Verify directory does not contain any files/directories within it.
            PathOperFunc.StringList files = PathOperFunc.listDir(file.getPath(), new NameFilter() {

                private boolean bHaveSub = false;

                @Override
                public boolean accept(String parent, String sub, boolean bFolder) {
                    if (bHaveSub) {
                        return false;
                    }

                    bHaveSub = true;

                    return true;
                }

            });
            try {
                if (files != null && files.size() > 0) {
                    return false;
                }
            } finally {
                if (null != files) {
                    files.release();
                    files = null;
                }
            }
        }

        String where = MediaStore.MediaColumns.DATA + "=?";
        String[] selectionArgs = new String[] { file.getAbsolutePath() };

        try {
            // Delete the entry from the media database. This will actually delete media files (images, audio, and video).
            contentResolver.delete(filesUri, where, selectionArgs);

            if (file.exists()) {
                // If the file is not a media file, create a new entry suggesting that this location is an image, even
                // though it is not.
                ContentValues values = new ContentValues();
                values.put(MediaStore.Files.FileColumns.DATA, file.getAbsolutePath());
                //由imagesUri插入改成filesUri插入提高效率！
                //contentResolver.insert(imagesUri, values);
                contentResolver.insert( filesUri, values);
                values = new ContentValues();
                values.put(MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                contentResolver.update( filesUri, values, where, selectionArgs);

                // Delete the created entry, such that content provider will delete the file.
                contentResolver.delete(filesUri, where, selectionArgs);
            }

            if (file.exists()) {
                //Log.d("mf", "old " + file.exists());
                deleteByMediaTypePlaylist();

                //Log.d("mf", "new " + file.exists());
            }

        } catch (Exception e) {
            // TODO: handle exception
        }

        return !file.exists();
    }

    /**
     * 据4.4.2源码，MediaProvider::delete 对MEDIA_TYPE_IMAGE/MEDIA_TYPE_VIDEO/MEDIA_TYPE_AUDIO三个type调用无效，
     * 用MEDIA_TYPE_PLAYLIST可行
     *
     * */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private boolean deleteByMediaTypePlaylist() {
        Context ctx = SpaceApplication.getInstance().getContext();

        Uri uri = getMediaUri(ctx, file.getPath());
        if (uri != null) {
            try {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.MEDIA_TYPE_PLAYLIST);
                ctx.getContentResolver().update(uri, values, null, null);
                ctx.getContentResolver().delete(uri, null, null);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        return !file.exists();
    }

    /**
     * Query out the file record in media database, if not found, create one.
     * @param context
     * @param path
     * @return
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static Uri getMediaUri(Context context, String path) {

        Uri result = null;
        try {
            Cursor cursor = context.getContentResolver().query(MediaStore.Files.getContentUri("external"), new String[]{MediaStore.Files.FileColumns._ID}, "_data=?", new String[]{path}, null);
            try {
                if (cursor != null && cursor.getCount() == 1) { // for files need to be overridden
                    cursor.moveToFirst();
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
                    result = MediaStore.Files.getContentUri("external", id);
                } else { // for files need to be added
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Files.FileColumns.DATA, path);
                    result = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                }
                return result;
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return result;
    }
}
