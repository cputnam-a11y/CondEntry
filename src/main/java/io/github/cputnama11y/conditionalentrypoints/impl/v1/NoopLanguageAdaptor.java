package io.github.cputnama11y.conditionalentrypoints.impl.v1;

import net.fabricmc.loader.api.LanguageAdapter;
import net.fabricmc.loader.api.LanguageAdapterException;
import net.fabricmc.loader.api.ModContainer;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;


public class NoopLanguageAdaptor implements LanguageAdapter {
    @Override
    public <T> T create(ModContainer mod, String value, Class<T> type) throws LanguageAdapterException {
        if (!type.isInterface())
            throw new LanguageAdapterException(new IllegalArgumentException("NoopLanguageAdaptor only supports interface entrypoint classes"));
        var m = findSam(type);
        MethodHandle mh = MethodHandles.dropArguments(makeReturnHandle(value, m.getReturnType()), 0, m.getParameterTypes());
        return MethodHandleProxies.asInterfaceInstance(type, mh);
    }

    private static final Map<Class<?>, Function<String, Object>> PARSERS = new HashMap<>(Map.<Class<?>, Function<String, Object>>ofEntries(
            Map.entry(String.class, value -> {
                if (value.startsWith("\"") && value.endsWith("\"")) return value.substring(1, value.length() - 1);
                throw new IllegalArgumentException("Value is not a string");
            }),
            Map.entry(Character.class, value -> {
                if (value.startsWith("'") && value.endsWith("'")) {
                    var chars = value.substring(1, value.length() - 1).toCharArray();
                    if (chars.length == 1) return chars[0];
                }
                throw new IllegalArgumentException("Value is not a char");
            }),
            Map.entry(Integer.class, value -> {
                try {
                    return Integer.parseInt(value);
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }),
            Map.entry(Long.class, value -> {
                try {
                    return Long.parseLong(value);
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }),
            Map.entry(Byte.class, value -> {
                try {
                    return Byte.parseByte(value);
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }),
            Map.entry(Short.class, value -> {
                try {
                    return Short.parseShort(value);
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }),
            Map.entry(Float.class, value -> {
                try {
                    return Short.parseShort(value);
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }),
            Map.entry(Double.class, value -> {
                try {
                    return Short.parseShort(value);
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            })
    ));

    static {
        PARSERS.put(long.class, PARSERS.get(Long.class));
        PARSERS.put(int.class, PARSERS.get(Integer.class));
        PARSERS.put(short.class, PARSERS.get(Short.class));
        PARSERS.put(byte.class, PARSERS.get(Byte.class));
        PARSERS.put(char.class, PARSERS.get(Character.class));
        PARSERS.put(float.class, PARSERS.get(Float.class));
        PARSERS.put(double.class, PARSERS.get(Double.class));
    }

    private MethodHandle makeReturnHandle(String value, Class<?> returnType) {
        if (Objects.equals(value, "default")) return MethodHandles.zero(returnType);
        if (!value.startsWith("default:")) throw new IllegalArgumentException("Excepted: \"default:<default_value>\"");
        value = value.substring(9);
        var parser = PARSERS.get(returnType);
        if (parser == null) throw new IllegalArgumentException("No Default Value Expected for " + returnType);
        return MethodHandles.constant(returnType, parser.apply(value));
    }

    private Method findSam(Class<?> intfc) throws LanguageAdapterException {
        try {
            Method uniqueMethod = null;
            for (Method m : intfc.getMethods()) {
                if (!Modifier.isAbstract(m.getModifiers()))
                    continue;

                if (isObjectMethod(m))
                    continue;

                // ensure it's SAM interface
                String methodName = m.getName();
                if (uniqueMethod == null) {
                    uniqueMethod = m;
                } else if (!uniqueMethod.getName().equals(methodName)) {
                    // too many abstract methods
                    throw new IllegalArgumentException("not a single-method interface" + ":" + intfc.getName());
                }
            }

            if (uniqueMethod == null)
                throw new IllegalArgumentException("no method in " + ":" + intfc.getName());
            return uniqueMethod;
        } catch (Exception e) {
            throw new LanguageAdapterException(e);
        }
    }

    private static boolean isObjectMethod(Method m) {
        return switch (m.getName()) {
            case "toString" -> m.getReturnType() == String.class
                    && m.getParameterCount() == 0;
            case "hashCode" -> m.getReturnType() == int.class
                    && m.getParameterCount() == 0;
            case "equals" -> m.getReturnType() == boolean.class
                    && m.getParameterCount() == 1
                    && m.getParameterTypes()[0] == Object.class;
            default -> false;
        };
    }
}
