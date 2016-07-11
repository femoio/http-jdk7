package io.femo.http;


import io.femo.support.jdk7.Supplier;

/**
 * Created by Felix Resch on 29-Apr-16.
 */
public abstract class HttpRoutable<T extends HttpRoutable> {

    public abstract T use(HttpMiddleware handler);


    public abstract T use(String path, HttpMiddleware handler);


    public abstract T use(HttpHandler handler);

    public abstract T use(String path, HttpHandler httpHandler);

    public abstract T use(String method, String path, HttpHandler httpHandler);

    public abstract T after(HttpMiddleware middleware);

    public T get(String path, HttpHandler httpHandler) {
        return use(Http.GET, path, httpHandler);
    }

    public T get(String path, Supplier<HttpHandler> httpHandler) {
        return use(Http.GET, path, httpHandler.get());
    }

    public T post(String path, HttpHandler httpHandler) {
        return use(Http.POST, path, httpHandler);
    }
    public T post(String path, Supplier<HttpHandler> httpHandler) {
        return use(Http.POST, path, httpHandler.get());
    }

    public T put(String path, HttpHandler httpHandler) {
        return use(Http.PUT, path, httpHandler);
    }
    public T put(String path, Supplier<HttpHandler> httpHandler) {
        return use(Http.PUT, path, httpHandler.get());
    }

    public T delete(String path, HttpHandler httpHandler) {
        return use(Http.DELETE, path, httpHandler);
    }
    public T delete(String path, Supplier<HttpHandler> httpHandler) {
        return use(Http.DELETE, path, httpHandler.get());
    }

    public T update(String path, HttpHandler httpHandler) {
        return use(Http.UPDATE, path, httpHandler);
    }
    public T update(String path, Supplier<HttpHandler> httpHandler) {
        return use(Http.UPDATE, path, httpHandler.get());
    }

    public T patch(String path, HttpHandler httpHandler) {
        return use(Http.PATCH, path, httpHandler);
    }
    public T patch(String path, Supplier<HttpHandler> httpHandler) {
        return use(Http.PATCH, path, httpHandler.get());
    }

    public abstract boolean matches(HttpRequest httpRequest);

    public abstract HttpRoutable<T> prependPath(String path);

}
