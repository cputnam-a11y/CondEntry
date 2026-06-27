package io.github.cputnama11y.conditionalentrypoints.impl.v1.model.condition;

import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;
import net.fabricmc.loader.api.FabricLoader;

import java.util.List;

import static io.github.cputnama11y.conditionalentrypoints.impl.v1.model.condition.DefaultConditions.modsLoaded;

public record AllModsLoaded(List<String> mods) implements Condition {
    public static final MapCodec<AllModsLoaded> CODEC = Codec.STRING.listOrSingle().fieldOf("mods").xmap(
            AllModsLoaded::new,
            AllModsLoaded::mods
    );

    @Override
    public MapCodec<? extends Condition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context context) {
        return modsLoaded(mods, true);
    }
}
