package pw.kaboom.papermixins.mixin.perf.lazy_op_commands;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.commands.DeOpCommands;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pw.kaboom.papermixins.duck.LazyCommandsExtension;

@Mixin(DeOpCommands.class)
public abstract class DeOpCommandsMixin {
    @WrapOperation(method = "deopPlayers",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;deop(Lnet/minecraft/server/players/NameAndId;)V"))
    private static void opPlayers$op(final PlayerList instance, final NameAndId nameAndId, final Operation<Void> original) {
        ScopedValue.where(LazyCommandsExtension.LAZY_COMMANDS, null)
            .call(() -> original.call(instance, nameAndId));
    }
}
