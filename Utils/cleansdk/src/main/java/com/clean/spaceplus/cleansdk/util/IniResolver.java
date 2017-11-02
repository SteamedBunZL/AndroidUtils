package com.clean.spaceplus.cleansdk.util;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.hawkclean.framework.log.NLog;
import com.hawkclean.mig.commonframework.util.PublishVersionManager;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author zengtao.kuang
 * @Description: Ini解析类
 * @REMARK
 * 1.INI文件不允许包含BOM头
 * 2.保存时会自动添加自校验码，加载时会自动校验
 * @date 2016/4/6 15:58
 * @copyright TCL-MIG
 */

public class IniResolver {

    private String mMd5Check;
    private Collection<String> mSectionList = new LinkedList<String>();
    private Map<String, Section> mSectionMap = new ArrayMap<String, Section>();

    private static final String ENCODING = "utf-8";

    public boolean load(byte[] content)
    {
        try
        {
            int off = 0;
            int len = content.length;

            mMd5Check = null;
            if(content[0] == '#')
            {
                MessageDigest md5Digest = MessageDigest.getInstance("MD5");
                md5Digest.update(content, 35, content.length - 35);

                mMd5Check = new String(content, 1, 32, ENCODING);
                String md5String = FileUtils.encodeHex(md5Digest.digest());
                if(!mMd5Check.equalsIgnoreCase(md5String))
                {
                    return false;
                }

                off += 35;
                len -= off;
            }

            ByteArrayInputStream is = new ByteArrayInputStream(content, off, len);
            return load(new InputStreamReader(is, ENCODING));
        }
        catch(Exception e)
        {
            return false;
        }
    }

    public boolean load(File file)
    {
        FileInputStream is = null;
        try
        {
            is = new FileInputStream(file);

            int len = is.available();
            byte[] content = new byte[len];
            is.read(content);

            return load(content);
        }
        catch(Exception e)
        {
            return false;
        }
        finally
        {
            if(is != null)
            {
                try
                {
                    is.close();
                }
                catch(Exception e) {}
            }
        }
    }

    public boolean load(Reader reader)
    {
        try
        {
            clear();

            String line = null;
            Section section = null;
            BufferedReader is = new BufferedReader(reader);

            while((line = is.readLine()) != null)
            {
                line = line.trim();
                if(TextUtils.isEmpty(line))
                {
                    continue;
                }

                switch(line.charAt(0))
                {
                    // comments
                    case ';':
                    case '#':
                        break;

                    // section
                    case '[':
                        if(line.length() <= 2 || line.charAt(line.length() - 1) != ']')
                        {
                            if (PublishVersionManager.isTest()) {
                                throw new Exception("invalid section name");
                            }
                        }

                        String sectionName = line.substring(1, line.length() - 1);
                        if(!mSectionMap.containsKey(sectionName)) {
                            section = new Section();
                            mSectionList.add(sectionName);
                            mSectionMap.put(sectionName, section);
                        }
                        break;

                    // key-value
                    default:
                        if(section == null)
                        {
                            if (PublishVersionManager.isTest()) {
                                throw new Exception("not found section name");
                            }
                        }

                        int index = line.indexOf('=');
                        if(index == -1)
                        {
                            if (PublishVersionManager.isTest()) {
                                throw new Exception("invalid key-value format");
                            }
                        }

                        String key = line.substring(0, index);
                        String value = line.substring(index + 1, line.length());

                        section.keyList.add(key);
                        section.keyValueMap.put(key, value);
                        break;
                }
            }
            return true;
        }
        catch(Exception e)
        {
            NLog.printStackTrace(e);
            return false;
        }
    }

//    public boolean save(File file, boolean check)
//    {
//        FileOutputStream fos = null;
//
//        try
//        {
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            OutputStreamWriter osw = new OutputStreamWriter(bos, ENCODING);
//
//            if(!save(osw))
//            {
//                return false;
//            }
//
//            byte[] content = bos.toByteArray();
//            fos = new FileOutputStream(file);
//
//            if(check)
//            {
//                MessageDigest md5Digest = MessageDigest.getInstance("MD5");
//                md5Digest.update(content);
//                mMd5Check = FileUtils.encodeHex(md5Digest.digest());
//
//                fos.write('#');
//                fos.write(mMd5Check.getBytes(ENCODING));
//                fos.write('\r');
//                fos.write('\n');
//            }
//
//            fos.write(content);
//            fos.flush();
//            return true;
//        }
//        catch(Exception e)
//        {
//            return false;
//        }
//        finally
//        {
//            try
//            {
//                if(fos != null)
//                {
//                    fos.close();
//                }
//            }
//            catch(Exception e) {}
//        }
//    }

//    private boolean save(Writer writer)
//    {
//        try
//        {
//            BufferedWriter os = new BufferedWriter(writer);
//
//            Iterator<String> itSection = mSectionList.iterator();
//            while(itSection.hasNext())
//            {
//                String sectionName = itSection.next();
//                os.write("[" + sectionName + "]");
//                os.newLine();
//
//                Section section = mSectionMap.get(sectionName);
//                Iterator<String> itKey = section.keyList.iterator();
//                while(itKey.hasNext())
//                {
//                    String key = itKey.next();
//                    String value = section.keyValueMap.get(key);
//
//                    os.write(key + "=" + value);
//                    os.newLine();
//                }
//
//                os.newLine();
//            }
//
//            os.flush();
//            return true;
//        }
//        catch(Exception e)
//        {
//            return false;
//        }
//    }
//
//    public boolean isEqual(IniResolver other)
//    {
//        return other != null &&
//                !TextUtils.isEmpty(other.mMd5Check) &&
//                !TextUtils.isEmpty(mMd5Check) &&
//                mMd5Check.equalsIgnoreCase(other.mMd5Check);
//    }

    public String getValue(String sectionName, String keyName)
    {
        Section section = mSectionMap.get(sectionName);
        if(section != null)
        {
            return section.keyValueMap.get(keyName);
        }
        return null;
    }

//    public final Collection<String> getAllSection()
//    {
//        return mSectionList;
//    }
//
//    public final Collection<String> getAllKey(String sectionName)
//    {
//        Section section = mSectionMap.get(sectionName);
//        if(section != null)
//        {
//            return section.keyList;
//        }
//        return null;
//    }

    public void clear()
    {
        mSectionList.clear();
        mSectionMap.clear();
    }
    public void log(){
        Collection<Section> collection = mSectionMap.values();
        for(Section sec: collection){
            sec.description();
        }
    }

    private static class Section
    {
        public Collection<String> keyList = new LinkedList<String>();
        public Map<String, String> keyValueMap = new ArrayMap<String, String>();
        public void description(){
            for(String key: keyList){
//				ZipLog.getLogInstance().log("section key = " + key + " value = " + keyValueMap.get(key));
            }
        }
    }

}
