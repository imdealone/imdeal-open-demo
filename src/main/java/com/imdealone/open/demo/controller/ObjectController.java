package com.imdealone.open.demo.controller;

import com.imdealone.open.demo.utils.ImdealClient;
import com.imdealone.open.demo.vo.PutObjectRequest;
import com.imdealone.open.demo.vo.R;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Validated
public class ObjectController {

    private final ImdealClient imdealClient;

    @PutMapping("/object")
    public R<String> object(@RequestParam("file") MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        long size = file.getSize();
        PutObjectRequest.PutObjectResp putObjectResp = imdealClient.execute(new PutObjectRequest(size));
        CloseableHttpClient httpClient = imdealClient.getHttpClient();
        HttpPut httpPut = new HttpPut();
        httpPut.setURI(URI.create(putObjectResp.getUrl()));

        // 获取文件的字节数组
        byte[] fileBytes = file.getBytes();

        // 创建 ByteArrayEntity 作为请求体
        HttpEntity entity = new ByteArrayEntity(fileBytes);
        httpPut.setEntity(entity);

        httpPut.setHeader("Content-Type", contentType);
        Map<String, String> headers = putObjectResp.getHeaders();
        // 过滤掉host
        headers.forEach((k, v) -> {
            if (!k.equalsIgnoreCase("host")) {
                httpPut.setHeader(k, v);
            }
        });

        try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                return R.success(putObjectResp.getObjectId());
            }
            String result = EntityUtils.toString(response.getEntity());
            return R.failure(-10000, result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
