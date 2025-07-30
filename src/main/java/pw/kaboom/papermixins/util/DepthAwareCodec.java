package pw.kaboom.papermixins.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

public final class DepthAwareCodec<T> implements Codec<T> {
    private final ThreadLocal<Integer> depth = ThreadLocal.withInitial(() -> 0);

    private final Codec<T> codec;
    private final int maxDepth;

    public DepthAwareCodec(final Codec<T> codec, final int maxDepth) {
        this.codec = codec;
        this.maxDepth = maxDepth;
    }

    @Override
    public <S> DataResult<S> encode(final T input, final DynamicOps<S> ops, final S prefix) {
        return codec.encode(input, ops, prefix);
    }

    @Override
    public <S> DataResult<Pair<T, S>> decode(final DynamicOps<S> ops, final S input) {
        final int newDepth = depth.get() + 1;
        if (newDepth > this.maxDepth) return DataResult.error(() -> "Depth limit exceeded");
        depth.set(newDepth);

        try {
            return codec.decode(ops, input);
        } finally {
            depth.set(depth.get() - 1);
        }
    }

    @Override
    public String toString() {
        return codec.toString();
    }
}