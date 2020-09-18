package com.lzd.upload.utils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * http请求的工具类
 * @author lzd
 * @date 2019年4月3日
 * @version
 */
@SuppressWarnings("deprecation")
public class HttpClientUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
    
    //重试次数
  	private static final int TRY_TIMES=3;
  	
    public static void main(String [] args) throws Exception{
        String url = "http://php.cnstock.com/news_new/index.php/api/getAnnounceList?column=减持";
        System.out.println(doGet(url,"UTF-8"));
    }

    
    /**
     * Post方式请求外部接口
     * @author lzd
     * @date 2019年11月13日:下午1:58:57
     * @param url
     * @param map
     * @param charset
     * @return
     * @description
     */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String doPost(String url, Map<String, String> map, String charset){
    	int timeout = 30000;
    	RequestConfig config = RequestConfig.custom()
				.setConnectionRequestTimeout(timeout)
				.setConnectTimeout(timeout)
				.setSocketTimeout(timeout).build();
    	HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setConnectionManager(new PoolingHttpClientConnectionManager());
        clientBuilder.setUserAgent("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        HttpClient httpClient =clientBuilder.build();
        String result = null;
        try {
        	HttpPost httpPost = new HttpPost(url);
        	httpPost.setConfig(config);
            //设置参数
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, String> elem = (Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
            }
            if (list.size() > 0) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
                httpPost.setEntity(entity);
            }
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, charset);
                }
            }
        } catch (Exception e) {
        	logger.error(e.getMessage(),e);
        } finally {
            // 关闭连接,释放资源
            if (httpClient != null) {
                httpClient.getConnectionManager().shutdown();
            }
        }
        return result;
    }

    /**
     * Get方式请求外部接口
     * @author lzd
     * @date 2019年11月13日:下午1:58:14
     * @param url
     * @param charset
     * @return
     * @description
     */
    public static String doGet(String url, String charset){
        int timeout = 30000;
        RequestConfig config = RequestConfig.custom()
                		.setConnectionRequestTimeout(timeout)
                		.setConnectTimeout(timeout)
                		.setSocketTimeout(timeout).build();
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setConnectionManager(new PoolingHttpClientConnectionManager());
        clientBuilder.setRetryHandler(getHttpRequestRetryHandler());
        clientBuilder.setUserAgent("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        HttpClient httpClient =clientBuilder.build();
        String result = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(config);
            //设置参数
            HttpResponse response = httpClient.execute(httpGet);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, charset);
                }
            }
            httpGet.abort();
            httpClient.getConnectionManager().shutdown();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return result;
    }

    /**
     * 将形如key=value&key=value的字符串转换为相应的Map对象
     *
     * @param result
     * @return
     */
    public static Map<String, String> convertResultStringToMap(String result) {
        Map<String, String> map = null;
        try {
            if (result != null && "".equals(result)) {
                result = result.trim();
                if (result.startsWith("{") && result.endsWith("}")) {
                    result = result.substring(1, result.length() - 1);
                }
                map = parseQString(result);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return map;
    }


    /**
     * 解析应答字符串，生成应答要素
     *
     * @param str 需要解析的字符串
     * @return 解析的结果map
     * @throws
     */
    public static Map<String, String> parseQString(String str) {
        Map<String, String> map = new HashMap<String, String>();
        int len = str.length();
        StringBuilder temp = new StringBuilder();
        char curChar;
        String key = null;
        boolean isKey = true;
        boolean isOpen = false;//值里有嵌套
        char openName = 0;
        if (len > 0) {
            for (int i = 0; i < len; i++) {// 遍历整个带解析的字符串
                curChar = str.charAt(i);// 取当前字符
                if (isKey) {// 如果当前生成的是key
                    if (curChar == '=') {// 如果读取到=分隔符
                        key = temp.toString();
                        temp.setLength(0);
                        isKey = false;
                    } else {
                        temp.append(curChar);
                    }
                } else {// 如果当前生成的是value
                    if (isOpen) {
                        if (curChar == openName) {
                            isOpen = false;
                        }
                    } else {//如果没开启嵌套
                        if (curChar == '{') {//如果碰到，就开启嵌套
                            isOpen = true;
                            openName = '}';
                        }
                        if (curChar == '[') {
                            isOpen = true;
                            openName = ']';
                        }
                    }
                    if (curChar == '&' && !isOpen) {// 如果读取到&分割符,同时这个分割符不是值域，这时将map里添加
                        putKeyValueToMap(temp, isKey, key, map);
                        temp.setLength(0);
                        isKey = true;
                    } else {
                        temp.append(curChar);
                    }
                }

            }
            putKeyValueToMap(temp, isKey, key, map);
        }
        return map;
    }

    
    private static void putKeyValueToMap(StringBuilder temp, boolean isKey,String key, Map<String, String> map) {
        if (isKey) {
            key = temp.toString();
            if (key.length() == 0) {
                throw new RuntimeException("QString format illegal");
            }
            map.put(key, "");
        } else {
            if (key.length() == 0) {
                throw new RuntimeException("QString format illegal");
            }
            map.put(key, temp.toString());
        }
    }
    
    /**
     * 获取重试控制器
     * @author lzd
     * @date 2019年11月14日:下午12:49:07
     * @return
     * @description
     */
    private static HttpRequestRetryHandler getHttpRequestRetryHandler() {
        return new HttpRequestRetryHandler() {
        	@Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                logger.warn("第"+executionCount+"次请求发生错误！", exception);
                if (executionCount >TRY_TIMES-1) return false;
                if (exception instanceof NoHttpResponseException) {
                    logger.warn("没有响应异常,重试第{}次!",executionCount+1);
                    sleep(executionCount);
                    return true;
                } else if (exception instanceof SocketTimeoutException) {
                    logger.warn("连接超时，重试第{}次!",executionCount+1);
                    sleep(executionCount);
                    return true;
                } else if (exception instanceof ConnectTimeoutException) {
                    logger.warn("连接超时，重试第{}次!",executionCount+1);
                    sleep(executionCount);
                    return true;
                } else if (exception instanceof SSLHandshakeException) {
                    logger.warn("本地证书异常！");
                    return false;
                } else if (exception instanceof InterruptedIOException) {
                    logger.warn("IO中断异常,重试第{}次!",executionCount+1);
                    sleep(executionCount);
                    return true;
                } else if (exception instanceof UnknownHostException) {
                    logger.warn("找不到服务器异常！");
                    return false;
                } else if (exception instanceof SSLException) {
                    logger.warn("SSL异常！");
                    return false;
                } else if (exception instanceof HttpHostConnectException) {
                    logger.warn("主机连接异常！");
                    return false;
                } else if (exception instanceof SocketException) {
                    logger.warn("socket异常！");
                    return false;
                } else {
                    logger.warn("未记录的请求异常：{}", exception.getClass());
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，则重试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    sleep(executionCount);
                    return true;
                }
                return false;
            }
            
            /**
             * 休眠时间
             * @author lzd
             * @date 2019年11月14日:下午12:48:39
             * @param i
             * @description
             */
			private void sleep(int second) {
				// TODO Auto-generated method stub
				try {
					logger.info("休眠{}秒！",second);
					Thread.sleep(second*1000);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
        };
    }

}