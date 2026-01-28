package pw.kaboom.papermixins.util;

import com.mojang.brigadier.CommandDispatcher;
import io.papermc.paper.command.brigadier.ApiMirrorRootNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;

import java.util.Set;

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

    public static final Set<Class<? extends Packet<?>>> FAIL_IGNORED_PACKETS = Set.of(
        ClientboundSetTitleTextPacket.class, ClientboundSetSubtitleTextPacket.class,
        ClientboundSetActionBarTextPacket.class, ClientboundSetPlayerTeamPacket.class,
        ClientboundLevelParticlesPacket.class,  ClientboundSetEntityDataPacket.class,
        ClientboundContainerSetContentPacket.class, ClientboundContainerSetSlotPacket.class,
        ClientboundLevelChunkWithLightPacket.class, ClientboundBlockEntityDataPacket.class,
        ClientboundSetPlayerInventoryPacket.class, ClientboundSetObjectivePacket.class
    );

    private MixinConstants() {}
}
