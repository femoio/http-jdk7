package io.femo.http;

/**
 * Created by felix on 4/26/16.
 */
public interface HttpMiddleware {

    void handle(HttpRequest request, HttpResponse response) throws HttpHandleException;
}
