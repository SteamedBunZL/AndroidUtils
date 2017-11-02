package com.clean.spaceplus.cleansdk.base.db;

import android.net.Uri;
import android.text.TextUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/4/25 19:17
 * @copyright TCL-MIG
 */
public class DbUriUtil {
    static public boolean isMyUri(Uri baseUri, Uri targetUri) {
        if (null == baseUri || null == targetUri)
            return false;

        boolean result = false;
        String target = targetUri.toString();
        String base = baseUri.toString();
        if (target.startsWith(base)) {
            result = true;
        }
        return result;
    }

    static public Uri getUri(Uri baseUri, String table) {
        if (null == baseUri || TextUtils.isEmpty(table))
            return null;

        Uri result =  baseUri.buildUpon().appendPath(table).build();

        return result;
    }

    static public String getTable(Uri uri) {
        if (null == uri)
            return null;

        return uri.getLastPathSegment();
    }

    static public class TableNameToUriCache {
        private Uri mBaseUri;
        private ConcurrentHashMap<String, Uri> mUriCache = new ConcurrentHashMap<String, Uri>();

        public TableNameToUriCache() {
        }

//		public TableNameToUriCache(Uri baseUri) {
//			mBaseUri = baseUri;
//		}

        public void clear() {
            mUriCache.clear();
        }

        public void setBaseUri(Uri baseUri) {
            mBaseUri = baseUri;
        }

/*		public Uri getBaseUri() {
			return mBaseUri;
		}*/

        public Uri getUri(String table) {
            Uri result = mUriCache.get(table);
            if (result != null)
                return result;

            result = DbUriUtil.getUri(mBaseUri, table);

            mUriCache.put(table, result);

            return result;
        }
    }
}
