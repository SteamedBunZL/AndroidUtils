package com.clean.spaceplus.cleansdk.base.utils.root;

import android.text.TextUtils;

import com.clean.spaceplus.cleansdk.util.IOUtils;
import com.hawkclean.framework.log.NLog;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/23 17:55
 * @copyright TCL-MIG
 */
public class SuExec {
    private static final String TAG = SuExec.class.getSimpleName();
//    public static final int ERROR_CHECK_ROOT_FAILED = -1000;
    private static SuExec mInstance = new SuExec();
//    private Thread mThreadGetSu = null;
//    private IRootKeeper mRootKeeper = null;
    private SuExec() {}

    public static synchronized SuExec getInstance() {
        return mInstance;
    }

    /**
     * 判断是否有root权限
     * @return
     */
    public boolean checkRoot() {
        final String binPath = "/system/bin/su";
        final String xBinpath = "/system/xbin/su";

        if(new File(binPath).exists() && isExcutable(binPath)){
            return true;
        }

        if(new File(xBinpath).exists() && isExcutable(xBinpath)){
            return true;
        }

        return false;
    }

    private boolean isExcutable(String path){
//        Process process = null;
//        BufferedReader bufferedReader = null;
//
//        try {
//            String cmd = "ls -l " + path;
//            process = Runtime.getRuntime().exec(cmd);
//            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
////            String result = bufferedReader.readLine();
//            int len;
//
//            if(result != null && result.length() >=4){
//                char flag = result.charAt(3);
//                if(flag == 's' || flag == 'x'){
//                    return true;
//                }
//            }
//        } catch (Exception e){
//            NLog.e(TAG, e.getMessage());
//        } finally {
//            if(process != null){
//                process.destroy();
//            }
//
//            if(bufferedReader != null){
//                try {
//                    bufferedReader.close();
//                } catch (IOException e) {
//
//                }
//            }
//        }
//
//        return false;
        return true;
    }

//    /**
//     * 枚举目录
//     * @param parentPath
//     * @return
//     */
//    public static ArrayList<String> enumSubFileAsRoot(String parentPath) {
//        boolean retval = false;
//
//        ArrayList<String> pathList = new ArrayList<>();
//        try {
//            if (!TextUtils.isEmpty(parentPath)){
//                StringBuilder sb = new StringBuilder();
//                Process process = Runtime.getRuntime().exec("su");
//                DataOutputStream os = null;
//                try {
//                    os = new DataOutputStream(process.getOutputStream());
//                    sb.append("ls ").append(parentPath).append("\n");
//                    os.writeBytes(sb.toString());
//                    os.flush();
//                    os.writeBytes("exit\n");
//                    os.flush();
//                } catch (Exception e) {
//                    NLog.printStackTrace(e);
//                } finally {
//                    IOUtils.closeSilently(os);
//                }
//
//                BufferedReader reader = null;
//                try {
//                    reader = new BufferedReader(new InputStreamReader(
//                            process.getInputStream()));
//
//                    String strLine = null;
//                    StringBuffer sbResult = new StringBuffer();
//                    while ((strLine = reader.readLine()) != null) {
//                        sbResult.append(parentPath).append(strLine);
//                        pathList.add(sbResult.toString());
//                        sbResult.delete(0, sbResult.length() - 1);
//                    }
//                } catch (Exception e) {
//                    NLog.printStackTrace(e);
//                } finally {
//                    IOUtils.closeSilently(reader);
//                }
//
//                try {
//                    int suProcessRetval = process.waitFor();
//                    if (255 != suProcessRetval) {
//                        retval = true;
//                    } else {
//                        retval = false;
//                    }
//                } catch (Exception ex) {
//                    NLog.e(TAG , "Error executing root action %s", ex);
//                }
//            }
//        } catch (IOException ex) {
//            NLog.w(TAG, "Can't get root access %s", ex);
//        } catch (SecurityException ex) {
//            NLog.w(TAG, "Can't get root access  %s", ex);
//        } catch (Exception ex) {
//            NLog.w(TAG, "Error executing internal operation  %s", ex);
//        }
//        NLog.i(TAG," retVal rootAction %s", retval);
//
//        if (retval){
//            return pathList;
//        }
//
//        return null;
//    }
//
//    /**
//     * 复合命令获取垃圾目录信息
//     * @param suCMD
//     * @return
//     */
//    public static JunkFileInfoNew enumJunkFiles(ArrayList<String> suCMD) {
//        boolean retval = false;
//        ArrayList<String> pathList = new ArrayList<>();
//        long size = 0L;
//        try {
//            ArrayList<String> suCommand = new ArrayList<>();
//            suCommand.add("ls /data/tombstones/");
//            suCommand.add("du -k /data/tombstones/");
//            ArrayList<String> commands = suCommand;
//            if (null != commands && commands.size() > 0) {
//                Process process = Runtime.getRuntime().exec("su");
//
//                DataOutputStream os = null;
//                try {
//                    os = new DataOutputStream(process.getOutputStream());
//                    for (String currCommand : commands) {
//                        os.writeBytes(currCommand + "\n");
//                        os.flush();
//                    }
//                    os.writeBytes("exit\n");
//                    os.flush();
//                } catch (Exception e) {
//                    NLog.printStackTrace(e);
//                } finally {
//                    IOUtils.closeSilently(os);
//                }
//
//                BufferedReader reader = null;
//                try {
//                    reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//
//                    String strLine = null;
//                    StringBuffer sbResult = new StringBuffer();
//                    while ((strLine = reader.readLine()) != null) {
//                        sbResult.append(strLine);
//                        NLog.i(TAG, " while strLine %s", strLine);
//                        NLog.i(TAG, "sbResult %s", sbResult.toString());
//                        if (strLine.endsWith("/data/tombstones/")) {
//                            String[] s = strLine.split("/data/tombstones/");
//                            size = Integer.parseInt(s[0].trim());
//                            NLog.i(TAG, "du -k size %d", size);
//                            continue;
//                        }
//                        pathList.add(strLine);
//                    }
//                } catch (Exception e) {
//                    NLog.printStackTrace(e);
//                } finally {
//                    IOUtils.closeSilently(reader);
//                }
//
//                try {
//                    int suProcessRetval = process.waitFor();
//                    if (255 != suProcessRetval) {
//                        retval = true;
//                    } else {
//                        retval = false;
//                    }
//                } catch (Exception ex) {
//                    NLog.e(TAG , "Error executing root action %s", ex);
//                }
//            }
//        } catch (IOException ex) {
//            NLog.w(TAG, "Can't get root access %s", ex);
//        } catch (SecurityException ex) {
//            NLog.w(TAG, "Can't get root access  %s", ex);
//        } catch (Exception ex) {
//            NLog.w(TAG, "Error executing internal operation  %s", ex);
//        }
//        NLog.i(TAG," retVal rootAction %s", retval);
//
//        if (retval){
//            JunkFileInfoNew junkInfo = new JunkFileInfoNew();
//            junkInfo.pathList.addAll(pathList);
//            junkInfo.size = size * 1024;//以字节为单位
//            return junkInfo;
//        }
//
//        return null;
//    }
//
//    /**
//     * 枚举目录
//     * @param suCmd
//     * @return
//     */
//    public static JunkFileInfoNew enumJunkFiles(String suCmd) {
//        boolean retval = false;
//
//        ArrayList<String> pathList = new ArrayList<>();
//        long size = 0L;
//        try {
//            String command = suCmd;
//            if (!TextUtils.isEmpty(command)){
//                Process process = Runtime.getRuntime().exec("su");
//                DataOutputStream os = null;
//                try {
//                    os = new DataOutputStream(process.getOutputStream());
//                    os.writeBytes(command + "\n");
//                    os.flush();
//                    os.writeBytes("exit\n");
//                    os.flush();
//                } catch (Exception e) {
//                    NLog.printStackTrace(e);
//                } finally {
//                    IOUtils.closeSilently(os);
//                }
//
//                BufferedReader reader = null;
//                try {
//                    reader = new BufferedReader(new InputStreamReader(
//                            process.getInputStream()));
//                    String strLine = null;
//                    StringBuffer sbResult = new StringBuffer();
//                    while ((strLine = reader.readLine()) != null) {
//                        sbResult.append(strLine);
//                        NLog.i(TAG, " while strLine %s", strLine);
//                        NLog.i(TAG, "sbResult %s", sbResult.toString());
//                        pathList.add(strLine);
//                    }
//                } catch (Exception e) {
//                    NLog.printStackTrace(e);
//                } finally {
//                    IOUtils.closeSilently(reader);
//                }
//
//                try {
//                    int suProcessRetval = process.waitFor();
//                    if (255 != suProcessRetval) {
//                        retval = true;
//                    } else {
//                        retval = false;
//                    }
//                } catch (Exception ex) {
//                    NLog.e(TAG , "Error executing root action %s", ex);
//                }
//            }
//        } catch (IOException ex) {
//            NLog.w(TAG, "Can't get root access %s", ex);
//        } catch (SecurityException ex) {
//            NLog.w(TAG, "Can't get root access  %s", ex);
//        } catch (Exception ex) {
//            NLog.w(TAG, "Error executing internal operation  %s", ex);
//        }
//        NLog.i(TAG," retVal rootAction %s", retval);
//
//        if (retval){
//            JunkFileInfoNew junkInfo = new JunkFileInfoNew();
//            junkInfo.pathList.addAll(pathList);
//            junkInfo.size = size;
//            return junkInfo;
//        }
//
//        return null;
//    }

