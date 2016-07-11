package io.femo.http;

import io.femo.http.middleware.EnvironmentReplacerMiddleware;
import io.femo.support.jdk7.Supplier;

/**
 * Created by felix on 7/2/16.
 */
public abstract class Environment implements Driver {


    public abstract boolean has(String variable);

    public abstract EnvironmentReplacerMiddleware.HttpSupplier<String> get(String key);

    public abstract Environment set(String name, EnvironmentReplacerMiddleware.HttpSupplier<String> value);

    public Environment set(String name, final String value) {
        return set(name, new EnvironmentReplacerMiddleware.HttpSupplier<String>() {
            @Override
            public String get(HttpRequest req, HttpResponse res) {
                return value;
            }
        });
    }

    public Environment set(String name, final Supplier<String> value) {
        return set(name, new EnvironmentReplacerMiddleware.HttpSupplier<String>() {
            @Override
            public String get(HttpRequest req, HttpResponse res) {
                return value.get();
            }
        });
    }
}
