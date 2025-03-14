package pw.kaboom.papermixins.mixin.disable_configuration_saving.server_config;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.bukkit.configuration.file.YamlConfiguration;
import org.spigotmc.SpigotConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.File;

@Mixin(SpigotConfig.class)
public abstract class SpigotConfigMixin {
    @WrapOperation(
            method = "readConfig",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/bukkit/configuration/file/YamlConfiguration;save(Ljava/io/File;)V"
            )
    )
    private static void readConfig$save(final YamlConfiguration instance, final File file, final Operation<Void> original) {

    }
}
