package space.network.commondata;


import java.util.zip.CRC32;

import space.network.util.compress.EnDeCodeUtils;

public class KQueryHeaderUtils {

    /* 清理云查询新的头部
     * PostHead：type PostHead struct {
        dataSize  uint16
        crc      uint32
        vt     uint8
        channelId  uint16
        version    uint32
        languageCode  byte[6]
        uuid      byte[24]
        mcc      uint16
        }
     */
    public static final int QUERY_POST_DATA_HEAD_SIZE =
            2 + //dataSize  uint16
                    4 + //crc       uint32
                    1 + //vt     uint8
                    2 + //channelId uint16
                    4 + //version   uint32
                    6 + //languageCode  byte[6]
                    24+ //uuid      byte[24]
                    2;  //mcc      uint16
    //total 45

    public static boolean fillQueryHeader(
            byte[] data,
            short datasize,
            short channelId,
            int version,
            byte[] lang,
            byte[] xaid,
            short mcc) {
        EnDeCodeUtils.copyShortToBytes(datasize, data, 0);
        // skip crc value offset 2 ,length 4 :
        data[6] = 1;//vt  uint8
        EnDeCodeUtils.copyShortToBytes(channelId, data, 7);
        EnDeCodeUtils.copyIntToBytes(version,     data, 9);
        System.arraycopy(lang, 0,                 data, 13, 6);
        System.arraycopy(xaid, 0,                 data, 19, xaid.length);//XAID_BUFFER_SIZE = 24
        EnDeCodeUtils.copyShortToBytes(mcc,       data, 43);

        return true;
    }

    public static boolean encodeQueryHeader(byte[] data, short datasize, byte[] encodekey) {
        EnDeCodeUtils.xorEncodeBytes(
                data,
                9, // begin from section 'version'
                datasize - 9,
                encodekey);

        CRC32 crc32 = new CRC32();
        crc32.update(data,
                6, // begin from section 'channelId'
                datasize - 6);

        crc32.update(encodekey);
        int crcvalue = (int) crc32.getValue();
        EnDeCodeUtils.copyIntToBytes(crcvalue, data, 2);
        return true;
    }
}
