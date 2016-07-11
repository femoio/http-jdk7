package io.femo.http.support;

/**
 * Created by felix on 7/11/16.
 */
public interface Optional<T> {

    T get();
    boolean isPresent();
}
