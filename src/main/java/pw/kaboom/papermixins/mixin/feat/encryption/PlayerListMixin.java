package pw.kaboom.papermixins.mixin.feat.encryption;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    // This value doesn't actually control whether authentication is performed, only whether player heads are shown on
    // the tab menu.
    @WrapOperation(method = "placeNewPlayer",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;usesAuthentication()Z"))
    private static boolean placeNewPlayer$usesAuthentication(final MinecraftServer instance,
                                                             final Operation<Boolean> original) {
        return true;
    }
}
