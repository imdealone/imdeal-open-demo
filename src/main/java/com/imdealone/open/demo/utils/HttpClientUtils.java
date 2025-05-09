package com.imdealone.open.demo.utils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

public class HttpClientUtils {

    /**
     * 全局连接池对象
     */
    private static final PoolingHttpClientConnectionManager CONN_MANAGER = new PoolingHttpClientConnectionManager();

    static {
        // 设置最大连接数
        CONN_MANAGER.setMaxTotal(200);
        // 设置每个连接的路由数
        CONN_MANAGER.setDefaultMaxPerRoute(20);
        CONN_MANAGER.setValidateAfterInactivity(2000);
    }

    /**
     * 获取Http客户端连接对象
     * 没有单独做定时线程自动清理过期链接和空闲链接，所以每次用到的时候都重新获取client并且触发清除机制，连接池是共享的，所以调用该方法可以复用连接
     *
     * @param connectionRequestTimeout 连接池获取连接超时时间
     * @param connectTimeout           连接超时时间
     * @param socketTimeout            读取响应超时时间
     * @return Http客户端连接对象
     */
    public static CloseableHttpClient getHttpClient(int connectionRequestTimeout, int connectTimeout, int socketTimeout) {
        // 创建Http请求配置参数
        RequestConfig requestConfig = RequestConfig.custom()
                // 连接池获取连接超时时间
                .setConnectionRequestTimeout(connectionRequestTimeout)
                // 请求超时时间
                .setConnectTimeout(connectTimeout)
                // 响应超时时间
                .setSocketTimeout(socketTimeout)
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(20);
        // 创建httpClient
        return HttpClients.custom()
                // 把请求相关的超时信息设置到连接客户端
                .setDefaultRequestConfig(requestConfig)
                // 配置连接池管理对象
                .setConnectionManager(connectionManager)
                //设置存活时间
                .setConnectionTimeToLive(60, TimeUnit.SECONDS)
                //清理过期连接
                .evictExpiredConnections()
                //清理空闲
                .evictIdleConnections(30, TimeUnit.SECONDS)
                //自动重定向
                .setRedirectStrategy(new LaxRedirectStrategy())
                .build();
    }

}
