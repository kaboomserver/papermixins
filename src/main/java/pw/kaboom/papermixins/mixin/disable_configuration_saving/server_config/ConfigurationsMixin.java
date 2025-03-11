package pw.kaboom.papermixins.mixin.disable_configuration_saving.server_config;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.papermc.paper.configuration.Configurations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

@Mixin(Configurations.class)
public abstract class ConfigurationsMixin {
    @WrapMethod(method = "trySaveFileNode")
    private void trySaveFileNode(final YamlConfigurationLoader loader,
                                 final ConfigurationNode node,
                                 final String filename,
                                 final Operation<Void> original) {

    }
}
