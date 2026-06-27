package io.github.cputnama11y.conditionalentrypoints.impl.v1.model.condition.builtin;

import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;
import io.github.cputnama11y.conditionalentrypoints.impl.v1.model.condition.Condition;

import java.util.List;

import static io.github.cputnama11y.conditionalentrypoints.impl.v1.model.condition.DefaultConditions.conditionsMet;

public record AllOfCondition(List<Condition> conditions) implements Condition {
    public static final MapCodec<AllOfCondition> CODEC = Condition.CODEC.listOrSingle().fieldOf("values").xmap(
            AllOfCondition::new,
            AllOfCondition::conditions
    );
    @Override
    public MapCodec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context context) {
        return conditionsMet(conditions, context, true);
    }
}
