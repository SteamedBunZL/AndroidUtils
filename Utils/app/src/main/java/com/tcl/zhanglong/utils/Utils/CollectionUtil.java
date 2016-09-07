package com.tcl.zhanglong.utils.Utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Steve on 16/9/7.
 */
public class CollectionUtil {

    /**
     * 最有效率的遍历集合
     * @param map
     */
    public static void transformingMap(){
        HashMap<String,String> map = new HashMap<>();
        map.put("111","222");
        Set<Map.Entry<String,String>> entrySet = map.entrySet();
        Iterator<Map.Entry<String,String>> it = entrySet.iterator();
        while(it.hasNext()){
            Map.Entry<String,String> entry = it.next();
            DebugLog.w("Key : %s , Value : %s",entry.getKey(),entry.getValue());
        }

    }


}
