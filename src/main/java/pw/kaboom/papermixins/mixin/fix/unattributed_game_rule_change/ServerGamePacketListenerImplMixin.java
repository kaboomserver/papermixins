package pw.kaboom.papermixins.mixin.fix.unattributed_game_rule_change;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.gamerules.GameRule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @Inject(method = "broadcastGameRuleChangeToOperators",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getPlayerList()" +
            "Lnet/minecraft/server/players/PlayerList;"), cancellable = true)
    private<T> void broadcastGameRuleChangeToOperators$getPlayerList(final GameRule<T> rule, final T value, final CallbackInfo ci,
                                                                     final @Local(name = "message") Component message) {
        this.player.createCommandSourceStack().sendSuccess(() -> message, true);
        ci.cancel();
    }
}
