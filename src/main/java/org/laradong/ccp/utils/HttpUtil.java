package org.laradong.ccp.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP工具类.
 *
 * <p>
 * 使用方法：描述
 * </p>
 *
 * <p>
 * 注意事项：描述
 * </p>
 *
 * @author laradong
 * @version 2018年4月26日 上午10:32:34
 * @since JDK1.8
 */
public class HttpUtil {
    public static final String METHOD_GET = "GET";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_DELETE = "DELETE";
    public static final long DOWNLOAD_FAIL_PARAMETER = -1;
    public static final long DOWNLOAD_FAIL_STATUSCODE = -2;
    public static final long DOWNLOAD_FAIL_NETWORK = -3;
    private static final Logger logger = LoggerFactory.getLogger(ConstantUtil.LOG_SYSTEM);
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private static final int MAX_RETRY_TIME = 2;
    private static final int SOCKET_TO_MS = 60000;
    private static final int CONN_TO_MS = 60000;

    private static HttpRequestBase getHttpRequestBase(String url, String method, Map<String, String> header, HttpEntity entity) {
        HttpRequestBase httpRequest = null;
        switch (method) {
            case METHOD_GET:
                httpRequest = new HttpGet(url);
                break;
            case METHOD_PUT:
                httpRequest = new HttpPut(url);
                break;
            case METHOD_POST:
                httpRequest = new HttpPost(url);
                if (entity != null) {
                    ((HttpPost) httpRequest).setEntity(entity);
                }
                break;
            case METHOD_DELETE:
                httpRequest = new HttpDelete(url);
                break;
            default:
                logger.warn("unsupported method, url={}, method={}", url, method);
                return null;
        }
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SOCKET_TO_MS).setConnectTimeout(CONN_TO_MS).build();// 设置请求和传输超时时间
        httpRequest.setConfig(requestConfig);
        if (header != null) {
            for (String key : header.keySet()) {
                httpRequest.setHeader(key, header.get(key));
            }
        }
        return httpRequest;
    }

    /**
     * 获取Json结果.
     *
     * <p>
     * 示例： xx
     * </p>
     * <p>
     * 注意事项： xx
     * </p>
     *
     * @param result HTTP结果
     * @param clazz  JSON类
     * @return JSON对象
     */
    public static <T> T getJsonResult(Map<Integer, byte[]> result, Class<T> clazz) {
        if (result == null || result.isEmpty()) {
            logger.warn("getJsonResult failed, http return null, Class={}.", clazz);
            return null;
        }

        Integer statusCode = result.keySet().iterator().next();
        if (statusCode != HttpServletResponse.SC_OK) {
            logger.warn("getJsonResult failed, statusCode={}.", statusCode);
            return null;
        }

        byte[] resultData = result.get(statusCode);
        if (resultData == null) {
            logger.warn("getJsonResult failed, http return data is null, statusCode={}.", statusCode);
            return null;
        }

        String resultStr = new String(resultData);
        if (StringUtils.isBlank(resultStr)) {
            logger.warn("getJsonResult failed, http return data is empty, statusCode={}.", statusCode);
            return null;
        }

        T json = null;
        try {
            json = gson.fromJson(resultStr, clazz);
        } catch (JsonSyntaxException ex) {
            logger.warn("getJsonResult parse gson failed, statusCode={}, resultStr={}, JsonSyntaxException={}", statusCode, resultStr,
                    ex.getMessage(), ex);
            json = null;
        }
        return json;
    }

    /**
     * 获取HTTP上下文内容.
     *
     * <p>
     * 示例： xx
     * </p>
     * <p>
     * 注意事项： xx
     * </p>
     *
     * @param uri      请求地址
     * @param user     认证用户名
     * @param password 认证密码
     * @return 上下文内容
     */
    private static HttpClientContext getHttpClientContext(URI uri, String username, String password) {
        HttpClientContext localContext = HttpClientContext.create();
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()), new UsernamePasswordCredentials(username, password));
            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            // Generate BASIC scheme object and add it to the local auth cache
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(host, basicAuth);
            // Add AuthCache to the execution context
            localContext.setAuthCache(authCache);
        }
        return localContext;
    }

    private static CredentialsProvider getCredentialsProvider(URI uri, String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return null;
        }
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()), new UsernamePasswordCredentials(username, password));
        return credsProvider;
    }

    private static CloseableHttpClient getCloseableHttpClient(CredentialsProvider credsProvider) {
        if (credsProvider == null) {
            return HttpClients.createDefault();
        }
        return HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
    }

    public static Map<Integer, byte[]> request(String url, String user, String password, Map<String, String> header, String method) {
        return upload(url, user, password, header, method, null);
    }

    /**
     * HTTP请求.
     *
     * <p>
     * 示例： xx
     * </p>
     * <p>
     * 注意事项： xx
     * </p>
     *
     * @param url          请求URL
     * @param user         账户
     * @param password     密码
     * @param header       HTTP头
     * @param method       HTTP方法
     * @param uploadEntity HTTP内容
     * @return HTTP请求结果&lt;状态码，内容&gt;
     */
    public static Map<Integer, byte[]> upload(String url, String user, String password, Map<String, String> header, String method,
                                              HttpEntity uploadEntity) {
        final long begin = System.currentTimeMillis();
        logger.info("request start, url={}, method={}", url, method);

        HttpRequestBase httpRequest = getHttpRequestBase(url, method, header, uploadEntity);
        if (httpRequest == null) {
            logger.info("request failed, url={}, method={}, HttpRequestBase is null.", url, method);
            return null;
        }

        URI uri = URI.create(url);
        CredentialsProvider credsProvider = getCredentialsProvider(uri, user, password);
        CloseableHttpClient client = getCloseableHttpClient(credsProvider);
        HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        HttpClientContext context = getHttpClientContext(uri, user, password);

        int statusCode = 0;
        byte[] data = null;
        Map<Integer, byte[]> result = new HashMap<>();
        for (int i = 1; i <= MAX_RETRY_TIME; i++) {
            CloseableHttpResponse response = null;
            result.clear();
            try {
                response = client.execute(host, httpRequest, context);
                if (response == null) {
                    logger.warn("request failed, url={}, method={}, currTime={}, maxRetry={}, httpResponse is null.", url, method, i, MAX_RETRY_TIME);
                    continue;
                }
                statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    data = new byte[0];
                } else {
                    data = EntityUtils.toByteArray(entity);
                }
                result.put(Integer.valueOf(statusCode), data);
                logger.info("request executed, url={}, method={}, statusCode={}", url, method, statusCode);
                break;
            } catch (IOException ex) {
                logger.warn("request failed, url={}, method={}, currRetry={}, maxRetry={}, IOException={}.", url, method, i, MAX_RETRY_TIME,
                        ex.getMessage(), ex);
                result.clear();
            } finally {
                if (response != null) {
                    IOUtils.closeQuietly(response);
                }
            }
        }
        IOUtils.closeQuietly(client);

        logger.info("request finished, url={}, method={}, final statusCode={}, dataSize={}, usedTime={}", url, method, statusCode,
                data == null ? "-1" : data.length, System.currentTimeMillis() - begin);
        return result;
    }

    /**
     * 下载HTTP文件.
     *
     * <p>
     * 示例： xx
     * </p>
     * <p>
     * 注意事项： 通过返回size判断请求结果
     * </p>
     *
     * @param url       下载地址
     * @param user      账户
     * @param password  密码
     * @param header    HTTP头
     * @param method    HTTP方法
     * @param localPath 本地文件绝对路径
     * @return 下载文件大小
     */
    public static long download(String url, String user, String password, Map<String, String> header, String method, String localPath) {
        final long begin = System.currentTimeMillis();
        logger.info("download start, url={}, method={}", url, method);

        HttpRequestBase httpRequest = getHttpRequestBase(url, method, header, null);
        if (httpRequest == null) {
            logger.info("download failed, url={}, method={}, HttpRequestBase is null.", url, method);
            return DOWNLOAD_FAIL_PARAMETER;
        }

        URI uri = URI.create(url);
        CredentialsProvider credsProvider = getCredentialsProvider(uri, user, password);
        CloseableHttpClient client = getCloseableHttpClient(credsProvider);
        HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        HttpClientContext context = getHttpClientContext(uri, user, password);
        File localFile = new File(localPath);
        for (int i = 1; i <= MAX_RETRY_TIME; i++) {
            CloseableHttpResponse response = null;
            InputStream in = null;
            try {
                response = client.execute(host, httpRequest, context);
                if (response == null) {
                    logger.warn("download failed, url={}, method={}, currTime={}, maxRetry={}, httpResponse is null.", url, method, i,
                            MAX_RETRY_TIME);
                    continue;
                }

                int statusCode = response.getStatusLine().getStatusCode();
                logger.info("download executed, url={}, method={}, statusCode={}", url, method, statusCode);
                if (statusCode != HttpServletResponse.SC_OK) {
                    return DOWNLOAD_FAIL_STATUSCODE;
                }

                HttpEntity entity = response.getEntity();
                in = entity.getContent();
                FileUtils.copyInputStreamToFile(in, localFile);
                long size = localFile.length();
                logger.info("download finished, url={}, method={}, localPath={}, dataSize={}, usedTime={}", url, method, localPath, size,
                        System.currentTimeMillis() - begin);
                return size;
            } catch (IOException ex) {
                logger.warn("download failed, url={}, method={}, currRetry={}, maxRetry={}, IOException={}.", url, method, i, MAX_RETRY_TIME,
                        ex.getMessage(), ex);
            } finally {
                if (in != null) {
                    IOUtils.closeQuietly(in);
                }
                if (response != null) {
                    IOUtils.closeQuietly(response);
                }
            }
        }
        IOUtils.closeQuietly(client);

        logger.info("download failed, url={}, method={}, localPath={}, usedTime={}", url, method, localPath,
                System.currentTimeMillis() - begin);
        return DOWNLOAD_FAIL_NETWORK;
    }

}
