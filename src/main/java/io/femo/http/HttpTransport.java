package io.femo.http;

import io.femo.http.transport.Http11Transport;
import org.jetbrains.annotations.Contract;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by felix on 6/9/16.
 */
public abstract class HttpTransport implements Driver {

    private static ThreadLocal<HttpTransport> version11 = new ReadonlyThreadLocal<HttpTransport>() {
        @Contract(" -> !null")
        @Override
        protected HttpTransport initialValue() {
            return new Http11Transport();
        }

    };

    public abstract void write(HttpRequest httpRequest, OutputStream outputStream);
    public abstract void write(HttpResponse httpResponse, OutputStream outputStream, InputStream entityStream);

    public abstract HttpRequest readRequest(InputStream inputStream) throws IOException;
    public abstract HttpResponse readResponse(InputStream inputStream, OutputStream pipe) throws IOException;

    public static HttpTransport version11() {
        return version11.get();
    }

    public static HttpTransport def() {
        return version11();
    }
}
