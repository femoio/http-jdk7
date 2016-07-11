package io.femo.http.support;

/**
 * Created by felix on 7/11/16.
 */
public class ValueSupplier<T> implements Supplier<T> {

    private T value;

    public ValueSupplier(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }
}
