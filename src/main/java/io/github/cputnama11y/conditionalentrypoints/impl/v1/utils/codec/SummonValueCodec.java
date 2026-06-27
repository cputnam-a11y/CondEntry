package io.github.cputnama11y.conditionalentrypoints.impl.v1.utils.codec;

import fish.cichlidmc.fishflakes.api.value.Result;
import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinycodecs.api.codec.dual.DualCodec;
import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;
import fish.cichlidmc.tinyjson.value.primitive.JsonNull;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.function.Supplier;

@NullMarked
public interface SummonValueCodec<T> {
    ScopedValue<T> value();

    Optional<Supplier<T>> defaultValue();

    record Map<T>(ScopedValue<T> value, Optional<Supplier<T>> defaultValue) implements SummonValueCodec<T>, MapCodec<T> {
        @Override
        public Result<T> decode(JsonObject json) {
            if (value.isBound()) return Result.success(value.get());
            if (defaultValue.isPresent()) return Result.success(defaultValue.orElseThrow().get());
            return Result.error("Unable to summon value");
        }

        @Override
        public Optional<String> encode(JsonObject json, T value) {
            return Optional.empty();
        }
    }

    record Value<T>(ScopedValue<T> value, Optional<Supplier<T>> defaultValue) implements SummonValueCodec<T>, Codec<T> {

        @Override
        public Result<T> decode(JsonValue json) {
            if (value.isBound()) return Result.success(value.get());
            if (defaultValue.isPresent()) return Result.success(defaultValue.orElseThrow().get());
            return Result.error("Unable to summon value");
        }

        @Override
        public Result<? extends JsonValue> encode(T value) {
            return Result.success(new JsonNull());
        }
    }

    static <T> DualCodec<T> of(ScopedValue<T> value) {
        return new DualCodec<>(new Value<>(value, Optional.empty()), new Map<>(value, Optional.empty()));
    }

    static <T> DualCodec<T> of(ScopedValue<T> value, T defaultValue) {
        return new DualCodec<>(new Value<>(value, Optional.of(() -> defaultValue)), new Map<>(value, Optional.of(() -> defaultValue)));
    }

    static <T> DualCodec<T> of(ScopedValue<T> value, Supplier<T> defaultValue) {
        return new DualCodec<>(new Value<>(value, Optional.of(defaultValue)), new Map<>(value, Optional.of(defaultValue)));
    }


}
