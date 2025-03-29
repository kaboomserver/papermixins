package pw.kaboom.papermixins.pluginmixin.mixins.perf.gc_optimization;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.commands.Commandgc;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;
import pw.kaboom.papermixins.pluginmixin.PluginMixin;

import java.util.Collections;
import java.util.List;

@PluginMixin("Essentials")
@Mixin(Commandgc.class)
public abstract class CommandgcMixin {
    @WrapOperation(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/bukkit/World;getLoadedChunks()[Lorg/bukkit/Chunk;"
            ))
    private Chunk[] run$getTileEntities(final World instance,
                                        final Operation<Chunk[]> original) {
        return new Chunk[0];
    }

    @WrapOperation(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/bukkit/World;getEntities()Ljava/util/List;"))
    private List<Entity> run$getEntities(final World instance,
                                         final Operation<List<Entity>> original) {
        return Collections.emptyList();
    }

    @WrapOperation(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/earth2me/essentials/CommandSource;sendTl(Ljava/lang/String;[Ljava/lang/Object;)V"
            ),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lorg/bukkit/Chunk;getTileEntities()[Lorg/bukkit/block/BlockState;")
            ))
    private void run$sendTl(final CommandSource instance,
                            final String key,
                            final Object[] args,
                            final Operation<Void> original,
                            final @Local World world) {
        if (key.equals("gcWorld")) {
            final ServerLevel handle = ((CraftWorld) world).getHandle();

            int loadedChunks = 0;
            int tileEntities = 0;

            for (final ServerChunkCache.ChunkAndHolder chunk : handle.moonrise$getLoadedChunks()) {
                loadedChunks++;
                tileEntities += chunk.chunk().blockEntities.size();
            }

            args[2] = loadedChunks;
            args[3] = handle.moonrise$getEntityLookup().getEntityCount();
            args[4] = tileEntities;
        }

        original.call(instance, key, args);
    }
}