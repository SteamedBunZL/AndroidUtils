//=============================================================================
/**
 * @file EnDeCodeUtils.java
 */
//=============================================================================
package space.network.util.compress;

public final class EnDeCodeUtils {

    public static void xorEncodeBytes(byte[] bytes, int pos, int length, byte[] encodekey) {
        int max = pos + length;
        int buflen = bytes.length;
        if (max > buflen)
            max = buflen;

        int keylen = encodekey.length;
        for (int i = pos, j = 0; i < max; ++i, ++j) {
            if (j == keylen)
                j = 0;

            bytes[i] = (byte)(bytes[i] ^ encodekey[j]);
        }
    }

    //////////////////////////////////////////////////////////////////
    private static final char HEXES[] = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
    public static String byteToHexString(byte[] data) {
        if (data == null) {
            return null;
        }

        final int len = data.length;
        if (0 == len)
            return "";

        char strBuf[] = new char[2 * len];
        for (int i = 0; i < len; ++i) {
            strBuf[i * 2]     = HEXES[(data[i] & 0xF0) >>> 4];
            strBuf[i * 2 + 1] = HEXES[(data[i] & 0x0F)];
        }
        String result = new String(strBuf);
        return result;
    }

    public static String byteToHexString(byte[] data, int offset, int count) {
        if (data == null) {
            return null;
        }

        int remainLen = data.length - offset;
        int len = count > remainLen ? remainLen : count;
        if (0 == len)
            return "";

        char strBuf[] = new char[2 * len];
        for (int i = 0; i < len; ++i) {
            strBuf[i * 2]     = HEXES[(data[i + offset] & 0xF0) >>> 4];
            strBuf[i * 2 + 1] = HEXES[(data[i + offset] & 0x0F)];
        }
        String result = new String(strBuf);
        return result;
    }


    /**
     * 32位16进制转换为16位字节
     *
     * @param hexStr
     * @return
     */
    public static byte[] hexStringtoBytes(String hexStr) {
        byte[] bytes = new byte[hexStr.length() / 2];
        copyHexStringtoBytes(hexStr, bytes, 0, bytes.length);
        return bytes;
    }

    public static byte hexChartoByte(char c) {
        byte b = 0;
        if (c >= '0' && c <= '9') {
            b = (byte)(c - '0');
        } else if (c >= 'a' && c<= 'f') {
            b = (byte)(10 + (c - 'a'));
        } else if (c >= 'A' && c <= 'F') {
            b = (byte)(10 + (c - 'A'));
        } else {
            //error
        }
        return b;
    }

    public static void copyHexStringtoBytes(String hexStr, byte[] bytes, int pos, int maxsize) {
        int len = hexStr.length() / 2;
        if (len > maxsize) {
            len = maxsize;
        }
        byte b1;
        byte b2;
        for (int idx = 0, spos = 0; idx < len; ++idx) {
            spos = 2 * idx;
            b1 = hexChartoByte(hexStr.charAt(spos));
            b2 = hexChartoByte(hexStr.charAt(spos + 1));
            bytes[pos + idx] = (byte)(b1 << 4);
            bytes[pos + idx] |= (b2);
        }
    }
    //////////////////////////////////////////////////////////////////

    /**
     * short转换为字节
     *
     * @param num
     * @return
     */
/*	public static byte[] shortToBytes(short num) {
		byte[] b = new byte[2];
		copyShortToBytes(num, b, 0);
		return b;
	}*/

    public static void copyShortToBytes(short num, byte[] bytes, int pos) {
        for (int i = 0; i < 2; ++i) {
            bytes[pos + i] = (byte) (num >>> (i * 8));
        }
    }

/*	public static short bytesToShort(byte[] bytes, int pos) {
		short num = 0;
		for (int i = 0; i < 2; ++i) {
			num |= (bytes[pos + i]&0xFF) << (i * 8);
		}
		return num;
	}*/

    /**
     * int类型转换为byte[]类型
     *
     * @param num
     * @return
     */
/*	public static byte[] intToBytes(int num) {
		byte[] b = new byte[4];
		copyIntToBytes(num, b, 0);
		return b;
	}*/

    public static void copyIntToBytes(int num, byte[] bytes, int pos) {
        for (int i = 0; i < 4; ++i) {
            bytes[pos + i] = (byte) (num >>> (i * 8));
        }
    }

