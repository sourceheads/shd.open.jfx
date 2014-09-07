package org.sourceheads.jfx.service;

import java.util.function.BiConsumer;

/**
 * (...)
 *
 * @author Stefan Fiedler
 */
public interface ThrowingBiConsumer<T, U> {

    public void accept(T t, U u) throws Exception;

    public static <T, U> BiConsumer<T, U> wrap(final ThrowingBiConsumer<T, U> throwingBiConsumer) {
        return (t, u) -> {
            try {
                throwingBiConsumer.accept(t, u);
            }
            catch (Exception e) {
                throw new IllegalStateException(e);
            }
        };
    }
}
