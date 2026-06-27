package io.github.cputnama11y.conditionalentrypoints.impl.v1.model.condition;

import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;

import java.util.List;

import static io.github.cputnama11y.conditionalentrypoints.impl.v1.model.condition.DefaultConditions.modsLoaded;

record NoModsLoaded(List<String> mods) implements Condition {
    public static final MapCodec<NoModsLoaded> CODEC = Codec.STRING.listOrSingle().fieldOf("mods").xmap(
            NoModsLoaded::new,
            NoModsLoaded::mods
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
