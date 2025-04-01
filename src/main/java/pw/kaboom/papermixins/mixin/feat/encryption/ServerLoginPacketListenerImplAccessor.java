package pw.kaboom.papermixins.mixin.feat.encryption;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerLoginPacketListenerImpl.class)
public interface ServerLoginPacketListenerImplAccessor {
    @Accessor
    MinecraftServer getServer();

    @Accessor
    String getRequestedUsername();

    @Invoker
    void invokeStartClientVerification(final GameProfile authenticatedProfile);
}