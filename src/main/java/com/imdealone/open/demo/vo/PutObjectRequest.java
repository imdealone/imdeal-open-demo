package com.imdealone.open.demo.vo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imdealone.open.demo.config.ImdealConfiguration;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Data
@Accessors(chain = true)
public class PutObjectRequest implements ImdealRequest<PutObjectRequest.PutObjectResp> {

    private Long contentLength;
    private final Gson gson = new Gson();

    public PutObjectRequest(Long contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public HttpRequestBase request(ImdealConfiguration configuration) {
        try {
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
            ZonedDateTime expires = now.plusMinutes(10);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            String expiresStr = java.net.URLEncoder.encode(expires.format(formatter), "UTF-8");
            return defaultPut(configuration, "/open/object?expires=" + expiresStr + "&contentLength=" + contentLength, null);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public R<PutObjectRequest.PutObjectResp> handleResponse(String result) {
        return gson.fromJson(result, new TypeToken<R<PutObjectRequest.PutObjectResp>>() {}.getType());
    }

    @Data
    @Accessors(chain = true)
    public static class PutObjectResp {
        private String url;
        private String httpMethod;
        private Map<String, String> headers;
        private String objectId;
    }
}
