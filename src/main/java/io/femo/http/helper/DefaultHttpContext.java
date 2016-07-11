package io.femo.http.helper;

import io.femo.http.*;
import io.femo.http.drivers.DefaultBase64Driver;
import io.femo.http.drivers.DefaultEnvironment;
import io.femo.http.drivers.DefaultMimeService;
import io.femo.http.support.DefaultOptional;
import io.femo.http.support.Optional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by felix on 6/8/16.
 */
public class DefaultHttpContext implements HttpContext {

    private List<Driver> drivers;

    public DefaultHttpContext() {
        this.drivers = new ArrayList<>();
    }

    @Override
    public Base64Driver base64() {
        Optional<Base64Driver> driver = getFirstDriver(Base64Driver.class);
        if(driver.isPresent()) {
            return driver.get();
        } else {
            Base64Driver base64Driver = new DefaultBase64Driver();
            useDriver(base64Driver);
            return base64Driver;
        }
    }

    private <T extends Driver> Optional<T> getFirstDriver(Class<T> type) {
        for (Driver driver :
                drivers) {
            if (type.isAssignableFrom(driver.getClass())) {
                return new DefaultOptional<>(type.cast(driver));
            }
        }
        return new DefaultOptional<>();
    }

    @Override
    public void useDriver(Driver driver) {
        this.drivers.add(driver);
    }

    @Override
    public MimeService mime() {
        Optional<MimeService> service = getFirstDriver(MimeService.class);
        if(service.isPresent()) {
            return service.get();
        } else {
            MimeService mimeService = new DefaultMimeService();
            useDriver(mimeService);
            return mimeService;
        }
    }

    @Override
    public Environment environment() {
        Optional<Environment> driver = getFirstDriver(Environment.class);
        if(driver.isPresent()) {
            return driver.get();
        } else {
            Environment environment = new DefaultEnvironment();
            useDriver(environment);
            return environment;
        }
    }
}
