package com.leo.support.network;

import com.leo.support.bean.KeyValuePair;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * done
 * Created by leo on 2017/6/8.
 */

public interface HttpExecutor {

    public static class HttpRequestParams {
        public long mStartPos = 0;

        public int mTimeout = 0;

        public int mBufferSize = 1024;

        public HashMap<String, String> mHeader;

        public ArrayList<KeyValuePair> mParams;

        public HashMap<String, ByteFile> mByteFileMap;

        public OutputStreamHandler mOsHandler;

        public boolean isProxy;
    }

    public static class ByteFile {
        public String mFileName;
        public String mMimeType = "application/octet-stream";
        public byte[] mBytes;

        public ByteFile(String fileName, String mimeType, byte[] bytes){
            this.mFileName = fileName;
            this.mMimeType = mimeType;
            this.mBytes = bytes;
        }
    }

    public static interface OutputStreamHandler {
        public void writeTo(OutputStream os) throws IOException;
        public long getLength();
    }

    public static class ProxyHost {
        public String mHost;
        public int mPort;

        public ProxyHost() {}

        public ProxyHost(String host, int port) {
            this.mHost = host;
            this.mPort = port;
        }
    }

    public HttpResult doGet(String url, HttpRequestParams params, HttpListener httpListener);

    public HttpResult doPost(String url, HttpRequestParams params, HttpListener httpListener);
}
