package io.femo.http;

import io.femo.http.auth.DefaultBasicStrategy;
import io.femo.http.auth.DefaultDigestStrategy;
import io.femo.http.support.Supplier;
import org.jetbrains.annotations.Contract;


/**
 * Created by felix on 6/21/16.
 */
public abstract class Authentication implements Driver {

    public abstract boolean isInitialized();
    public abstract boolean matches(HttpRequest request);
    public abstract boolean supportsMulti();
    public abstract boolean supportsDirect();

    public abstract void init(HttpResponse response);

    public abstract String strategy();

    public abstract void authenticate(HttpRequest request);

    public boolean supports(HttpResponse response) {
        String authenticate = response.header("WWW-Authenticate").value();
        return authenticate.startsWith(strategy());
    }

    @Contract("_, _ -> !null")
    public static Authentication basic(Supplier<String> username, Supplier<String> password) {
        return new DefaultBasicStrategy(username, password);
    }

    @Contract("_, _ -> !null")
    static Authentication basic(String username, String password) {
        return new DefaultBasicStrategy(username, password);
    }

    @Contract("_, _ -> !null")
    static Authentication digest(String username, String password) {
        return new DefaultDigestStrategy(username, password);
    }

    @Contract("_, _ -> !null")
    static Authentication digest(Supplier<String> username, Supplier<String> password) {
        return new DefaultDigestStrategy(username, password);
    }
}
