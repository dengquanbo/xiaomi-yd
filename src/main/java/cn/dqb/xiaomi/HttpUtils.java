package cn.dqb.xiaomi;

import cn.hutool.json.JSONUtil;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 基于 apache 的 HttpClients，用于手动调用 http/https 请求的工具类
 *
 * @date 2019/10/16 15:04
 */
public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static final String APPLICATION_JSON = "application/json";

    private static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final int MAX_TOTAL = 200;

    private static final int DEFAULT_PER_ROUTE = 100;

    private static final int SO_TIMEOUT = 3000;

    private static final int CONNECT_TIMEOUT = 3000;

    private static final int REQUEST_TIMEOUT = 3000;

    private static PoolingHttpClientConnectionManager HTTP_MANAGER;

    private static PoolingHttpClientConnectionManager HTTPS_MANAGER;

    private static final RequestConfig REQUEST_CONFIG;

    /**
     * 是否忽略https
     */
    private static boolean ignoreHttps = false;

    static {
        HTTP_MANAGER = new PoolingHttpClientConnectionManager();
        HTTP_MANAGER.setDefaultMaxPerRoute(DEFAULT_PER_ROUTE);
        HTTP_MANAGER.setMaxTotal(MAX_TOTAL);

        HTTPS_MANAGER = generateHttpsManger();
        if (HTTPS_MANAGER != null) {
            HTTPS_MANAGER.setDefaultMaxPerRoute(DEFAULT_PER_ROUTE);
            HTTPS_MANAGER.setMaxTotal(MAX_TOTAL);
        }

        REQUEST_CONFIG = RequestConfig
                .custom()
                .setConnectionRequestTimeout(REQUEST_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SO_TIMEOUT)
                .build();
    }

    private HttpUtils() {
    }

    public static PoolingHttpClientConnectionManager generateHttpsManger() {
        //采用绕过验证的方式处理https请求
        SSLContext sslcontext = null;
        try {
            sslcontext = createIgnoreVerifySSL();
            //设置协议http和https对应的处理socket链接工厂的对象
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslcontext))
                    .build();
            return new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setIgnoreHttps(boolean ignoreHttps) {
        HttpUtils.ignoreHttps = ignoreHttps;
    }

    private static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        // 调用 jdk8 的发布的 https 接口，使用协议 TLSv1.2
        SSLContext sc = SSLContext.getInstance("TLSv1.2");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }

    private static CloseableHttpClient getHttpClient() {
        if (!ignoreHttps) {
            return HttpClients.custom().setConnectionManager(HTTP_MANAGER).setDefaultRequestConfig(REQUEST_CONFIG).build();
        } else {
            assert HTTPS_MANAGER != null;
            return HttpClients.custom().setConnectionManager(HTTPS_MANAGER).setDefaultRequestConfig(REQUEST_CONFIG).build();
        }
    }

    public static <T> T jsonPost(String url, String requestData, Class<T> type) throws Exception {
        assert url != null;
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(requestData)) {
            return null;
        }
        HttpEntity requestEntity = new StringEntity(requestData, DEFAULT_CHARSET);
        HttpUriRequest request = postRequest(url, APPLICATION_JSON, requestEntity);
        return execute(request, type, DEFAULT_CHARSET);
    }

    public static <T> T jsonPost(String url, String requestData, Map<String, String> headers, Class<T> type)
            throws Exception {
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(requestData)) {
            return null;
        }
        HttpEntity requestEntity = new StringEntity(requestData, DEFAULT_CHARSET);
        HttpUriRequest request = postRequest(url, APPLICATION_JSON, requestEntity);
        return execute(request, type, DEFAULT_CHARSET, headers);
    }

    public static <T> T jsonPut(String url, String requestData, Class<T> type) throws Exception {
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(requestData)) {
            return null;
        }
        HttpEntity requestEntity = new StringEntity(requestData, DEFAULT_CHARSET);
        HttpUriRequest request = putRequest(url, APPLICATION_JSON, requestEntity);
        return execute(request, type, DEFAULT_CHARSET);
    }


    public static <T> T formPost(String url, Map<String, String> requestMap, Map<String, String> heads, Class<T> type)
            throws Exception {
        return formPost(url, requestMap, DEFAULT_CHARSET, heads, type);
    }

    public static <T> T formPost(String url, Map<String, String> requestMap, String charsetName,
                                 Map<String, String> heads, Class<T> type)
            throws Exception {
        if (StringUtils.isEmpty(url) || requestMap == null) {
            return null;
        }
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        for (Map.Entry<String, String> entry : requestMap.entrySet()) {
            nameValuePairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        HttpEntity requestEntity = new UrlEncodedFormEntity(nameValuePairList, Charset.forName(charsetName));
        HttpUriRequest request = postRequest(url, APPLICATION_FORM_URLENCODED, requestEntity);
        return execute(request, type, charsetName, heads);
    }

    public static <T> T get(String url, Class<T> type) throws Exception {
        assert url != null;
        HttpUriRequest request = getRequest("get", url);
        logger.debug(String.format("Request %s, Url: %s", request.getMethod(), url));
        return execute(request, type, DEFAULT_CHARSET);
    }

    public static <T> T delete(String url, Class<T> type) throws Exception {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        HttpUriRequest request = getRequest("delete", url);
        logger.debug(String.format("Request %s, Url: %s", request.getMethod(), url));
        request.addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
        return execute(request, type, DEFAULT_CHARSET);
    }

    private static HttpUriRequest getRequest(String method, String url) {
        Assert.notNull(method, "Method cannot be null");
        Assert.notNull(url, "Url cannot be null");
        method = method.toUpperCase();
        if (method.equals("GET")) {
            return new HttpGet(url);
        } else if (method.equals("POST")) {
            return new HttpPost(url);
        } else if (method.equals("PUT")) {
            return new HttpPut(url);
        } else if (method.equals("DELETE")) {
            return new HttpDelete(url);
        }
        return null;
    }

    private static HttpUriRequest postRequest(String url, String contentType, HttpEntity entity) {
        HttpUriRequest request = getRequest("post", url);
        logger.debug(String.format("Request %s, Url: %s", request.getMethod(), url));
        request.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
        ((HttpEntityEnclosingRequest) request).setEntity(entity);
        return request;
    }


    private static HttpUriRequest putRequest(String url, String contentType, HttpEntity entity) {
        HttpUriRequest request = getRequest("put", url);
        logger.debug(String.format("Request %s, Url: %s", request.getMethod(), url));
        request.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
        ((HttpEntityEnclosingRequest) request).setEntity(entity);
        return request;
    }

    private static <T> T execute(HttpUriRequest request, Class<T> type, String charset)
            throws Exception {
        return execute(request, type, charset, null);
    }

    @SuppressWarnings("unchecked")
    private static <T> T execute(HttpUriRequest request, Class<T> type, String charset, Map<String, String> headers)
            throws Exception {
        CloseableHttpResponse response = null;
        try {

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    request.addHeader(entry.getKey(), entry.getValue());
                }
            }
            CloseableHttpClient client = getHttpClient();
            response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity responseEntity = response.getEntity();
                String result = EntityUtils.toString(responseEntity, charset);
                logger.debug(String.format("Request result: %s", result));
                if (type == null || type.isAssignableFrom(String.class)) {
                    return (T) result;
                }
                T object = JSONUtil.toBean(result, type);
                return object;
            } else {
                logger.error("Request is fail," + EntityUtils.toString(response.getEntity(), charset));
                throw new IOException("HTTP返回状态不是200");
            }
        } catch (Exception e) {
            logger.error("Request has exception: ", e);
            throw e;
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                    response.close();
                } catch (IOException e) {
                    logger.error("execute", e);
                }
            }
        }
    }
}
