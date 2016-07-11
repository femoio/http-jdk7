package io.femo.http.drivers;

import io.femo.http.*;
import io.femo.http.drivers.server.*;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

import static io.femo.support.jdk7.HttpUtil.joinPaths;

/**
 * Created by felix on 2/24/16.
 */
public class DefaultHttpServer extends HttpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger("HTTP");

    private int port;
    private boolean ssl;

    private HttpHandlerStack httpHandlerStack;

    private HttpServerThread serverThread;

    private ThreadLocal<SimpleDateFormat> dateFormat = new ReadonlyThreadLocal<SimpleDateFormat>() {
        @Contract(" -> !null")
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        }
    };

    public DefaultHttpServer(int port, boolean ssl) {
        this.port = port;
        this.ssl = ssl;
        this.httpHandlerStack = new HttpHandlerStack();
        use(new HttpMiddleware() {
            @Override
            public void handle(HttpRequest req, HttpResponse res) throws HttpHandleException {
                Date date = new Date();
                res.header("Date", dateFormat.get().format(date));
            }
        });
    }

    @Override
    public HttpServer start() {
        use(new HttpHandler() {
            @Override
            public boolean handle(HttpRequest request, HttpResponse response) throws HttpHandleException {
                response.status(StatusCode.NOT_FOUND);
                response.entity("Could not find resource at " + request.method().toUpperCase() + " " + request.path());
                return true;
            }
        });
        this.serverThread = new HttpServerThread(httpHandlerStack);
        serverThread.setPort(port);
        serverThread.start();
        LOGGER.info("Started HTTP Server on port {}", port);
        return this;
    }

    @Override
    public HttpServer stop() {
        this.serverThread.interrupt();
        return this;
    }

    @Override
    public HttpServer use(HttpMiddleware handler) {
        HttpMiddlewareHandle handle = new HttpMiddlewareHandle();
        handle.setHttpMiddleware(handler);
        httpHandlerStack.submit(handle);
        return this;
    }

    @Override
    public HttpServer use(String path, HttpMiddleware handler) {
        HttpMiddlewareHandle handle = new HttpMiddlewareHandle();
        handle.setPath(path);
        handle.setHttpMiddleware(handler);
        httpHandlerStack.submit(handle);
        return this;
    }

    @Override
    public HttpServer use(HttpHandler handler) {
        if(handler instanceof HttpRouter) {
            HttpRouterHandle handle = new HttpRouterHandle();
            ((HttpRouter) handler).parentPath("/");
            handle.setRouter((HttpRouter) handler);
            httpHandlerStack.submit(handle);
        } else {
            HttpHandlerHandle handle = new HttpHandlerHandle();
            handle.setHandler(handler);
            httpHandlerStack.submit(handle);
        }
        return this;
    }

    @Override
    public HttpServer use(String path, HttpHandler httpHandler) {
        if(httpHandler instanceof HttpRoutable) {
            HttpRouterHandle handle = new HttpRouterHandle();
            ((HttpRouter) httpHandler).parentPath(joinPaths("/", path));
            handle.setRouter((HttpRouter) httpHandler);
            httpHandlerStack.submit(handle);
        } else {
            HttpHandlerHandle handle = new HttpHandlerHandle();
            handle.setHandler(httpHandler);
            handle.setPath(path);
            httpHandlerStack.submit(handle);
        }
        return this;
    }

    @Override
    public HttpServer use(String method, String path, HttpHandler httpHandler) {
        if(httpHandler instanceof HttpRouter) {
            return this;
        }
        HttpHandlerHandle handle = new HttpHandlerHandle();
        handle.setHandler(httpHandler);
        handle.setMethod(method);
        handle.setPath(path);
        httpHandlerStack.submit(handle);
        return this;
    }

    @Override
    public HttpServer after(HttpMiddleware middleware) {
        httpHandlerStack.submitAfter(middleware);
        return this;
    }

    @Override
    public boolean matches(HttpRequest httpRequest) {
        return httpHandlerStack.matches(httpRequest);
    }

    @Override
    public HttpRoutable<HttpServer> prependPath(String path) {
        return this;
    }

    @Override
    public boolean ready() {
        return this.serverThread.ready();
    }

}
