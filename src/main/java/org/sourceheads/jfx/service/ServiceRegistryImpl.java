package org.sourceheads.jfx.service;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javafx.application.Platform;
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
            final Object controller = param.newInstance();
            register(controller);
            wireAndInitializeService(controller);
            return controller;
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
    public void preShow() {
        services.forEach(ThrowingBiConsumer.wrap((k, v) -> invoke(v, PreShow.class)));
    }

    @Override
    public void postShow() {
        services.forEach(ThrowingBiConsumer.wrap((k, v) -> invoke(v, PostShow.class)));

        new Thread(() -> Platform.runLater(() ->
                services.forEach(ThrowingBiConsumer.wrap((k, v) -> invoke(v, PostShowDelayed.class))))).start();
    }

    @Override
    public void onCloseRequest() {
        services.forEach(ThrowingBiConsumer.wrap((k, v) -> invoke(v, OnCloseRequest.class)));
    }

    @Override
    public void stop() {
        services.forEach(ThrowingBiConsumer.wrap((k, v) -> invoke(v, Stop.class)));
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
    public <T> T loadFxml(final URL source, final Supplier<?> controllerSupplier) {
        try {
            final Object controller = controllerSupplier.get();
            register(controller);
            wireAndInitializeService(controller);

            final FXMLLoader fxmlLoader = new FXMLLoader(source);
            fxmlLoader.setControllerFactory(clazz -> controller);
            return fxmlLoader.load();
        }
        catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    protected <T> void wireAndInitializeService(final T service) {
        try {
            wireService(service);
            initializeService(service);
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
                final Object dependency = resolveDependency(field);
                field.set(service, Objects.requireNonNull(dependency));
            }
        }
    }

    protected Object resolveDependency(final Field field) {
        final Class<?> type = field.getType();
        if (Collection.class.isAssignableFrom(type)) {
            final Class<?> collectionType = getCollectionType(field);
            return findServices(collectionType);
        }

        return services.get(type);
    }

    protected Class<?> getCollectionType(final Field field) {
        final Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            final Type[] typeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
            return (Class<?>) typeArguments[0];
        }
        throw new IllegalStateException("Failed to get collection type: field=" + field);
    }

    protected <T> Collection<T> findServices(final Class<T> clazz) {
        return services.entrySet().stream()
                .filter(e -> clazz.isAssignableFrom(e.getKey()))
                .map(Map.Entry::getValue)
                .map(clazz::cast)
                .collect(Collectors.toList());
    }

    protected <T> void initializeService(final T service) throws InvocationTargetException, IllegalAccessException {
        invoke(service, Init.class);
    }

    protected <T> void invoke(final T service, final Class<? extends Annotation> annotationClass)
            throws InvocationTargetException, IllegalAccessException {
        final Class<?> serviceClass = service.getClass();
        final Method[] methods = serviceClass.getMethods();
        for (final Method method : methods) {
            if (method.isAnnotationPresent(annotationClass)) {
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
