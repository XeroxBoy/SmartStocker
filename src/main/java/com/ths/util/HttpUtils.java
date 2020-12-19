package com.ths.util;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xieshuai 2016/11/26 0026.
 */
@Component
public class HttpUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);
    public static final String UTF_8 = "UTF-8";
    private static int timeout = 60 * 1000;
    //    public static final HttpHost PROXY_FIDDLER = new HttpHost(HttpUtils.FIDDLER_IP, HttpUtils.FIDDLER_PORT, "http");
    public static final String FIDDLER_IP = "127.0.0.1";
    public static final int FIDDLER_PORT = 8888;
    private static RequestConfig DEFAULT_REQUEST_CONFIG = null;
    private final static String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:32.0) Gecko/20100101 Firefox/33.0";

    /**
     * 生成HttpPost请求
     *
     * @param url
     * @return
     */
    public static HttpPost post(String url) {
        return post(url, null);
    }

    public static HttpPost post(String url, Map<String, Object> params) {
        return post(url, params, null);
    }

    public static HttpPost post(String url, Map<String, Object> params, HttpHost proxy) {
        return post(url, params, proxy, DEFAULT_USER_AGENT);
    }

    public static HttpPost post(String url, Map<String, Object> params, HttpHost proxy, String userAgent) {
        HttpPost result = new HttpPost(url);
//        result.addHeader("User-Agent", userAgent == null ? DEFAULT_USER_AGENT : userAgent);
        if (params != null && !params.isEmpty()) {
            result.setEntity(buildParams(params));
        }
//        addHeader(result);
        return result;
    }

    /**
     * 生成HttpGet请求
     *
     * @param url
     * @return
     */
    public static HttpGet get(String url) {
        return get(url, null);
    }

    public static HttpGet get(String url, Map<String, Object> params, String userAgent) {
        url += buildParamString(params);
        HttpGet result = new HttpGet(url);
        result.addHeader("User-Agent", userAgent == null ? DEFAULT_USER_AGENT : userAgent);
        return result;
    }

    public static HttpGet get(String url, Map<String, Object> params) {
        url += buildParamString(params);
        LOGGER.info("请求的url为:" + url);
        HttpGet result = new HttpGet(url);
        return result;
    }

    public static String buildParamString(Map<String, ? extends Object> params) {
        return buildParamString(params, UTF_8);
    }

    public static String buildParamString(Map<String, ? extends Object> params, String encoding) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder("?");
        try {
            for (Map.Entry<String, ? extends Object> entry : params.entrySet()) {
                Object value = entry.getValue();
                value = value == null ? "" : value.toString();
                sb.append(URLEncoder.encode(entry.getKey(), encoding))
                        .append("=")
                        .append(URLEncoder.encode((String) value, encoding))
                        .append("&");
            }
            return sb.substring(0, sb.length() - 1).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

//    public static void main(String[] args) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("123456");
//        System.out.println(sb.substring(0,sb.length()-2));
//    }

    public static UrlEncodedFormEntity buildParams(Map<String, ? extends Object> params) {
        return buildParams(params, UTF_8);
    }

    @SuppressWarnings("rawtypes")
    public static UrlEncodedFormEntity buildParams(Map<String, ? extends Object> params, String encoding) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        for (Map.Entry<String, ? extends Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (value != null) {
                if (value instanceof List) {
                    for (Object o : (List) value) {
                        if (o != null) {
                            parameters.add(new BasicNameValuePair(entry.getKey(), o.toString()));
                        }
                    }
                } else {
                    parameters.add(new BasicNameValuePair(entry.getKey(), value.toString()));
                }
            } else {
                parameters.add(new BasicNameValuePair(entry.getKey(), null));
            }
        }
        return new UrlEncodedFormEntity(parameters, Charset.forName(encoding));
    }

//    public static RequestConfig.Builder copyDefaultConfig() {
//        RequestConfig.Builder builder = RequestConfig.copy(getDefaultRequestConfig());
//        if (userFiddler || Boolean.valueOf(System.getProperty("use.fiddler", "false"))) {
//            builder.setProxy(PROXY_FIDDLER);
//        }
//        return builder;
//    }

    public static RequestConfig getDefaultRequestConfig() {
        if (DEFAULT_REQUEST_CONFIG == null) {
            synchronized (HttpUtils.class) {
                if (DEFAULT_REQUEST_CONFIG == null) {
                    RequestConfig.Builder builder = RequestConfig.custom();
                    builder.setRedirectsEnabled(false).setRelativeRedirectsAllowed(false);
                    builder.setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY);
                    // connect to a url 1min
                    builder.setConnectTimeout(timeout);
                    // socket inputStream.read() 2min
                    builder.setSocketTimeout(timeout * 2);
                    DEFAULT_REQUEST_CONFIG = builder.build();
                }
            }
        }
        return DEFAULT_REQUEST_CONFIG;
    }

    public static HttpPost addHeader(HttpPost httpPost) {
        httpPost.addHeader("Connection", "keep-alive");
        httpPost.addHeader("Upgrade-Insecure-Requests", "1");
        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
        httpPost.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpPost.addHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
        return httpPost;
    }

    public static HttpGet addHeader(HttpGet httpGet) {
        httpGet.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        httpGet.addHeader("Accept-Encoding", "gzip, deflate");
        httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
        httpGet.addHeader("Cache-Control", "max-age=0");
        httpGet.addHeader("Connection", "keep-alive");
        httpGet.addHeader("Cookie", "JSESSIONID=879DEB42A87F7B45721BF5B1704E3E8E");
        httpGet.addHeader("Host", "wx.stockdatamining.com");
        httpGet.addHeader("Upgrade-Insecure-Requests", "1");
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1");
//        httpGet.addHeader("Host", "jwgl.ahnu.edu.cn");
//        httpGet.addHeader("Connection", "keep-alive");
//        httpGet.addHeader("Upgrade-Insecure-Requests", "1");
//        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
//        httpGet.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//        httpGet.addHeader("Accept-Encoding", "gzip, deflate, sdch");
//        httpGet.addHeader("Referer", "http://jwgl.ahnu.edu.cn/");
//        httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
//        httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
        return httpGet;
    }
}
