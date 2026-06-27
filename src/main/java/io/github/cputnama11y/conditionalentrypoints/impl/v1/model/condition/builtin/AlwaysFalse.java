package io.github.cputnama11y.conditionalentrypoints.impl.v1.model.condition.builtin;

import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;
import io.github.cputnama11y.conditionalentrypoints.impl.v1.model.condition.Condition;

public record AlwaysFalse() implements Condition {
    public static final AlwaysFalse INSTANCE = new AlwaysFalse();
    public static final MapCodec<AlwaysFalse> CODEC = MapCodec.unit(AlwaysFalse.INSTANCE);

    @Override
    public MapCodec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context context) {
        return false;
    }
}