package io.github.cputnama11y.conditionalentrypoints.impl.v1.utils;

import java.util.function.Supplier;

public final class ExceptionUtils {
    private ExceptionUtils() {

    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> RuntimeException rethrowUnchecked(Throwable t) throws T {
        throw (T) t;
    }

    public static <T> Supplier<T> uncheckedSupplier(ThrowingSupplier<T> getter) {
        return () -> {
            try {
                return getter.get();
            } catch (Throwable t) {
                throw rethrowUnchecked(t);
            }
        };
    }

    public interface ThrowingSupplier<T> {
        T get() throws Throwable;
    }
}
