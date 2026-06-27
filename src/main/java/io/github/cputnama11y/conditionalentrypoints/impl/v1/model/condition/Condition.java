package io.github.cputnama11y.conditionalentrypoints.impl.v1.model.condition;

import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;
import io.github.cputnama11y.conditionalentrypoints.impl.v1.utils.registry.SimpleRegistry;
import org.jetbrains.annotations.ApiStatus;

public interface Condition {
        SimpleRegistry<MapCodec<? extends Condition>> REGISTRY = SimpleRegistry.create("cond_entry");

        Codec<Condition> CODEC = Codec.codecDispatch(Condition.REGISTRY.byIdCodec(), Condition::codec);
        MapCodec<? extends Condition> codec();

        boolean test(Context context);

        @ApiStatus.NonExtendable
        interface Context {

        }
    }