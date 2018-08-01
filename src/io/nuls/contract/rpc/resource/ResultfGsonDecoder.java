package io.nuls.contract.rpc.resource;

import feign.Response;
import feign.gson.GsonDecoder;
import io.nuls.kernel.model.ErrorData;
import io.nuls.kernel.model.RpcClientResult;
import org.apache.commons.beanutils.BeanUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class ResultfGsonDecoder extends GsonDecoder {

    @Override
    public Object decode(Response response, Type type) throws IOException {
        Object result = super.decode(response, type);
        if (result != null && result instanceof RpcClientResult) {
            RpcClientResult rpcClientResult = (RpcClientResult) result;
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
