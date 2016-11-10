package com.tcl.security.virusengine.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 病毒描述工具类
 * Created by Steve on 2016/6/29.
 */
public class DescriptionUtil {

    /**权限-id的映射集*/
    public static Map<String,String> permissionMap;

    /**
     *
     * 通过id获取单个描述
     * @param context
     * @param id
     * @return
     */
    public static String findDescriptionById(Context context,String id) {
        try{
            Resources resources = context.getResources();
            int res = resources.getIdentifier("id_" + id,"string",context.getPackageName());
            return resources.getString(res);
        }catch (Exception e){
            e.printStackTrace();
        }
        return findDefaultDescription(context);
    }

    /**
     *针对TCL云返回的id字符串是一个数组型的，这里进行拆分解析，返回所有描述集
     * @param context
     * @param ids  [20,30,40]
     * @return
     */
    public static String findDescriptionByIds(Context context,String ids){
        try{
            List<String> description_array = new ArrayList<>();
            StringBuffer buffer = new StringBuffer();
            String new_ids = ids.substring(1,ids.length()-1);
            if (new_ids.contains(",")){
                String[] splits = new_ids.split(",");
                if (splits.length > 0){
                    for(String str:splits){
                        String description = findDescriptionById(context,str);
                        if (!description_array.contains(description))
                            description_array.add(description);
                    }
                    for(String des_str:description_array){
                        buffer.append(des_str).append("\n");
                    }
                    return buffer.toString();
                }
                return findDefaultDescription(context);
            }else{
                if (!TextUtils.isEmpty(new_ids)){
                    return findDescriptionById(context,new_ids);
                }else return findDefaultDescription(context);
            }
        }catch (Exception e){
            VirusLog.e(e.getCause(),"findDescriptionByIds inovke fail.");
        }

       return findDefaultDescription(context);
    }

    /**
     *针对TCL云返回的id字符串是一个数组型的，这里进行拆分解析，返回所有描述集
     * @param context
     * @param ids  [20,30,40]
     * @return
     */
    public static String findDescriptionByIds(Context context,List<Integer> ids){
        List<String> descriptions = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        if (ids!=null&&ids.size()>=0){
            for (Integer it:ids){
                String description = findDescriptionById(context,String.valueOf(it));
                if (!descriptions.contains(description)){
                    descriptions.add(description);
                }
            }
            if (descriptions.size()>0){
                for(String des_str:descriptions){
                    buffer.append(des_str).append("\n");
                }
            }else{
                buffer.append(findDescriptionById(context,"95"));
            }
        }else{
            buffer.append(findDescriptionById(context,"95"));
        }
        return buffer.toString();
    }

