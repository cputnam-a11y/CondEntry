package io.github.cputnama11y.conditionalentrypoints.impl.v1.utils;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public class NullableUtil {
    public static <T extends @Nullable Object, R extends @Nullable Object> R map(T value, Function<@NotNull T, R> mapper) {
        if (value == null) return null;
        return mapper.apply(value);
    }
}
