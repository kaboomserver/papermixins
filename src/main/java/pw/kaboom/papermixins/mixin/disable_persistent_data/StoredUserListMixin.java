package pw.kaboom.papermixins.mixin.disable_persistent_data;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.players.StoredUserList;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StoredUserList.class)
public abstract class StoredUserListMixin {
    @WrapMethod(method = "save")
    private void save(final Operation<Void> original) {

    }

    @WrapMethod(method = "load")
    private void load(final Operation<Void> original) {

    }
}
