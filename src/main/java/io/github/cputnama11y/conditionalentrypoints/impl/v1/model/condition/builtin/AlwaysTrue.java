package io.github.cputnama11y.conditionalentrypoints.impl.v1.model.condition.builtin;

import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;
import io.github.cputnama11y.conditionalentrypoints.impl.v1.model.condition.Condition;

public record AlwaysTrue() implements Condition {
    public static final AlwaysTrue INSTANCE = new AlwaysTrue();
    public static final MapCodec<AlwaysTrue> CODEC = MapCodec.unit(AlwaysTrue.INSTANCE);

    @Override
    public MapCodec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context context) {
        return true;
    }
}