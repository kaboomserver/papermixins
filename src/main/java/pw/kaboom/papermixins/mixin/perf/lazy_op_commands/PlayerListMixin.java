package pw.kaboom.papermixins.mixin.perf.lazy_op_commands;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.TickThrottler;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.kaboom.papermixins.duck.LazyCommandsExtension;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Unique
    private final Set<UUID> papermixins$commandUpdateQueue = new LinkedHashSet<>();
    @Unique
    private final TickThrottler papermixins$commandUpdateThrottler = new TickThrottler(1, 10);

    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    public abstract @Nullable ServerPlayer getPlayer(UUID uuid);

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void tick(final CallbackInfo ci) {
        this.papermixins$commandUpdateThrottler.tick();
        if (this.papermixins$commandUpdateQueue.isEmpty()) return;

        final Iterator<UUID> it = this.papermixins$commandUpdateQueue.iterator();
        while (this.papermixins$commandUpdateThrottler.isUnderThreshold() && it.hasNext()) {
            final ServerPlayer player = this.getPlayer(it.next());
            it.remove();

            if (player == null) continue;

            this.papermixins$commandUpdateThrottler.increment();
            this.server.getCommands().sendCommands(player);
        }
    }

    @WrapOperation(method = "sendPlayerPermissionLevel(Lnet/minecraft/server/level/ServerPlayer;" +
        "Lnet/minecraft/server/permissions/LevelBasedPermissionSet;Z)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;sendCommands(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void sendPlayerPermissionLevel$sendCommands(final Commands instance, final ServerPlayer player,
                                                        final Operation<Void> original) {
        if (!LazyCommandsExtension.LAZY_COMMANDS.isBound()) {
            original.call(instance, player);
            return;
        }

        final UUID uuid = player.getUUID();

        // don't be lazy if we can
        if (this.papermixins$commandUpdateThrottler.isUnderThreshold()) {
            this.papermixins$commandUpdateThrottler.increment();

            this.papermixins$commandUpdateQueue.remove(uuid);
            original.call(instance, player);
            return;
        }

        this.papermixins$commandUpdateQueue.add(uuid);
    }
}
