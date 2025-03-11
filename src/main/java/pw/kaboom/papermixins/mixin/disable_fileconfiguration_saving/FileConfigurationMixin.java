package pw.kaboom.papermixins.mixin.disable_fileconfiguration_saving;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.bukkit.configuration.file.FileConfiguration;
import org.spongepowered.asm.mixin.Mixin;

import java.io.File;

@Mixin(FileConfiguration.class)
public abstract class FileConfigurationMixin {
    @WrapMethod(method = "save(Ljava/io/File;)V")
    private void save(File file, Operation<Void> original) {

    }
}
