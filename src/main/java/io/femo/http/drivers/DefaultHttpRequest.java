package io.femo.http.drivers;

import io.femo.http.*;
import io.femo.http.HttpCookie;
import io.femo.http.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;

import io.femo.support.jdk7.Supplier;

/**
 * Created by felix on 9/11/15.
 */
public class DefaultHttpRequest extends HttpRequest {

    private static Logger log = LoggerFactory.getLogger("HTTP");

    private String method;
    private Map<String, HttpCookie> cookies;
    private Map<String, HttpHeader> headers;
    private byte[] entity;
    protected URL url;
    private HttpResponse response;
    private Map<String, byte[]> data;
    private Transport transport = Transport.HTTP;
    protected HttpEventManager manager;
    private OutputStream pipe;
    private HttpTransport httpTransport;

    private List<Driver> drivers;

    private boolean reauth = false;

    public DefaultHttpRequest(URL url) {
        this();
        this.url = url;
        header("Connection", "close");
        header("User-Agent", "FeMoIO HTTP/0.1");
        header("Host", url.getHost());
        this.drivers = new ArrayList<>();
        manager = new HttpEventManager();
    }

    protected DefaultHttpRequest() {
        this.cookies = new HashMap<>();
        this.headers = new HashMap<>();
    }

    @Override
    public HttpRequest method(String method) {
        this.method = method;
        return this;
    }

    @Override
    public HttpRequest cookie(String name, String value) {
        cookies.put(name, new HttpCookie(name, value));
        return this;
    }

    @Override
    public HttpRequest header(String name, String value) {
        headers.put(name, new HttpHeader(name, value));
        return this;
    }

    @Override
    public HttpRequest entity(byte[] entity) {
        header("Content-Length", entity.length + "");
        if(!hasHeader("Content-Type")) {
            header("Content-Type", "text/plain");
        }
        this.entity = entity;
        return this;
    }

    @Override
    public HttpRequest entity(String entity) {
        return entity(entity.getBytes());
    }

    @Override
    public HttpRequest entity(Object entity) {
        return entity(String.valueOf(entity));
    }

    @Override
    public HttpRequest basicAuth(Supplier<String> username, Supplier<String> password) {
        Authentication.basic(username, password).authenticate(this);
        return this;
    }

    @Override
    public HttpRequest execute(HttpResponseCallback callback) {
        int port = url.getPort() == -1 ? url.getDefaultPort() : url.getPort();
        HttpResponse response;
        try {
            Socket socket = transport.openSocket(url.getHost(), port);
            print(socket.getOutputStream());
            manager.raise(new HttpSentEvent(this));
            response = httpTransport.readResponse(socket.getInputStream(), pipe);
            response.request(this);
            manager.raise(new HttpReceivedEvent(this, response));
            socket.close();
        } catch (IOException e) {
            manager.raise(new HttpEvent(HttpEventType.ERRORED) {
            });
            throw new HttpException(this, e);
        }
        boolean handled = false;
        if(callback != null) {
            try {
                callback.receivedResponse(response);
                handled = true;
            } catch (Throwable t) {
                t.printStackTrace();
                handled = false;
            }
        }
        manager.raise(new HttpHandledEvent(this, response, handled));
        this.response = response;
        if(response.status().status() == StatusCode.FOUND.status()) {
            try {
                this.url = new URL(response.header("Location").value());
                for (HttpCookie cookie : response.cookies()) {
                    cookie(cookie.name(), cookie.value());
                }
                execute(callback);
            } catch (MalformedURLException e) {
                throw new HttpException(this, e);
            }
        } else if (!reauth && response.status().status() == StatusCode.UNAUTHORIZED.status()) {
            reauth = true;
            List<Authentication> authentications = drivers(Authentication.class);
            Iterator<Authentication> iterator = authentications.iterator();
            while (iterator.hasNext()) {
                Authentication auth = iterator.next();
                if(!auth.supports(response)) {
                    iterator.remove();
                }
            }
            if(authentications.size() > 0) {
                Authentication authentication = authentications.get(0);
                if(authentication.isInitialized() && authentication.matches(this)) {
                    authentication.authenticate(this);
                    execute(callback);
                } else if (!authentication.isInitialized()) {
                    authentication.init(response);
                    authentication.authenticate(this);
                    execute(callback);
                } else if (authentication.supportsMulti()) {
                    authentication.init(response);
                    authentication.authenticate(this);
                    execute(callback);
                }
            }
        }
        return this;
    }

