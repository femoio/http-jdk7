package io.femo.http;

import io.femo.http.events.HttpEventHandler;
import io.femo.http.events.HttpEventManager;
import io.femo.http.events.HttpEventType;
import io.femo.support.jdk7.Supplier;
import io.femo.support.jdk7.ValueSupplier;

import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.List;

/**
 * Created by felix on 9/10/15.
 */
public abstract class HttpRequest {

    public abstract HttpRequest method(String method);
    public abstract HttpRequest cookie(String name, String value);
    public abstract HttpRequest header(String name, String value);
    public abstract HttpRequest entity(byte[] entity);
    public abstract HttpRequest entity(String entity);
    public abstract HttpRequest entity(Object entity);
    public abstract HttpRequest execute(HttpResponseCallback callback);
    public abstract HttpRequest transport(Transport transport);
    public abstract HttpRequest version(HttpVersion version);
    public abstract HttpRequest print(OutputStream outputStream);
    public abstract HttpRequest data(String key, String value);
    public abstract HttpRequest eventManager(HttpEventManager manager);
    public abstract HttpRequest event(HttpEventType type, HttpEventHandler handler);

    public abstract HttpRequest using(Driver driver);

    public abstract HttpRequest pipe(OutputStream outputStream);

    public abstract HttpRequest prepareEntity();

    public abstract String method();
    public abstract Collection<HttpCookie> cookies();
    public abstract Collection<HttpHeader> headers();
    public abstract byte[] entityBytes();
    public abstract String entityString();
    public abstract boolean checkAuth(String username, String password);
    public abstract HttpResponse response();
    public abstract HttpRequest use(HttpTransport httpTransport);

    public abstract Transport transport();
    public abstract String requestLine();

    public HttpRequest execute() {
        return execute(null);
    }

    public abstract HttpHeader header(String name);
    public abstract boolean hasHeader(String name);

    public boolean hasHeaders(String ... names) {
        for (String name :
                names) {
            if (!hasHeader(name))
                return false;
        }
        return true;
    }


    public HttpRequest contentType(String contentType) {
        return header("Content-Type", contentType);
    }
    public abstract HttpRequest basicAuth(Supplier<String> username, Supplier<String> password);
    public HttpRequest basicAuth(String username, String password) {
        return basicAuth(new ValueSupplier<>(username), new ValueSupplier<>(password));
    }

    public abstract <T extends Driver> List<T> drivers(Class<T> type);

    public HttpRequest https() {
        return transport(Transport.HTTPS);
    }

    public abstract String path();

    public abstract URL url();
}
