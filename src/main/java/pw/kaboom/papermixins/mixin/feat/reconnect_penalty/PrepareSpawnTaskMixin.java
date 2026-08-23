package pw.kaboom.papermixins.mixin.feat.reconnect_penalty;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.config.PrepareSpawnTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.kaboom.papermixins.util.ReconnectPenalty;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static pw.kaboom.papermixins.util.ReconnectPenalty.PENALTY_MAP;

@Mixin(PrepareSpawnTask.class)
public abstract class PrepareSpawnTaskMixin {
    @Unique
    private static final double PENALTY_MULTIPLIER = 1.5;
    @Unique
    private long papermixins$penalty;
    @Unique
    private long papermixins$lastTick;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void afterInit(final MinecraftServer server,
                           final GameProfile profile,
                           final ServerConfigurationPacketListenerImpl listener, final CallbackInfo ci) {
        // Will be zero if the server is behind a frontend like Velocity or Bungeecord, or the connection-throttle is disabled
        if (ReconnectPenalty.BASE_PENALTY == 0) return;

        final SocketAddress remoteAddress = listener.connection.getRemoteAddress();
        if (!(remoteAddress instanceof final InetSocketAddress inetSocket)
            || inetSocket.isUnresolved()
            || inetSocket.getAddress().isLoopbackAddress()) return;
        final ReconnectPenalty.Penalty defaultPenalty = new ReconnectPenalty.Penalty(
            new AtomicInteger(ReconnectPenalty.BASE_PENALTY),
            new AtomicLong(System.currentTimeMillis())
        );
        final ReconnectPenalty.Penalty mapPenalty = PENALTY_MAP.computeIfAbsent(inetSocket.getAddress(), _ -> defaultPenalty);

        mapPenalty.touched().setRelease(System.currentTimeMillis());

        this.papermixins$lastTick = System.currentTimeMillis();
        this.papermixins$penalty = mapPenalty == defaultPenalty
            ? 0
            : mapPenalty.penalty().getAndUpdate(penalty -> Math.min(ReconnectPenalty.MAX_PENALTY, (int) (penalty * PENALTY_MULTIPLIER)));
    }

    @WrapMethod(method = "tick")
    private boolean wrapTick(final Operation<Boolean> original) {
        if (this.papermixins$penalty <= 0) return original.call();
        final long now = System.currentTimeMillis();
        this.papermixins$penalty -= ((now - this.papermixins$lastTick) / 50);
        this.papermixins$lastTick = now;
        return false;
    }
}
