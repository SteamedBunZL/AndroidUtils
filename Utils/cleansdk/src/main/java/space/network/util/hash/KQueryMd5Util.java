//=============================================================================
/**
 * @file KQueryMd5Util.java
 * @brief 获取查询用的md5的工兿
 */
//=============================================================================
package space.network.util.hash;

import android.text.TextUtils;

import com.hawkclean.framework.log.NLog;

import java.io.File;
import java.security.MessageDigest;

import space.network.util.compress.EnDeCodeUtils;

//import java.util.ArrayList;
//import java.util.Collection;

public class KQueryMd5Util {
    public static final String TAG = "KQueryMd5Util";

    public static MessageDigest getMd5Digest() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            NLog.e(TAG, "Unable to find digest algorithm MD5");
            return null;
        }
        return md;
    }


    public static String getDirQueryMd5(MessageDigest md, String dirname) {
//        String dirLowercase = toLowerCase(dirname) + "ijinshan";
//        md.update(dirLowercase.getBytes());
//        String strmd5 = EnDeCodeUtils.byteToHexString(md.digest());
//        md.reset();
//        return strmd5;
        return getDirQueryMd5_v2(md, dirname);
    }

    public static String getDirQueryMd5_v2(MessageDigest md, String dirname) {
        String dirLowercase = "ha" + toLowerCase(dirname) + "wk";
        NLog.d(TAG, "getDirQueryMd5_v2 dirname = %s, encrypt dirname = %s",dirname, dirLowercase);
        md.update(dirLowercase.getBytes());
        String strmd5 = EnDeCodeUtils.byteToHexString(md.digest());
        md.reset();
        return strmd5;
    }

    public static String getDirQueryMd5(String dirname) {
        MessageDigest md = getMd5Digest();
        if (null == md)
            return "";

        String strmd5 = getDirQueryMd5(md, dirname);
        return strmd5;
    }

    public static String getPkgQueryMd5(MessageDigest md, String pkgname) {
        return getMd5String(md, pkgname);
    }

    public static byte[] getPkgQueryMd5Bytes(MessageDigest md, String pkgname) {
        md.update(pkgname.getBytes());
        byte[] result = md.digest();
        md.reset();
        return result;
    }

    public static String getMd5String(MessageDigest md, String str) {
        md.update(str.getBytes());
        String strmd5 = EnDeCodeUtils.byteToHexString(md.digest());
        md.reset();
        return strmd5;
    }


