package com.hawkclean.mig.commonframework.network;


import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.hawkclean.framework.log.NLog;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * @author Jerry
 * @Description:
 * @date 2017/2/23 12:38
 * @copyright TCL-MIG
 */

final class CustomGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    CustomGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
//        String response = value.string();


//        byte[] decodeByte = Base64.decode(response.getBytes(),1);
//        byte[] unzipByte = GZipUtils.decompress(decodeByte);
//        String result = DataCryptor.decryptToJson(Base64.decode(unzipByte,1));
        //NLog.e("CustomGsonResponseBodyConverter","result : %s",result);
        String result = DataCryptor.decodeStringAndDecompress(value.string());
        MediaType contentType = value.contentType();
        Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
        InputStream inputStream = new ByteArrayInputStream(result.getBytes());
        Reader reader = new InputStreamReader(inputStream, charset);
        JsonReader jsonReader = gson.newJsonReader(reader);
        NLog.i("GsonResponseBodyConvert", "converter-result:%s", result);
        try {
            return adapter.read(jsonReader);
        }catch (Exception w){
            return  null;
        }finally {
            value.close();
        }
    }



}