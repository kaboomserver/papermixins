package pw.kaboom.papermixins.pluginmixin.bootstrap;

import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class SimpleMixinPropertyService implements IGlobalPropertyService {
    private final Map<String, Object> properties = new ConcurrentHashMap<>();

    @Override
    public IPropertyKey resolveKey(final String name) {
        return new BlackBoardKey(name);
    }

    @Override
    public String getPropertyString(final IPropertyKey key, final String defaultValue) {
        return this.getProperty(key, defaultValue);
    }

    @Override
    public <T> T getProperty(final IPropertyKey key) {
        return this.getProperty(key, null);
    }

    @Override
    public <T> T getProperty(final IPropertyKey key, final T defaultValue) {
        //noinspection unchecked
        return (T) this.properties.getOrDefault(BlackBoardKey.resolve(key), defaultValue);
    }

    @Override
    public void setProperty(final IPropertyKey key, final Object value) {
        this.properties.put(BlackBoardKey.resolve(key), value);
    }

    private record BlackBoardKey(String key) implements IPropertyKey {
        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof BlackBoardKey(final String otherKey))) return false;

            return Objects.equals(key, otherKey);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key);
        }

        @Override
        public String toString() {
            return this.key;
        }

        public static String resolve(final IPropertyKey key) {
            return ((BlackBoardKey) key).key;
        }
    }
}
