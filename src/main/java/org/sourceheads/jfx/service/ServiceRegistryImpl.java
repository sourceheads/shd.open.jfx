package org.sourceheads.jfx.service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import javafx.fxml.FXMLLoader;

/**
 * (...)
 *
 * @author Stefan Fiedler
 */
public class ServiceRegistryImpl implements ServiceRegistry {

    private final Map<Class<?>, Object> services = new HashMap<>();

    //

    public ServiceRegistryImpl() {
        services.put(ServiceRegistry.class, this);
    }

    //

    @Override
    public <T> void register(final T service) {
        services.put(service.getClass(), service);
    }

    @Override
    public <T> void register(final Class<T> serviceClass, final T service) {
        services.put(serviceClass, service);
    }

    @Override
    public <T> Optional<T> get(final Class<T> serviceClass) {
        return Optional.ofNullable(services.get(serviceClass)).map(serviceClass::cast);
    }

    @Override
    public Object call(final Class<?> param) {
        try {
            final Object service = param.newInstance();
            wireAndInitializeService(service);
            return service;
        }
        catch (final InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void initialize() {
        services.forEach(ThrowingBiConsumer.wrap((k, v) -> wireService(v)));
        services.forEach(ThrowingBiConsumer.wrap((k, v) -> initializeService(v)));
    }

    @Override
    public <T> T loadFxml(final URL source) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(source);
            fxmlLoader.setControllerFactory(this);
            return fxmlLoader.load();
        }
        catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <T> T loadFxml(final URL source, final Supplier<?> serviceSupplier) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(source);
            fxmlLoader.setControllerFactory(clazz -> wireAndInitializeService(serviceSupplier.get()));
            return fxmlLoader.load();
        }
        catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    protected <T> T wireAndInitializeService(final T service) {
        try {
            wireService(service);
            initializeService(service);
            return service;
        }
        catch (final IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    protected <T> void wireService(final T service) throws IllegalAccessException {
        final Class<?> serviceClass = service.getClass();
        //noinspection Convert2streamapi
        for (final Field field : getAllFields(serviceClass)) {
            if (field.isAnnotationPresent(Autowired.class)) {
                field.setAccessible(true);
                final Object dependency = Objects.requireNonNull(services.get(field.getType()));
                field.set(service, dependency);
            }
        }
    }

    protected <T> void initializeService(final T service) throws InvocationTargetException, IllegalAccessException {
        final Class<?> serviceClass = service.getClass();
        final Method[] methods = serviceClass.getMethods();
        for (final Method method : methods) {
            if (method.isAnnotationPresent(Init.class)) {
                method.invoke(service);
            }
        }
    }

    protected List<Field> getAllFields(final Class<?> clazz) {
        final List<Field> fields = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            current = current.getSuperclass();
        }
        return fields;
    }
}
