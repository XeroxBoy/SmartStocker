package com.ths.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 测试httpclient 4.0 1. 重新设计了HttpClient 4.0 API架构，彻底从内部解决了所有 HttpClient 3.x
 * 已知的架构缺陷代码。 2. HttpClient 4.0 提供了更简洁，更灵活，更明确的API。 3. HttpClient 4.0
 * 引入了很多模块化的结构。 4. HttpClient
 * 4.0性能方面得到了不小的提升，包括更少的内存使用，通过使用HttpCore模块更高效完成HTTP传输。 5. 通过使用 协议拦截器(protocol
 * interceptors), HttpClient 4.0实现了 交叉HTTP（cross-cutting HTTP protocol） 协议 6.
 * HttpClient 4.0增强了对连接的管理，更好的处理持久化连接，同时HttpClient 4.0还支持连接状态 7. HttpClient
 * 4.0增加了插件式（可插拔的）的 重定向（redirect） 和 验证（authentication）处理。 8. HttpClient
 * 4.0支持通过代理发送请求，或者通过一组代理发送请求。 9. 更灵活的SSL context 自定义功能在HttpClient 4.0中得以实现。 10.
 * HttpClient 4.0减少了在省城HTTP请求 和 解析HTTP响应 过程中的垃圾信息。 11. HttpClient团队鼓励所有的项目升级成
 * HttpClient 4.0
 */

/**
 * Created by xieshuai
 */
public class HttpClientUtils {
    private static Logger LOGGER = LoggerFactory.getLogger(HttpClientUtils.class);

    private static final int HTTP_CONNECT_TIMEOUT = 10 * 60 * 1000;
    private static final int HTTP_SOCKET_TIMEOUT = 10 * 60 * 1000;
    private CloseableHttpClient client;
    private RequestConfig requestConfig;

    public HttpClientUtils() {
        this.requestConfig = RequestConfig.custom()
                .setConnectTimeout(HTTP_CONNECT_TIMEOUT)
                .setSocketTimeout(HTTP_SOCKET_TIMEOUT)
                .build();
        client = HttpClients.createDefault();
    }

    /**
     * 执行Get请求
     *
     * @param httpPost
     * @return
     * @throws IOException
     */
    public String executeWithResult(HttpPost httpPost) throws IOException {
        return executeWithResult(httpPost, "utf-8");
    }

    public String executeWithResult(HttpPost httpPost, String encoding) throws IOException {
        httpPost.setConfig(requestConfig);
        HttpResponse httpResponse = client.execute(httpPost);
        return EntityUtils.toString(httpResponse.getEntity(), encoding);
    }

    public String executeWithCookieStoreResult(HttpRequestBase httpRequestBase, String encoding, CookieStore cookieStore) throws IOException {
        HttpClientContext localContext = new HttpClientContext();
        localContext.setCookieStore(cookieStore);
        HttpResponse httpResponse = client.execute(httpRequestBase, localContext);
        String result = EntityUtils.toString(httpResponse.getEntity(), encoding);
        close();
        return result;
    }

    public String executeWithCookieStoreResult(HttpRequestBase httpRequestBase, CookieStore cookieStore) throws IOException {
        HttpClientContext localContext = new HttpClientContext();
        localContext.setCookieStore(cookieStore);
        HttpResponse httpResponse = client.execute(httpRequestBase, localContext);
        String result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
        close();
        return result;
    }

    public InputStream executeReturnInputStream(HttpGet httpGet, CookieStore cookieStore) throws IOException {
        httpGet.setConfig(requestConfig);
        HttpClientContext localContext = new HttpClientContext();
        localContext.setCookieStore(cookieStore);
        HttpResponse httpResponse = client.execute(httpGet, localContext);
        return httpResponse.getEntity().getContent();
    }

    /**
     * 执行Post请求
     *
     * @param httpGet
     * @return
     * @throws IOException
     */
    public String executeWithResult(HttpGet httpGet) throws IOException {
        return executeWithResult(httpGet, "utf-8");
    }

    public String executeWithResult(HttpGet httpGet, String encoding) throws IOException {
        httpGet.setConfig(requestConfig);
        HttpResponse httpResponse = client.execute(httpGet);
        return EntityUtils.toString(httpResponse.getEntity(), encoding);
    }

    /**
     * 关闭httpClient 请求
     */
    public void close() {
        try {
            client.close();
        } catch (IOException e) {
            LOGGER.error("Call close method is error!", e);
        }
    }

    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

}

