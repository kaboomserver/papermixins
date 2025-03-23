package pw.kaboom.papermixins.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class PaperMixinsConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("kaboom-paper-mixins");
    private final HashMap<String, Boolean> config = new HashMap<>();

    public PaperMixinsConfig(final File file) {
        final Properties properties = new Properties();

        try (final FileReader reader = new FileReader(file)) {
            properties.load(reader);
        } catch (final FileNotFoundException e) {
            return;
        } catch (final IOException e) {
            LOGGER.warn("Failed to read config:", e);
            return;
        }

        for (final Map.Entry<Object, Object> entry: properties.entrySet()) {
            final String key = (String)entry.getKey();
            final String value = (String)entry.getValue();

            final boolean parsedValue;
            switch (value) {
                case "true" -> parsedValue = true;
                case "false" -> parsedValue = false;
                default -> {
                    LOGGER.error("Failed to parse config key {} as boolean", key);
                    continue;
                }
            }

            this.config.put(key, parsedValue);
        }
    }

    public boolean canLoad(final String mixinClassName) {
        int lastSplit = mixinClassName.length();
        int nextSplit;

        while ((nextSplit = mixinClassName.lastIndexOf('.', lastSplit)) != -1) {
            final String key = mixinClassName.substring(0, nextSplit);
            final Boolean option = this.config.get(key);
            if (option != null) return option;

            lastSplit = nextSplit - 1;
        }

        return true;
    }
}
