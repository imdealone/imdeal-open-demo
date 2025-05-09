package com.imdealone.open.demo.vo;

import com.google.gson.Gson;
import com.imdealone.open.demo.config.ImdealConfiguration;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

public interface ImdealRequest<T> {

    HttpRequestBase request(ImdealConfiguration configuration);

    /**
     * 获取返回值
     */
    R<T> handleResponse(String result);

    default HttpRequestBase defaultGet(ImdealConfiguration configuration, String uri) {
        return new HttpGet(configuration.getApiGateway() + uri);
    }

    default HttpRequestBase defaultPost(ImdealConfiguration configuration, String uri, Object data) {
        HttpPost httpPost = new HttpPost(configuration.getApiGateway() + uri);
        if (data != null) {
            httpPost.setEntity(new StringEntity(new Gson().toJson(data), ContentType.APPLICATION_JSON));
        }
        return httpPost;
    }

    default HttpRequestBase defaultPut(ImdealConfiguration configuration, String uri, Object data) {
        HttpPut httpPost = new HttpPut(configuration.getApiGateway() + uri);
        if (data != null) {
            httpPost.setEntity(new StringEntity(new Gson().toJson(data), ContentType.APPLICATION_JSON));
        }
        return httpPost;
    }
}
