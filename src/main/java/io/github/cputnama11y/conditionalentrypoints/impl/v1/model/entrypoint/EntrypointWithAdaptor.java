package io.github.cputnama11y.conditionalentrypoints.impl.v1.model.entrypoint;

import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinycodecs.api.codec.CompositeCodec;
import fish.cichlidmc.tinycodecs.api.codec.dual.DualCodec;
import io.github.cputnama11y.conditionalentrypoints.impl.v1.utils.codec.SummonValueCodec;

public record EntrypointWithAdaptor(String value, String adaptor) {
    public static final DualCodec<EntrypointWithAdaptor> CODEC = CompositeCodec.of(
            Codec.STRING.fieldOf("value"),
            EntrypointWithAdaptor::value,
            Codec.STRING.optional("default").fieldOf("adaptor"),
            EntrypointWithAdaptor::adaptor,
            EntrypointWithAdaptor::new
    );
    static final DualCodec<EntrypointWithAdaptor> TOP_LEVEL_CODEC = CompositeCodec.of(
            SummonValueCodec.of(() -> EntrypointWithConditions.TOP_LEVEL_ENTRYPOINT).mapCodec(),
            EntrypointWithAdaptor::value,
            Codec.STRING.optional("default").fieldOf("adaptor"),
            EntrypointWithAdaptor::adaptor,
            EntrypointWithAdaptor::new
    );
}