/*	public static String getMd5String(String str) {
		MessageDigest md = getMd5Digest();
		if (null == md)
			return "";

		String strmd5 = getMd5String(md, str);
		return strmd5;
	}*/

    public static String getPathStringFromBytes(byte[] datas) {
        if (null == datas)
            return null;

        int len = datas.length;
        boolean formatCorrect = false;
        if (len == 16) {
            formatCorrect = true;
        } else if (len >= 32) {
            formatCorrect = true;
        }

        if (!formatCorrect) {
            return null;
        }

        String md51  = null;
        String md52  = null;
        String other = null;
        md51 = EnDeCodeUtils.byteToHexString(datas, 0, 16);
        if (len >= 32) {
            md52 = EnDeCodeUtils.byteToHexString(datas, 16, 16);
            if (len > 32) {
                try {
                    other = new String(datas, 32, len - 32, "utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (null == md51)
            return null;

        String result = null;
        if (md52 == null && other == null) {
            result = md51;
        } else if (md52 != null && other == null) {
            StringBuilder builder = new StringBuilder(33);
            builder.append(md51);
            builder.append('+');
            builder.append(md52);
            result = builder.toString();
        } else if (md52 != null && other != null) {
            StringBuilder builder = new StringBuilder(34+other.length());
            builder.append(md51);
            builder.append('+');
            builder.append(md52);
            builder.append('+');
            builder.append(other);
            result = builder.toString();
        }
        return result;
    }

    public static String getHexPathString(String path) {
        if (TextUtils.isEmpty(path))
            return path;

        if (path.length() < 32)
            return null;

        int len = path.length();
        String md51 = null;
        String md52 = null;
        String other = null;
        if (len == 32) {
            md51 = path;
        } else if (len > 32 && path.charAt(32)== '+') {
            md51 = path.substring(0, 32);
            md52 = path.substring(33, 65);
            if (len > 66 && path.charAt(65) == '+') {
                other = path.substring(66);
            }
        }

        String result = null;
        if (md52 == null && other == null) {
            result = md51;
        } else if (md52 != null && other == null) {
            StringBuilder builder = new StringBuilder(64);
            builder.append(md51);
            builder.append(md52);
            result = builder.toString();
        }else if (md52 != null && other != null) {
            String hexOtherStr = null;
            try {
                hexOtherStr = EnDeCodeUtils.byteToHexString(other.getBytes("utf-8"));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            StringBuilder builder = new StringBuilder(64+hexOtherStr.length());
            builder.append(md51);
            builder.append(md52);
            if (hexOtherStr != null) {
                builder.append(hexOtherStr);
            }
            result = builder.toString();
        }
        return result;
    }

	/*
	 * ё·޶ؖԉ3ҿؖì1ܶۍ2ܶĿ¼תۻԉmd5ìʣԠҿؖˇķτ
	 */
	/*
	public static String[] getPathQueryData(MessageDigest md, String path) {
		if (TextUtils.isEmpty(path) || md == null)
			return null;

		String[] result = null;
		String   md51   = null;
		String   md52   = null;
		String   remain = null;

		int lastpos =  path.length() - 1;
		int pos1 = path.indexOf(File.separatorChar);
		if (-1 == pos1) {
			md51 = KQueryMd5Util.getDirQueryMd5(md, path);
		} else {
			String strDir1 = path.substring(0, pos1);
			String strDir2 = null;
			String strDir3 = null;
			int pos2 = -1;
			if (pos1 != lastpos) {
				pos2 = path.indexOf(File.separatorChar, pos1 + 1);
				if (-1 == pos2) {
					strDir2 = path.substring(pos1 + 1);
				} else {
					strDir2 = path.substring(pos1 + 1, pos2);
					if (pos2 != lastpos) {
						strDir3 = path.substring(pos2 + 1);
					}
				}
			}
			if (strDir1 != null) {
				md51 = KQueryMd5Util.getDirQueryMd5(md, strDir1);
			}
			if (strDir2 != null) {
				md52 = KQueryMd5Util.getDirQueryMd5(md, strDir2);
			}
			if (strDir3 != null) {
				remain = strDir3;
			}
		}
		if (remain != null) {
			result = new String[3];
			result[0] = md51;
			result[1] = md52;
			result[2] = remain;
		} else if (md52 != null) {
			result = new String[2];
			result[0] = md51;
			result[1] = md52;
		} else {
			result = new String[1];
			result[0] = md51;
		}
		return result;
	}
	*/

    /*
     * 把路径分成3部分，1级和2级目录转换成md5，剩余部分是明文
     */
    public static String[] getPathQueryData(MessageDigest md, String path) {
        if (TextUtils.isEmpty(path) || md == null)
            return null;
        //把路径分成3部分
        String[] data = parsePathQueryData(path);
        //把路径的1级和2级目录转换成md5，剩余部分是明文
        String[] result = transPathQueryData(md, data);

        return result;
    }

    public static String[] transPathQueryData(MessageDigest md, String[] data) {
        if (null == data || data.length == 0 || null == md) {
            return null;
        }

        String[] result = new String[data.length];
        if (data.length > 0) {
            result[0] = KQueryMd5Util.getDirQueryMd5(md, data[0]);
            if (data.length > 1) {
                result[1] = KQueryMd5Util.getDirQueryMd5(md, data[1]);
                if (data.length > 2) {
                    result[2] = data[2];
                }
            }
        }
        return result;
    }

    /*
     * 把路径分成3部分，1级和2级目录转换成md5，剩余部分是明文
     */
    public static String[] parsePathQueryData(String path) {
        if (TextUtils.isEmpty(path))
            return null;

        String[] result = null;
        String   dir1   = null;
        String   dir2   = null;
        String   remain = null;

        int lastpos =  path.length() - 1;
        int pos1 = path.indexOf(File.separatorChar);
        if (-1 == pos1) {
            dir1 = path;
        } else {
            dir1 = path.substring(0, pos1);
            int pos2 = -1;
            if (pos1 != lastpos) {
                pos2 = path.indexOf(File.separatorChar, pos1 + 1);
                if (-1 == pos2) {
                    dir2 = path.substring(pos1 + 1);
                } else {
                    dir2 = path.substring(pos1 + 1, pos2);
                    if (pos2 != lastpos) {
                        remain = path.substring(pos2 + 1);
                    }
                }
            }
        }
        if (remain != null) {
            result = new String[3];
            result[0] = dir1;
            result[1] = dir2;
            result[2] = remain;
        } else if (dir2 != null) {
            result = new String[2];
            result[0] = dir1;
            result[1] = dir2;
        } else {
            result = new String[1];
            result[0] = dir1;
        }
        return result;
    }

    /**
     * 获取高64位的MD5值
     * */
    public static long getMD5High64BitFromString(MessageDigest md, String src){
        long halfMd5 = 0;
        if(src != null) {
            try{
                md.update(src.getBytes());
                byte[] m = md.digest();
                halfMd5 = getMD5High64BitFromMD5(m);
            }catch(Exception e){
                halfMd5 = 0;
            }
        }
        md.reset();
        return halfMd5;
    }

    /**
     * 获取高64位的MD5值
     * */
    public static long getMD5High64BitFromMD5(byte[] md5Bytes){
        long halfMd5 = 0;
        if(md5Bytes != null && md5Bytes.length == 16){
            int v = 0;
            for(int i = 0; i < 8; i++){
                v = 0x0ff&md5Bytes[i];
                halfMd5 = halfMd5<<8;
                halfMd5 = halfMd5|v;
            }
        }

        return halfMd5;
    }

    /**
     * 获取long类型数值的hex字符串
     * */
    public static String getHexStringFromLong(long md5Half){
        String hexStr = null;
        byte[] md5bytes = new byte[8];
        for(int i = 0; i < 8; i++){
            long v = md5Half>>((7-i)*8);
            byte b = (byte)(v&0x0ff);
            md5bytes[i] = b;
        }
        hexStr = EnDeCodeUtils.byteToHexString(md5bytes);
        return hexStr;
    }



    public static String toLowerCase( String strSrc ) {
        if ( TextUtils.isEmpty(strSrc) ) {
            return strSrc;
        }
        char[] chars = strSrc.toCharArray();
        if ( chars == null ) {
            return strSrc;
        }
        for ( int i=0; i<chars.length; i++ ) {
            char c = chars[i];
            if ('A' <= c && c <= 'Z') {
                chars[i] = (char) (c + 'a' - 'A');
            }
        }
        return String.valueOf(chars);
    }
}