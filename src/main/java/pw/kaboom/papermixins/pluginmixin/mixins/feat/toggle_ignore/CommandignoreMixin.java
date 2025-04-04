package pw.kaboom.papermixins.pluginmixin.mixins.feat.toggle_ignore;

import com.earth2me.essentials.IUser;
import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.Commandignore;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pw.kaboom.papermixins.pluginmixin.PluginMixin;

@PluginMixin("Essentials")
@Mixin(Commandignore.class)
public abstract class CommandignoreMixin {
    @WrapOperation(method = "run", at = @At(value = "INVOKE",
            target = "Lcom/earth2me/essentials/User;isIgnoredPlayer(Lcom/earth2me/essentials/IUser;)Z"))
    private boolean run$isIgnoredPlayer(final User user,
                                        final IUser toCheck,
                                        final Operation<Boolean> original,
                                        final @Local(argsOnly = true) String[] args) {
        if (args.length < 2) return original.call(user, toCheck);

        final Boolean targetValue = switch (args[1].toLowerCase()) {
            case "on", "enable" -> Boolean.TRUE;
            case "off", "disable" -> Boolean.FALSE;
            default -> null;
        };

        if (targetValue == null) return original.call(user, toCheck);
        return !targetValue;
    }
}
