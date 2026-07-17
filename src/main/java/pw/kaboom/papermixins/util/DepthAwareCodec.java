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
        return this.codec.encode(input, ops, prefix);
    }

    @Override
    public <S> DataResult<Pair<T, S>> decode(final DynamicOps<S> ops, final S input) {
        final int newDepth = this.depth.get() + 1;
        if (newDepth > this.maxDepth) return DataResult.error(() -> "Depth limit exceeded");
        this.depth.set(newDepth);

        try {
            return this.codec.decode(ops, input);
        } finally {
            this.depth.set(this.depth.get() - 1);
        }
    }

    @Override
    public String toString() {
        return this.codec.toString();
    }
}
