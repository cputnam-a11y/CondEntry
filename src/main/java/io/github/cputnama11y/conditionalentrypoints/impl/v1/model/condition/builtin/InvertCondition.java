package io.github.cputnama11y.conditionalentrypoints.impl.v1.model.condition.builtin;

import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;
import io.github.cputnama11y.conditionalentrypoints.impl.v1.model.condition.Condition;

public record InvertCondition(Condition condition) implements Condition {
    public static final MapCodec<InvertCondition> CODEC = Condition.CODEC.fieldOf("value").xmap(
            InvertCondition::new,
            InvertCondition::condition
    );
    @Override
    public MapCodec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context context) {
        return !condition.test(context);
    }
}
