package pw.kaboom.papermixins.mixin.disable_configuration_saving.server_config;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.dedicated.Settings;
import org.spongepowered.asm.mixin.Mixin;

import java.nio.file.Path;

@Mixin(Settings.class)
public abstract class SettingsMixin {
    @WrapMethod(method = "store")
    private void store(final Path path, final Operation<Void> original) {

    }
}
