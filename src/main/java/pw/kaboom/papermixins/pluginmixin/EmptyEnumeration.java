package pw.kaboom.papermixins.pluginmixin;

import java.util.Enumeration;

public final class EmptyEnumeration implements Enumeration<Object> {
    private static final EmptyEnumeration INSTANCE = new EmptyEnumeration();

    private EmptyEnumeration() {

    }

    @Override
    public boolean hasMoreElements() {
        return false;
    }

    @Override
    public Object nextElement() {
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> Enumeration<T> empty() {
        return (Enumeration<T>) INSTANCE;
    }
}
