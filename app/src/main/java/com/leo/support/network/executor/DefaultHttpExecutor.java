package com.leo.support.network.executor;

import android.text.TextUtils;

import com.leo.support.bean.KeyValuePair;
import com.leo.support.network.HttpError;
import com.leo.support.network.HttpExecutor;
import com.leo.support.network.HttpListener;
import com.leo.support.network.HttpResult;
import com.leo.support.network.NetworkProvider;
import com.leo.support.network.NetworkSensor;
import com.leo.support.network.utils.EntityUtil;
import com.leo.support.network.utils.HttpUtil;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.entity.*;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by leo on 2017/6/10.
 */

public class DefaultHttpExecutor implements HttpExecutor {

    private static final String TAG = "DefaultHttpExecutor";

    private static final int DEFAULT_TIMEOUT = 30;

    @Override
    public HttpResult doGet(String url, HttpRequestParams params, HttpListener httpListener) {
        return execute(true, url, params, httpListener);
    }

    @Override
    public HttpResult doPost(String url, HttpRequestParams params, HttpListener httpListener) {
        return execute(false, url, params, httpListener);
    }

    private HttpResult execute(boolean isGet, String url, HttpRequestParams params, HttpListener httpListener) {

        // 设置超时时间
        int timeout = DEFAULT_TIMEOUT;
        if (null != params && params.mTimeout > 0) {
            timeout = params.mTimeout;
        }

        HttpResult result = new HttpResult();
        result.mHttpListener = httpListener;

        //没有网络
        NetworkSensor networkSensor = NetworkProvider.getNetworkProvider().getNetworkSensor();
        if (null == networkSensor || !networkSensor.isNetworkAvailable()) {
            result.mErrorCode = HttpError.ERROR_NO_AVAILABLE_NETWORK;
            if (null != httpListener) {
                try {
                    httpListener.onError(HttpError.ERROR_NO_AVAILABLE_NETWORK);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        //url为空
        if (TextUtils.isEmpty(url)) {
            result.mErrorCode = HttpError.ERROR_URL_EMPTY;
            if (null != httpListener) {
                try {
                    httpListener.onError(HttpError.ERROR_URL_EMPTY);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        InputStream is = null;
        HttpUriRequest request = null;
        HttpClient client = null;
        try {
            //初始化client
            client = HttpUtil.createHttpClient(timeout);

            //在ready前请求cancel
            if (null != httpListener && !httpListener.onReady(url)) {
                result.mErrorCode = HttpError.ERROR_CANCEL_READY;
                return result;
            }

            //重新构建url,特殊字符进行url编码
            String newUrl = networkSensor.rebuildUrl(url);
            if (!TextUtils.isEmpty(newUrl)) {
                url = newUrl;
            }

            //同步请求的url
            result.mUrl = url;

            if (isGet) {
                //拼接请求参数
                if (null != params && null != params.mParams) {
                    url = HttpUtil.encodeUrl(url, params.mParams);
                }
                request = new HttpGet(url);
            } else {
                request = new HttpPost(url);
                HttpEntity entity = null;
                if (null != params && null != params.mOsHandler) {
                    entity = new CustomHttpEntity(params.mOsHandler);
                } else {
                    entity = new MultipartEntity();
                    if (null != params && null != params.mByteFileMap) {
                        if (null != params && null != params.mParams) {
                            for (KeyValuePair param : params.mParams) {
                                ((MultipartEntity)entity).addPart(param.getKey(),
                                        new StringBody(param.getValue(), Charset.forName(HTTP.UTF_8)));
                            }
                        }

                        for (Map.Entry<String, ByteFile> entry : params.mByteFileMap.entrySet()) {
                            ByteFile file = entry.getValue();
                            if (!TextUtils.isEmpty(file.mMimeType) && !TextUtils.isEmpty(file.mFileName)) {
                                ((MultipartEntity) entity).addPart(entry.getKey(),
                                        new ByteArrayBody(file.mBytes, file.mMimeType, file.mFileName));
                            }
                        }
                    } else {
                        if (null != params && null != params.mParams) {
                            List<BasicNameValuePair> valuePairs = new ArrayList<BasicNameValuePair>();
                            for (KeyValuePair pair : params.mParams) {
                                valuePairs.add(new BasicNameValuePair(pair.getKey(), pair.getValue()));
                            }
                            entity = new UrlEncodedFormEntity(valuePairs, HTTP.UTF_8);
                        }
                    }
                }

                ((HttpPost)request).setEntity(entity);

            }

            //断点续传
            long startPos = 0;
            if(null != params && params.mStartPos > 0) {
                startPos = params.mStartPos;
                request.addHeader("RANGE", "bytes=" + startPos + "-");
                result.mStartPos = startPos;
            }

            //添加默认请求头
            List<KeyValuePair> defaultHeaders = networkSensor.getCommonHeaders(url, params.isProxy);
            if (null != defaultHeaders) {
                for (KeyValuePair pair : defaultHeaders) {
                    request.addHeader(pair.getKey(), pair.getValue());
                }
            }

            //添加自定义请求头
            if (null != params && null != params.mHeader) {
                for (Map.Entry<String, String> entry : params.mHeader.entrySet()) {
                    request.addHeader(entry.getKey(), entry.getValue());
                }
            }

            //添加默认的代理
            //当前框架 proxy 返回为空，不设置代理
            ProxyHost proxy = networkSensor.getProxyHost(url, params.isProxy);
            if (null != proxy) {
                HttpHost httpHost = new HttpHost(proxy.mHost, proxy.mPort);
                client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, httpHost);
            }

            //开始执行网络请求
            long start = System.currentTimeMillis();
            HttpResponse response = client.execute(request);

            int statusCode = response.getStatusLine().getStatusCode();

            HttpEntity httpEntity = response.getEntity();
            if (null != httpEntity) {
                is = httpEntity.getContent();
                //响应时cancel
                if (null != httpListener && httpListener.onResponse(is, null, statusCode,
                        httpEntity.getContentType() == null ? "" : httpEntity.getContentType().getValue(),
                        httpEntity.getContentEncoding() == null ? "" : httpEntity.getContentEncoding().getValue(),
                        httpEntity.getContentLength(), httpEntity.isRepeatable(), httpEntity.isChunked())) {
                    result.mErrorCode = HttpError.ERROR_CANCEL_RESPONSE;
                    return result;
                }
            }

            //同步请求响应时间
            result.mReqTs  = System.currentTimeMillis() - start;
            //同步状态码
            result.mStatusCode = statusCode;

            if (HttpStatus.SC_OK == statusCode
                    || HttpStatus.SC_PARTIAL_CONTENT == statusCode) {
                long contentLength = response.getEntity().getContentLength();
                //同步数据长度
                result.mContentLength = contentLength;

                Header header = response.getLastHeader("Transfer-Encoding");
                if (null != header && "chunked".equalsIgnoreCase(header.getValue())) {
                    result.mIsTrunked = true;
                }

                boolean isGzip = EntityUtil.isGZIPed(response.getEntity());
                result.mIsGzip = isGzip;

                //通知开始下载
                if (null != httpListener && !httpListener.onStart(startPos, contentLength)) {
                    //开始下载时cancel掉Http请求
                    result.mErrorCode = HttpError.ERROR_CANCEL_BEGIN;
                    return result;
                }

                if (isGzip) {
                    is = new GZIPInputStream(response.getEntity().getContent());
                }

                int len = -1;
                int bufferSize = 1024 * 10;
                if (null != params && params.mBufferSize > 0) {
                    bufferSize = params.mBufferSize;
                }
                byte[] buf = new byte[bufferSize];
                start = System.currentTimeMillis();
                while ((len = is.read(buf, 0, bufferSize)) > 0) {
                    //转化下载过程中cancel
                    if (null != httpListener && !httpListener.onAdvance(buf, 0, len)) {
                        result.mErrorCode = HttpError.ERROR_CANCEL_ADVANCE;
                        break;
                    }

                    if (null != networkSensor) {
                        networkSensor.updateFlowRate(len);
                    }
                }

                //同步读取时间
                result.mReadTs = System.currentTimeMillis() - start;
                if (null != httpListener && result.isSuccess()) {
                    httpListener.onComplete();
                }
            } else {
                result.mErrorCode = HttpError.ERROR_STATUS_CODE;
                if (null != httpListener) {
                    httpListener.onError(statusCode);
                }
            }
        } catch (ClientProtocolException e) {
            result.mErrorCode = HttpError.ERROR_UNKNOWN;
            if (null !=  httpListener) {
                try {
                    httpListener.onError(HttpError.ERROR_UNKNOWN);
                } catch (Throwable e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        } catch (IOException e) {
            result.mErrorCode = HttpError.ERROR_UNKNOWN;
            if (null !=  httpListener) {
                try {
                    httpListener.onError(HttpError.ERROR_UNKNOWN);
                } catch (Throwable e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        } catch (Throwable e) {
            result.mErrorCode = HttpError.ERROR_UNKNOWN;
            if (null !=  httpListener) {
                try {
                    httpListener.onError(HttpError.ERROR_UNKNOWN);
                } catch (Throwable e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (null != is && result.mErrorCode != HttpError.ERROR_CANCEL_RESPONSE) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (null != request && result.mErrorCode != HttpError.ERROR_CANCEL_RESPONSE) {
                try {
                    request.abort();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (null != client && result.mErrorCode != HttpError.ERROR_CANCEL_RESPONSE) {
                try {
                    client.getConnectionManager().shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (null != httpListener && result.mErrorCode != HttpError.ERROR_CANCEL_RESPONSE) {
                try {
                    httpListener.onRelease();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    private class CustomHttpEntity extends AbstractHttpEntity {

        private OutputStreamHandler mStreamHandler;
        private boolean consumed = false;

        CustomHttpEntity(OutputStreamHandler handler) {
            super();
            this.mStreamHandler = handler;
        }

        @Override
        public boolean isRepeatable() {
            return false;
        }

        @Override
        public long getContentLength() {
            return mStreamHandler.getLength();
        }

        @Override
        public InputStream getContent() throws IOException, IllegalStateException {
            throw new UnsupportedOperationException("Entity template does not implement getContent()");
        }

        @Override
        public void writeTo(OutputStream outputStream) throws IOException {
            if (null == outputStream) {
                throw new IllegalArgumentException("Output stream may be null");
            }
            this.mStreamHandler.writeTo(outputStream);
            this.consumed = true;
        }

        @Override
        public boolean isStreaming() {
            return !this.consumed;
        }

        @Override
        public void consumeContent() throws IOException {
            this.consumed = true;
        }
    }
}
