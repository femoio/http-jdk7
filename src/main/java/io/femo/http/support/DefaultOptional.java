package io.femo.http.support;

import java.util.*;

/**
 * Created by felix on 7/11/16.
 */
public class DefaultOptional<T> implements Optional<T> {

    private T obj;

    public DefaultOptional(T obj) {
        this.obj = obj;
    }

    public DefaultOptional() {
    }

    @Override
    public T get() {
        return obj;
    }

    @Override
    public boolean isPresent() {
        return obj != null;
    }
}
