package com.mooc.commonbusiness.net;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.ObjectTypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.mooc.common.utils.aesencry.AesEncryptUtil;
import com.mooc.common.utils.DebugUtil;


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;

import static com.mooc.common.ktextends.AnyExtentionKt.logd;

/**
 * @ProjectName: LenientGsonConverterFactory
 * @Package: com.example.c_module_commonbusiness.net
 * @ClassName: LenientGsonConverterFactory
 * @Description: 解密借口返回的加密数据
 * @Author: xym
 * @CreateDate: 2020/8/10 5:29 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/8/10 5:29 PM
 * @UpdateRemark: 更新内容
 * @Version: 1.0
 */
public class LenientGsonConverterFactory extends Converter.Factory {

    /**
     * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static LenientGsonConverterFactory create() {
//        Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<Map<String, Object>>() {
//        }.getType(), new GsonTypeAdapter()).create();
        return create(getGson());
    }

    @SuppressWarnings("unchecked")
    public static Gson getGson() {
        Gson gson = new GsonBuilder().create();
        try {
            Field factories = Gson.class.getDeclaredField("factories");
            factories.setAccessible(true);
            Object o = factories.get(gson);
            Class<?>[] declaredClasses = Collections.class.getDeclaredClasses();
            for (Class c : declaredClasses) {
                if ("java.util.Collections$UnmodifiableList".equals(c.getName())) {
                    Field listField = c.getDeclaredField("list");
                    listField.setAccessible(true);
                    List<TypeAdapterFactory> list = (List<TypeAdapterFactory>) listField.get(o);
                    int i = list.indexOf(ObjectTypeAdapter.FACTORY);
                    list.set(i, GsonTypeAdapter.FACTORY);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gson;

    }

    /**
     * Create an instance using {@code gson} for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static LenientGsonConverterFactory create(Gson gson) {
        return new LenientGsonConverterFactory(gson);
    }

    private Gson gson;

    private LenientGsonConverterFactory(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {

        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));

        //如果是学堂接口，也不需要转换
        if (ApiService.getXtRootUrl().contains(retrofit.baseUrl().host())) {
            return new GsonResponseBodyConverter<>(retrofit, gson, adapter);
        }
//        AnyExtentionKt.loge(this,retrofit.baseUrl().host());
        return new LenientGsonResponseBodyConverter<>(gson, adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new LenientGsonRequestBodyConverter<>(gson, adapter);
    }

    public class LenientGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final Gson gson;
        private final TypeAdapter<T> adapter;

        LenientGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {


            String bodyStr = value.string();
            try {
                //不需要解密的，直接解析
                if (DebugUtil.isNoEncrypt) {
                    Log.d("OkHttp", bodyStr);
                    return adapter.fromJson(bodyStr);
                }
                //线上环境需要解密后转换,通过{"data":"加密数据"}包裹
                EncryptParseData parseData = gson.fromJson(bodyStr, EncryptParseData.class);

                String data = parseData.getData();


                //如果解析后发现，并没有加密数据，直接将原数据返回
                if(TextUtils.isEmpty(data)){
                    data = bodyStr;
                }else {
                    //加密数据，需要解密
                    data = AesEncryptUtil.decrypt(data);
                }
                if (DebugUtil.debugMode)
                    Log.d("OkHttp", data);
                return adapter.fromJson(data);
            } catch (Exception e) {     //自定义一个异常json，方便后续处理
                return adapter.fromJson("{}");
            } finally {     //return之后仍会走这里
                value.close();
            }

        }
    }

    public class LenientGsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
        private final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
        private final Charset UTF_8 = Charset.forName("UTF-8");

        private final Gson gson;
        private final TypeAdapter<T> adapter;

        LenientGsonRequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public RequestBody convert(T value) throws IOException {
            Buffer buffer = new Buffer();
            Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
            JsonWriter jsonWriter = gson.newJsonWriter(writer);
            jsonWriter.setLenient(true);
            adapter.write(jsonWriter, value);
            jsonWriter.close();
            return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
        }
    }

    final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private Retrofit retrofit;
        private final Gson gson;
        private final TypeAdapter<T> adapter;

        GsonResponseBodyConverter(Retrofit retrofit, Gson gson, TypeAdapter<T> adapter) {
            this.retrofit = retrofit;
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            JsonReader jsonReader = gson.newJsonReader(value.charStream());
            try {
                T result = adapter.read(jsonReader);
                if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                    throw new JsonIOException("JSON document was not fully consumed.");
                }

                logd(this, retrofit.baseUrl().encodedPathSegments(), retrofit.baseUrl().encodedQuery(), result);
                return result;
            } finally {
                value.close();
            }
        }
    }


}