package com.clean.spaceplus.cleansdk.junk.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author dongdong.huang
 * @Description:
 * @date 2016/5/13 19:33
 * @copyright TCL-MIG
 */
public class PatternCache {
    Map<String, Map<Integer,Pattern>> mCache = new HashMap<String, Map<Integer,Pattern>>();

    public Pattern compile(String pattern) {
        return compile( pattern, 0 );
    }

    public Pattern compile(String regularExpression, int flags) {
        Pattern pattern = null;
        if ( (pattern = find(regularExpression,flags))!=null ) {
            return pattern;
        }
        pattern = Pattern.compile( regularExpression, flags);
        save( regularExpression, flags, pattern);
        return pattern;
    }

    private Pattern find( String regularExpression, int flags ) {
        synchronized (this) {
            if ( !mCache.containsKey(regularExpression) ) {
                return null;
            }
            Map<Integer,Pattern> map = mCache.get(regularExpression);
            if ( map == null ) {
                return null;
            }
            return map.get(flags);
        }
    }

    private void save( String regularExpression, int flags, Pattern pattern  ) {
        if ( pattern == null ) {
            return;
        }
        synchronized (this) {
            Map<Integer,Pattern> map = mCache.get(regularExpression);
            if( map == null ) {
                map = new HashMap<Integer,Pattern>();
                mCache.put(regularExpression, map);
            }
            map.put( flags, pattern );
        }
    }
}
