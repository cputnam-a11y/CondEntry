package io.github.cputnama11y.conditionalentrypoints.impl.v1.model.condition;

import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;
import io.github.cputnama11y.conditionalentrypoints.impl.v1.utils.registry.Id;
import io.github.cputnama11y.conditionalentrypoints.impl.v1.utils.registry.SimpleRegistry;
import net.fabricmc.loader.api.FabricLoader;

import java.util.List;

public final class DefaultConditions {
    private DefaultConditions() {
    }

    public static void bootstrap(SimpleRegistry<MapCodec<? extends Condition>> registry) {
        registry.register(id("true"), AlwaysTrue.CODEC);
        registry.register(id("false"), AlwaysFalse.CODEC);
        registry.register(id("mods_loaded"), AllModsLoaded.CODEC);
        registry.register(id("no_mods_loaded"), NoModsLoaded.CODEC);
        registry.register(id("or"), AnyOfCondition.CODEC);
        registry.register(id("and"), AllOfCondition.CODEC);
    }

    private static Id id(String path) {
        return new Id("cond_entry", path);
    }

    static boolean modsLoaded(List<String> mods, boolean and) {
        for (String modId : mods)
            if (FabricLoader.getInstance().isModLoaded(modId) != and)
                return !and;
        return and;
    }

    static boolean conditionsMet(List<Condition> conditions, Condition.Context context, boolean and) {
        for (Condition condition : conditions)
            if (condition.test(context) != and)
                return !and;
        return and;
    }
}