    /**
     * root权限删除垃圾目录
     * @param path
     * @return
     */
    public static boolean removeJunkFiles(String  path) {
        boolean retVal = false;
        try {
            //ArrayList<String> suCommand = new ArrayList<>();
            //suCommand.add("rm -r /data/data/com.UCMobile/cache");
            //suCommand.add("rm /data/data/com.UCMobile/cache/temp.txt");
            //suCommand.add("rm -r /data/data/com.tencent.mobileqq/cache");
            //ArrayList<String> commands = suCommand;
            //String suCommand = "rm -r " + dir;
            //String suCommand = "rm -r " + dir;
            if (!TextUtils.isEmpty(path)) {
                //该命令目录和文件均可以remove掉不用区分
                String suCommand = "rm -r " + path;
                Process process = Runtime.getRuntime().exec("su");
                DataOutputStream os = null;
                try {
                    os = new DataOutputStream(process.getOutputStream());
                    os.writeBytes(suCommand + "\n");
                    os.flush();
                    os.writeBytes("exit\n");
                    os.flush();
                } catch (Exception e) {
                    NLog.printStackTrace(e);
                } finally {
                    IOUtils.closeSilently(os);
                }
                try {
                    int suProRet = process.waitFor();
                    if (255 != suProRet) {
                        retVal = true;
                    } else {
                        retVal = false;
                    }
                } catch (Exception ex) {
                    NLog.e(TAG , "Error executing root action %s", ex);
                }
            }
        } catch (IOException ex) {
            NLog.w(TAG, "Can't get root access %s", ex);
        } catch (SecurityException ex) {
            NLog.w(TAG, "Can't get root access  %s", ex);
        } catch (Exception ex) {
            NLog.w(TAG, "Error executing internal operation  %s", ex);
        }
        NLog.i(TAG," retVal rootAction %s", retVal);

        return retVal;
    }

//    /**
//     * root权限删除垃圾目录：比如系统缓存相关目录
//     * @param suCMD
//     * @return
//     */
//    public static JunkFileInfoNew removeJunkFiles(ArrayList<String> suCMD) {
//        boolean retval = false;
//        ArrayList<String> pathList = new ArrayList<>();
//        long size = 0L;
//        try {
//            ArrayList<String> suCommand = new ArrayList<>();
//            suCommand.add("rm -r /data/data/com.UCMobile/cache");
//            suCommand.add("rm -r /data/data/com.tencent.mobileqq/cache");
//            ArrayList<String> commands = suCommand;
//            if (null != commands && commands.size() > 0) {
//                Process process = Runtime.getRuntime().exec("su");
//
//                DataOutputStream os = null;
//                try {
//                    os = new DataOutputStream(process.getOutputStream());
//                    for (String currCommand : commands) {
//                        os.writeBytes(currCommand + "\n");
//                        os.flush();
//                    }
//                    os.writeBytes("exit\n");
//                    os.flush();
//                } catch (Exception e) {
//                    NLog.printStackTrace(e);
//                } finally {
//                    IOUtils.closeSilently(os);
//                }
//
//                BufferedReader reader = null;
//                try {
//                    reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//                    String strLine = null;
//                    StringBuffer sbResult = new StringBuffer();
//                    while ((strLine = reader.readLine()) != null) {
//                        sbResult.append(strLine);
//                        NLog.i(TAG, " while strLine %s", strLine);
//                        NLog.i(TAG, "sbResult %s", sbResult.toString());
//                        if (strLine.endsWith("/data/tombstones/")) {
//                            String[] s = strLine.split("/data/tombstones/");
//                            size = Integer.parseInt(s[0].trim());
//                            NLog.i(TAG, "du -k size %d", size);
//                            continue;
//                        }
//                        pathList.add(strLine);
//                    }
//                } catch (Exception e) {
//                    NLog.printStackTrace(e);
//                } finally {
//                    IOUtils.closeSilently(reader);
//                }
//
//                try {
//                    int suProcessRetval = process.waitFor();
//                    if (255 != suProcessRetval) {
//                        retval = true;
//                    } else {
//                        retval = false;
//                    }
//                } catch (Exception ex) {
//                    NLog.e(TAG , "Error executing root action %s", ex);
//                }
//            }
//        } catch (IOException ex) {
//            NLog.w(TAG, "Can't get root access %s", ex);
//        } catch (SecurityException ex) {
//            NLog.w(TAG, "Can't get root access  %s", ex);
//        } catch (Exception ex) {
//            NLog.w(TAG, "Error executing internal operation  %s", ex);
//        }
//        NLog.i(TAG," retVal rootAction %s", retval);
//
//        if (retval){
//            JunkFileInfoNew junkInfo = new JunkFileInfoNew();
//            junkInfo.pathList.addAll(pathList);
//            junkInfo.size = size * 1024;//以字节为单位
//            return junkInfo;
//        }
//
//        return null;
//    }
//
//
//    /**
//     * 执行root shell命令
//     * @param cmds
//     * @return 返回结果，resut[0] == 0成功
//     */
//    public String[] executeRootCmds(String[] cmds){
//        Process process = null;
//        BufferedReader bufferedReader = null;
//        BufferedWriter bufferedWriter = null;
//        String[] result = new String[2];
//        result[0] = "-1";
//
//        try {
//            process = Runtime.getRuntime().exec("su");
//            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
//
//            for(String cmd : cmds){
//                bufferedWriter.write(cmd);
//                bufferedWriter.flush();
//            }
//
//            StringBuffer sbResult = new StringBuffer();
//            String strLine = null;
//            while((strLine = bufferedReader.readLine()) != null){
//                sbResult.append(strLine);
//            }
//
//            try {
//                bufferedWriter.write("exit\n");
//                bufferedWriter.flush();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            result[0] = "0";
//            result[1] = sbResult.toString();
//        } catch (Exception e){
//            NLog.e(TAG, e.getMessage());
//            result[0] = "-1";
//            result[1] = e.getMessage();
//        } finally {
//            if(bufferedWriter != null){
//                try {
//                    bufferedWriter.close();
//                } catch (Exception e) {
//
//                }
//            }
//
//            if(bufferedReader != null){
//                try {
//                    bufferedReader.close();
//                } catch (Exception e) {
//
//                }
//            }
//
//            if(process != null){
//                process.destroy();
//            }
//        }
//
//        return result;
//    }

