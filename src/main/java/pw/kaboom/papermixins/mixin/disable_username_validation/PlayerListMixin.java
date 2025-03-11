package pw.kaboom.papermixins.mixin.disable_username_validation;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.File;
import java.util.UUID;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @WrapOperation(
            method = "getPlayerStats(Ljava/util/UUID;Ljava/lang/String;)Lnet/minecraft/stats/ServerStatsCounter;",
            at = @At(
                    value = "NEW",
                    target = "(Ljava/io/File;Ljava/lang/String;)Ljava/io/File;"
            )
    )
    private File getPlayerStats$newFile(final File parent,
                                        final String child,
                                        final Operation<File> original,
                                        @Local(argsOnly = true) final UUID id) {
        return original.call(parent, id + ".json");
    }
}
