package io.github.cputnama11y.conditionalentrypoints.test;

import net.fabricmc.api.ModInitializer;
import org.slf4j.LoggerFactory;

public class TestEntry1 implements ModInitializer {
    @Override
    public void onInitialize() {
        LoggerFactory.getLogger("test").info("Called TestEntry1");
    }
}
