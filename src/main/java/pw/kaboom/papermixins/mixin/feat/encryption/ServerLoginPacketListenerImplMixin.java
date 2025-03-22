package pw.kaboom.papermixins.mixin.feat.encryption;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.protocol.login.ClientboundHelloPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerImplMixin {
    @Shadow
    @Final
    MinecraftServer server;

    @WrapOperation(
            method = "handleHello",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;usesAuthentication()Z"
            ))
    private boolean handleHello$startClientVerification(final MinecraftServer instance, final Operation<Boolean> original) {
        return true;
    }

    @WrapOperation(
            method = "handleHello",
            at = @At(
                    value = "NEW",
                    target = "(Ljava/lang/String;[B[BZ)Lnet/minecraft/network/protocol/login/ClientboundHelloPacket;"
            ))
    private ClientboundHelloPacket handleHello$initHelloPacket(final String serverId, final byte[] publicKey,
                                                               final byte[] challenge, final boolean shouldAuthenticate,
                                                               final Operation<ClientboundHelloPacket> original) {
        return original.call(serverId, publicKey, challenge, this.server.usesAuthentication());
    }

    @Mixin(targets = "net/minecraft/server/network/ServerLoginPacketListenerImpl$1", remap = false)
    private abstract static class AuthenticationThreadMixin {
        @Shadow
        @Final
        ServerLoginPacketListenerImpl this$0;

        @WrapMethod(method = "run")
        private void runAuth(final Operation<Void> original) {
            final ServerLoginPacketListenerImplAccessor accessor = ((ServerLoginPacketListenerImplAccessor) this$0);
            if (accessor.getServer().usesAuthentication()) {
                original.call();
                return;
            }

            final String username = accessor.getRequestedUsername();
            if (username == null) throw new IllegalStateException("Missing username");
            accessor.invokeStartClientVerification(UUIDUtil.createOfflineProfile(username));
        }
    }
}