    @Override
    public HttpRequest transport(Transport transport) {
        this.transport = transport;
        return this;
    }

    @Override
    public HttpRequest version(HttpVersion version) {
        return this;
    }

    @Override
    public HttpRequest print(OutputStream output) {
        if(httpTransport == null) {
            httpTransport = HttpTransport.version11();
        }
        httpTransport.write(this, output);
        return this;
    }

    private void writeUrlFormEncoded() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> iterator = data.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            try {
                stringBuilder.append(URLEncoder.encode(key, "UTF-8"))
                        .append("=")
                        .append(new String(data.get(key)));
            } catch (UnsupportedEncodingException e) {
                throw new HttpException(this, e);
            }
            if(iterator.hasNext()) {
                stringBuilder.append("&");
            }
        }
        entity(stringBuilder.toString());
    }

    @Override
    public HttpRequest data(String key, String value) {
        if(data == null)
            data = new HashMap<>();
        data.put(key, value.getBytes());
        return this;
    }

    @Override
    public HttpRequest eventManager(HttpEventManager manager) {
        this.manager = manager;
        return this;
    }

    @Override
    public HttpRequest event(HttpEventType type, HttpEventHandler handler) {
        this.manager.addEventHandler(type, handler);
        return this;
    }

    @Override
    public HttpRequest using(Driver driver) {
        this.drivers.add(driver);
        return this;
    }

    @Override
    public HttpRequest pipe(OutputStream outputStream) {
        this.pipe = outputStream;
        return this;
    }

    @Override
    public HttpRequest prepareEntity() {
        if(data != null) {
            if(hasHeader("Content-Type")) {
                String contentType = header("Content-Type").value();
                if(contentType.equals("application/x-www-form-urlencoded")) {
                    writeUrlFormEncoded();
                }
            } else {
                header("Content-Type", "application/x-www-form-urlencoded");
                writeUrlFormEncoded();
            }
        }
        return this;
    }

    public <T extends Driver> List<T> drivers(Class<T> type) {
        ArrayList<T> drivers = new ArrayList<>();
        for(Driver driver : this.drivers) {
            if(type.isAssignableFrom(driver.getClass()))
                drivers.add((T) driver);
        }
        return drivers;
    }

    @Override
    public String method() {
        return method;
    }

    @Override
    public Collection<HttpCookie> cookies() {
        return cookies.values();
    }

    @Override
    public Collection<HttpHeader> headers() {
        return headers.values();
    }

    public boolean isHeader(String name) {
        return headers.containsKey(name);
    }

    @Override
    public byte[] entityBytes() {
        return entity;
    }

    @Override
    public String entityString() {
        return new String(entity);
    }

    @Override
    public boolean checkAuth(String username, String password) {
        return false;
    }

    @Override
    public HttpResponse response() {
        if(response == null)
                execute();
        return response;
    }

    @Override
    public HttpRequest use(HttpTransport httpTransport) {
        this.httpTransport = httpTransport;
        return this;
    }

    @Override
    public Transport transport() {
        return transport;
    }

    @Override
    public String path() {
        return url.getPath();
    }

    public String requestLine() {
        return method.toUpperCase() +  " " + url.getHost() + " HTTP/1.1";
    }

    @Override
    public HttpHeader header(String name) {
        return headers.get(name);
    }

    @Override
    public boolean hasHeader(String name) {
        return headers.containsKey(name);
    }

    protected void response(HttpResponse response) {
        this.response = response;
    }

    public URL url() {
        return url;
    }


}
