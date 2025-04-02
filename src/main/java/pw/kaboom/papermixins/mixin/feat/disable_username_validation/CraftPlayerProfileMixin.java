package pw.kaboom.papermixins.mixin.feat.disable_username_validation;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CraftPlayerProfile.class)
public abstract class CraftPlayerProfileMixin {
    @WrapOperation(method = "createAuthLibProfile",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/StringUtil;isValidPlayerName(Ljava/lang/String;)Z"))
    private static boolean createAuthLibProfile$isValidPlayerName(final String playerName,
                                                                  final Operation<Boolean> original) {
        return true; // :)
    }
}
