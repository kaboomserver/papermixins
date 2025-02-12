package pw.kaboom.papermixins.mixin.execute_vanilla_only;

import com.mojang.brigadier.tree.RootCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.bukkit.BukkitBrigForwardingMap;
import io.papermc.paper.command.brigadier.bukkit.BukkitCommandNode;
import org.bukkit.command.Command;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pw.kaboom.papermixins.util.BrigadierConstants;

@Mixin(BukkitBrigForwardingMap.class)
public abstract class BukkitBrigForwardingMapMixin {
    @Inject(method = "put(Ljava/lang/String;Lorg/bukkit/command/Command;)Lorg/bukkit/command/Command;", at = @At("TAIL"))
    private void put(final String key, final Command value, final CallbackInfoReturnable<Command> cir) {
        if (!key.startsWith("minecraft:")) {
            return;
        }

        final String cmd = key.substring("minecraft:".length());
        final RootCommandNode<CommandSourceStack> root = BrigadierConstants.VANILLA_DISPATCHER_PAPER.getRoot();
        if (root.getChild(cmd) == null) {
            return;
        }

        root.removeCommand(cmd);
        root.addChild(BukkitCommandNode.of(cmd, value));
    }

    @Inject(method = "remove(Ljava/lang/Object;)Lorg/bukkit/command/Command;", at = @At(value = "INVOKE",
            target = "Lcom/mojang/brigadier/tree/RootCommandNode;removeCommand(Ljava/lang/String;)V"))
    private void remove$removeCommand(final Object key, final CallbackInfoReturnable<Command> cir) {
        final String string = (String) key; // instanceof check already performed for us
        final RootCommandNode<CommandSourceStack> root = BrigadierConstants.VANILLA_DISPATCHER_PAPER.getRoot();
        if (root.getChild(string) == null) {
            return;
        }

        root.removeCommand(string);
    }
}
