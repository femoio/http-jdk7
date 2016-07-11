package io.femo.http.drivers;

import io.femo.http.Constants;
import io.femo.http.Environment;
import io.femo.http.HttpRequest;
import io.femo.http.HttpResponse;
import io.femo.http.middleware.EnvironmentReplacerMiddleware;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by felix on 7/2/16.
 */
public class DefaultEnvironment extends Environment {

    private Map<String, EnvironmentReplacerMiddleware.HttpSupplier<String>> variables;

    public DefaultEnvironment () {
        this.variables = new HashMap<>();
        set("iso_time", new EnvironmentReplacerMiddleware.HttpSupplier<String>() {
            @Override
            public String get(HttpRequest req, HttpResponse res) {
                return DateFormat.getTimeInstance().format(new Date());
            }
        });
        set("iso_date", new EnvironmentReplacerMiddleware.HttpSupplier<String>() {
            @Override
            public String get(HttpRequest req, HttpResponse res) {
                return DateFormat.getDateInstance().format(new Date());
            }
        });
        set("iso_datetime", new EnvironmentReplacerMiddleware.HttpSupplier<String>() {
            @Override
            public String get(HttpRequest req, HttpResponse res) {
                return DateFormat.getDateTimeInstance().format(new Date());
            }
        });
        set("server", "FeMo.IO HTTP Server " + Constants.VERSION);
    }

    @Override
    public boolean has(String variable) {
        return variables.containsKey(variable);
    }

    @Override
    public EnvironmentReplacerMiddleware.HttpSupplier<String> get(String key) {
        return variables.get(key);
    }

    @Override
    public Environment set(String name, EnvironmentReplacerMiddleware.HttpSupplier<String> value) {
        variables.put(name, value);
        return this;
    }
}
