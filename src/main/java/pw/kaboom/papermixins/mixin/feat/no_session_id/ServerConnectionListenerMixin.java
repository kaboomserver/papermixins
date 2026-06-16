package pw.kaboom.papermixins.mixin.feat.no_session_id;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.network.ServerConnectionListener;
import org.spongepowered.asm.mixin.Mixin;

import java.util.UUID;

@Mixin(ServerConnectionListener.class)
public abstract class ServerConnectionListenerMixin {
    @WrapMethod(method = "getSessionId")
    private UUID getSessionId(final Operation<UUID> original) {
        return UUID.randomUUID();
    }
}
