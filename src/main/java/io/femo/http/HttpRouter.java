package io.femo.http;

/**
 * Created by Felix Resch on 29-Apr-16.
 */
public abstract class HttpRouter extends HttpRoutable<HttpRouter> implements HttpHandler {

    public abstract HttpRouter parentPath(String path);

}
