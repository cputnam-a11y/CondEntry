package io.github.cputnama11y.conditionalentrypoints.impl.v1.model.condition.builtin;

import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;
import io.github.cputnama11y.conditionalentrypoints.impl.v1.model.condition.Condition;

import java.util.List;

import static io.github.cputnama11y.conditionalentrypoints.impl.v1.model.condition.DefaultConditions.modsLoaded;

public record AnyModsLoaded(List<String> mods) implements Condition {
    public static final MapCodec<AnyModsLoaded> CODEC = Codec.STRING.listOrSingle().fieldOf("mods").xmap(
            AnyModsLoaded::new,
            AnyModsLoaded::mods
    );

    @Override
    public MapCodec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context context) {
        return modsLoaded(mods, false);
    }
}