    public boolean deleteFilesLeftFoder(String path){
        if (TextUtils.isEmpty(path) || !checkRoot())
            return false;

        NLog.d("(R)deleteFilesLeftFoder", path);

//        try {
//            mRootKeeper.deleteFilesLeftFoder(path, new FileDeleteObserver());
//            return true;
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }

        return false;
    }

//    public JunkFileInfoNew enumJunkFiles(String path, String libPath){
//        if (!checkRoot())
//            return null;
//
//        try {
//            //return mRootKeeper.enumJunkFilesNew(path,libPath);
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


    public List<String> convertRootCacheCleanCloudPathREG(String rootPath, String path, String pkgName) {
        if (!checkRoot())
            return null;
        try {
            //return mRootKeeper.convertRootCacheCleanCloudPathREG(rootPath, path, pkgName);
            return null;
        } catch (Exception e) {
        }

        return null;
    }


//    /**
//     * 返回全路径列表
//     * @return
//     */
//    public ArrayList<String> GetDalvikDirFullPathFiles(){
//        ArrayList<String> dalvikCacheList = new ArrayList<>();
//        for ( String dir : getDalvikDirs() ) {
//            //List<FileInfo> infos = SuExec.getInstance().EnumDirs(dir);
//            List<FileInfo> infos = SuExec.getInstance().EnumDirs_v2(dir);
//            if ( infos != null && infos.size() > 0 ){
//                for ( FileInfo fin : infos ){
//                    dalvikCacheList.add(fin.fullPath);
//                }
//            }
//        }
//        return dalvikCacheList;
//    }

