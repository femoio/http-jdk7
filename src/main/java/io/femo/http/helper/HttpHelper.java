package io.femo.http.helper;

import io.femo.http.Driver;
import io.femo.http.HttpContext;
import io.femo.http.HttpRequest;
import io.femo.http.HttpResponse;
import io.femo.http.drivers.server.HttpThread;
import io.femo.support.jdk7.Optional;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.xjs.dynamic.Pluggable;
import org.xjs.dynamic.PluggableAccessor;

import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by Felix Resch on 29-Apr-16.
 */
public class HttpHelper {

    private static ThreadLocal<HttpContext> context = new ThreadLocal<HttpContext>() {
        @Contract(" -> !null")
        @Override
        protected HttpContext initialValue() {
            return new DefaultHttpContext();
        }
    };

    @Nullable
    public static HttpResponse response() {
        if(Thread.currentThread() instanceof Pluggable) {
            Optional<HttpResponse> httpResponse = PluggableAccessor.getFirst(((Pluggable<HttpThread>) Thread.currentThread()), HttpResponse.class);
            if(httpResponse.isPresent()) {
                return httpResponse.get();
            }
        }
        return null;
    }

    @Nullable
    public static HttpRequest request() {
        if(Thread.currentThread() instanceof Pluggable) {
            Optional<HttpRequest> httpRequest = PluggableAccessor.getFirst(((Pluggable<HttpThread>) Thread.currentThread()), HttpRequest.class);
            if(httpRequest.isPresent()) {
                return httpRequest.get();
            }
        }
        return null;
    }

    public static void response(HttpResponse response) {
        if(Thread.currentThread() instanceof Pluggable) {
            Pluggable pluggable = (Pluggable) Thread.currentThread();
            PluggableAccessor.removeAll(pluggable, HttpResponse.class);
            PluggableAccessor.add(pluggable, response);
        }
    }

    public static void request(HttpRequest request) {
        if(Thread.currentThread() instanceof Pluggable) {
            Pluggable pluggable = (Pluggable) Thread.currentThread();
            PluggableAccessor.removeAll(pluggable, HttpRequest.class);
            PluggableAccessor.add(pluggable, request);
        }
    }

    @Nullable
    public static SocketAddress remote() {
        if(Thread.currentThread() instanceof Pluggable) {
            Optional<SocketAddress> socketAddress = PluggableAccessor.getFirst(((Pluggable<HttpThread>) Thread.currentThread()), SocketAddress.class);
            if(socketAddress.isPresent()) {
                return socketAddress.get();
            }
        }
        return null;
    }

    public static void remote(SocketAddress socketAddress) {
        if(Thread.currentThread() instanceof Pluggable) {
            Pluggable pluggable = (Pluggable) Thread.currentThread();
            PluggableAccessor.removeAll(pluggable, SocketAddress.class);
            PluggableAccessor.add(pluggable, socketAddress);
        }
    }

    public static HttpContext context() {
        return context.get();
    }

    public static void useDriver(Driver driver) {
        context.get().useDriver(driver);
    }

    @Nullable
    public static Pluggable<HttpThread> get() {
        if(Thread.currentThread() instanceof Pluggable) {
            return (Pluggable<HttpThread>) Thread.currentThread();
        } else {
            return null;
        }
    }

    public static void keepOpen() {
        if(Thread.currentThread() instanceof Pluggable) {
            ((HttpSocketOptions)PluggableAccessor.getFirst(((Pluggable<HttpThread>) Thread.currentThread()), HttpSocketOptions.class).get()).setClose(false);
        }
    }

    public static void callback(HandledCallback handledCallback) {
        if(Thread.currentThread() instanceof Pluggable) {
            ((HttpSocketOptions)PluggableAccessor.getFirst(((Pluggable<HttpThread>) Thread.currentThread()), HttpSocketOptions.class).get()).setHandledCallback(handledCallback);
        }
    }
}