    /**
     * 获取默认的描述，如果失败，返回“”  默认描述对应id是 id_95
     * @param context
     * @return
     */
    public static String findDefaultDescription(Context context){
        try{
            Resources resources = context.getResources();
            int res = resources.getIdentifier("id_95","string",context.getPackageName());
            return resources.getString(res);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 通过权限来获取所有的描述集，传入的文件路径
     * 如果路径不是apk文件，返回null
     * @param context
     * @param path
     * @return
     */
    public static String getDescriptionByPermissionFromPath(Context context,String path){
        try{
            List<String> array = new ArrayList<>();
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageArchiveInfo(path, 0);
            String[] permissionStrings = packageInfo.requestedPermissions;
            StringBuffer sb = new StringBuffer();
            if (permissionStrings==null){
                return findDefaultDescription(context);
            }
            for(String str:permissionStrings){
                List<String> list = findDescriptionByPermission(context,str);
                if (list!=null&&list.size()!=0){
                    for(String sub:list){
                        if (!array.contains(sub)){
                            array.add(sub);
                            sb.append(sub).append("\n");
                        }
                    }
                }
            }
            return sb.toString();
        }catch (Exception e){
            e.printStackTrace();
            return findDefaultDescription(context);
        }
    }

    /**
     * 通过权限来获取所有的描述集
     * @param context
     * @param packagename
     * @return
     */
    public static String getDescriptionByPermission(Context context,String packagename){
        PackageInfo pack;
        try {
            List<String> array = new ArrayList<>();
            pack = context.getPackageManager().getPackageInfo(packagename, PackageManager.GET_PERMISSIONS);
            String[] permissionStrings = pack.requestedPermissions;
            StringBuffer sb = new StringBuffer();
            if (permissionStrings==null){
                return findDefaultDescription(context);
            }
            for(String str:permissionStrings){
                List<String> list = findDescriptionByPermission(context,str);
                if (list!=null&&list.size()!=0){
                    for(String sub:list){
                        if (!array.contains(sub)){
                            array.add(sub);
                            sb.append(sub).append("\n");
                        }
                    }
                }
            }
            return sb.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return findDefaultDescription(context);
    }

    /**
     * 通过某个权限，获取该权限所有的描述
     * @param context
     * @param findKey
     * @return
     */
    public static List<String> findDescriptionByPermission(Context context,final String findKey){
        if (permissionMap == null || permissionMap.isEmpty())
            fetchDescriptionDataFromXml(context,context.getPackageName());

        Resources resources = context.getResources();

        List<String> list =new ArrayList<>();
        if (permissionMap.containsKey(findKey)){
            String ids = permissionMap.get(findKey);
            if (!TextUtils.isEmpty(ids)){
                String[] splits = ids.split("\\|");
                for(String id:splits){
                    try{
                        if (!TextUtils.isEmpty(id)){
                            int res = resources.getIdentifier("id_" + id,"string",context.getPackageName());
                            String description = resources.getString(res);
                            if (!list.contains(description))
                                list.add(description);
                        }
                    }catch (Exception e){
                        VirusLog.e(e.getCause(),"id %s 没有找到",id);
                    }
                }
                return list;
            }
        }else{
            int res = resources.getIdentifier("id_95" ,"string",context.getPackageName());
            String description = resources.getString(res);
            if(!list.contains(description))
                list.add(description);
        }
        return list;
    }

    /**
     * 通过某个权限获取id集
     * @param context
     * @param permmison
     * @return
     */
    public static String getDescriptionIdsByPermission(Context context,String permmison){
        if (permissionMap == null || permissionMap.isEmpty())
            fetchDescriptionDataFromXml(context,context.getPackageName());

        if (permissionMap.containsKey(permmison)){
            String ids = permissionMap.get(permmison);
            if (!TextUtils.isEmpty(ids)){
                return ids;
            }
        }else{
           return "95";
        }
        return "95";
    }

    /**
     * 通过包名获取所有ID集  [20,30,40]这种  可能重复
     * @param context
     * @param packageName
     * @return
     */
    public static String getDescrptionIdsByPackageName(Context context,String packageName){
        PackageInfo pack;
        StringBuffer buffer =new StringBuffer();
        buffer.append("[");
        try {
            pack = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            String[] permissionStrings = pack.requestedPermissions;

            if (permissionStrings==null||permissionStrings.length==0){
                buffer.append("95").append("]");
                return buffer.toString();
            }else{
                for(String str:permissionStrings){
                    String descriptionIds = getDescriptionIdsByPermission(context, str);
                    if (!TextUtils.isEmpty(descriptionIds)){
                        String[] splits = descriptionIds.split("\\|");
                        for(String split:splits){

                            buffer.append(split).append(",");
                        }
                    }
                }
                buffer.substring(0,buffer.length()-2);
                buffer.append("]");
                return buffer.toString();
            }
        }catch (Exception e){
            e.printStackTrace();
            buffer.append("95").append("]");
            return buffer.toString();
        }
    }

    /**
     * 从xml中解析出权限和id的映射，存入缓存中
     * @param context
     * @param packageName
     */
    private static void fetchDescriptionDataFromXml(Context context, String packageName) {
        try {
            Resources res = context.getPackageManager().getResourcesForApplication(packageName);
            int resId = res.getIdentifier("description_permission", "xml", packageName);
            XmlResourceParser xmlResParser = res.getXml(resId);
            String key;
            String value;
            int eventType = xmlResParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        permissionMap = new HashMap<>();
                        break;
                    case XmlPullParser.START_TAG:
                        if ("string".equals(xmlResParser.getName())){
                            key = xmlResParser.getAttributeValue(0);
                            if (key!=null){
                                value = xmlResParser.nextText();
                                permissionMap.put(key,value);
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:

                        break;
                }
                eventType = xmlResParser.next();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 把idsStr传为list
     * @param ids
     * @return
     */
    public static List<Integer> converseIdStrArrayToList(String ids){
        List<Integer> list = new ArrayList<>();
        if (TextUtils.isEmpty(ids))
            return list;
        String substring = ids.substring(1, ids.length() - 2);
        if (substring.contains(",")){
            String[] splits = substring.split(",");
            for(String split:splits){
                try{
                    int id = Integer.parseInt(split);
                    if (!list.contains(id))
                        list.add(id);
                }catch (Exception e){
                    VirusLog.e(e.getCause(),"converse");
                }
            }
        }else{
            if (!list.contains(95))
                list.add(95);
        }

        return list;

    }

}