    public static int bytesToInt(byte[] bytes, int pos) {
        int num = 0;
        for (int i = 0; i < 4; ++i) {
            num |= (bytes[pos + i]&0xFF) << (i * 8);
        }
        return num;
    }


//    /**
//     * 将一个long编码进流的指定位置
//     * @param value:要编码的值
//     * @param stream:要生成的流
//     * @param pos:编进流的位置
//     */
//    public static void encode(long value, byte[] stream, int pos){
//        if ((stream.length - pos) < Sizeof.LONG){
//            return;
//        }
//
//        byte txt;
//        txt = (byte)((value >> 56) & 0x00000000000000ff);
//        stream[pos] = txt;
//        txt = (byte)((value >> 48) & 0x00000000000000ff);
//        stream[pos + 1] = txt;
//        txt = (byte)((value >> 40) & 0x00000000000000ff);
//        stream[pos + 2] = txt;
//        txt = (byte)((value >> 32) & 0x00000000000000ff);
//        stream[pos + 3] = txt;
//        txt = (byte)((value >> 24) & 0x00000000000000ff);
//        stream[pos + 4] = txt;
//        txt = (byte)((value >> 16) & 0x00000000000000ff);
//        stream[pos + 5] = txt;
//        txt = (byte)((value >> 8) & 0x00000000000000ff);
//        stream[pos + 6] = txt;
//        txt = (byte)(value & 0x000000ff);
//        stream[pos + 7] = txt;
//    }
//
//    /**
//     * 将一个int编码进流的指定位置
//     * @param value:要编码的值
//     * @param stream:要生成的流
//     * @param pos:编进流的位置
//     */
//    public static void encode(int value, byte[] stream, int pos){
//        if ((stream.length - pos) < Sizeof.INT){
//            return;
//        }
//
//        byte txt;
//        txt = (byte)((value >> 24) & 0x000000ff);
//        stream[pos] = txt;
//        txt = (byte)((value >> 16) & 0x000000ff);
//        stream[pos + 1] = txt;
//        txt = (byte)((value >> 8) & 0x000000ff);
//        stream[pos + 2] = txt;
//        txt = (byte)(value & 0x000000ff);
//        stream[pos + 3] = txt;
//    }
//    /**
//     * 将一个short编码进流的指定位置
//     * @param value:要编码的值
//     * @param stream:要生成的流
//     * @param pos:编进流的位置
//     */
//    public static void encode(short value, byte[] stream, int pos){
//        if ((stream.length - pos) < Sizeof.SHORT){
//            return;
//        }
//
//        byte txt;
//        txt = (byte)((value >> 8) & 0x00ff);
//        stream[pos] = txt;
//        txt = (byte)(value & 0x00ff);
//        stream[pos + 1] = txt;
//    }
//
//    /**
//     * 从流中一个指定的位置，解出long
//     * @param stream:要解码的流
//     * @param pos:解码流的位置
//     * @return 返回long
//     */
//    public static long decodeLong(byte[] stream, int pos) {
//        if (stream.length - pos < Sizeof.LONG){
//            return 0;
//        }
//
//        long valueH = 0;
//        long valueL = 0;
//
//        valueH |= (stream[pos] << 24) & 0xff000000;
//        valueH |= (stream[pos + 1] << 16) & 0x00ff0000;
//        valueH |= (stream[pos + 2] << 8) & 0x0000ff00;
//        valueH |= stream[pos + 3] & 0x000000ff;
//
//        valueL |= (stream[pos + 4] << 24) & 0xff000000;
//        valueL |= (stream[pos + 5] << 16) & 0x00ff0000;
//        valueL |= (stream[pos + 6] << 8) & 0x0000ff00;
//        valueL |= stream[pos + 7] & 0x000000ff;
//
//        if (valueL < 0) {
//            valueL = (long)Math.pow(2, 32) + valueL;
//        }
//
//        long ret = (valueH << 32) | valueL;
//        return ret;
//    }
//
//    /**
//     * 从流中一个指定的位置，解出int
//     * @param stream:要解码的流
//     * @param pos:解码流的位置
//     * @return 返回int
//     */
//    public static int decodeInt(byte[] stream, int pos){
//        if (stream.length - pos < Sizeof.INT){
//            return 0;
//        }
//        int ret = 0;
//
//        ret |= (stream[pos] << 24) & 0xff000000;
//        ret |= (stream[pos + 1] << 16) & 0x00ff0000;
//        ret |= (stream[pos + 2] << 8) & 0x0000ff00;
//        ret |= stream[pos + 3] & 0x000000ff;
//        return ret;
//    }
//    /**
//     * 从流中一个指定的位置，解出short
//     * @param stream:要解码的流
//     * @param pos:解码流的位置
//     * @return 返回short
//     */
//    public static short decodeInt16(byte[] stream, int pos){
//        if (stream.length - pos < Sizeof.SHORT){
//            return 0;
//        }
//        short ret = 0;
//
//        ret |= (stream[pos] << 8) & 0x0000ff00;
//        ret |= stream[pos + 1] & 0x000000ff;
//        return ret;
//    }
}