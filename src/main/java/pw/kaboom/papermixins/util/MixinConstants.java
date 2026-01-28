package pw.kaboom.papermixins.util;

import com.mojang.brigadier.CommandDispatcher;
import io.papermc.paper.command.brigadier.ApiMirrorRootNode;
import net.minecraft.commands.CommandSourceStack;

public final class MixinConstants {
    // Static abuse so we can reference stuff in multiple mixins
    // (Mixin classes cannot be referenced directly)

    public static final CommandDispatcher<CommandSourceStack> VANILLA_DISPATCHER = new CommandDispatcher<>();

    public static final CommandDispatcher<io.papermc.paper.command.brigadier.CommandSourceStack> VANILLA_DISPATCHER_PAPER =
            new CommandDispatcher<>(new ApiMirrorRootNode() {
                @Override
                public CommandDispatcher<CommandSourceStack> getDispatcher() {
                    return MixinConstants.VANILLA_DISPATCHER;
                }
            });

    private MixinConstants() {}
}
