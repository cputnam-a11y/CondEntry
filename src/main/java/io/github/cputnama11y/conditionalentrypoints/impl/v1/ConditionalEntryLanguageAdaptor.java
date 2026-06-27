package io.github.cputnama11y.conditionalentrypoints.impl.v1;

import fish.cichlidmc.fishflakes.api.value.Lazy;
import io.github.cputnama11y.conditionalentrypoints.impl.v1.model.condition.Condition;
import io.github.cputnama11y.conditionalentrypoints.impl.v1.model.condition.DefaultConditions;
import io.github.cputnama11y.conditionalentrypoints.impl.v1.model.entrypoint.EntrypointWithAdaptor;
import io.github.cputnama11y.conditionalentrypoints.impl.v1.model.entrypoint.EntrypointWithConditions;
import net.fabricmc.loader.api.LanguageAdapter;
import net.fabricmc.loader.api.LanguageAdapterException;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.impl.FabricLoaderImpl;

import java.lang.invoke.MethodHandles;
import java.util.Map;

import static io.github.cputnama11y.conditionalentrypoints.impl.v1.utils.CustomValueUtil.toTinyJson;
import static io.github.cputnama11y.conditionalentrypoints.impl.v1.utils.ExceptionUtils.uncheckedSupplier;
import static io.github.cputnama11y.conditionalentrypoints.impl.v1.utils.NullableUtil.map;

public class ConditionalEntryLanguageAdaptor implements LanguageAdapter {
    private static final Condition.Context CONTEXT = new Condition.Context() {
    };
    private static final Lazy<AdapterGetter> ADAPTERS = Lazy.of(uncheckedSupplier(AdapterGetter::new));
    @Override
    public <T> T create(ModContainer mod, String value, Class<T> type) throws LanguageAdapterException {
        var index = value.lastIndexOf("//:");
        String delegated;
        String id;
        if (index != -1) {
            delegated = value.substring(0, index);
            id = value.substring(index);
        } else {
            delegated = id = value;
        }
        var entrypoint = getEntrypoint(mod, id, delegated);
        return ADAPTERS.get().get(entrypoint.adaptor()).create(mod, entrypoint.value(), type);
    }

    private static EntrypointWithAdaptor getEntrypoint(ModContainer container, String conditionId, String value) {
        var unparsedMeta = map(
                map(
                        container.getMetadata().getCustomValue("cond-entry:v1"),
                        root -> root.getAsObject().get("metadata")
                ),
                meta -> meta.getAsObject().get(conditionId)
        );
        if (unparsedMeta != null) {
            var parsed = EntrypointWithConditions.codec(value).codec().decode(toTinyJson(unparsedMeta));
            return parsed.valueOrThrow().evaluate(CONTEXT);
        }
        return new EntrypointWithAdaptor(value, "default");
    }

    static {
        DefaultConditions.bootstrap(Condition.REGISTRY);
    }

    record AdapterGetter(Map<String, LanguageAdapter> adapters) {
        @SuppressWarnings("unchecked")
        public AdapterGetter() throws LanguageAdapterException {
            Map<String, LanguageAdapter> map;
            try {
                var handle = MethodHandles.privateLookupIn(FabricLoaderImpl.class, MethodHandles.lookup()).findGetter(FabricLoaderImpl.class, "adapterMap", Map.class);
                map = (Map<String, LanguageAdapter>) handle.invokeExact(FabricLoaderImpl.INSTANCE);
            } catch (Throwable e) {
                throw new LanguageAdapterException(e);
            }
            this(map);
        }

        LanguageAdapter get(String name) throws LanguageAdapterException {
            var adapter = adapters.get(name);
            if (adapter == null) throw new LanguageAdapterException("failed to find adapter: " + name);
            return adapter;
        }
    }
}