//    private ArrayList<String> mDalvikDirs = new ArrayList<>();

//    public ArrayList<String> getDalvikDirs() {
//        if ( !mDalvikDirs.isEmpty() ) {
//            return mDalvikDirs;
//        }
//        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
//            String strABI = SystemProperties.get("ro.product.cpu.abi", "unknown");
//            if ( strABI.equalsIgnoreCase("x86") ) {
//                mDalvikDirs.add( "/data/dalvik-cache/x86" );
//            }else if( strABI.equalsIgnoreCase("x86_64") ) {
//                mDalvikDirs.add( "/data/dalvik-cache/x86" );
//                mDalvikDirs.add( "/data/dalvik-cache/x86_64" );
//            }else {
//                mDalvikDirs.add( "/data/dalvik-cache/arm" );
//                mDalvikDirs.add( "/data/dalvik-cache/arm64" );
//            }
//            return mDalvikDirs;
//        }
//        mDalvikDirs.add( "/data/dalvik-cache" );
//        return mDalvikDirs;
//    }

//    public List<FileInfo> EnumDirs(String path) {
//        if (!checkRoot())
//            return null;
//
//        try {
//            //return mRootKeeper.EnumFiles(path);
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

//    public List<FileInfo> EnumDirs_v2(String path) {
//        if (!checkRoot()){
//            return null;
//        }
//
//        List<FileInfo> fileInfoList = new ArrayList<>();
//        try {
//            List<String> fileList = enumSubFileAsRoot(path);
//            if(fileList != null){
//                FileInfo fileInfo;
//
//                for(String subPath : fileList){
//                    if(!TextUtils.isEmpty(subPath)){
//                        fileInfo = new FileInfo();
//                        fileInfo.fullPath = subPath;
//                        fileInfoList.add(fileInfo);
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return fileInfoList;
//    }

    public long getPathFileSize(String path){
        if (!checkRoot()){
            return 0;
        }
        try {
            //return mRootKeeper.getPathFileSize(path);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String convertRootCacheCleanCloudPath(String rootPath,String path,String pkgName){
        if (!checkRoot())
            return null;
        try {
            //return mRootKeeper.convertRootCacheCleanCloudPath(rootPath, path, pkgName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public boolean isFile(String filePath){
        if (TextUtils.isEmpty(filePath) || !checkRoot()) {
            return false;
        }

        try {
            //return mRootKeeper.isFile(filePath);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

//    public boolean isFileExist(String filePath){
//        if (TextUtils.isEmpty(filePath) || !checkRoot()) {
//            return false;
//        }
//
//        try {
//            //return mRootKeeper.isFileExist(filePath);
//            return  true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return false;
//    }

    public boolean deleteFile(String path){

        if (TextUtils.isEmpty(path) || !checkRoot())
            return false;

        if(!TextUtils.isEmpty(path)){
            File file = new File(path);

            if(file.exists()){
                boolean result = file.delete();
                NLog.d(TAG, "deleteFile--"+path+": "+result);
                return result;
            }
        }

//        try {
//            return mRootKeeper.deleteFile(path);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }

        NLog.d(TAG, "deleteFile--"+path+": "+false);
        return false;
    }


//    public long getFileSize(String pathFile) {
//
//       /* if (TextUtils.isEmpty(pathFile) || !checkRoot())
//            return 0;
//
//        try {
//            return mRootKeeper.getFileSize(pathFile);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }*/
//
//        return 0;
//    }
//
//    public String getIsRootMark(){
//        if(checkRoot()){
//            return "root";
//        }
//        return "not root";
//    }
}
