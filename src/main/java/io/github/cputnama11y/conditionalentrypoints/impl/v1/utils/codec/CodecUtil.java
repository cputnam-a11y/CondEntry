package io.github.cputnama11y.conditionalentrypoints.impl.v1.utils.codec;

import fish.cichlidmc.fishflakes.api.value.Either;
import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;

import java.util.Optional;

public class CodecUtil {
    public static <T> MapCodec<Optional<T>> optionalMapCodec(MapCodec<T> codec) {
        return EitherMapCodec.lenient(codec.xmap(
                Optional::of,
                Optional::orElseThrow
        ), MapCodec.unit(Optional.<T>empty())).xmap(
                Either::join,
                o -> o.isPresent() ? Either.left(o) : Either.right(o)
        );
    }
}
