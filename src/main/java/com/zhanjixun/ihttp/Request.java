package com.zhanjixun.ihttp;

/**
 * http请求实体
 *
 * @author zhanjixun
 */
public final class Request {

    public static class Options {

        private final int connectTimeoutMillis;

        private final int readTimeoutMillis;

        public Options(int connectTimeoutMillis, int readTimeoutMillis) {
            this.connectTimeoutMillis = connectTimeoutMillis;
            this.readTimeoutMillis = readTimeoutMillis;
        }

        public Options() {
            this(10 * 1000, 60 * 1000);
        }

        public int connectTimeoutMillis() {
            return connectTimeoutMillis;
        }

        public int readTimeoutMillis() {
            return readTimeoutMillis;
        }
    }
}
