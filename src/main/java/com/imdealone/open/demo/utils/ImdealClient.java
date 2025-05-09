package com.imdealone.open.demo.utils;


import com.google.gson.Gson;
import com.imdealone.open.demo.config.ImdealConfiguration;
import com.imdealone.open.demo.vo.ImdealRequest;
import com.imdealone.open.demo.vo.R;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.TreeMap;

@Slf4j
public class ImdealClient {

    private final ImdealConfiguration configuration;
    @Getter
    private final CloseableHttpClient httpClient;

    private final Gson gson = new Gson();

    public ImdealClient(ImdealConfiguration configuration) {
        this.configuration = configuration;
        this.httpClient = HttpClientUtils.getHttpClient(30000, 30000, 30000);

    }

    public <T> T execute(ImdealRequest<T> request) {
        return execute(request, SecurityContextUtil.getToken());
    }

    public <T> T executeWithoutToken(ImdealRequest<T> request) {
        return execute(request, null);
    }

    private <T> T execute(ImdealRequest<T> request, String token) {
        HttpRequestBase httpRequestBase = request.request(configuration);
        if (StringUtils.isNotBlank(token)) {
            httpRequestBase.setHeader("Authorization", "Bearer " + token);
        }
        try (CloseableHttpResponse response = httpClient.execute(httpRequestBase)) {
            try {
                int statusCode = response.getStatusLine().getStatusCode();
                String result = EntityUtils.toString(response.getEntity());
                if (statusCode == HttpStatus.SC_OK) {
                    R<T> r = request.handleResponse(result);
                    if (!r.isSuccess()) {
                        log.warn("请求imdeal异常 地址:{} 请求参数:{} 响应码:{} 返回值:{}", httpRequestBase.getURI(), gson.toJson(request), statusCode, result);
                        throw new RuntimeException();
                    }
                    return r.getData();
                }
                log.warn("请求imdeal异常 地址:{} 响应码:{} 返回值:{} 请求参数:{} ", httpRequestBase.getURI(), statusCode, result, gson.toJson(request));
                if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                    throw new UnauthorizedException();
                }
                throw new RuntimeException();
            } finally {
                EntityUtils.consumeQuietly(response.getEntity());
            }
        } catch (IOException e) {
            log.warn("请求imdeal异常 地址:{} 请求参数:{} 获取请求响应失败 e=", httpRequestBase.getURI(), gson.toJson(request), e);
            throw new RuntimeException();
        }
    }


    public boolean verifySign(String canonicalHeaderNames,
                              String signature, String method, String uri, String body) {

        TreeMap<String, String> queryParams = new TreeMap<>();
        TreeMap<String, String> headers = new TreeMap<>();
        String[] splitHeaders = canonicalHeaderNames.split(";");
        Arrays.stream(splitHeaders).forEach(header -> headers.put(header, HeaderUtils.getHeaderValue(header)));
        String canonicalRequest;
        try {
            canonicalRequest = ImdealSignatureUtils.canonicalRequest(method, uri, queryParams, headers, body);
            String signatureCheck = ImdealSignatureUtils.bytesToHex(ImdealSignatureUtils.hmacSha256(configuration.getClientSecret(), canonicalRequest));
            return signatureCheck.equals(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.warn("签名验证失败", e);
            return false;
        }
    }
}
