package io.github.cputnama11y.conditionalentrypoints.impl.v1.model.entrypoint;

import fish.cichlidmc.fishflakes.api.value.Result;
import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinycodecs.api.codec.CompositeCodec;
import fish.cichlidmc.tinycodecs.api.codec.dual.DualCodec;
import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;
import io.github.cputnama11y.conditionalentrypoints.impl.v1.model.condition.builtin.AllOfCondition;
import io.github.cputnama11y.conditionalentrypoints.impl.v1.model.condition.builtin.AlwaysTrue;
import io.github.cputnama11y.conditionalentrypoints.impl.v1.utils.codec.CodecUtil;
import io.github.cputnama11y.conditionalentrypoints.impl.v1.model.condition.Condition;

import java.util.List;
import java.util.Optional;

public record EntrypointWithConditions(EntrypointWithAdaptor entrypoint, Optional<Else> _else) {
    private static final DualCodec<EntrypointWithConditions> CODEC = CompositeCodec.of(
            EntrypointWithAdaptor.CODEC.mapCodec(),
            EntrypointWithConditions::entrypoint,
            CodecUtil.optionalMapCodec(Else.CODEC.mapCodec()),
            EntrypointWithConditions::_else,
            EntrypointWithConditions::new
    );
    static final ScopedValue<String> TOP_LEVEL_ENTRYPOINT = ScopedValue.newInstance();
    private static final DualCodec<EntrypointWithConditions> TOP_LEVEL_CODEC = DualCodec.lazy(() -> CompositeCodec.of(
            EntrypointWithAdaptor.TOP_LEVEL_CODEC.mapCodec(),
            EntrypointWithConditions::entrypoint,
            CodecUtil.optionalMapCodec(Else.CODEC.mapCodec()),
            EntrypointWithConditions::_else,
            EntrypointWithConditions::new
    ));

    public EntrypointWithConditions(String value) {
        this(new EntrypointWithAdaptor(value, "default"), Optional.empty());
    }

    public EntrypointWithConditions(String value, String adaptor) {
        this(new EntrypointWithAdaptor(value, adaptor), Optional.empty());
    }

    public EntrypointWithConditions(String value, String adaptor, Else _else) {
        this(new EntrypointWithAdaptor(value, adaptor), Optional.of(_else));
    }

    public EntrypointWithAdaptor evaluate(Condition.Context context) {
        if (this._else().isPresent() && !this._else().orElseThrow().condition().test(context))
            return this._else().orElseThrow()._else().evaluate(context);
        return this.entrypoint();
    }

    public static DualCodec<EntrypointWithConditions> codec(String topLevelEntrypoint) {
        return new DualCodec<>(
                Codec.of(
                        value ->
                                ScopedValue.where(TOP_LEVEL_ENTRYPOINT, topLevelEntrypoint)
                                        .call(() -> TOP_LEVEL_CODEC.codec().encode(value)),
                        (json) ->
                                ScopedValue.where(TOP_LEVEL_ENTRYPOINT, topLevelEntrypoint)
                                        .call(() -> TOP_LEVEL_CODEC.codec().decode(json))
                ),
                MapCodec.of(
                        (json, value) -> ScopedValue.where(TOP_LEVEL_ENTRYPOINT, topLevelEntrypoint)
                                .call(() -> TOP_LEVEL_CODEC.mapCodec().encode(json, value)),
                        json -> ScopedValue.where(TOP_LEVEL_ENTRYPOINT, topLevelEntrypoint)
                                .call(() -> TOP_LEVEL_CODEC.mapCodec().decode(json))
                )
        );
    }

    public record Else(AllOfCondition condition, EntrypointWithConditions _else) {
        public Else(Condition condition, EntrypointWithConditions _else) {
            this(new AllOfCondition(List.of(condition)), _else);
        }
        public static final DualCodec<Else> CODEC = DualCodec.lazy(() -> CompositeCodec.of(
                Condition.CODEC.listOrSingle().optional(List.of(AlwaysTrue.INSTANCE)).xmap(
                        AllOfCondition::new,
                        AllOfCondition::conditions
                ).fieldOf("conditions"),
                Else::condition,
                EntrypointWithConditions.CODEC.codec().withAlternative(Codec.STRING.validate(s -> {
                    if (!s.startsWith("default")) {
                        return Result.error("Excepted: \"default:<default_value>\" | \"default\"");
                    }
                    return Result.success(s);
                }).xmap(
                        value -> new EntrypointWithConditions(value, "cond_entry:v1:noop"),
                        entrypoint -> entrypoint.entrypoint().value()
                )).withAlternative(Codec.STRING.xmap(
                        value -> new EntrypointWithConditions(value, "default"),
                        entrypoint -> entrypoint.entrypoint().value()
                )).fieldOf("else"),
                Else::_else,
                Else::new
        ));
    }



}
