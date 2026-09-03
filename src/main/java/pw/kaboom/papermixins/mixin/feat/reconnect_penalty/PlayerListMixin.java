package pw.kaboom.papermixins.mixin.feat.reconnect_penalty;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pw.kaboom.papermixins.util.ReconnectPenalty.*;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Shadow
    @Final
    private List<ServerPlayer> players;

    @Inject(method = "tick", at = @At("HEAD"))
    private void beforeTick(final CallbackInfo ci) {
        final Set<InetAddress> incrementedAddresses = new HashSet<>();

        for (final ServerPlayer player : this.players) {
            final SocketAddress socketAddress = player.connection.getRemoteAddress();
            if (!(socketAddress instanceof final InetSocketAddress inetSocket)) continue;
            final InetAddress address = inetSocket.getAddress();
            if (!incrementedAddresses.add(address)) continue;
            PENALTY_MAP.compute(address, (_, v) -> {
                if (v == null) return null;
                final int n = v.penalty().decrementAndGet();
                return n == 0 ? null : v;
            });
        }

        final long now = System.currentTimeMillis();
        final var iterator = PENALTY_MAP.entrySet().iterator();

        while (iterator.hasNext()) {
            final var entry = iterator.next();
            if ((now - entry.getValue().touched().getAcquire()) < ENTRY_EXPIRY) continue;
            iterator.remove();
        }
    }
}
