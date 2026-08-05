package pw.kaboom.papermixins.mixin.fix.commands_yml_redirects;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.PaperBrigadier;
import io.papermc.paper.command.brigadier.ShadowBrigNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.command.VanillaCommandWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(SimpleCommandMap.class)
public abstract class SimpleCommandMapMixin implements CommandMap {
    @WrapOperation(method = "registerServerAliases",
        at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private <K, V> V registerServerAliases$put(final Map<K, V> instance, final K k, final V v, final Operation<V> original,
                                               final @Local(name = "commandStrings") String[] commandStrings) {
        if (commandStrings.length != 1) return original.call(instance, k, v);

        final String[] targetArgs = commandStrings[0].split(" ");
        if (targetArgs.length != 2 || !targetArgs[1].equals("$1-")) return original.call(instance, k, v);

        final Command target = this.getCommand(targetArgs[0]);
        if (!(target instanceof final VanillaCommandWrapper wrapper)) return original.call(instance, k, v);

        final LiteralCommandNode<?> shadowedTarget = (LiteralCommandNode<?>) wrapper.vanillaCommand;
        if (!(shadowedTarget instanceof final ShadowBrigNode shadowed)) return original.call(instance, k, v);

        final LiteralCommandNode<?> targetLiteral = (LiteralCommandNode<?>) shadowed.getHandle();

        final String redirectName = (String) k;
        final CommandNode<?> redirect = PaperBrigadier.copyLiteral(redirectName, targetLiteral);

        return original.call(instance, redirectName, PaperBrigadier.wrapNode(redirect));
    }
}
