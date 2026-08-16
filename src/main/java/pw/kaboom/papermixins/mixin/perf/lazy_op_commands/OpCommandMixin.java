package pw.kaboom.papermixins.mixin.perf.lazy_op_commands;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.commands.OpCommand;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pw.kaboom.papermixins.duck.LazyCommandsExtension;

@Mixin(OpCommand.class)
public abstract class OpCommandMixin {
    @WrapOperation(method = "opPlayers",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;op(Lnet/minecraft/server/players/NameAndId;)V"))
    private static void opPlayers$op(final PlayerList instance, final NameAndId nameAndId, final Operation<Void> original) {
        ScopedValue.where(LazyCommandsExtension.LAZY_COMMANDS, null)
            .call(() -> original.call(instance, nameAndId));
    }
}
