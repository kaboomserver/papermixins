package pw.kaboom.papermixins.pluginmixin.mixins.perf.gc_optimization;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.commands.Commandgc;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pw.kaboom.papermixins.pluginmixin.PluginMixin;

import java.util.Collections;
import java.util.List;

@PluginMixin("Essentials")
@Mixin(Commandgc.class)
public abstract class CommandgcMixin {
    @WrapOperation(method = "run",
            at = @At(value = "INVOKE", target = "Lorg/bukkit/Server;getWorlds()Ljava/util/List;"),
            require = 0)
    private List<World> run$getWorlds(final Server instance, final Operation<List<World>> original,
                                      final @Local(argsOnly = true) CommandSource sender) {
        for (final World world : original.call(instance)) {
            final World.Environment worldEnvironment = world.getEnvironment();

            // Switch statement here breaks due to subclasses not being passed to the mixin service ClassLoader
            String worldType = "World";
            if (worldEnvironment == World.Environment.NETHER) {
                worldType = "Nether";
            } else if (worldEnvironment == World.Environment.THE_END) {
                worldType = "The End";
            }

            final ServerLevel handle = ((CraftWorld) world).getHandle();
            final int entityCount = handle.moonrise$getEntityLookup().getEntityCount();
            int loadedChunks = 0;
            int tileEntities = 0;

            for (final ServerChunkCache.ChunkAndHolder chunk : handle.moonrise$getLoadedChunks()) {
                loadedChunks++;
                tileEntities += chunk.chunk().blockEntities.size();
            }

            sender.sendTl("gcWorld", worldType, world.getName(), loadedChunks, entityCount, tileEntities);
        }

        return Collections.emptyList();
    }
}