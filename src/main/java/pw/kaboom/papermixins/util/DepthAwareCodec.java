package pw.kaboom.papermixins.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

public final class DepthAwareCodec<T> implements Codec<T> {
    private static final ScopedValue<Integer> DEPTH = ScopedValue.newInstance();

    private final Codec<T> codec;
    private final int maxDepth;

    public DepthAwareCodec(final Codec<T> codec, final int maxDepth) {
        this.codec = codec;
        this.maxDepth = maxDepth;
    }

    @Override
    public <S> DataResult<S> encode(final T input, final DynamicOps<S> ops, final S prefix) {
        final int newDepth = DEPTH.orElse(0) + 1;
        if (newDepth >= this.maxDepth) return DataResult.error(() -> "Depth limit exceeded");

        return ScopedValue.where(DEPTH, newDepth)
            .call(() -> this.codec.encode(input, ops, prefix));
    }

    @Override
    public <S> DataResult<Pair<T, S>> decode(final DynamicOps<S> ops, final S input) {
        final int newDepth = DEPTH.orElse(0) + 1;
        if (newDepth >= this.maxDepth) return DataResult.error(() -> "Depth limit exceeded");

        return ScopedValue.where(DEPTH, newDepth)
            .call(() -> this.codec.decode(ops, input));
    }

    @Override
    public String toString() {
        return this.codec.toString();
    }
}
