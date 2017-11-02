package com.clean.spaceplus.cleansdk.appmgr.uninstall;

/*
 * Copyright (C) 2014 NextApp, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Files.FileColumns;

import com.clean.spaceplus.cleansdk.app.SpaceApplication;
import com.clean.spaceplus.cleansdk.junk.engine.util.NameFilter;
import com.clean.spaceplus.cleansdk.junk.engine.util.PathOperFunc;

import java.io.File;

/**
 * Wrapper for manipulating files via the Android Media Content Provider. As of Android 4.4 KitKat, applications can no longer write
 * to the "secondary storage" of a device. Write operations using the java.io.File API will thus fail. This class restores access to
 * those write operations by way of the Media Content Provider.
 * 
 * Note that this class relies on the internal operational characteristics of the media content provider API, and as such is not
 * guaranteed to be future-proof. Then again, we did all think the java.io.File API was going to be future-proof for media card
 * access, so all bets are off.
 * 
 * If you're forced to use this class, it's because Google/AOSP made a very poor API decision in Android 4.4 KitKat.
 * Read more at https://plus.google.com/+TodLiebeck/posts/gjnmuaDM8sn
 *
 * Your application must declare the permission "android.permission.WRITE_EXTERNAL_STORAGE".
 */
public class MyMediaFile {

    private final File file;
    private final ContentResolver contentResolver;
    private final Uri filesUri;
    private final Uri imagesUri;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public MyMediaFile(ContentResolver contentResolver, File file) {
        this.file = file;
        this.contentResolver = contentResolver;
        filesUri = Files.getContentUri("external");
        imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    /**
     * Deletes the file. Returns true if the file has been successfully deleted or otherwise does not exist. This operation is not
     * recursive.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public boolean delete()
             {
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
                values.put(FileColumns.DATA, file.getAbsolutePath());
                //由imagesUri插入改成filesUri插入提高效率！
                //contentResolver.insert(imagesUri, values);
                contentResolver.insert( filesUri, values);
                values = new ContentValues();
                values.put(FileColumns.MEDIA_TYPE, FileColumns.MEDIA_TYPE_IMAGE);
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
        		values.put(FileColumns.MEDIA_TYPE, FileColumns.MEDIA_TYPE_PLAYLIST);
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
    		Cursor cursor = context.getContentResolver().query(Files.getContentUri("external"), new String[]{FileColumns._ID}, "_data=?", new String[]{path}, null);
    		try {
    			if (cursor != null && cursor.getCount() == 1) { // for files need to be overridden
    				cursor.moveToFirst();
    				long id = cursor.getLong(cursor.getColumnIndex(FileColumns._ID));
    				result = Files.getContentUri("external", id);
    			} else { // for files need to be added
    				ContentValues values = new ContentValues();
    				values.put(FileColumns.DATA, path);
    				result = context.getContentResolver().insert(Files.getContentUri("external"), values);
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