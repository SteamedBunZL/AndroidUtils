package com.clean.spaceplus.cleansdk.junk.engine;

import java.util.Locale;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/5 13:59
 * @copyright TCL-MIG
 */
public class MediaFileDownloader {
    public enum OtherScheme {
        VIDEO("video"), UNKNOWN("");

        private String scheme;
        private String uriPrefix;

        OtherScheme(String scheme) {
            this.scheme = scheme;
            uriPrefix = scheme + "://";
        }

        /**
         * Defines scheme of incoming URI
         *
         * @param uri URI for scheme detection
         * @return Scheme of incoming URI
         */
        public static OtherScheme ofUri(String uri) {
            if (uri != null) {
                for (OtherScheme s : values()) {
                    if (s.belongsTo(uri)) {
                        return s;
                    }
                }
            }
            return UNKNOWN;
        }

        private boolean belongsTo(String uri) {
            return uri.toLowerCase(Locale.US).startsWith(uriPrefix);
        }

        /** Appends scheme to incoming path */
        public String wrap(String path) {
            return uriPrefix + path;
        }

    }
}
