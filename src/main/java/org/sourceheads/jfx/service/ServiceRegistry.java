package org.sourceheads.jfx.service;

import java.net.URL;
import java.util.Optional;
import java.util.function.Supplier;

import javafx.util.Callback;

/**
 * (...)
 *
 * @author Stefan Fiedler
 */
public interface ServiceRegistry extends Callback<Class<?>, Object> {

    public <T> void register(T service);

    public <T> void register(Class<T> serviceClass, T service);

    public <T> Optional<T> get(Class<T> serviceClass);

    public void initialize();

    public void preShow();

    public void onCloseRequest();

    public void stop();

    public <T> T loadFxml(URL source);

    public <T> T loadFxml(URL source, Supplier<?> serviceSupplier);
}
