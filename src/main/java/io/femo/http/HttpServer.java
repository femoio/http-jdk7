package io.femo.http;

/**
 * Created by Felix Resch on 25-Apr-16.
 */
public abstract class HttpServer extends HttpRoutable<HttpServer> {

    public abstract HttpServer start();
    public abstract HttpServer stop();

    public abstract boolean ready();
}
