package io.nuls.contract.rpc.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import feign.Response;
import feign.Util;
import feign.codec.Decoder;
import feign.gson.DoubleToIntMapTypeAdapter;
import io.nuls.kernel.model.ErrorData;
import io.nuls.kernel.model.RpcClientResult;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

import static feign.Util.resolveLastTypeParameter;

public class ResultfGsonDecoder implements Decoder {

    private final Gson gson;

    public ResultfGsonDecoder(Iterable<TypeAdapter<?>> adapters) {
        this(create(adapters));
    }

    public ResultfGsonDecoder() {
        this(Collections.<TypeAdapter<?>>emptyList());
    }

    public ResultfGsonDecoder(Gson gson) {
        this.gson = gson;
    }

    static Gson create(Iterable<TypeAdapter<?>> adapters) {
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        builder.registerTypeAdapter(new TypeToken<Map<String, Object>>() {
        }.getType(), new DoubleToIntMapTypeAdapter());
        for (TypeAdapter<?> adapter : adapters) {
            Type type = resolveLastTypeParameter(adapter.getClass(), TypeAdapter.class);
            builder.registerTypeAdapter(type, adapter);
        }
        return builder.create();
    }

    @Override
    public Object decode(Response response, Type type) throws IOException {
        if (response.status() == 404) return Util.emptyValueOf(type);
        if (response.body() == null) return null;
        //Reader reader = response.body().asReader();
        try {
            String json = IOUtils.toString(response.body().asInputStream(), "utf8");
            Object object = gson.fromJson(json, type);
            return result(object, json);
        } catch (JsonIOException e) {
            if (e.getCause() != null && e.getCause() instanceof IOException) {
                throw IOException.class.cast(e.getCause());
            }
            throw e;
        } finally {
            //ensureClosed(reader);
        }
    }

    public Object result(Object result, String json) {
        if (result != null && result instanceof RpcClientResult) {
            RpcClientResult rpcClientResult = (RpcClientResult) result;
            rpcClientResult.setJson(json);
            Object data = rpcClientResult.getData();
            if (data != null && !rpcClientResult.isSuccess()) {
                if (data instanceof Map) {
                    Map map = (Map) data;
                    ErrorData errorData = new ErrorData();
                    populate(errorData, map);
                    rpcClientResult.setErrorData(errorData);
                    return rpcClientResult;
                } else if (data instanceof ErrorData) {
                    ErrorData errorData = new ErrorData();
                    copyProperties(errorData, data);
                    rpcClientResult.setErrorData(errorData);
                    return rpcClientResult;
                }
            }
        }
        return result;
    }

    public void populate(Object bean, Map properties) {
        try {
            BeanUtils.populate(bean, properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void copyProperties(Object dest, Object orig) {
        try {
            BeanUtils.copyProperties(dest, orig);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
