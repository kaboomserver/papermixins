package pw.kaboom.papermixins.pluginmixin.mixins.fix.geyser_command_limits;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOverloadData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import pw.kaboom.papermixins.pluginmixin.PluginMixin;

import java.util.List;

@PluginMixin("Geyser-Spigot")
@Mixin(targets = {"org.geysermc.geyser.translator.protocol.java.JavaCommandsTranslator$ParamInfo"})
public abstract class JavaCommandsTranslatorParamInfoMixin {
    // https://www.minecraft.net/en-us/article/minecraft-1-21-130-bedrock-changelog#:~:text=AvailableCommandsPacket
    @Unique
    private static final int MAX_COMMAND_OVERLOADS = 250;

    @ModifyExpressionValue(method = "getTree",
        at = @At(value = "INVOKE", target = "Lorg/geysermc/geyser/translator/protocol/java/JavaCommandsTranslator" +
            "$ParamInfo;getTree()Ljava/util/List;"))
    private List<CommandOverloadData> getTree$getTree(final List<CommandOverloadData> original) {
        if (original.size() <= MAX_COMMAND_OVERLOADS) return original;

        return List.of();
    }
}
