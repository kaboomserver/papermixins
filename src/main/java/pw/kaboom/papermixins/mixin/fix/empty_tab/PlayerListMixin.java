package pw.kaboom.papermixins.mixin.fix.empty_tab;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Bukkit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import pw.kaboom.papermixins.util.ViaVersionHax;

import static pw.kaboom.papermixins.util.ProtocolVersions.v1_8;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Shadow
    public abstract int getPlayerCount();

    @WrapOperation(method = "placeNewPlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;getMaxPlayers()I"))
    private int placeNewPlayer$getMaxPlayers(PlayerList instance,
                                             Operation<Integer> original,
                                             @Local(argsOnly = true, name = "connection") Connection connection,
                                             @Local(argsOnly = true, name = "player") ServerPlayer player) {
        if (ViaVersionHax.getOriginalVersion(connection.channel) >= v1_8)
            return original.call(instance);

        // This is a bit of an unsolvable problem, because 1.7.x will never expand the player list beyond the maxPlayers
        // value sent here, so we have to make a bit of a compromise between form & function here.

        // We also can't update it after join.

        // Setting it to a value too large (i.e. one over 80) will result in usernames clipping out of their row,
        // and setting it to something too low will result in new players not being shown.

        // So, the next nearest multiple of 10 from the online player count (minimum 20) should be able to accommodate
        // all players, save a spambot attack, since Kaboom is a niche server +10 players from join should be a pretty
        // liberal growth estimate.

        // We'd also like to keep it as low as possible because that's what looks best in the client.

        final int onlinePlayers = (int) Bukkit.getOnlinePlayers().stream()
                .filter(onlinePlayer -> player.getBukkitEntity().canSee(onlinePlayer))
                .count();
        return Math.max(20, onlinePlayers - (onlinePlayers % 10) + 10);
    }
}
