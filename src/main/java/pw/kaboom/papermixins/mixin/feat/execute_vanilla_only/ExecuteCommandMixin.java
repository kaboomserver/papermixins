package pw.kaboom.papermixins.mixin.feat.execute_vanilla_only;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.ExecuteCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pw.kaboom.papermixins.util.MixinConstants;

@Mixin(ExecuteCommand.class)
public abstract class ExecuteCommandMixin {
    @WrapOperation(method = "register",
            at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;getRoot()" +
                    "Lcom/mojang/brigadier/tree/RootCommandNode;"))
    private static RootCommandNode<CommandSourceStack> register$getRoot(final CommandDispatcher<CommandSourceStack> instance,
                                                                        final Operation<RootCommandNode<CommandSourceStack>> original) {
        return MixinConstants.VANILLA_DISPATCHER.getRoot();
    }
}
