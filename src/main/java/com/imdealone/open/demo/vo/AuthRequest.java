package com.imdealone.open.demo.vo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imdealone.open.demo.config.ImdealConfiguration;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicHeader;
import org.springframework.http.MediaType;

import java.util.Base64;

/**
 * @author 611 on 2025/5/8.
 */
public class AuthRequest implements ImdealRequest<AuthRequest.TokenResp> {
    private final Gson gson = new Gson();

    static String base64(String context) {
        return Base64.getEncoder().encodeToString(context.getBytes());
    }
    @Override
    public HttpRequestBase request(ImdealConfiguration configuration) {
        HttpRequestBase httpPost = defaultPost(configuration, "/open/token", null);
        httpPost.setHeaders(new Header[]{new BasicHeader(HttpHeaders.AUTHORIZATION,
                "Basic " + base64(configuration.getClientId() + ":" + configuration.getClientSecret())),
                new BasicHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)});

        return httpPost;
    }

    @Override
    public R<AuthRequest.TokenResp> handleResponse(String result) {
        return gson.fromJson(result, new TypeToken<R<TokenResp>>() {}.getType());
    }

    @Data
    @Accessors(chain = true)
    public static class TokenResp {
        private String token;
    }
}
